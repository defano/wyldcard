package com.defano.wyldcard.parts.button;

import com.defano.wyldcard.parts.button.styles.RadioButton;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * A mixin providing reusable functionality for enabling shared highlight features (i.e., selecting radio buttons by
 * group).
 */
public interface SharedHilight {

    default void setSharedHilite(ExecutionContext context, ButtonPart button, boolean hilite) {

        // Cannot unselect a radio button by pressing it; can only be unselected by family
        if (button.getComponent() instanceof RadioButton) {
            hilite = true;
        }

        button.getPartModel().setKnownProperty(context, ButtonModel.PROP_HILITE, new Value(hilite));

        if (isSharingHilite(context, button)) {

            for (ButtonPart thisButton : button.getCard().getButtons()) {

                if (thisButton.getId(context) == button.getId(context)) {
                    continue;
                }

                if (isSharingHilite(context, thisButton) && sharedHiliteFamily(context, thisButton) == sharedHiliteFamily(context, button)) {
                    thisButton.getPartModel().setKnownProperty(context, ButtonModel.PROP_HILITE, new Value(false));
                }
            }
        }
    }

    default boolean isSharingHilite(ExecutionContext context, ButtonPart buttonPart) {
        return buttonPart.getPartModel().getKnownProperty(context, ButtonModel.PROP_FAMILY).isInteger();
    }

    default int sharedHiliteFamily(ExecutionContext context, ButtonPart buttonPart) {
        return buttonPart.getPartModel().getKnownProperty(context, ButtonModel.PROP_FAMILY).integerValue();
    }
}
