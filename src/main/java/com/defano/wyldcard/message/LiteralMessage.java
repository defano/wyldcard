package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.compiler.CompilationUnit;
import com.defano.wyldcard.runtime.compiler.ScriptCompiler;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class LiteralMessage implements Message {

    private final String text;
    private final String messageNameText;
    private final String messageArgumentsText;

    public LiteralMessage(String messageText) {
        this.text = messageText;
        this.messageNameText = messageText.trim().split("\\s+")[0];
        this.messageArgumentsText = messageText.substring(text.indexOf(messageNameText) + messageNameText.length()).trim();
    }

    @Override
    public String getMessageName() {
        return messageNameText;
    }

    @Override
    public List<Value> getArguments(ExecutionContext context) {
        try {
            ListExp listExp = (ListExp) ScriptCompiler.blockingCompile(CompilationUnit.LIST_EXPRESSION, messageArgumentsText);
            return listExp == null ? new ArrayList<>() : listExp.evaluateAsList(context);
        } catch (HtException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public String toMessageString(ExecutionContext context) {
        return text;
    }
}
