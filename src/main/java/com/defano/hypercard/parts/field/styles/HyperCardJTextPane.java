package com.defano.hypercard.parts.field.styles;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import java.awt.*;

/**
 * An extension to {@link JTextPane} that adds the ability to disable auto-wrapping of text and to draw dotted lines
 * underneath each line of text.
 */
public class HyperCardJTextPane extends JTextPane {

    private boolean wrapText = true;
    private boolean showLines = false;

    HyperCardJTextPane(StyledDocument doc) {
        super(doc);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (showLines) {
            float lastLineHeight = 0;
            int dottedLineY = 0;
            int lineCount = getWrappedLineCount();

            Stroke dottedLine = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{1}, 0);
            ((Graphics2D) g).setStroke(dottedLine);

            // Draw dotted line under all lines with text
            for (int line = 0; line < lineCount; line++) {
                lastLineHeight = getLineHeight(line);
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

    /**
     * Determines the rendered height of the request line index.
     *
     * @param line The index of the line whose height should be calculated; a value between 0 and {@link #getWrappedLineCount()}
     * @return The height, in pixels.
     */
    private float getLineHeight(int line) {
        int lineCount = 0;
        View document = getUI().getRootView(this).getView(0);

        // Walk each paragraph in document
        for (int paragraphIdx = 0; paragraphIdx < document.getViewCount(); paragraphIdx++) {
            View paragraph = document.getView(paragraphIdx);

            // Walk each line in paragraph
            for (int lineIdx = 0; lineIdx < paragraph.getViewCount(); lineIdx++) {

                // Did we find requested line?
                if (lineCount++ == line) {
                    return paragraph.getView(lineIdx).getPreferredSpan(View.Y_AXIS);
                }
            }
        }

        return 0;
    }

    /**
     * Determines the number of rendered lines in the text field. Not the same as the number of lines of text because
     * any given line of text could wrap across multiple lines of the field depending on its width.
     *
     * @return The number of lines of text in the field; some lines may be wrapped.
     */
    private int getWrappedLineCount()
    {
        int lines = 0;
        View document = getUI().getRootView(this).getView(0);

        int paragraphs = document.getViewCount();

        for (int i = 0; i < paragraphs; i++) {
            View paragraph = document.getView(i);
            lines += paragraph.getViewCount();
        }

        return lines;
    }

}