package com.defano.wyldcard.part;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.enums.ToolType;
import com.defano.jmonet.tools.util.MarchingAnts;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.awt.keyboard.KeyListenable;
import com.defano.wyldcard.awt.mouse.MouseListenable;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.part.button.HyperCardButton;
import com.defano.wyldcard.part.card.CardDisplayLayer;
import com.defano.wyldcard.part.card.CardLayerPart;
import com.defano.wyldcard.part.card.CardLayerPartModel;
import com.defano.wyldcard.part.field.styles.HyperCardTextField;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * An object, like buttons and fields, that can be selected, moved, resized, and edited using the button field tool on
 * the tools palette.
 */
public interface ToolEditablePart<T extends PartModel> extends MouseListenable, KeyListenable, CardLayerPart<T> {

    /**
     * Indicates whether or not the part is currently selected for being edited (i.e., user clicked the part and
     * should be highlighted with marching ants).
     *
     * @param context     The execution context.
     * @param beingEdited True if selected; false otherwise.
     */
    void setSelectedForEditing(ExecutionContext context, boolean beingEdited);

    /**
     * Determines if the part is currently selected for editing.
     *
     * @return True if selected; false otherwise
     */
    boolean isSelectedForEditing();

    /**
     * Gets the Part object associated with this ToolEditablePart.
     *
     * @return The associated Part
     */
    CardLayerPart getPart();

    /**
     * Determines the tool that is used to edit parts of this type (i.e., ButtonTool or FieldTool).
     *
     * @return The appropriate edit tool.
     */
    ToolType getEditTool();

    void setComponentHierarchyEnabled(boolean enabled);

    /**
     * Returns a rectangle representing the bounds of the bottom-right drag handle for this part.
     *
     * @return The drag handle bounds.
     */
    default Rectangle getResizeDragHandle() {
        final int dragHandleSize = 10;
        return new Rectangle(getComponent().getWidth() - dragHandleSize, getComponent().getHeight() - dragHandleSize, dragHandleSize, dragHandleSize);
    }

    /**
     * Draws the selection rectangle (marching ants), plus the drag handles for this part when it's been selected
     * for editing.
     *
     * @param g The graphics context in which to draw.
     */
    @RunOnDispatch
    default void drawSelectionRectangle(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (isSelectedForEditing()) {
            // Draw marching ants
            g2d.setPaint(Color.WHITE);
            g2d.drawRect(0, 0, getComponent().getWidth() - 1, getComponent().getHeight() - 1);
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(MarchingAnts.getInstance().getMarchingAnts());
            g2d.drawRect(0, 0, getComponent().getWidth() - 1, getComponent().getHeight() - 1);

            // Draw drag handle
            g2d.setPaint(Color.BLACK);
            g2d.fill(getResizeDragHandle());
        }
    }

    /**
     * Invoke to indicate that the selected tool has been changed by the user.
     *
     * @param context The execution context.
     */
    default void onToolModeChanged(ExecutionContext context) {
        SwingUtilities.invokeLater(() -> {
            setVisibleWhenBrowsing(context, !isHidden(context));
            setEnabledOnCard(context, isEnabled(context));
        });
    }

    /**
     * Determines if this part is presently visible on the card (as determined by its "visible" property).
     *
     * @param context The execution context.
     * @return True if visible; false otherwise.
     */
    default boolean isHidden(ExecutionContext context) {
        return !getPartModel().get(context, PartModel.PROP_VISIBLE).booleanValue();
    }

    /**
     * Determines if this part is presently enabled on the card (as determined by its "enabled" property) and
     * not currently disabled as a result of the part's edit tool being active.
     *
     * @param context The execution context.
     * @return True if enabled; false if disabled.
     */
    default boolean isEnabled(ExecutionContext context) {
        return getPartModel().get(context, CardLayerPartModel.PROP_ENABLED).booleanValue();
    }

    /**
     * Sets whether this part should be visible on the card (mutating its "visible" HyperTalk property) when in browse
     * mode, taking into account that the actual visibility of the UI component may be overridden by tool context (i.e.,
     * hidden parts will be visible when the part tool is active; foreground parts visible when browsing may be hidden
     * when editing the background).
     *
     * @param context       The execution context.
     * @param visibleOnCard True to make it visible; false otherwise
     */
    @RunOnDispatch
    default void setVisibleWhenBrowsing(ExecutionContext context, boolean visibleOnCard) {
        getPartModel().setQuietly(context, PartModel.PROP_VISIBLE, new Value(visibleOnCard));

        // Force hide when part is in foreground and foreground is hidden
        boolean forceHidden = getCardLayer() == CardDisplayLayer.CARD_PARTS && getCard().isEditingBackground();

        // Force show when part tool is active and part is in the editing part layer
        boolean forceVisible = isPartToolActive() && getCardLayer() == CardLayerPart.getActivePartLayer();

        getComponent().setVisible((visibleOnCard && !forceHidden) || forceVisible);
    }

