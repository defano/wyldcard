package hypercard.paint.tools;


import hypercard.paint.canvas.Canvas;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.base.AbstractPaintTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

public class TextTool extends AbstractPaintTool implements Observer {

    private final JTextArea textArea;
    private Point textLocation;

    public TextTool() {
        super(PaintToolType.TEXT);
        setToolCursor(new Cursor(Cursor.TEXT_CURSOR));

        textArea = new JTextArea();
        textArea.setVisible(true);
        textArea.setOpaque(false);
        textArea.setBackground(new Color(0, 0, 0, 0));
    }

    @Override
    public void deactivate() {
        super.deactivate();

        commitTextImage();
        removeTextArea();
        getFontProvider().deleteObserver(this);
    }

    @Override
    public void activate(Canvas canvas) {
        super.activate(canvas);
        getFontProvider().addObserver(this);
    }

    @Override
    public void mousePressed(MouseEvent e, int scaleX, int scaleY) {
        if (!isEditing()) {
            textLocation = new Point(scaleX, scaleY - getScaledFontAscent());
            addTextArea(e.getX(), e.getY() - getFontAscent());
        } else {
            commitTextImage();
            removeTextArea();
        }
    }

    public boolean isEditing() {
        return textArea.getParent() == getCanvas();
    }

    protected void removeTextArea() {
        getCanvas().removeComponent(textArea);
    }

    protected void addTextArea(int x, int y) {
        int left = getCanvas().getBounds().x + x;
        int top = getCanvas().getBounds().y + y;

        textArea.setText("");
        textArea.setBounds(left, top, getCanvas().getWidth() - left, getCanvas().getHeight() - top);
        textArea.setFont(getScaledFont());
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TextTool.this.mousePressed(e, 0, 0);
            }
        });

        getCanvas().addComponent(textArea);

        textArea.requestFocus();
    }

    private BufferedImage renderTextImage() {

        // Clear selection before rasterizing
        textArea.setSelectionStart(0);
        textArea.setSelectionEnd(0);

        textArea.getCaret().setVisible(false);
        textArea.setFont(getFont());

        BufferedImage image = new BufferedImage(textArea.getWidth(), textArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        textArea.printAll(g);
        g.dispose();

        textArea.getCaret().setVisible(true);

        return image;
    }

    private void commitTextImage() {

        // Don't commit if user hasn't entered any text
        if (textArea.getText().trim().length() > 0) {
            Graphics g = getCanvas().getScratchImage().getGraphics();
            g.drawImage(renderTextImage(), textLocation.x, textLocation.y, null);
            g.dispose();

            getCanvas().commit();
        }
    }

    @Override
    public void update(Observable o, Object newValue) {
        if (newValue instanceof Font) {
            textArea.setFont((Font) newValue);
        }
    }

    private Font getScaledFont() {
        return new Font(getFont().getFamily(), getFont().getStyle(), (int) (getFont().getSize() * getCanvas().getScaleProvider().get()));
    }

    private int getScaledFontAscent() {
        return (int) (getFontAscent() / getCanvas().getScaleProvider().get());
    }

    private int getFontAscent() {
        Graphics g = getCanvas().getScratchImage().getGraphics();
        FontMetrics metrics = g.getFontMetrics(getFont());
        g.dispose();

        return metrics.getAscent();
    }
}
