package com.defano.hypercard.parts.fields.styles;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;
import java.awt.*;

/**
 * An extension to {@link JTextPane} that adds the ability to disable auto-wrapping of text and to draw dotted lines
 * underneath each line of text.
 */
public class HyperCardJTextPane extends JTextPane {

    private boolean wrapText = true;
    private boolean showLines = false;

    public HyperCardJTextPane(StyledDocument doc) {
        super(doc);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        Component parent = getParent();
        ComponentUI ui = getUI();

        if (wrapText) {
            return super.getScrollableTracksViewportWidth();
        } else {
            return parent == null || (ui.getPreferredSize(this).width <= parent.getSize().width);
        }
    }

    public boolean isWrapText() {
        return wrapText;
    }

    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;

        // Cause the field to re-wrap the text inside of it
        this.setSize(this.getWidth() - 1, this.getHeight());
        this.setSize(this.getWidth() + 1, this.getHeight());
    }

    public boolean isShowLines() {
        return showLines;
    }

    public void setShowLines(boolean showLines) {
        this.showLines = showLines;
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (showLines) {
            int lastLineHeight = getHeightForAttributes(g, getCharacterAttributes());
            int dottedLineY = 0;
            int lineCount = getWrappedLineCount();

            Stroke dottedLine = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{1}, 0);
            ((Graphics2D) g).setStroke(dottedLine);

            // Draw dotted line under all lines with text
            for (int line = 0; line < lineCount; line++) {
                lastLineHeight = getLineHeight(g, line);
                dottedLineY += lastLineHeight;
                g.drawLine(0, dottedLineY, getWidth(), dottedLineY);
            }

            // Interpolate dotted lines under unused lines
            while (dottedLineY < getHeight()) {
                dottedLineY += lastLineHeight;
                g.drawLine(0, dottedLineY, getWidth(), dottedLineY);
            }
        }
    }

    private int getLineHeight(Graphics g, int line) {
        int maxHeight = 0;

        if (line > getWrappedLineCount()) {
            return getHeightForAttributes(g, getCharacterAttributes());
        }

        for (int index = getLineStartOffset(line); index <= getLineEndOffset(line); index++) {
            int height = getHeightForAttributes(g, getStyledDocument().getCharacterElement(index).getAttributes());

            if (height > maxHeight) {
                maxHeight = height;
            }
        }

        return maxHeight;
    }

    private int getHeightForAttributes(Graphics g, AttributeSet attributes) {
        return g.getFontMetrics(getStyledDocument().getFont(attributes)).getHeight();
    }

    private int getLineEndOffset(int line) {

        // Special case: Empty field
        if (getDocument().getLength() == 0) {
            return 0;
        }

        int thisLine = 0;
        int lastRowStart = 0;

        for (int index = 0; index < getDocument().getLength(); index++) {
            try {
                int thisRowStart = Utilities.getRowStart(this, index);

                if (lastRowStart != thisRowStart) {
                    lastRowStart = thisRowStart;
                    thisLine++;
                }

                if (thisLine == line) {
                    return Utilities.getRowEnd(this, index);
                }

            } catch (BadLocationException e) {
                throw new IllegalStateException("Bug! Caught exception measuring text.", e);
            }
        }

        // No such line number
        return -1;
    }

    private int getLineStartOffset(int line) {

        // Special case: Line 0 always starts at char 0.
        if (line == 0) {
            return 0;
        }

        int thisLine = 0;
        int lastRowStart = 0;

        for (int index = 0; index < getDocument().getLength(); index++) {
            try {
                int thisRowStart = Utilities.getRowStart(this, index);
                if (lastRowStart != thisRowStart) {
                    lastRowStart = thisRowStart;
                    thisLine++;

                    if (thisLine == line) {
                        return index;
                    }
                }
            } catch (BadLocationException e) {
                throw new IllegalStateException("Bug! Caught exception measuring text.", e);
            }
        }

        // No such line number
        return -1;
    }

    private int getWrappedLineCount() {

        // Special case, empty document has zero lines
        if (getDocument().getLength() == 0) {
            return 0;
        }

        int thisLine = 1;
        int lastRowStart = 0;

        for (int index = 0; index < getDocument().getLength(); index++) {
            try {
                int thisRowStart = Utilities.getRowStart(this, index);
                if (lastRowStart != thisRowStart) {
                    lastRowStart = thisRowStart;
                    thisLine++;
                }
            } catch (BadLocationException e) {
                throw new IllegalStateException("Bug! Caught exception measuring text.", e);
            }
        }

        return thisLine;
    }

}