    /**
     * Sets whether this part should be enabled on the card (mutating its "enabled" HyperTalk property), but the actual
     * enable of this Swing component may be overridden by tool context (i.e., all parts will be disabled while they
     * are being edited by the part tool).
     *
     * @param context       The execution context.
     * @param enabledOnCard True to make the part enabled; false to disable.
     */
    @RunOnDispatch
    default void setEnabledOnCard(ExecutionContext context, boolean enabledOnCard) {
        getPartModel().setQuietly(context, CardLayerPartModel.PROP_ENABLED, new Value(enabledOnCard));

        // Force disabled when part tool is active
        boolean forceDisabled = isPartToolActive();
        setComponentHierarchyEnabled(enabledOnCard && !forceDisabled);
    }

    /**
     * Adjust the z-order of this part, moving it one part closer to the front of the part stack.
     *
     * @param context The execution context.
     */
    @RunOnDispatch
    default void bringCloser(ExecutionContext context) {
        getPart().setDisplayOrder(context, getZOrder(context) + 1);
    }

    /**
     * Adjust the z-order of this part, moving it one part further from the front of the part stack.
     *
     * @param context The execution context.
     */
    @RunOnDispatch
    default void sendFurther(ExecutionContext context) {
        getPart().setDisplayOrder(context, getZOrder(context) - 1);
    }

    /**
     * Determines the z-order of this part.
     *
     * @param context The execution context.
     * @return The relative front-to-back position of this part to others drawn on the card.
     */
    default int getZOrder(ExecutionContext context) {
        return getPartModel().get(context, CardLayerPartModel.PROP_ZORDER).integerValue();
    }

    @Override
    @RunOnDispatch
    default void mousePressed(MouseEvent e) {
        if (WyldCard.getInstance().getPaintManager().getToolMode() == ToolMode.BUTTON && this.getComponent() instanceof HyperCardButton) {
            WyldCard.getInstance().getPartToolManager().setSelectedPart(this);
        } else if (WyldCard.getInstance().getPaintManager().getToolMode() == ToolMode.FIELD && this.getComponent() instanceof HyperCardTextField) {
            WyldCard.getInstance().getPartToolManager().setSelectedPart(this);
        }
    }

    @Override
    @RunOnDispatch
    default void mouseClicked(MouseEvent e) {
        boolean wasDoubleClicked = isSelectedForEditing() && e.getClickCount() == 2;

        // Command-option click to edit script
        if (WyldCard.getInstance().getKeyboardManager().isPeeking(new ExecutionContext())) {
            getPartModel().editScript(new ExecutionContext());
        }

        // Double-click to edit properties
        else if (wasDoubleClicked) {
            getPartModel().editProperties(new ExecutionContext());
        }

        // Single click to select part
        else if ((WyldCard.getInstance().getPaintManager().getToolMode() == ToolMode.BUTTON && this.getComponent() instanceof HyperCardButton) ||
                (WyldCard.getInstance().getPaintManager().getToolMode() == ToolMode.FIELD && this.getComponent() instanceof HyperCardTextField)) {
            WyldCard.getInstance().getPartToolManager().setSelectedPart(this);
        }
    }

    @Override
    @RunOnDispatch
    default void keyPressed(KeyEvent e) {
        if (isSelectedForEditing()) {
            int top = getPartModel().get(new ExecutionContext(), PartModel.PROP_TOPLEFT).getItems(new ExecutionContext()).get(1).integerValue();
            int left = getPartModel().get(new ExecutionContext(), PartModel.PROP_TOPLEFT).getItems(new ExecutionContext()).get(0).integerValue();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    WyldCard.getInstance().getPartToolManager().deleteSelectedPart();
                    break;

                case KeyEvent.VK_LEFT:
                    getPartModel().set(new ExecutionContext(), PartModel.PROP_TOPLEFT, new Value(new Point(--left, top)));
                    break;

                case KeyEvent.VK_RIGHT:
                    getPartModel().set(new ExecutionContext(), PartModel.PROP_TOPLEFT, new Value(new Point(++left, top)));
                    break;

                case KeyEvent.VK_UP:
                    getPartModel().set(new ExecutionContext(), PartModel.PROP_TOPLEFT, new Value(new Point(left, --top)));
                    break;

                case KeyEvent.VK_DOWN:
                    getPartModel().set(new ExecutionContext(), PartModel.PROP_TOPLEFT, new Value(new Point(left, ++top)));
                    break;
            }
        }
    }

    default BufferedImage getPreviewImage(ExecutionContext context, int width, int height) {
        int origWidth = getPartModel().get(context, PartModel.PROP_WIDTH).integerValue();
        int origHeight = getPartModel().get(context, PartModel.PROP_HEIGHT).integerValue();
        boolean origSelected = isSelectedForEditing();

        setSelectedForEditing(context, false);
        getPartModel().set(context, PartModel.PROP_WIDTH, new Value(width));
        getPartModel().set(context, PartModel.PROP_HEIGHT, new Value(height));

        BufferedImage preview = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Invoke.onDispatch(() -> {
            Graphics2D g2d = preview.createGraphics();
            getComponent().printAll(g2d);
            g2d.dispose();
        });

        setSelectedForEditing(context, origSelected);
        getPartModel().set(context, PartModel.PROP_WIDTH, new Value(origWidth));
        getPartModel().set(context, PartModel.PROP_HEIGHT, new Value(origHeight));

        return preview;
    }

}
