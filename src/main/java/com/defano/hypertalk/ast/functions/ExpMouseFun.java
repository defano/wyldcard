/*
 * ExpMouseFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpMouseFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the mouse"
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.gui.util.MouseManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;

public class ExpMouseFun extends Expression {

    public ExpMouseFun () {}
    
    public Value evaluate () {
        return MouseManager.isMouseDown() ? new Value("down") : new Value("up");
    }
}
