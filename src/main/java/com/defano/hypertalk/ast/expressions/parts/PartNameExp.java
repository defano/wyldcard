package com.defano.hypertalk.ast.expressions.parts;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartNameSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartNumberSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartNameExp extends PartExp {

    public final Owner layer;
    public final PartType type;
    public final Expression name;
    public final boolean marked;

    public PartNameExp(ParserRuleContext context, PartType type, Expression name, boolean marked) {
        this(context, null, type, name, marked);
    }

    public PartNameExp(ParserRuleContext context, Owner layer, PartType type, Expression name) {
        this(context, layer, type, name, false);
    }

    public PartNameExp(ParserRuleContext context, Owner layer, PartType type, Expression name, boolean marked) {
        super(context);
        this.layer = layer;
        this.type = type;
        this.name = name;
        this.marked = marked;
    }

    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) throws HtException {
        Value evaluatedName = name.evaluate(context);

        // Quoted literals are always assumed to refer to name, even if value is a number (i.e., 'button "1"' refers
        // to button named "1')
        if (evaluatedName.isInteger() && !evaluatedName.isQuotedLiteral()) {
            return new PartNumberSpecifier(layer, type, evaluatedName.integerValue(), marked);
        } else {
            return new PartNameSpecifier(layer, type, evaluatedName.toString(), marked);
        }
    }
}
