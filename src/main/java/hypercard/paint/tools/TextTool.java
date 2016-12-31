package hypercard.paint.tools;


import hypercard.paint.canvas.Canvas;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created by matt on 12/30/16.
 */
public class TextTool extends AbstractPaintTool {

    private final JTextArea textArea;

    public TextTool() {
        super(PaintToolType.TEXT);

        textArea = new JTextArea();
        textArea.setVisible(true);
        textArea.setOpaque(false);
        textArea.setBackground(new Color(0, 0, 0, 0));
        textArea.addMouseListener(this);
    }

    @Override
    public void deactivate() {
        super.deactivate();

        commitTextImage(textArea.getX(), textArea.getY());
        removeTextArea();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isEditing()) {
            addTextArea(e.getX(), e.getY());
        } else {
            commitTextImage(textArea.getX(), textArea.getY());
            removeTextArea();
        }
    }

    public boolean isEditing() {
        return textArea.getParent() == getCanvas();
    }

    protected void removeTextArea() {
        getCanvas().remove(textArea);
        getCanvas().revalidate();
        getCanvas().repaint();
    }

    protected void addTextArea(int x, int y) {
        int left = getCanvas().getInsets().left + x;
        int top = getCanvas().getInsets().top + y;

        textArea.setText("");
        textArea.setBounds(left, top, getCanvas().getWidth() - left, getCanvas().getHeight() - top);

        getCanvas().add(textArea);
        getCanvas().revalidate();
        getCanvas().repaint();

        textArea.requestFocus();
    }

    protected BufferedImage renderTextImage() {
        textArea.getCaret().setVisible(false);

        BufferedImage image = new BufferedImage(textArea.getWidth(), textArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        textArea.printAll(g);
        g.dispose();

        textArea.getCaret().setVisible(true);

        return image;
    }

    public void commitTextImage(int x, int y) {

        // Don't commit if user hasn't entered any text
        if (textArea.getText().trim().length() > 0) {
            Graphics g = getCanvas().getScratchGraphics();
            g.drawImage(renderTextImage(), x, y, null);
            g.dispose();

            getCanvas().commit();
        }
    }

}
