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
        PartExp partExp = evaluateAsPart();

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
    public PartExp evaluateAsPart() {
        if (this instanceof PartExp) {
            return (PartExp) this;
        }

        try {
            return Interpreter.evaluate(this.evaluate(), PartExp.class);
        } catch (HtException e) {
            return null;
        }
    }


}
