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
import com.defano.hypercard.parts.buttons.ButtonView;
import com.defano.hypercard.parts.fields.FieldView;
import com.defano.hypercard.parts.model.AbstractPartModel;
import com.defano.hypercard.parts.model.ButtonModel;
import com.defano.hypertalk.ast.common.Tool;
import com.defano.jmonet.tools.util.MarchingAnts;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypertalk.ast.common.Value;

import java.awt.*;
import java.awt.event.*;

public interface ToolEditablePart extends Part, KeyListener, MouseListener, ActionListener {

    void setBeingEdited(boolean beingEdited);
    boolean isBeingEdited();
    void move();
    void resize(int fromQuadrant);
    void delete();
    void editProperties();
    Part getPart();
    Tool getEditTool();

    default int getDragHandleSize() {
        return 8;
    }

    default Rectangle getTopLeftDragHandle() {
        return new Rectangle(0, 0, getDragHandleSize(), getDragHandleSize());
    }

    default Rectangle getBottomLeftDragHandle() {
        return new Rectangle(0, getComponent().getHeight() - getDragHandleSize(), getDragHandleSize(), getDragHandleSize());
    }

    default Rectangle getTopRightDragHandle() {
        return new Rectangle(getComponent().getWidth() - getDragHandleSize(), 0, getDragHandleSize(), getDragHandleSize());
    }

    default Rectangle getBottomRightDragHandle() {
        return new Rectangle(getComponent().getWidth() - getDragHandleSize(), getComponent().getHeight() - getDragHandleSize(), getDragHandleSize(), getDragHandleSize());
    }

    default void drawSelectionRectangle(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (isBeingEdited()) {
            g2d.setPaint(Color.BLACK);

            // TODO: Add resizer support for dragging from any corner
            // g2d.fill(getTopLeftDragHandle());
            // g2d.fill(getBottomLeftDragHandle());
            //  g2d.fill(getTopRightDragHandle());
            g2d.fill(getBottomRightDragHandle());

            g2d.setPaint(Color.WHITE);
            g2d.drawRect(0,0, getComponent().getWidth() - 1, getComponent().getHeight() - 1);
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(MarchingAnts.getInstance().getMarchingAnts());
            g2d.drawRect(0, 0, getComponent().getWidth() -1 , getComponent().getHeight() - 1);
        }
    }

    default void onToolModeChanged() {
        getComponent().setVisible(isVisibleOnCard() || isPartToolActive());
    }

    default boolean isVisibleOnCard() {
        return getPartModel().getKnownProperty(AbstractPartModel.PROP_VISIBLE).booleanValue();
    }

    default void setVisibleOnCard(boolean visibleOnCard) {
        getPartModel().setKnownProperty(AbstractPartModel.PROP_VISIBLE, new Value(visibleOnCard), true);
        getComponent().setVisible(visibleOnCard || isPartToolActive());
    }

    default boolean isAutoHilited() {
        return getPartModel().getKnownProperty(ButtonModel.PROP_AUTOHILIGHT).booleanValue();
    }

    default void bringCloser() {
        getPart().setZorder(getZOrder() - 1);
    }

    default void sendFurther() {
        getPart().setZorder(getZOrder() + 1);
    }

    default int getZOrder() {
        return getPartModel().getKnownProperty(AbstractPartModel.PROP_ZORDER).integerValue();
    }

    @Override
    default void mousePressed(MouseEvent e) {
        if (isBeingEdited()) {
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
        }
    }

    @Override
    default void mouseClicked(MouseEvent e) {
        if (isBeingEdited() && e.getClickCount() == 2) {
            editProperties();
        } else {
            if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON && this.getComponent() instanceof ButtonView) {
                PartToolContext.getInstance().setSelectedPart(this);
            } else if (ToolsContext.getInstance().getToolMode() == ToolMode.FIELD && this.getComponent() instanceof FieldView) {
                PartToolContext.getInstance().setSelectedPart(this);
            }
        }
    }

    @Override
    default void keyPressed(KeyEvent e) {

        if (isBeingEdited()) {
            int top = getPartModel().getKnownProperty(AbstractPartModel.PROP_TOPLEFT).getItems().get(1).integerValue();
            int left = getPartModel().getKnownProperty(AbstractPartModel.PROP_TOPLEFT).getItems().get(0).integerValue();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    delete();

                case KeyEvent.VK_LEFT:
                    getPartModel().setKnownProperty(AbstractPartModel.PROP_TOPLEFT, new Value(new Point(--left, top)));
                    break;

                case KeyEvent.VK_RIGHT:
                    getPartModel().setKnownProperty(AbstractPartModel.PROP_TOPLEFT, new Value(new Point(++left, top)));
                    break;

                case KeyEvent.VK_UP:
                    getPartModel().setKnownProperty(AbstractPartModel.PROP_TOPLEFT, new Value(new Point(left, --top)));
                    break;

                case KeyEvent.VK_DOWN:
                    getPartModel().setKnownProperty(AbstractPartModel.PROP_TOPLEFT, new Value(new Point(left, ++top)));
                    break;
            }
        }
    }

    @Override
    default void keyReleased(KeyEvent e) {}

    @Override
    default void mouseReleased(MouseEvent e) {}

    @Override
    default void mouseEntered(MouseEvent e) {}

    @Override
    default void mouseExited(MouseEvent e) {}

    @Override
    default void keyTyped(KeyEvent e) {}

    @Override
    default void actionPerformed(ActionEvent e) {}
}
