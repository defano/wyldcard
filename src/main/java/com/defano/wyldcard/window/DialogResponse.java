package com.defano.wyldcard.window;

import com.defano.hypertalk.ast.model.Value;

public class DialogResponse {

    private final Value buttonResponse;
    private final Value fieldResponse;

    public DialogResponse(Value buttonResponse, Value fieldResponse) {
        this.buttonResponse = buttonResponse;
        this.fieldResponse = fieldResponse;
    }

    public Value getButtonResponse() {
        return buttonResponse;
    }

    public Value getFieldResponse() {
        return fieldResponse;
    }
}
