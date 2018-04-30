package com.defano.hypertalk.ast.expressions.parts;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Ordinal;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartNumberSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartOrdinalSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartNumberExp extends PartExp {

    public final Owner layer;
    public final PartType type;
    public final Expression number;
    public final Ordinal ordinal;

    public PartNumberExp(ParserRuleContext context, Owner owner, PartType type, Ordinal ordinal) {
        this(context, owner, type, null, ordinal);
    }

    /**
     * Create an expression referring to a button or field based on its "part number" on either the card or background
     * layer.
     * @param context The parser context
     * @param owner The owning part layer (card or background)
     * @param expression An integer expression referring to the part number.
     */
    public PartNumberExp(ParserRuleContext context, Owner owner, Expression expression) {
        this(context, owner,  null, expression, null);
    }

    public PartNumberExp(ParserRuleContext context, PartType type, Ordinal ordinal) {
        this(context, null, type, null, ordinal);
    }

    private PartNumberExp(ParserRuleContext context, Owner layer, PartType type, Expression number, Ordinal ordinal) {
        super(context);
        this.layer = layer;
        this.number = number;
        this.type = type;
        this.ordinal = ordinal;
    }

    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) throws HtException {
        if (ordinal != null) {
            return new PartOrdinalSpecifier(layer, type, ordinal);
        } else {
            return new PartNumberSpecifier(layer, type, number.evaluate(context).integerValue());
        }
    }
}
