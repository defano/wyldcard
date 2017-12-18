package com.defano.hypertalk.ast.model;

import java.util.ArrayList;
import java.util.List;

public class ParameterList {

    public final List<String> list;
    
    public ParameterList () {
        list = new ArrayList<>();
    }
    
    public ParameterList(String p) {
        list = new ArrayList<>();
        list.add(p);
    }
    
    public ParameterList addParameter (String p) {
        list.add(p);
        return this;
    }
}
