package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.interpreter.Interpreter;
import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;


public abstract class Expression extends ASTNode {

    public Expression(ParserRuleContext context) {
        super(context);
    }

    /**
     * Evaluates the expression as a HyperTalk {@link Value}.
     *
     * @return The evaluated {@link Value} of this expression.
     * @throws HtException Thrown if an error occurs evaluating the expression.
     */
    protected abstract Value onEvaluate() throws HtException;

    /**
     * Evaluates this expression, returning a HyperTalk {@link Value} or throwing a "contextualized" exception if an
     * error occurs during evaluation. A contextualized exception is one containing a
     * {@link com.defano.hypercard.runtime.Breadcrumb} referring to the script token and script-owning part that
     * resulted in the exception.
     *
     * @return The value that this expression evaluated to.
     * @throws HtException Thrown to indicate a semantic error was encountered during evaluation.
     */
    public Value evaluate() throws HtException {
        try {
            return onEvaluate();
        } catch (HtException e) {
            rethrowContextualizedException(e);
        }

        throw new IllegalStateException("Bug! Contextualized exception not thrown.");
    }

    /**
     * Attempts to evaluate the expression as a reference to a part of the given {@link PartModel} class
     * type. Returns null if the expression cannot be evaluated as a part of this type (either because this expression
     * cannot be factored into a valid part expression, or because the resultant part expression does not refer to
     * an existing part).
     * <p>
     * For example, if the expression "cd fld 1" was evaluated as a CardPart.class, the text of card field 1 would be
     * interpreted as a HyperTalk expression, that, if containing a reference to a card (for example, if the field
     * contained the text "the last card") then a CardPart representing the last card in the stack would be returned.
     * <p>
     * If, in this example, card field 1 contained an expression like "cd fld 2", then this method would attempt to
     * evaluate the text of card field 2 looking for a valid card reference.
     *
     * @param clazz The class of part model to coerce this expression to.
     * @param <T>   A subtype of PartModel
     * @return The part model referred to by this expression or null if the expression does not refer to a part of this
     * type.
     */
    public <T extends PartModel> T partFactor(Class<T> clazz) {
        PartExp partExp = factor(PartExp.class);

        if (partExp == null) {
            return null;
        }

        try {
            PartSpecifier partSpecifier = partExp.evaluateAsSpecifier();

            if (partSpecifier == null) {
                return null;
            }

            PartModel model = ExecutionContext.getContext().getPart(partSpecifier);
            if (clazz.isAssignableFrom(model.getClass())) {
                return (T) model;
            } else {
                PartExp refExp = Interpreter.blockingEvaluate(partExp.evaluate(), PartExp.class);
                return refExp == null ? null : refExp.partFactor(clazz);
            }
        } catch (HtException e) {
            return null;
        }
    }

    /**
     * A convenience form of {@link #partFactor(Class)} that throws an exception rather than returning null if this
     * expression cannot be evaluated as a {@link PartModel} of the requested type.
     *
     * @param clazz   The class of part model to coerce this expression to.
     * @param <T>     A subtype of PartModel
     * @param orError An exception to be thrown if the factor cannot be evaluated as requested.
     * @return The part model referred to by this expression or null if the expression does not refer to a part of this
     * type.
     * @throws HtException Thrown if the factor cannot be evaluated as requested.
     */
    public <T extends PartModel> T partFactor(Class<T> clazz, HtException orError) throws HtException {
        T factor = partFactor(clazz);
        if (factor == null) {
            throw orError;
        } else {
            return factor;
        }
    }

