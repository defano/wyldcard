package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.window.WyldCardWindow;

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

    private final WyldCardWindow window;

    public WindowProxyPartModel(WyldCardWindow window) {
        super(PartType.WINDOW, Owner.HYPERCARD, null);
        this.window = window;

        defineProperty(PROP_OWNER, new Value("HyperCard"), true);
        defineProperty(PartModel.PROP_CONTENTS, new Value(), false);

        defineComputedReadOnlyProperty(PartModel.PROP_NAME, (context, model, propertyName) -> new Value(window.getTitle()));

        defineComputedGetterProperty(PROP_ZOOMED, (context, model, propertyName) -> new Value((((JFrame) window.getWindow()).getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0));
        defineComputedSetterProperty(PROP_ZOOMED, (context, model, propertyName, value) -> {
            if (window.getWindow() instanceof JFrame) {
                JFrame frame = (JFrame) window.getWindow();
                if (value.booleanValue()) {
                    frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                } else {
                    frame.setExtendedState(frame.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
                }
            }
        });

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

    public WyldCardWindow getWindow() {
        return window;
    }
}
