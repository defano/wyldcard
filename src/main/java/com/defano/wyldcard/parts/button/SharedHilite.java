package com.defano.wyldcard.parts.button;

import com.defano.wyldcard.parts.button.styles.RadioButton;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * A mixin providing reusable functionality for enabling shared highlight features (i.e., selecting radio buttons by
 * group).
 */
public interface SharedHilite {

    int MIN_FAMILY = 1;
    int MAX_FAMILY = 7;

    default void setSharedHilite(ExecutionContext context, ButtonPart button, boolean hilite) {

        // Cannot unselect a radio button by pressing it; can only be unselected by family
        if (button.getComponent() instanceof RadioButton) {
            hilite = true;
        }

        button.getPartModel().set(context, ButtonModel.PROP_HILITE, new Value(hilite));

        if (isSharingHilite(context, button)) {

            for (ButtonPart thisButton : button.getCard().getButtons()) {
                if (thisButton.getId(context) == button.getId(context)) {
                    continue;
                }

                if (isSharingHilite(context, thisButton) && getSharedHiliteFamily(context, thisButton) == getSharedHiliteFamily(context, button)) {
                    thisButton.getPartModel().set(context, ButtonModel.PROP_HILITE, new Value(false));
                }
            }
        }
    }

    default boolean isSharingHilite(ExecutionContext context, ButtonPart buttonPart) {
        int family = buttonPart.getPartModel().get(context, ButtonModel.PROP_FAMILY).integerValue();
        return family >= MIN_FAMILY && family <= MAX_FAMILY;
    }

    default int getSharedHiliteFamily(ExecutionContext context, ButtonPart buttonPart) {
        return buttonPart.getPartModel().get(context, ButtonModel.PROP_FAMILY).integerValue();
    }
}
