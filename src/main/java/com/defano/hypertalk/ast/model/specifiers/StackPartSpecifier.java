package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public class StackPartSpecifier implements PartSpecifier {

    private final String stackName;

    public StackPartSpecifier() {
        this.stackName = null;
    }

    public StackPartSpecifier(String stackName) {
        this.stackName = stackName;
    }

    public boolean isThisStack() {
        return stackName == null;
    }

    @Override
    public Object getValue() {
        return stackName;
    }

    @Override
    public Owner getOwner() {
        return Owner.HYPERCARD;
    }

    @Override
    public PartType getType() {
        return PartType.STACK;
    }

    @Override
    public String getHyperTalkIdentifier(ExecutionContext context) {
        if (stackName == null) {
            return "this stack";
        } else {
            return "stack " + stackName;
        }
    }
}
