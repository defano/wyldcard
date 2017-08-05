/*
 * ToolEditablePart
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts;

import com.defano.hypercard.context.PartToolContext;
import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.parts.button.ButtonComponent;
import com.defano.hypercard.parts.card.CardLayer;
import com.defano.hypercard.parts.card.CardLayerPart;
import com.defano.hypercard.parts.field.FieldComponent;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.Tool;
import com.defano.hypertalk.ast.common.Value;
import com.defano.jmonet.tools.util.MarchingAnts;

import java.awt.*;
import java.awt.event.*;

/**
 * An interface defining actions common to all tool-editable parts (i.e., buttons and fields that can be edited
 * using the button tool or field tool).
 */
public interface ToolEditablePart extends CardLayerPart, MouseListener, KeyListener, ActionListener {

    /**
     * Indicates whether or not the part is currently selected for being edited (i.e., user clicked the part and
     * should be highlighted with marching ants).
     *
     * @param beingEdited True if selected; false otherwise.
     */
    void setIsSelectedForEditing(boolean beingEdited);

    /**
     * Determines if the part is currently selected for editing.
     *
     * @return True if selected; false otherwise
     */
    boolean isSelectedForEditing();

    /**
     * Begin moving the part; implies that the user has clicked the part and is beginning to drag it around the
     * card.
     */
    void move();

    /**
     * Begin resizing the part; implies that the user has clicked a resize drag handle and is beginning to drag a corner
     * of the part.
     * @param fromQuadrant Indicates which corner/quadrant has been dragged. See {@link PartResizer} for constants.
     */
    void resize(int fromQuadrant);

    /**
     * Delete the part from the card; implies that the part has been selected and the user has requested it be
     * deleted (i.e. via DELETE or BACKSPACE)
     */
    void delete();

    /**
     * Show the property editor for this part; implies the user has selected and double-clicked the part, or chosen
     * the appropriate command from the Objects menu.
     */
    void editProperties();

    /**
     * Gets the Part object associated with this ToolEditablePart.
     * @return The associated Part
     */
    CardLayerPart getPart();

    /**
     * Determines the tool that is used to edit parts of this type (i.e., ButtonTool or FieldTool).
     * @return The appropriate edit tool.
     */
    Tool getEditTool();

    /**
     * Returns the size of the drag handle square to be rendered in the marching ants.
     * @return The size, in pixels, of the handle
     */
    default int getDragHandleSize() {
        return 8;
    }

    /**
     * Returns a rectangle representing the bounds of the top-left drag handle for this part.
     * @return The drag handle bounds.
     */
    default Rectangle getTopLeftDragHandle() {
        return new Rectangle(0, 0, getDragHandleSize(), getDragHandleSize());
    }

    /**
     * Returns a rectangle representing the bounds of the bottom-left drag handle for this part.
     * @return The drag handle bounds.
     */
    default Rectangle getBottomLeftDragHandle() {
        return new Rectangle(0, getComponent().getHeight() - getDragHandleSize(), getDragHandleSize(), getDragHandleSize());
    }

    /**
     * Returns a rectangle representing the bounds of the top-right drag handle for this part.
     * @return The drag handle bounds.
     */
    default Rectangle getTopRightDragHandle() {
        return new Rectangle(getComponent().getWidth() - getDragHandleSize(), 0, getDragHandleSize(), getDragHandleSize());
    }

    /**
     * Returns a rectangle representing the bounds of the bottom-right drag handle for this part.
     * @return The drag handle bounds.
     */
    default Rectangle getBottomRightDragHandle() {
        return new Rectangle(getComponent().getWidth() - getDragHandleSize(), getComponent().getHeight() - getDragHandleSize(), getDragHandleSize(), getDragHandleSize());
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

            // TODO: Add resizer support for dragging from any corner
            // g2d.fill(getTopLeftDragHandle());
            // g2d.fill(getBottomLeftDragHandle());
            // g2d.fill(getTopRightDragHandle());
            g2d.fill(getBottomRightDragHandle());

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
    }

    /**
     * Determines if this part is presently visible on the card (as determined by its "visible" property).
     * @return True if visible; false otherwise.
     */
    default boolean isHidden() {
        return !getPartModel().getKnownProperty(PartModel.PROP_VISIBLE).booleanValue();
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
     * Adjust the z-order of this part, moving it one part closer to the front of the part stack.
     */
    default void bringCloser() {
        getPart().setDisplayOrder(getZOrder() - 1);
    }

    /**
     * Adjust the z-order of this part, moving it one part further from the front of the part stack.
     */
    default void sendFurther() {
        getPart().setDisplayOrder(getZOrder() + 1);
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
        if (isSelectedForEditing()) {
            if (getTopLeftDragHandle().contains(e.getPoint())) {
                resize(PartResizer.QUADRANT_TOPLEFT);
            } else if (getTopRightDragHandle().contains(e.getPoint())) {
                resize(PartResizer.QUADRANT_TOPRIGHT);
            } else if (getBottomLeftDragHandle().contains(e.getPoint())) {
                resize(PartResizer.QUADRANT_BOTTOMLEFT);
            } else if (getBottomRightDragHandle().contains(e.getPoint())) {
                resize(PartResizer.QUADRANT_BOTTOMRIGHT);
            } else {
                move();
            }
        } else if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON && this.getComponent() instanceof ButtonComponent) {
            PartToolContext.getInstance().setSelectedPart(this);
        } else if (ToolsContext.getInstance().getToolMode() == ToolMode.FIELD && this.getComponent() instanceof FieldComponent) {
            PartToolContext.getInstance().setSelectedPart(this);
        }
    }

    @Override
    default void mouseReleased(MouseEvent e) {}

    @Override
    default void mouseEntered(MouseEvent e) {}

    @Override
    default void mouseExited(MouseEvent e) {}

    @Override
    default void mouseClicked(MouseEvent e) {
        if (isSelectedForEditing() && e.getClickCount() == 2) {
            editProperties();
        } else {
            if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON && this.getComponent() instanceof ButtonComponent) {
                PartToolContext.getInstance().setSelectedPart(this);
            } else if (ToolsContext.getInstance().getToolMode() == ToolMode.FIELD && this.getComponent() instanceof FieldComponent) {
                PartToolContext.getInstance().setSelectedPart(this);
            }
        }
    }

    @Override
    default void keyTyped(KeyEvent e) {}

    @Override
    default void keyPressed(KeyEvent e) {
        if (isSelectedForEditing()) {
            int top = getPartModel().getKnownProperty(PartModel.PROP_TOPLEFT).getItems().get(1).integerValue();
            int left = getPartModel().getKnownProperty(PartModel.PROP_TOPLEFT).getItems().get(0).integerValue();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    delete();

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

    @Override
    default void keyReleased(KeyEvent e) {}

    @Override
    default void actionPerformed(ActionEvent e) {}
}
