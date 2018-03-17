package com.defano.wyldcard.parts.field.styles;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.parts.field.highlighters.AutoSelectionHighlighterPainter;
import com.defano.wyldcard.parts.field.highlighters.FoundSelectionHighlightPainter;
import com.defano.wyldcard.parts.util.FieldUtilities;
import com.defano.wyldcard.util.Throttle;
import com.defano.hypertalk.utils.Range;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
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

    private static Throttle lineCalculationThrottle = new Throttle("line-calculation-throttle", 50);

    private boolean wrapText = true;
    private boolean scrollable = true;
    private boolean showLines = false;

    private HashMap <Integer, Integer> baselinesCache;
    private int startLine, endLine, viewPortBottom;

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
        return wrapText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public boolean getScrollableTracksViewportHeight() {
        return !scrollable || super.getScrollableTracksViewportHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public Dimension getPreferredSize() {
        if (wrapText) {
            return super.getPreferredSize();
        } else {
            return new Dimension(Math.max(getDocumentWidth(), getParent().getBounds().width), super.getPreferredSize().height);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
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
    @RunOnDispatch
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
    @RunOnDispatch
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
    @RunOnDispatch
    public int getSelectionEnd() {
        if (isAutoSelection()) {
            return getAutoSelectionRange().end;
        } else {
            return super.getSelectionEnd();
        }
    }

    @RunOnDispatch
    public void clearSearchHilights() {
        for (Highlighter.Highlight thisHighlight : highlighter.getHighlights()) {
            if (thisHighlight.getPainter() instanceof FoundSelectionHighlightPainter) {
                highlighter.removeHighlight(thisHighlight);
            }
        }

        // Removing highlights sometimes leaves graphic turds; fix that
        repaint();
    }

    @RunOnDispatch
    public void applySearchHilight(Range range) {
        try {
            highlighter.addHighlight(range.start, range.end, foundPainter);
        } catch (BadLocationException e) {
            throw new IllegalStateException("Bug! Invalid search result.");
        }
    }

    @RunOnDispatch
    private Range getAutoSelectionRange() {
        int[] lines = autoSelection.stream().mapToInt(Number::intValue).toArray();
        return FieldUtilities.getLinesRange(this, lines);
    }

    public boolean isScrollable() {
        return scrollable;
    }

    @RunOnDispatch
    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
        rewrapText();
    }

    public boolean isWrapText() {
        return wrapText;
    }

    @RunOnDispatch
    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;
        rewrapText();
    }

    @RunOnDispatch
    private void rewrapText() {
        // Cause the field to re-wrap the text inside of it
        this.setSize(this.getWidth() - 1, this.getHeight());
        this.setSize(this.getWidth() + 1, this.getHeight());
    }

    public boolean isShowLines() {
        return showLines;
    }

    @RunOnDispatch
    public void setShowLines(boolean showLines) {
        this.showLines = showLines;
        this.baselinesCache = null;
        this.repaint();
    }

    public void invalidateViewport(JViewport viewport) {
        if (viewport != null) {

            lineCalculationThrottle.submitOnUiThread(hashCode(), () -> {
                Point startPoint = viewport.getViewPosition();
                Dimension size = viewport.getExtentSize();
                Point endPoint = new Point(startPoint.x + size.width, startPoint.y + size.height);

                try {
                    startLine = FieldUtilities.getWrappedLineOfChar(HyperCardTextPane.this, viewToModel(startPoint));
                    endLine = FieldUtilities.getWrappedLineOfChar(HyperCardTextPane.this, viewToModel(endPoint));
                    viewPortBottom = startPoint.y + size.height;

                    invalidateLineHeights();

                    viewport.repaint();

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            });
        }
    }

    public void invalidateLineHeights() {
        this.baselinesCache = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (showLines) {
            HashMap<Integer, Integer> baselines = this.baselinesCache;

            // Calculate line heights
            if (baselines == null) {
                baselines = buildLineCache();
            }

            int minX = getInsets().left;
            int maxX = getBounds().width - getInsets().right;
            int lastBaseline = 0;

            Stroke dottedLine = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{1}, 0);
            ((Graphics2D) g).setStroke(dottedLine);

            // Draw lines under visible text
            for (int line = startLine; line <= endLine; line++) {
                Integer baselineY = baselines.get(line);
                if (baselineY != null && baselineY > 0) {
                    lastBaseline = baselines.get(line);
                    g.drawLine(minX, lastBaseline, maxX, lastBaseline);
                }
            }

            // If text does not fill entire field, interpolate lines in unused space
            float interpolatedHeight = (int) getLineHeight(baselinesCache.size() - 1);
            int thisBaseline = lastBaseline + (int)interpolatedHeight;
            while (thisBaseline <= viewPortBottom && interpolatedHeight > 0) {
                g.drawLine(minX, thisBaseline, maxX, thisBaseline);
                thisBaseline += interpolatedHeight;
            }
        }
    }

    @RunOnDispatch
    private HashMap<Integer, Integer> buildLineCache() {
        this.baselinesCache = new HashMap<>();
        float lastLineHeight = 0;
        int dottedLineY = getInsets().top;

        // Draw dotted line under all lines with text
        for (int line = 0; line < getWrappedLineCount(); line++) {
            float thisLineHeight = getLineHeight(line);
            if (thisLineHeight == 0) {
                break;
            }

            lastLineHeight = thisLineHeight;
            dottedLineY += lastLineHeight;

            baselinesCache.put(line, dottedLineY);
        }

        return baselinesCache;
    }

    /**
     * Gets the natural width of this document, in pixels. That is, the number of horizontal pixels required to display
     * the document without wrapping or truncating the text.
     *
     * @return The natural width of the document.
     */
    @RunOnDispatch
    private int getDocumentWidth() {
        try {
            View document = getUI().getRootView(this).getView(0);
            return (int) document.getPreferredSpan(View.X_AXIS);
        } catch (Exception e) {
            return this.getWidth();
        }
    }

    /**
     * Determines the rendered height of the request line index.
     *
     * @param line The index of the line whose height should be calculated; a value between 0 and {@link #getWrappedLineCount()}
     * @return The height, in pixels.
     */
    @RunOnDispatch
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
    @RunOnDispatch
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
    @RunOnDispatch
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
    @RunOnDispatch
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

    @RunOnDispatch
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

    @RunOnDispatch
    private void autoSelectLine(int selectedLine) {
        Range clickRange = FieldUtilities.getLineRange(this, selectedLine);
        if (!clickRange.isEmpty()) {
            autoSelect(FieldUtilities.getLineRange(this, selectedLine));
        }
    }

    @RunOnDispatch
    private void autoSelect(Range range) {
        try {
            highlighter.addHighlight(range.start, range.end, hilitePainter);
        } catch (BadLocationException ble) {
            // Nothing to select
        }
    }

    @RunOnDispatch
    private void removeAutoSelections() {
        highlighter.removeAllHighlights();
    }

    private boolean isAutoSelection() {
        return autoSelection.size() > 0;
    }
}