package com.defano.hypertalk.ast.common;

import java.util.List;
import java.util.Vector;

public class ParameterList {

    public final List<String> list;
    
    public ParameterList () {
        list = new Vector<>();
    }
    
    public ParameterList(String p) {
        list = new Vector<>();
        list.add(p);
    }
    
    public ParameterList addParameter (String p) {
        list.add(p);
        return this;
    }
}
