package com.defano.wyldcard.parts.field;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.Styleable;
import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.field.styles.*;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.DefaultFontManager;
import com.defano.jmonet.tools.util.MarchingAnts;
import com.defano.jmonet.tools.util.MarchingAntsObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import javax.swing.*;
import java.awt.*;

/**
 * The "view" object representing a styleable HyperCard field.
 * <p>
 * Note that we cannot simply extend a Swing component because the underlying component bound to this view can change at
 * runtime (i.e., a transparent field can morph into a rectangular one).
 * <p>
 * This class provides common functionality for "styleable" field parts; the actual style of the field is provided by
 * a concrete subclass.
 */
public abstract class StyleableField implements Styleable<FieldStyle,HyperCardTextField>, ToolEditablePart, MarchingAntsObserver {

    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private Disposable toolModeSubscription;
    private HyperCardTextField fieldComponent;
    private boolean isBeingEdited;

    public StyleableField(FieldStyle style) {
        fieldComponent = getComponentForStyle(style);
    }

    @Override
    public boolean isSelectedForEditing() {
        Window ancestorWindow = SwingUtilities.getWindowAncestor(getFieldComponent());
        return ancestorWindow != null && ancestorWindow.isActive() && isBeingEdited;
    }

    @Override
    public void setSelectedForEditing(ExecutionContext context, boolean beingEdited) {
        isBeingEdited = beingEdited;

        if (isSelectedForEditing()) {
            MarchingAnts.getInstance().addObserver(this);

            // TODO: Focus style only reflects first char; should reflect entire field
            WyldCard.getInstance().getFontManager().setFocusedTextStyle((((CardLayerPartModel) getPartModel()).getTextStyle(context)));
        } else {
            MarchingAnts.getInstance().removeObserver(this);
        }

        getComponent().repaint();
    }

    @Override
    public void setStyle(ExecutionContext context, FieldStyle style) {
        Component oldComponent = getFieldComponent();
        fieldComponent = getComponentForStyle(style);
        replaceViewComponent(context, oldComponent, fieldComponent);
    }

    @Override
    public HyperCardTextField getComponentForStyle(FieldStyle style) {
        switch (style) {
            case TRANSPARENT:
                return new TransparentField(this);
            case OPAQUE:
                return new OpaqueField(this);
            case SHADOW:
                return new ShadowField(this);
            case RECTANGLE:
                return new RectangleField(this);

            default:
                throw new IllegalArgumentException("No such field style: " + style);
        }
    }

    /**
     * Gets the Swing component representing the field as a whole; this is typically a JTextComponent plus some other
     * hierarchy (like a scroll pane).
     *
     * @return
     */
    public JComponent getFieldComponent() {
        return (JComponent) fieldComponent;
    }

    public HyperCardTextPane getHyperCardTextPane() {
        return fieldComponent.getTextPane();
    }

    @Override
    public void setComponentHierarchyEnabled(boolean enabled) {
        getComponent().setEnabled(enabled);
        getHyperCardTextPane().setEnabled(enabled);
    }

    @Override
    public void partOpened(ExecutionContext context) {
        fieldComponent.partOpened(context);

        getPartModel().addPropertyChangedObserver(fieldComponent);
        toolModeSubscription = WyldCard.getInstance().getToolsManager().getToolModeProvider().subscribe(toolModeObserver);
        WyldCard.getInstance().getKeyboardManager().addGlobalKeyListener(this);
    }

    @Override
    public void partClosed(ExecutionContext context) {
        fieldComponent.partClosed(context);

        getPartModel().removePropertyChangedObserver(fieldComponent);
        WyldCard.getInstance().getKeyboardManager().removeGlobalKeyListener(this);
        toolModeSubscription.dispose();
    }

    @Override
    public void onAntsMoved(Stroke ants) {
        SwingUtilities.invokeLater(getFieldComponent()::repaint);
    }

    private class ToolModeObserver implements Consumer<ToolMode> {
        @Override
        public void accept(ToolMode toolMode) {
            onToolModeChanged(new ExecutionContext());
        }
    }
}
