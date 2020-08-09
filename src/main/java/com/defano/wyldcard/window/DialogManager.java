package com.defano.wyldcard.window;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface DialogManager {

    DialogResponse answer(ExecutionContext context, Value msg, Value choice1, Value choice2, Value choice3);
    DialogResponse answerFile(ExecutionContext context, Value promptString, Value fileFilter);
    DialogResponse ask(ExecutionContext context, Value question, Value suggestion);
    DialogResponse askFile(ExecutionContext context, Value prompt, Value file);
    DialogResponse askPassword(ExecutionContext context, Value question, Value suggestion, boolean hashResponse) throws HtSemanticException;
}
