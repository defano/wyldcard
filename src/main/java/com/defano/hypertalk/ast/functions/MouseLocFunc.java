/*
 * ExpMouseLocFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * MouseLocFunc.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the mouseLoc"
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.awt.MouseManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;

public class MouseLocFunc extends Expression {

    public MouseLocFunc() {}
    
    public Value evaluate () {
        return new Value(MouseManager.getMouseLoc());
    }
}
