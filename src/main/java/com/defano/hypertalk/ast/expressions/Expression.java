package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;


public abstract class Expression extends ASTNode {

    public Expression(ParserRuleContext context) {
        super(context);
    }

    protected abstract Value onEvaluate() throws HtException;

    public Value evaluate() throws HtException {
        try {
            return onEvaluate();
        } catch (HtException e) {
            rethrowContextualizedException(e);
        }

        throw new IllegalStateException("Bug! Contextualized exception not thrown.");
    }

    /**
     * Recursively attempts to evaluate the expression as a reference to a part of the given {@link PartModel} class
     * type. Returns null if the expression cannot be evaluated as a part of this type.
     *
     * For example, if the expression "cd fld 1" was evaluated as a CardPart.class, the text of card field 1 would be
     * interpreted as a HyperTalk expression, that, if containing a reference to a card (for example, if the field
     * contained the text "the last card") then a CardPart representing the last card in the stack would be returned.
     *
     * If, in this example, card field 1 contained an expression like "cd fld 2", then this method would attempt to
     * evaluate the text of card field 2 looking for a valid card reference.
     *
     * @param clazz The class of part model to coerce this expression to.
     * @param <T> A subtype of PartModel
     * @return The part model referred to by this expression or null if the expression does not refer to a part of this
     * type.
     */
    public <T extends PartModel> T evaluateAsPartModel(Class<T> clazz) {
        PartExp partExp = evaluateAsPartExp();

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
                PartExp refExp = Interpreter.evaluate(partExp.evaluate(), PartExp.class);
                return refExp == null ? null : refExp.evaluateAsPartModel(clazz);
            }
        } catch (HtException e) {
            return null;
        }
    }

    /**
     * Returns the part expression evaluation of this expression, or null, if this expression cannot be represented
     * as a part expression.
     *
     * If this expression is already a {@link PartExp}, then this is returned. If this is not a {@link PartExp}, then
     * the value of the expression is evaluated and returned.
     *
     * @return A {@link PartExp} representation of this expression or null
     * @throws HtException
     */
    private PartExp evaluateAsPartExp() {
        if (this.ungrouped() instanceof PartExp) {
            return (PartExp) this.ungrouped();
        }

        try {
            return Interpreter.evaluate(this.evaluate(), PartExp.class);
        } catch (HtException e) {
            return null;
        }
    }

    private MenuItemExp evaluateAsMenuItemExp() {
        if (this.ungrouped() instanceof MenuItemExp) {
            return (MenuItemExp) this.ungrouped();
        }

        try {
            return Interpreter.evaluate(this.evaluate(), MenuItemExp.class);
        } catch (Exception e) {
            return null;
        }
    }

    private MenuExp evaluateAsMenuExp() {
        if (this.ungrouped() instanceof MenuExp) {
            return (MenuExp) this.ungrouped();
        }

        try {
            return Interpreter.evaluate(this.evaluate(), MenuExp.class);
        } catch (Exception e) {
            return null;
        }
    }

    private VisualEffectExp evaluateAsVisualEffect() {
        if (this.ungrouped() instanceof VisualEffectExp) {
            return (VisualEffectExp) this.ungrouped();
        }

        try {
            return Interpreter.evaluate(this.evaluate(), VisualEffectExp.class);
        } catch (HtException e) {
            return null;
        }
    }

    private <T extends Expression> T evaluateAs(Class<T> klazz) {
        if (klazz.equals(PartExp.class)) {
            return (T) evaluateAsPartExp();
        } else if (klazz.equals(MenuExp.class)) {
            return (T) evaluateAsMenuExp();
        } else if (klazz.equals(MenuItemExp.class)) {
            return (T) evaluateAsMenuItemExp();
        } else if (klazz.equals(VisualEffectExp.class)) {
            return (T) evaluateAsVisualEffect();
        }

        return null;
    }

    private Expression ungrouped() {
        if (this instanceof GroupExp) {
            return ((GroupExp) this).expression.ungrouped();
        } else {
            return this;
        }
    }

    /**
     * Attempts to evaluate this expression as a factor conforming to one of a prioritized list of acceptable types.
     * When the factor can be evaluated as an acceptable type, the associated {@link FactorAction} is invoked. No more
     * than one {@link FactorAction} will be invoked (but no actions will be invoked if this expression cannot be
     * interpreted as an acceptable type).
     *
     * This method enables a recursive, context-sensitive evaluation of terms.
     *
     * @param evaluations A prioritized order list of acceptable
     * @return True if this expression can be interpreted as an acceptable type (indicates that a {@link FactorAction}
     * was invoked); false otherwise.
     * @throws HtException Thrown if an invoked {@link FactorAction} produces an exception. Will not be thrown as part
     * of the process of evaluating the expression.
     */
    public boolean factor(FactorAssociation... evaluations) throws HtException {

        // If this expression directly matches the requested type, then take action
        for (FactorAssociation thisEvaluation : evaluations) {
            if(thisEvaluation.expressionType.isAssignableFrom(this.getClass())) {
                thisEvaluation.action.accept(this);
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

    public <T extends Expression> T factor(Class<T> clazz) throws HtException {
        final Object[] expr = new Object[1];
        factor(new FactorAssociation(clazz, object -> expr[0] = object));
        return (T) expr[0];
    }

    public <T extends Expression> T factor(Class<T> clazz, HtException orError) throws HtException {
        T result = factor(clazz);
        if (result == null) {
            throw orError;
        } else {
            return result;
        }
    }

}
