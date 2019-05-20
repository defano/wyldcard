package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.expression.ListExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.compiler.CompilationUnit;
import com.defano.wyldcard.runtime.compiler.ScriptCompiler;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a HyperTalk message in String-literal form (i.e., 'doMenu "New Button"').
 * <p>
 * Before execution, the string must be parsed into message name and arguments, and arguments must be evaluated.
 * For example, if the text contains 'doSomething (cd fld 1), x` the value of 'cd fld 1' and 'x' will be evaluated
 * when {@link Message#evaluateArguments(ExecutionContext)} is called using the provided execution context.
 */
public class LiteralMessage implements Message {

    private final String text;
    private final String messageNameText;
    private final String messageArgumentsText;

    public LiteralMessage(String messageText) {
        this.text = messageText;
        this.messageNameText = messageText.trim().split("\\s+")[0];
        this.messageArgumentsText = messageText.substring(text.indexOf(messageNameText) + messageNameText.length()).trim();
    }

    public String getMessageLiteral() {
        return text;
    }

    @Override
    public String getMessageName() {
        return messageNameText;
    }

    @Override
    public List<Value> evaluateArguments(ExecutionContext context) {
        try {
            ListExp listExp = (ListExp) ScriptCompiler.blockingCompile(CompilationUnit.LIST_EXPRESSION, messageArgumentsText);
            return listExp == null ? new ArrayList<>() : listExp.evaluateAsList(context);
        } catch (HtException e) {
            return Lists.newArrayList(new Value(messageArgumentsText));
        }
    }
}