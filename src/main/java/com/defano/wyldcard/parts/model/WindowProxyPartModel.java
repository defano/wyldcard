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

        define(PROP_OWNER).asConstant("HyperCard");
        define(PROP_CONTENTS).asValue();

        define(PROP_NAME).asComputedReadOnlyValue((context, model) -> new Value(window.getTitle()));
        define(PROP_NUMBER).asComputedReadOnlyValue((context, model) -> getWindow().getNumberOfWindow());

        define(PROP_ZOOMED).asComputedValue()
                .withSetter((context, model, value) -> {
                    if (window.getWindow() instanceof JFrame) {
                        JFrame frame = (JFrame) window.getWindow();
                        if (value.booleanValue()) {
                            frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                        } else {
                            frame.setExtendedState(frame.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
                        }
                    }
                })
                .withGetter((context, model) -> new Value((((JFrame) window.getWindow()).getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0));

        define(PROP_LEFT).asComputedValue()
                .withSetter((context, model, value) -> window.getWindow().setLocation(value.integerValue(), window.getWindow().getY()))
                .withGetter((context, model) -> new Value(window.getWindow().getLocation().x));

        define(PROP_TOP).asComputedValue()
                .withSetter((context, model, value) -> window.getWindow().setLocation(window.getWindow().getX(), value.integerValue()))
                .withGetter((context, model) -> new Value(window.getWindow().getLocation().y));

        define(PROP_WIDTH).asComputedValue()
                .withSetter((context, model, value) -> window.getWindow().setSize(value.integerValue(), window.getWindow().getHeight()))
                .withGetter((context, model) -> new Value(window.getWindow().getSize().width));

        define(PROP_HEIGHT).asComputedValue()
                .withSetter((context, model, value) -> window.getWindow().setSize(window.getWindow().getWidth(), value.integerValue()))
                .withGetter((context, model) -> new Value(window.getWindow().getSize().height));

        define(PROP_VISIBLE).asComputedValue()
                .withSetter((context, model, value) -> window.getWindow().setVisible(value.booleanValue()))
                .withGetter((context, model) -> new Value(window.getWindow().isVisible()));

        define(PROP_ID).asComputedReadOnlyValue((context, model) -> new Value(System.identityHashCode(window.getWindow())));
    }

    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        // Nothing to do
    }

    public WyldCardFrame getWindow() {
        return window;
    }
}
