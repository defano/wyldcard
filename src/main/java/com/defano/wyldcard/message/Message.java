package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.model.Value;

import java.util.List;

public interface Message {

    String getMessageName();

    List<Value> getArguments();
}
