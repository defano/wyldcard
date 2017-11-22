package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.field.highlighters.AutoSelectionHighlighterPainter;
import com.defano.hypercard.parts.field.highlighters.FoundSelectionHighlightPainter;
import com.defano.hypercard.parts.util.FieldUtilities;
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
 * An extension to {@link JTextPane} that adds a variety of HyperCard-specific features to a standard text pane,
 * including:
 *
 * - Ability to enable or disable vertical scrolling (within a JScrollPane),
 * - Ability to disable auto-wrapping text across lines,
 * - Draw dotted lines underneath each line of text,
 * - Ability to position the cursor beyond the bounds of the field contents
 * - Support for per-line "auto-selection" features.
 */
public class HyperCardTextPane extends JTextPane {

    private boolean wrapText = true;
    private boolean scrollable = true;
    private boolean showLines = false;

    private final Set<Integer> autoSelection = new HashSet<>();
    private final Highlighter highlighter = new DefaultHighlighter();
    private final AutoSelectionHighlighterPainter hilitePainter = new AutoSelectionHighlighterPainter();
    private final FoundSelectionHighlightPainter foundPainter = new FoundSelectionHighlightPainter();

    HyperCardTextPane(StyledDocument doc) {
        super(doc);

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE && isEditable() && isEnabled()) {
                    expandContentsToClickLoc(e);
                }
            }
        });

        super.setHighlighter(highlighter);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return !scrollable || super.getScrollableTracksViewportHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        try {
            return getStyledDocument().getText(0, getStyledDocument().getLength());
        } catch (BadLocationException e) {
            return "";
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSelectedText() {
        if (isAutoSelection()) {
            return getText().substring(getAutoSelectionRange().start, getAutoSelectionRange().end);
        } else {
            return super.getSelectedText();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSelectionStart() {
        if (isAutoSelection()) {
            return getAutoSelectionRange().start;
        } else {
            return super.getSelectionStart();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSelectionEnd() {
        if (isAutoSelection()) {
            return getAutoSelectionRange().end;
        } else {
            return super.getSelectionEnd();
        }
    }

    public void clearSearchHilights() {
        for (Highlighter.Highlight thisHighlight : highlighter.getHighlights()) {
            if (thisHighlight.getPainter() instanceof FoundSelectionHighlightPainter) {
                highlighter.removeHighlight(thisHighlight);
            }
        }

        // Removing highlights sometimes leaves graphic turds; fix that
        repaint();
    }

    public void applySearchHilight(Range range) {
        try {
            highlighter.addHighlight(range.start, range.end, foundPainter);
        } catch (BadLocationException e) {
            throw new IllegalStateException("Bug! Invalid search result.");
        }
    }

    private Range getAutoSelectionRange() {
        int[] lines = autoSelection.stream().mapToInt(Number::intValue).toArray();
        return FieldUtilities.getLinesRange(this, lines);
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;

        // Cause the field to re-wrap the text inside of it
        this.setSize(this.getWidth() - 1, this.getHeight());
        this.setSize(this.getWidth() + 1, this.getHeight());
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
            int maxX = getBounds().width - getInsets().right;
            int maxY = getBounds().height - getInsets().bottom;

            int lineCount = getWrappedLineCount();

            Stroke dottedLine = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{1}, 0);
            ((Graphics2D) g).setStroke(dottedLine);

            // Draw dotted line under all lines with text
            for (int line = 0; line < lineCount; line++) {
                lastLineHeight = getLineHeight(line);
                dottedLineY += lastLineHeight;

                if (dottedLineY < maxY) {
                    g.drawLine(minX, dottedLineY, maxX, dottedLineY);
                }
            }
            
            // Interpolate dotted lines under unused lines
            dottedLineY += lastLineHeight;
            while (dottedLineY < maxY) {
                g.drawLine(minX, dottedLineY, maxX, dottedLineY);
                dottedLineY += lastLineHeight;
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
     * @param evt The location of the mouse click.
     */
    private void expandContentsToClickLoc(MouseEvent evt) {
        if (isEditable()) {
            int clickLine = getClickedLine(evt);

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
    }

    /**
     * Determines the line number (counting from 1) where the given point is located.
     *
     * @param evt A point within the bounds of this component
     * @return The line number where the give point is located
     */
    public int getClickedLine(MouseEvent evt) {

        float cumulativeLineHeight = getInsets().top;
        float thisLineHeight = 0;
        Point clickLoc = SwingUtilities.convertPoint(evt.getComponent(), evt.getPoint(), this);

        // Special case; user clicked inside of top inset margin
        if (clickLoc.y < getInsets().top) {
            return 1;
        }

        for (int line = 0; ; line++) {
            thisLineHeight = getLineHeight(line) == 0 ? thisLineHeight : getLineHeight(line);

            if (clickLoc.y >= cumulativeLineHeight && clickLoc.y < cumulativeLineHeight + thisLineHeight) {
                return line + 1;
            }

            cumulativeLineHeight += thisLineHeight;
        }
    }

    public void autoSelectLines(Set<Integer> selectedLines) {
        if (selectedLines != null) {
            this.autoSelection.clear();
            this.autoSelection.addAll(selectedLines);

            removeAutoSelections();

            for (int thisLine : selectedLines) {
                autoSelectLine(thisLine);
            }
        }
    }

    private void autoSelectLine(int selectedLine) {
        Range clickRange = FieldUtilities.getLineRange(this, selectedLine);
        if (!clickRange.isEmpty()) {
            autoSelect(FieldUtilities.getLineRange(this, selectedLine));
        }
    }

    private void autoSelect(Range range) {
        try {
            highlighter.addHighlight(range.start, range.end, hilitePainter);
        } catch (BadLocationException ble) {
            // Nothing to select
        }
    }

    private void removeAutoSelections() {
        highlighter.removeAllHighlights();
    }

    private boolean isAutoSelection() {
        return autoSelection.size() > 0;
    }
}