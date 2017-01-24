package hypercard.parts;

import hypercard.parts.model.ButtonModel;
import hypertalk.ast.common.Value;

public class SharedHiliteDelegate {

    public static void changeHilite(ButtonPart buttonPart, boolean hilite) {

        Value hiliteFamily = buttonPart.getPartModel().getKnownProperty(ButtonModel.PROP_FAMILY);
        buttonPart.getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(hilite));

        if (hiliteFamily.isInteger()) {
            for (ButtonPart thisButton : buttonPart.getCard().getButtons().getParts()) {
                if (thisButton.getId() == buttonPart.getId()) {
                    continue;
                }

                Value thisButtonFamily = thisButton.getPartModel().getKnownProperty(ButtonModel.PROP_FAMILY);
                if (thisButtonFamily.isInteger() && thisButtonFamily.integerValue() == hiliteFamily.integerValue()) {
                    thisButton.getPartModel().setKnownProperty(ButtonModel.PROP_HILITE, new Value(false));
                }
            }
        }
    }
}
