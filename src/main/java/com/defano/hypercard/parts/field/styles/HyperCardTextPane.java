package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.utils.Range;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * An extension to {@link JTextPane} that adds the ability to disable auto-wrapping of text, to draw dotted lines
 * underneath each line of text, to position the cursor beyond the bounds of the field contents, and to support
 * "auto-selection" features.
 */
public class HyperCardTextPane extends JTextPane {

    private boolean wrapText = true;
    private boolean showLines = false;
    private Set<Integer> autoSelection = new HashSet<>();

    HyperCardTextPane(StyledDocument doc) {
        super(doc);

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE && isEditable() && isEnabled()) {
                    expandContentsToClickLoc(e.getPoint());
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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

    @Override
    public String getText() {
        try {
            return getStyledDocument().getText(0, getStyledDocument().getLength());
        } catch (BadLocationException e) {
            return "";
        }
    }

    @Override
    public String getSelectedText() {
        if (isAutoSelection()) {
            return getText().substring(getAutoSelectionRange().start, getAutoSelectionRange().end);
        } else {
            return super.getSelectedText();
        }
    }

    @Override
    public int getSelectionStart() {
        if (isAutoSelection()) {
            return getAutoSelectionRange().start;
        } else {
            return super.getSelectionStart();
        }
    }

    @Override
    public int getSelectionEnd() {
        if (isAutoSelection()) {
            return getAutoSelectionRange().end;
        } else {
            return super.getSelectionEnd();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (showLines) {
            float lastLineHeight = 0;
            int dottedLineY = getInsets().top;
            int minX = getInsets().left;
            int maxX = getWidth() - getInsets().right;

            int lineCount = getWrappedLineCount();

            Stroke dottedLine = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{1}, 0);
            ((Graphics2D) g).setStroke(dottedLine);

            // Draw dotted line under all lines with text
            for (int line = 0; line < lineCount; line++) {
                lastLineHeight = getLineHeight(line);
                dottedLineY += lastLineHeight;
                g.drawLine(minX, dottedLineY, maxX, dottedLineY);
            }

            // Interpolate dotted lines under unused lines
            while (dottedLineY < getHeight()) {
                dottedLineY += lastLineHeight;
                g.drawLine(minX, dottedLineY, maxX, dottedLineY);
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
    private int getWrappedLineCount() {
        int lines = 0;
        View document = getUI().getRootView(this).getView(0);

        int paragraphs = document.getViewCount();

        for (int i = 0; i < paragraphs; i++) {
            View paragraph = document.getView(i);
            lines += paragraph.getViewCount();
        }

        return lines;
    }

    /**
     * Expands the contents of the field (with newlines) if/as needed to reach the line represented by the clickLoc.
     * This lets a user click within an empty field and move the cursor to an arbitrary line, even the contents of
     * the field don't reach the line they clicked.
     *
     * @param clickLoc The location of the mouse click.
     */
    private void expandContentsToClickLoc(Point clickLoc) {
        int clickLine = getClickedLine(clickLoc);

        // Did we click beyond the length of the field contents
        if (getWrappedLineCount() < clickLine) {
            int needsLines = clickLine - getWrappedLineCount();

            // Expand field contents to reach click line
            for (int index = 0; index < needsLines; index++) {
                try {
                    getStyledDocument().insertString(getStyledDocument().getLength(), "\n", getCharacterAttributes());
                } catch (BadLocationException e) {
                    throw new IllegalStateException("Bug! Shouldn't be possible.", e);
                }
            }
        }
    }

    /**
     * Determines the line number (counting from 1) where the given point is located.
     *
     * @param clickLoc A point within the bounds of this component
     * @return The line number where the give point is located
     */
    public int getClickedLine(Point clickLoc) {
        float cumulativeLineHeight = 0;
        float thisLineHeight = 0;

        for (int line = 0; ; line++) {
            thisLineHeight = getLineHeight(line) == 0 ? thisLineHeight : getLineHeight(line);

            if (clickLoc.y >= cumulativeLineHeight && clickLoc.y < cumulativeLineHeight + thisLineHeight) {
                return line + 1;
            }

            cumulativeLineHeight += thisLineHeight;
        }
    }

    private Range getLineRange(int line) {
        try {
            Element root = getDocument().getDefaultRootElement();
            int start = root.getElement(line - 1).getStartOffset();
            int end = Utilities.getRowEnd(this, start);

            return new Range(start, end);
        } catch (Exception e) {
            return new Range();
        }
    }

    private Range getAutoSelectionRange() {
        if (isAutoSelection()) {
            return getLineRange((Integer) autoSelection.toArray()[0]);
        } else {
            return new Range();
        }
    }

    public void autoSelectLines(Set<Integer> selectedLines) {
        this.autoSelection = selectedLines;
        removeAutoSelections();

        for (int thisLine : selectedLines) {
            autoSelectLine(thisLine);
        }
    }

    private void autoSelectLine(int selectedLine) {
        Range clickRange = getLineRange(selectedLine);
        if (!clickRange.isEmpty()) {
            autoSelect(getLineRange(selectedLine));
        }
    }

    private void autoSelect(Range range) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            SimpleAttributeSet sas = new SimpleAttributeSet();
            sas.addAttribute(StyleConstants.Background, Color.BLACK);
            sas.addAttribute(StyleConstants.Foreground, Color.WHITE);

            getStyledDocument().setCharacterAttributes(range.start, range.length(), sas, false);
        });
    }

    protected void removeAutoSelections() {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            SimpleAttributeSet sas = new SimpleAttributeSet();
            sas.addAttribute(StyleConstants.Background, new Color(0xff, 0x00, 0x00, 0x00));
            sas.addAttribute(StyleConstants.Foreground, Color.BLACK);

            getStyledDocument().setCharacterAttributes(0, getStyledDocument().getLength(), sas, false);
        });
    }

    private boolean isAutoSelection() {
        return autoSelection.size() > 0;
    }
}