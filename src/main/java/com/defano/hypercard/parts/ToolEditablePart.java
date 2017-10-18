package com.defano.hypercard.parts;

import com.defano.hypercard.runtime.context.PartToolContext;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.awt.KeyListenable;
import com.defano.hypercard.awt.MouseListenable;
import com.defano.hypercard.parts.button.ButtonComponent;
import com.defano.hypercard.parts.card.CardLayer;
import com.defano.hypercard.parts.card.CardLayerPart;
import com.defano.hypercard.parts.field.FieldComponent;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.ToolType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.jmonet.tools.util.MarchingAnts;

import java.awt.*;
import java.awt.event.*;

/**
 * An interface defining actions common to all tool-editable parts (i.e., buttons and fields that can be edited
 * using the button tool or field tool).
 */
public interface ToolEditablePart extends MouseListenable, KeyListenable, CardLayerPart {

    /**
     * Indicates whether or not the part is currently selected for being edited (i.e., user clicked the part and
     * should be highlighted with marching ants).
     *
     * @param beingEdited True if selected; false otherwise.
     */
    void setSelectedForEditing(boolean beingEdited);

    /**
     * Determines if the part is currently selected for editing.
     *
     * @return True if selected; false otherwise
     */
    boolean isSelectedForEditing();

    /**
     * Show the property editor for this part; implies the user has selected and double-clicked the part, or chosen
     * the appropriate command from the Objects menu.
     */
    void editProperties();

    /**
     * Show the script editor for this part; implies the user has selected and double-control-clicked the part.
     */
    void editScript();

    /**
     * Gets the Part object associated with this ToolEditablePart.
     * @return The associated Part
     */
    CardLayerPart getPart();

    /**
     * Determines the tool that is used to edit parts of this type (i.e., ButtonTool or FieldTool).
     * @return The appropriate edit tool.
     */
    ToolType getEditTool();

    void setEnabledRecursively(boolean enabled);

    /**
     * Returns a {@link PartSpecifier} identifying this part in the stack.
     * @return A PartSpecifier identifying this part.
     */
    default PartSpecifier getPartSpecifier() {
        return new PartIdSpecifier(getCardLayer().asOwner(), getType(), getId());
    }

    /**
     * Returns the size of the drag handle square to be rendered in the marching ants.
     * @return The size, in pixels, of the handle
     */
    default int getResizeDragHandleSize() {
        return 8;
    }

    /**
     * Returns a rectangle representing the bounds of the bottom-right drag handle for this part.
     * @return The drag handle bounds.
     */
    default Rectangle getResizeDragHandle() {
        return new Rectangle(getComponent().getWidth() - getResizeDragHandleSize(), getComponent().getHeight() - getResizeDragHandleSize(), getResizeDragHandleSize(), getResizeDragHandleSize());
    }

    /**
     * Draws the selection rectangle (marching ants), plus the drag handles for this part when it's been selected
     * for editing.
     *
     * @param g The graphics context in which to draw.
     */
    default void drawSelectionRectangle(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (isSelectedForEditing()) {
            g2d.setPaint(Color.BLACK);

            g2d.fill(getResizeDragHandle());

            g2d.setPaint(Color.WHITE);
            g2d.drawRect(0,0, getComponent().getWidth() - 1, getComponent().getHeight() - 1);
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(MarchingAnts.getInstance().getMarchingAnts());
            g2d.drawRect(0, 0, getComponent().getWidth() -1 , getComponent().getHeight() - 1);
        }
    }

    /**
     * Invoke to indicate that the selected tool has been changed by the user.
     */
    default void onToolModeChanged() {
        setVisibleOnCard(!isHidden());
        setEnabledOnCard(isEnabled());
    }

    /**
     * Determines if this part is presently visible on the card (as determined by its "visible" property).
     * @return True if visible; false otherwise.
     */
    default boolean isHidden() {
        return !getPartModel().getKnownProperty(PartModel.PROP_VISIBLE).booleanValue();
    }

    /**
     * Determines if this part is presently enabled on the card (as determined by its "enabled" property).
     * @return True if enabled; false if disabled.
     */
    default boolean isEnabled() {
        return getPartModel().getKnownProperty(CardLayerPartModel.PROP_ENABLED).booleanValue();
    }

