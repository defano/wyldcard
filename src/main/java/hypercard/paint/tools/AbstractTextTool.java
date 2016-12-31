package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTextTool extends AbstractPaintTool implements KeyListener {

    private List<String> textBuffer = new ArrayList<>();
    private int cursorIndex = 0;
    private int lineIndex = 0;
    private Point insertionLocation = null;

    public AbstractTextTool(PaintToolType type) {
        super(type);
    }

    @Override
    public void mousePressed(MouseEvent e) {

        // Start adding text
        if (!isEditingText()) {
            startAddingText(e.getPoint());
        }

        // Commit added text
        else {
            finishAddingText();
        }
    }

    @Override
    public void activate (Canvas canvas) {
        super.activate(canvas);
        getCanvas().addKeyListener(this);
    }

    @Override
    public void deactivate() {
        getCanvas().removeKeyListener(this);
        super.deactivate();
    }

    private void drawEditingText(boolean showCursor) {

        getCanvas().clearScratch();
        Graphics2D g2d = (Graphics2D) getCanvas().getScratchGraphics();

        for (int thisLine = 0; thisLine < textBuffer.size(); thisLine++) {

            StringBuilder textToDraw = new StringBuilder(textBuffer.get(thisLine));
            if (showCursor && thisLine == lineIndex) {
                textToDraw.insert(cursorIndex, '|');
            }

            g2d.setColor(Color.BLACK);
            drawText(g2d, textToDraw.toString(), insertionLocation.x, insertionLocation.y + (thisLine * g2d.getFontMetrics().getHeight()));
        }

        g2d.dispose();

        getCanvas().repaintCanvas();
    }

    protected void finishAddingText() {
        drawEditingText(false);
        getCanvas().commit();

        insertionLocation = null;
    }

    protected void startAddingText(Point atLocation) {
        insertionLocation = atLocation;
        cursorIndex = 0;
        textBuffer.clear();
        textBuffer.add("");

        drawEditingText(true);
    }

    protected boolean isEditingText() {
        return insertionLocation != null;
    }

    protected String getEditingLine() {
        return textBuffer.get(lineIndex);
    }

    protected void insertIntoEditingLine(char c) {
        textBuffer.set(lineIndex, new StringBuilder(textBuffer.get(lineIndex)).insert(cursorIndex, c).toString());
        cursorIndex++;

        drawEditingText(true);
    }

    protected void backspaceEditingLine() {
        if (cursorIndex > 0) {
            cursorIndex--;
            textBuffer.set(lineIndex, new StringBuilder(textBuffer.get(lineIndex)).deleteCharAt(cursorIndex).toString());

            drawEditingText(true);
        }
    }

    protected String newEditingLine() {
        textBuffer.add("");
        cursorIndex = 0;
        lineIndex++;

        return textBuffer.get(lineIndex);
    }

    public abstract void drawText(Graphics g, String text, int x, int y);

    @Override
    public void keyTyped(KeyEvent e) {
        if (isEditingText() && e.getKeyChar() != KeyEvent.VK_BACK_SPACE && e.getKeyChar() != KeyEvent.VK_ENTER) {
            insertIntoEditingLine(e.getKeyChar());
            drawEditingText(true);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {

            // Left arrow
            case KeyEvent.VK_LEFT:
                if (cursorIndex > 0) {
                    cursorIndex--;
                    drawEditingText(true);
                }
                break;

            // Right arrow
            case KeyEvent.VK_RIGHT:
                if (cursorIndex < getEditingLine().length()) {
                    cursorIndex++;
                    drawEditingText(true);
                }
                break;

            case KeyEvent.VK_UP:
                if (lineIndex > 0) {
                    lineIndex--;
                    if (cursorIndex > getEditingLine().length()) {
                        cursorIndex = getEditingLine().length();
                    }
                    drawEditingText(true);
                }
                break;

            case KeyEvent.VK_DOWN:
                if (lineIndex < textBuffer.size()) {
                    lineIndex++;
                    if (cursorIndex > getEditingLine().length()) {
                        cursorIndex = getEditingLine().length();
                    }
                    drawEditingText(true);
                }
                break;

            case KeyEvent.VK_BACK_SPACE:
                backspaceEditingLine();
                break;

            // Return key
            case KeyEvent.VK_ENTER:
                newEditingLine();
                drawEditingText(true);
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
