package com.defano.wyldcard.window;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface DialogManager {

    Value answer(ExecutionContext context, Value msg, Value choice1, Value choice2, Value choice3);
    Value answerFile(ExecutionContext context, Value promptString, Value fileFilter);
}
