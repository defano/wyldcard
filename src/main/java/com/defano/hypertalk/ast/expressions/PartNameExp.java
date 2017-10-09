/*
 * ExpPartName
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PartNameExp.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Encapsulation of name-based part specification, for example: "field myField"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartNameSpecifier;
import com.defano.hypertalk.ast.specifiers.PartNumberSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
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

    public Value onEvaluate() throws HtSemanticException {
        try {
            return ExecutionContext.getContext().get(evaluateAsSpecifier()).getValue();
        } catch (Exception e) {
            throw new HtSemanticException("Can't get that part.");
        }
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
