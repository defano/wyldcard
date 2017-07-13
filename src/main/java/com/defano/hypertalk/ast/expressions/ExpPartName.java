/*
 * ExpPartName
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpPartName.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Encapsulation of name-based part specification, for example: "field myField"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.common.PartLayer;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartNameSpecifier;
import com.defano.hypertalk.ast.containers.PartNumberSpecifier;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpPartName extends ExpPart {

    public final PartLayer layer;
    public final PartType type;
    public final Expression name;

    public ExpPartName(PartLayer layer, PartType type, Expression name) {
        this.layer = layer;
        this.type = type;
        this.name = name;
    }

    public Value evaluate() throws HtSemanticException {
        try {
            return GlobalContext.getContext().get(evaluateAsSpecifier()).getValue();
        } catch (Exception e) {
            throw new HtSemanticException("Can't get that part.");
        }
    }

    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        Value evaluatedName = name.evaluate();

        if (evaluatedName.isInteger()) {
            return new PartNumberSpecifier(layer, type, evaluatedName.integerValue());
        } else {
            return new PartNameSpecifier(layer, type, name.evaluate().stringValue());
        }
    }
}
