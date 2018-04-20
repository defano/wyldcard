package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.window.HyperCardWindow;

public class WindowPartModel extends PartModel {

    public WindowPartModel(HyperCardWindow window) {
        super(PartType.WINDOW, Owner.HYPERCARD, null);

        defineComputedGetterProperty(PartModel.PROP_LEFT, (context, model, propertyName) -> new Value(window.getWindow().getLocation().x));
        defineComputedSetterProperty(PartModel.PROP_LEFT, (context, model, propertyName, value) -> window.getWindow().setLocation(value.integerValue(), window.getWindow().getY()));

        defineComputedGetterProperty(PartModel.PROP_TOP, (context, model, propertyName) -> new Value(window.getWindow().getLocation().y));
        defineComputedSetterProperty(PartModel.PROP_TOP, (context, model, propertyName, value) -> window.getWindow().setLocation(window.getWindow().getX(), value.integerValue()));

        defineComputedGetterProperty(PartModel.PROP_WIDTH, (context, model, propertyName) -> new Value(window.getWindow().getSize().width));
        defineComputedSetterProperty(PartModel.PROP_WIDTH, (context, model, propertyName, value) -> window.getWindow().setSize(value.integerValue(), window.getWindow().getHeight()));

        defineComputedGetterProperty(PartModel.PROP_HEIGHT, (context, model, propertyName) -> new Value(window.getWindow().getSize().height));
        defineComputedSetterProperty(PartModel.PROP_HEIGHT, (context, model, propertyName, value) -> window.getWindow().setSize(window.getWindow().getWidth(), value.integerValue()));

        defineComputedGetterProperty(PartModel.PROP_VISIBLE, (context, model, propertyName) -> new Value(window.getWindow().isVisible()));
        defineComputedSetterProperty(PartModel.PROP_VISIBLE, (context, model, propertyName, value) -> window.getWindow().setVisible(value.booleanValue()));

        defineComputedReadOnlyProperty(PartModel.PROP_ID, (context, model, propertyName) -> new Value(System.identityHashCode(window.getWindow())));
    }

    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        // Nothing to do
    }
}
