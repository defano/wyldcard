package com.defano.hypertalk.ast.expression.part;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.enums.Ordinal;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.specifier.PartNumberSpecifier;
import com.defano.hypertalk.ast.model.specifier.PartOrdinalSpecifier;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartNumberExp extends PartExp {

    public final Owner layer;
    public final PartType type;
    public final Expression number;
    public final Ordinal ordinal;
    public final boolean marked;

    public PartNumberExp(ParserRuleContext context, Owner owner, PartType type, Ordinal ordinal, boolean marked) {
        this(context, owner, type, null, ordinal, marked);
    }

    /**
     * Create an expression referring to a button or field based on its "part number" on either the card or background
     * layer.
     * @param context The parser context
     * @param owner The owning part layer (card or background)
     * @param expression An integer expression referring to the part number.
     */
    public PartNumberExp(ParserRuleContext context, Owner owner, Expression expression) {
        this(context, owner,  null, expression, null, false);
    }

    public PartNumberExp(ParserRuleContext context, PartType type, Ordinal ordinal, boolean marked) {
        this(context, null, type, null, ordinal, marked);
    }

    private PartNumberExp(ParserRuleContext context, Owner layer, PartType type, Expression number, Ordinal ordinal, boolean marked) {
        super(context);
        this.layer = layer;
        this.number = number;
        this.type = type;
        this.ordinal = ordinal;
        this.marked = marked;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) throws HtException {
        if (ordinal != null) {
            return new PartOrdinalSpecifier(layer, type, ordinal, marked);
        } else {
            return new PartNumberSpecifier(layer, type, number.evaluate(context).integerValue(), marked);
        }
    }
}
