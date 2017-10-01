package com.defano.hypercard.parts.msgbox;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;

public class MsgBoxModel extends PartModel {

    public MsgBoxModel() {
        super(PartType.MESSAGE_BOX, Owner.HYPERCARD);

        defineProperty(PROP_CONTENTS, new Value(), false);
        defineComputedGetterProperty(PROP_CONTENTS, (model, propertyName) -> new Value(WindowManager.getMessageWindow().getMsgBoxText()));

        defineProperty(PROP_WIDTH, new Value(WindowManager.getMessageWindow().getWindow().getWidth()), true);
        defineProperty(PROP_HEIGHT, new Value(WindowManager.getMessageWindow().getWindow().getHeight()), true);
        defineProperty(PROP_NAME, new Value("Message"), true);

        defineComputedGetterProperty(PartModel.PROP_LEFT, (model, propertyName) -> new Value(WindowManager.getMessageWindow().getWindow().getLocation().x));
        defineComputedSetterProperty(PartModel.PROP_LEFT, (model, propertyName, value) -> WindowManager.getMessageWindow().getWindow().setLocation(value.integerValue(), WindowManager.getMessageWindow().getWindow().getY()));

        defineComputedGetterProperty(PartModel.PROP_TOP, (model, propertyName) -> new Value(WindowManager.getMessageWindow().getWindow().getLocation().y));
        defineComputedSetterProperty(PartModel.PROP_TOP, (model, propertyName, value) -> WindowManager.getMessageWindow().getWindow().setLocation(WindowManager.getMessageWindow().getWindow().getX(), value.integerValue()));

        defineComputedGetterProperty(PROP_VISIBLE, (model, propertyName) -> new Value(WindowManager.getMessageWindow().isVisible()));
        defineComputedSetterProperty(PROP_VISIBLE, (model, propertyName, value) -> WindowManager.getMessageWindow().setVisible(value.booleanValue()));
    }
}