    /**
     * Attempts to evaluate this expression as a factor conforming to one of a prioritized list of acceptable types.
     * <p>
     * When the expression can be evaluated as an acceptable type, the associated {@link FactorAction} is invoked. No
     * more than one {@link FactorAction} will be invoked (but no actions will be invoked if this expression cannot be
     * interpreted as an acceptable type).
     * <p>
     * This method enables a recursive, context-sensitive evaluation of terms.
     *
     * @param evaluations A prioritized order list of acceptable factor types, plus an action associated with each
     * @return True if this expression can be interpreted as an acceptable type (indicates that a {@link FactorAction}
     * was invoked); false otherwise.
     * @throws HtException Thrown if an invoked {@link FactorAction} produces an exception. Will not be thrown as part
     *                     of the process of evaluating the expression.
     */
    public boolean factor(FactorAssociation... evaluations) throws HtException {

        // Special case: Expression is a group (has parens around it), try to factor the evaluated result first. If not,
        // continue attempting to factor as if the expression was not grouped.
        if (this instanceof GroupExp) {
            try {
                LiteralExp exp = new LiteralExp(null, this.ungroup().evaluate());
                if (exp.factor(evaluations)) {
                    return true;
                }
            } catch (HtException e) {
                // Nothing to do
            }
        }

        // Base case: this expression (not including parens) directly matches the requested type, then take action
        for (FactorAssociation thisEvaluation : evaluations) {
            if (thisEvaluation.expressionType.isAssignableFrom(this.ungroup().getClass())) {
                thisEvaluation.action.accept(this.ungroup());
                return true;
            }
        }

        // If not, try to interpret this expression as each of the allowable types
        for (FactorAssociation thisEvaluation : evaluations) {
            Object coerced = this.evaluateAs(thisEvaluation.expressionType);
            if (coerced != null) {
                thisEvaluation.action.accept(coerced);
                return true;
            }
        }

        // Loser, loser, chicken boozer. Can't interpret this factor as a requested type.
        return false;
    }

    /**
     * A convenience form of {@link #factor(FactorAssociation[])} that accepts a single, acceptable expression type
     * and attempts to interpret this expression as that type. Returns null if this expression cannot be interpreted
     * in the requested format.
     *
     * @param clazz The requested type of expression to factor this expression into.
     * @param <T>   The requested factor subtype of {@link Expression}
     * @return This expression interpreted as the requested type, or null if unable to interpret as requested.
     */
    public <T extends Expression> T factor(Class<T> clazz) {
        try {
            final Object[] expr = new Object[1];
            factor(new FactorAssociation(clazz, object -> expr[0] = object));
            return (T) expr[0];
        } catch (HtException e) {
            // Thrown only if a factor action throws it; our action never throws it
            throw new IllegalStateException("Bug! This exception should not be possible.");
        }
    }

    /**
     * A convenience form of {@link #factor(FactorAssociation[])} that accepts a single, acceptable expression type and
     * an exception to throw if this expression cannot be interpreted as the requested type.
     *
     * @param clazz   The requested type of expression to factor this expression into.
     * @param orError The exception to be throw if this expression cannot be factored.
     * @param <T>     The requested factor subtype of {@link Expression}
     * @return A factored representation of this expression as T
     * @throws HtException Thrown if the factorization fails.
     */
    public <T extends Expression> T factor(Class<T> clazz, HtException orError) throws HtException {
        T result = factor(clazz);
        if (result == null) {
            throw orError;
        } else {
            return result;
        }
    }

    /**
     * Attempts to evaluate this expression as an Expression of the requested subtype.
     * <p>
     * Evaluates this expression as a HyperTalk {@link Value}, then invokes the {@link Interpreter} to re-parse the
     * value. If the re-interpreted value matches the requested type then it is returned. Otherwise, null is returned.
     *
     * @param klazz The requested class to evaluate this expression as.
     * @param <T>   The requested Expression subtype.
     * @return This expression evaluated as the requested type or null.
     */
    private <T extends Expression> T evaluateAs(Class<T> klazz) {
        if (this.ungroup().getClass().isAssignableFrom(klazz)) {
            return (T) this.ungroup();
        }

        try {
            return Interpreter.blockingEvaluate(this.evaluate(), klazz);
        } catch (HtException e) {
            return null;
        }
    }

    /**
     * Recursively un-groups this expression (returns the expression inside of parens); has no effect if this expression
     * is not grouped. For example, if this expression is "(2 + 3)" or "(((2 + 3)))", this produces "2 + 3".
     *
     * @return The un-grouped portion of this expression.
     */
    private Expression ungroup() {
        if (this instanceof GroupExp) {
            return ((GroupExp) this).expression.ungroup();
        } else {
            return this;
        }
    }

}
