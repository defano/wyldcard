package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.window.WyldCardFrame;

import javax.swing.*;

/**
 * A "proxy" part model that enables HyperTalk to address windows as first-class objects (parts) in the language.
 * <p>
 * Unlike "real" parts, instances of this model are ephemeral and generally violate MVC by acting as the model AND the
 * controller object.
 *
 * These models are never persisted with the {@link com.defano.wyldcard.parts.stack.StackModel}, they hold no state or
 * data themselves (they merely delegate to their "view"), and are instantiated each time HyperTalk requests to address
 * a window (that is, there is a not a one-to-one relationship between a window and it's part model--many of these
 * objects could be bound to the same window at any one time).
 */
public class WindowProxyPartModel extends PartModel {

    public static final String PROP_OWNER = "owner";
    public static final String PROP_ZOOMED = "zoomed";

    private final WyldCardFrame window;

    public WindowProxyPartModel(WyldCardFrame window) {
        super(PartType.WINDOW, Owner.HYPERCARD, null);
        this.window = window;

        newProperty(PROP_OWNER, new Value("HyperCard"), true);
        newProperty(PartModel.PROP_CONTENTS, new Value(), false);

        newComputedReadOnlyProperty(PartModel.PROP_NAME, (context, model, propertyName) -> new Value(window.getTitle()));

        newComputedGetterProperty(PROP_ZOOMED, (context, model, propertyName) -> new Value((((JFrame) window.getWindow()).getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0));
        newComputedSetterProperty(PROP_ZOOMED, (context, model, propertyName, value) -> {
            if (window.getWindow() instanceof JFrame) {
                JFrame frame = (JFrame) window.getWindow();
                if (value.booleanValue()) {
                    frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                } else {
                    frame.setExtendedState(frame.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
                }
            }
        });

        newComputedGetterProperty(PartModel.PROP_LEFT, (context, model, propertyName) -> new Value(window.getWindow().getLocation().x));
        newComputedSetterProperty(PartModel.PROP_LEFT, (context, model, propertyName, value) -> window.getWindow().setLocation(value.integerValue(), window.getWindow().getY()));

        newComputedGetterProperty(PartModel.PROP_TOP, (context, model, propertyName) -> new Value(window.getWindow().getLocation().y));
        newComputedSetterProperty(PartModel.PROP_TOP, (context, model, propertyName, value) -> window.getWindow().setLocation(window.getWindow().getX(), value.integerValue()));

        newComputedGetterProperty(PartModel.PROP_WIDTH, (context, model, propertyName) -> new Value(window.getWindow().getSize().width));
        newComputedSetterProperty(PartModel.PROP_WIDTH, (context, model, propertyName, value) -> window.getWindow().setSize(value.integerValue(), window.getWindow().getHeight()));

        newComputedGetterProperty(PartModel.PROP_HEIGHT, (context, model, propertyName) -> new Value(window.getWindow().getSize().height));
        newComputedSetterProperty(PartModel.PROP_HEIGHT, (context, model, propertyName, value) -> window.getWindow().setSize(window.getWindow().getWidth(), value.integerValue()));

        newComputedGetterProperty(PartModel.PROP_VISIBLE, (context, model, propertyName) -> new Value(window.getWindow().isVisible()));
        newComputedSetterProperty(PartModel.PROP_VISIBLE, (context, model, propertyName, value) -> window.getWindow().setVisible(value.booleanValue()));

        newComputedReadOnlyProperty(PartModel.PROP_ID, (context, model, propertyName) -> new Value(System.identityHashCode(window.getWindow())));
    }

    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        // Nothing to do
    }

    public WyldCardFrame getWindow() {
        return window;
    }
}
