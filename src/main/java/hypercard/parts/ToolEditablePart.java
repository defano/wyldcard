package hypercard.parts;

import com.defano.jmonet.tools.util.MarchingAnts;
import hypercard.context.ButtonToolContext;
import hypercard.parts.model.AbstractPartModel;
import hypertalk.ast.common.Value;

import java.awt.*;
import java.awt.event.*;

public interface ToolEditablePart extends KeyListener, MouseListener, ActionListener {

    void addTextChangeObserver(PartTextChangeObserver observer);
    void setBeingEdited(boolean beingEdited);
    boolean isBeingEdited();
    void move();
    void resize(int fromQuadrant);
    void delete();
    String getName();
    void editProperties();
    AbstractPartModel getModel();
    Component getComponent();

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

            g2d.setStroke(MarchingAnts.getInstance().getMarchingAnts());
            g2d.drawRect(0, 0, getComponent().getWidth(), getComponent().getHeight());
        }
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
    default void actionPerformed(ActionEvent e) {}

    @Override
    default void mouseClicked(MouseEvent e) {
        if (isBeingEdited() && e.getClickCount() == 2) {
            editProperties();
        } else {
            ButtonToolContext.getInstance().setSelectedButton(this);
        }
    }

    @Override
    default void keyPressed(KeyEvent e) {

        if (isBeingEdited()) {
            int top = getModel().getKnownProperty(AbstractPartModel.PROP_TOPLEFT).listValue().get(1).integerValue();
            int left = getModel().getKnownProperty(AbstractPartModel.PROP_TOPLEFT).listValue().get(0).integerValue();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_DELETE:
                case KeyEvent.VK_BACK_SPACE:
                    delete();

                case KeyEvent.VK_LEFT:
                    getModel().setKnownProperty(AbstractPartModel.PROP_TOPLEFT, new Value(new Point(--left, top)));
                    break;

                case KeyEvent.VK_RIGHT:
                    getModel().setKnownProperty(AbstractPartModel.PROP_TOPLEFT, new Value(new Point(++left, top)));
                    break;

                case KeyEvent.VK_UP:
                    getModel().setKnownProperty(AbstractPartModel.PROP_TOPLEFT, new Value(new Point(left, --top)));
                    break;

                case KeyEvent.VK_DOWN:
                    getModel().setKnownProperty(AbstractPartModel.PROP_TOPLEFT, new Value(new Point(left, ++top)));
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
}
