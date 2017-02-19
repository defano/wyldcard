/*
 * ExpMouseLocFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpMouseLocFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the mouseLoc"
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.gui.util.MouseManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;

public class ExpMouseLocFun extends Expression {

    public ExpMouseLocFun () {}
    
    public Value evaluate () {
        return new Value(MouseManager.getMouseLoc());
    }
}