    /**
     * Sets whether this part should be visible on the card (mutating its "visible" HyperTalk property), but the actual
     * visibility of the Swing component may be overridden by tool context (i.e., hidden parts will be visible when
     * the part tool is active; visible parts in the foreground may be hidden when editing the background).
     *
     * @param visibleOnCard True to make it visible; false otherwise
     */
    default void setVisibleOnCard(boolean visibleOnCard) {
        getPartModel().setKnownProperty(PartModel.PROP_VISIBLE, new Value(visibleOnCard), true);

        // Force hide when part is in foreground and foreground is hidden
        boolean forceHidden = getCardLayer() == CardLayer.CARD_PARTS && !getCard().isForegroundVisible();

        // Force show when part tool is active and part is in the editing part layer
        boolean forceVisible = isPartToolActive() && getCardLayer() == CardLayerPart.getActivePartLayer();

        getComponent().setVisible((visibleOnCard && !forceHidden) || forceVisible);
    }

    /**
     * Sets whether this part should be enabled on the card (mutating its "enabled" HyperTalk property), but the actual
     * enable of this Swing component may be overridden by tool context (i.e., all parts will be disabled while they
     * are being edited by the part tool).
     *
     * @param enabledOnCard True to make the part enabled; false to disable.
     */
    default void setEnabledOnCard(boolean enabledOnCard) {
        getPartModel().setKnownProperty(CardLayerPartModel.PROP_ENABLED, new Value(enabledOnCard), true);

        // Force disabled when part tool is active
        boolean forceDisabled = isPartToolActive();
        setEnabledRecursively(enabledOnCard && !forceDisabled);
    }

    /**
     * Adjust the z-order of this part, moving it one part closer to the front of the part stack.
     */
    default void bringCloser() {
        getPart().setDisplayOrder(getZOrder() + 1);
    }

    /**
     * Adjust the z-order of this part, moving it one part further from the front of the part stack.
     */
    default void sendFurther() {
        getPart().setDisplayOrder(getZOrder() - 1);
    }

    /**
     * Determines the z-order of this part.
     * @return The relative front-to-back position of this part to others drawn on the card.
     */
    default int getZOrder() {
        return getPartModel().getKnownProperty(CardLayerPartModel.PROP_ZORDER).integerValue();
    }

    @Override
    default void mousePressed(MouseEvent e) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON && this.getComponent() instanceof ButtonComponent) {
            PartToolContext.getInstance().setSelectedPart(this);
        } else if (ToolsContext.getInstance().getToolMode() == ToolMode.FIELD && this.getComponent() instanceof FieldComponent) {
            PartToolContext.getInstance().setSelectedPart(this);
        }
    }

    @Override
    default void mouseClicked(MouseEvent e) {

        // Double-command click to edit script
        if (isSelectedForEditing() && e.getClickCount() == 2 && (e.isControlDown() || e.isMetaDown())) {
            editScript();
        }

        // Double-click to edit properties
        else if (isSelectedForEditing() && e.getClickCount() == 2) {
            editProperties();
        }

        // Single click to select part
        else {
            if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON && this.getComponent() instanceof ButtonComponent) {
                PartToolContext.getInstance().setSelectedPart(this);
            } else if (ToolsContext.getInstance().getToolMode() == ToolMode.FIELD && this.getComponent() instanceof FieldComponent) {
                PartToolContext.getInstance().setSelectedPart(this);
            }
        }
    }

    @Override
    default void keyPressed(KeyEvent e) {
        if (isSelectedForEditing()) {
            int top = getPartModel().getKnownProperty(PartModel.PROP_TOPLEFT).getItems().get(1).integerValue();
            int left = getPartModel().getKnownProperty(PartModel.PROP_TOPLEFT).getItems().get(0).integerValue();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    PartToolContext.getInstance().deleteSelectedPart();
                    break;

                case KeyEvent.VK_LEFT:
                    getPartModel().setKnownProperty(PartModel.PROP_TOPLEFT, new Value(new Point(--left, top)));
                    break;

                case KeyEvent.VK_RIGHT:
                    getPartModel().setKnownProperty(PartModel.PROP_TOPLEFT, new Value(new Point(++left, top)));
                    break;

                case KeyEvent.VK_UP:
                    getPartModel().setKnownProperty(PartModel.PROP_TOPLEFT, new Value(new Point(left, --top)));
                    break;

                case KeyEvent.VK_DOWN:
                    getPartModel().setKnownProperty(PartModel.PROP_TOPLEFT, new Value(new Point(left, ++top)));
                    break;
            }
        }
    }

}
