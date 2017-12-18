package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartNameSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartNumberSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartNameExp extends PartExp {

    public final Owner layer;
    public final PartType type;
    public final Expression name;

    public PartNameExp(ParserRuleContext context, PartType type, Expression name) {
        this(context, null, type, name);
    }

    public PartNameExp(ParserRuleContext context, Owner layer, PartType type, Expression name) {
        super(context);
        this.layer = layer;
        this.type = type;
        this.name = name;
    }

    public PartSpecifier evaluateAsSpecifier() throws HtException {
        Value evaluatedName = name.evaluate();

        if (evaluatedName.isInteger()) {
            return new PartNumberSpecifier(layer, type, evaluatedName.integerValue());
        } else {
            return new PartNameSpecifier(layer, type, evaluatedName.stringValue());
        }
    }
}
