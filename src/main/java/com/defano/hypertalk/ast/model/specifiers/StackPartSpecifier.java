package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.List;

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

    public PartModel find(ExecutionContext context, List<StackPart> parts) throws HtNoSuchPartException {
        if (isThisStack()) {
            return context.getCurrentStack().getStackModel();
        } else {
            String stackName = String.valueOf(getValue());

            for (StackPart thisOpenStack : parts) {
                String shortName = thisOpenStack.getStackModel().getShortName(context);
                String abbrevName = thisOpenStack.getStackModel().getAbbreviatedName(context);
                String longName = thisOpenStack.getStackModel().getLongName(context);

                if (stackName.equalsIgnoreCase(shortName) || stackName.equalsIgnoreCase(longName) || stackName.equalsIgnoreCase(abbrevName)) {
                    return thisOpenStack.getStackModel();
                }
            }
        }

        throw new HtNoSuchPartException("No such stack.");
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
            return stackName;
        }
    }
}
