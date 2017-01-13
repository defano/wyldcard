package hypercard.paint.tools;

import hypercard.paint.canvas.Canvas;
import hypercard.paint.canvas.CanvasInteractionListener;
import hypercard.paint.canvas.CanvasObserver;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.model.Provider;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public abstract class AbstractPaintTool implements CanvasInteractionListener, CanvasObserver {

    private Canvas canvas;
    private final PaintToolType type;
    private AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

    private Stroke defaultStroke = new BasicStroke(2);
    private Paint defaultStrokePaint = Color.BLACK;
    private Paint defaultFillPaint = null;
    private int defaultShapeSides = 5;
    private Font defaultFont = new Font("Courier", Font.PLAIN, 14);
    private Cursor toolCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);

    private Provider<Stroke> strokeProvider = new Provider<>(defaultStroke);
    private Provider<Paint> strokePaintProvider = new Provider<>(defaultStrokePaint);
    private Provider<Paint> fillPaintProvider = new Provider<>(defaultFillPaint);
    private Provider<Integer> shapeSidesProvider = new Provider<>(defaultShapeSides);
    private Provider<Font> fontProvider = new Provider<>(defaultFont);

    public AbstractPaintTool(PaintToolType type) {
        this.type = type;
    }

    public void activate (Canvas canvas) {
        this.canvas = canvas;
        this.canvas.addCanvasInteractionListener(this);
        this.canvas.setCursor(toolCursor);
    }

    public void deactivate() {
        if (canvas != null) {
            canvas.removeCanvasInteractionListener(this);
            canvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void onCommit(Canvas canvas, BufferedImage committedElement, BufferedImage canvasImage) {
        // Nothing to do
    }

    public AlphaComposite getComposite() {
        return composite;
    }

    public void setComposite(AlphaComposite composite) {
        this.composite = composite;
    }

    public PaintToolType getToolType() {
        return this.type;
    }

    protected Canvas getCanvas() {
        return canvas;
    }

    public void setStrokePaintProvider(Provider<Paint> strokePaintProvider) {
        if (strokePaintProvider != null) {
            this.strokePaintProvider = strokePaintProvider;
        }
    }

    public void setStrokeProvider(Provider<Stroke> strokeProvider) {
        if (strokeProvider != null) {
            this.strokeProvider = strokeProvider;
        }
    }

    public void setShapeSidesProvider(Provider<Integer> shapeSidesProvider) {
        if (shapeSidesProvider != null) {
            this.shapeSidesProvider = shapeSidesProvider;
        }
    }

    public void setFontProvider(Provider<Font> fontProvider) {
        if (fontProvider != null) {
            this.fontProvider = fontProvider;
        }
    }

    public void setFillPaintProvider(Provider<Paint> fillPaintProvider) {
        if (fillPaintProvider != null) {
            this.fillPaintProvider = fillPaintProvider;
        }
    }

    public Stroke getStroke() {
        return strokeProvider.get();
    }

    public Paint getFillPaint() {
        return fillPaintProvider.get();
    }

    public Font getFont() {
        return fontProvider.get();
    }

    public int getShapeSides() {
        return shapeSidesProvider.get();
    }

    public Paint getStrokePaint() {
        return strokePaintProvider.get();
    }

    public Provider<Paint> getFillPaintProvider() {
        return fillPaintProvider;
    }

    public Provider<Stroke> getStrokeProvider() {
        return strokeProvider;
    }

    public Provider<Paint> getStrokePaintProvider() {
        return strokePaintProvider;
    }

    public Provider<Integer> getShapeSidesProvider() {
        return shapeSidesProvider;
    }

    public Provider<Font> getFontProvider() {
        return fontProvider;
    }

    public Cursor getToolCursor() {
        return toolCursor;
    }

    public void setToolCursor(Cursor toolCursor) {
        this.toolCursor = toolCursor;

        if (this.canvas != null) {
            this.canvas.setCursor(toolCursor);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e, int scaleX, int scaleY) {
        // Nothing to do
    }

    @Override
    public void mousePressed(MouseEvent e, int scaleX, int scaleY) {
        // Nothing to do
    }

    @Override
    public void mouseReleased(MouseEvent e, int scaleX, int scaleY) {
        // Nothing to do
    }

    @Override
    public void mouseEntered(MouseEvent e, int scaleX, int scaleY) {
        // Nothing to do
    }

    @Override
    public void mouseExited(MouseEvent e, int scaleX, int scaleY) {
        // Nothing to do
    }

    @Override
    public void mouseDragged(MouseEvent e, int scaleX, int scaleY) {
        // Nothing to do
    }

    @Override
    public void mouseMoved(MouseEvent e, int scaleX, int scaleY) {
        // Nothing to do
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Nothing to do
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Nothing to do
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Nothing to do
    }
}
