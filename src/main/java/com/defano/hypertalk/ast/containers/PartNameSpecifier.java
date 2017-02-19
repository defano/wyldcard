/*
 * PartNameSpecifier
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PartNameSpecifier.java
 * @author matt.defano@gmail.com
 * 
 * Name-based specification of a part, for example, "button myButton"
 */

package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.common.PartType;


public class PartNameSpecifier implements PartSpecifier {

    public PartType type;
    public String name;

    public PartNameSpecifier () {}

    public PartNameSpecifier (PartType type, String name) {
        this.type = type;
        this.name = name;
    }
    
    public PartType type() {
        return type;
    }
    
    public String value() {
        return name;
    }
    
    public String toString() {
        return type + " " + name;
    }
}
