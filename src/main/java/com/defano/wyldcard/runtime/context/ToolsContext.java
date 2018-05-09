package com.defano.wyldcard.runtime.context;

import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.SystemMessage;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.jmonet.algo.dither.Ditherer;
import com.defano.jmonet.algo.dither.FloydSteinbergDitherer;
import com.defano.jmonet.canvas.PaintCanvas;
import com.defano.jmonet.model.ImageAntiAliasingMode;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.SelectionTool;
import com.defano.jmonet.tools.base.AbstractSelectionTool;
import com.defano.jmonet.tools.builder.PaintTool;
import com.defano.jmonet.tools.builder.PaintToolBuilder;
import com.defano.jmonet.tools.builder.StrokeBuilder;
import com.defano.jmonet.tools.selection.TransformableCanvasSelection;
import com.defano.jmonet.tools.selection.TransformableImageSelection;
import com.defano.jmonet.tools.selection.TransformableSelection;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.PaintBrush;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.patterns.HyperCardPatternFactory;
import com.defano.wyldcard.window.WindowManager;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.Subject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

import static io.reactivex.subjects.BehaviorSubject.createDefault;

/**
 * A singleton representation of paint tool context. Responsible for all configurable properties of the paint subsystem
 * including line width, brush selection, pattern, poly sides, draw multiple, draw centered, grid spacing, etc.
 */
public class ToolsContext {

    private final static ToolsContext instance = new ToolsContext();

    // Tool mode properties
    private final Subject<ToolMode> toolModeProvider = createDefault(ToolMode.BROWSE);

    // Properties that the tools provide to us...
    private final Subject<Optional<BufferedImage>> selectedImageProvider = createDefault(Optional.empty());

    // Properties that we provide the tools...
    private final Subject<Boolean> shapesFilledProvider = createDefault(false);
    private final Subject<Boolean> isEditingBackground = createDefault(false);
    private final Subject<Stroke> lineStrokeProvider = createDefault(StrokeBuilder.withBasicStroke().ofWidth(2).withRoundCap().withRoundJoin().build());
    private final Subject<PaintBrush> brushStrokeProvider = createDefault(PaintBrush.ROUND_12X12);
    private final Subject<Paint> linePaintProvider = createDefault(Color.black);
    private final Subject<Integer> fillPatternProvider = createDefault(0);
    private final Subject<Integer> shapeSidesProvider = createDefault(5);
    private final Subject<PaintTool> paintToolProvider = createDefault(PaintToolBuilder.create(PaintToolType.ARROW).build());
    private final Subject<Boolean> drawMultipleProvider = createDefault(false);
    private final Subject<Boolean> drawCenteredProvider = createDefault(false);
    private final Subject<Color> foregroundColorProvider = createDefault(Color.BLACK);
    private final Subject<Color> backgroundColorProvider = createDefault(Color.WHITE);
    private final Subject<Double> intensityProvider = createDefault(0.1);
    private final Subject<Ditherer> dithererProvider = createDefault(new FloydSteinbergDitherer());
    private final Subject<ImageAntiAliasingMode> antiAliasingProvider = createDefault(ImageAntiAliasingMode.OFF);

    // Properties that we provide the canvas
    private final Subject<Integer> gridSpacingProvider = createDefault(1);

    private PaintToolType lastToolType;
    private Disposable selectedImageSubscription;
    private Disposable gridSpacingSubscription;

    public static ToolsContext getInstance() {
        return instance;
    }

    public int getGridSpacing() {
        return gridSpacingProvider.blockingFirst();
    }

    public void setGridSpacing(int spacing) {
        if (instance.gridSpacingSubscription == null) {
            instance.gridSpacingSubscription = instance.gridSpacingProvider.subscribe(integer -> WyldCard.getInstance().getFocusedCard().getCanvas().setGridSpacing(integer));
        }
        gridSpacingProvider.onNext(spacing);
    }

    public Subject<Integer> getGridSpacingProvider() {
        return gridSpacingProvider;
    }

    public Subject<Stroke> getLineStrokeProvider() {
        return lineStrokeProvider;
    }

    public Subject<Paint> getLinePaintProvider() {
        return linePaintProvider;
    }

    public void setLinePaint(Paint p) {
        linePaintProvider.onNext(p);
    }

    public void reactivateTool(PaintCanvas canvas) {
        paintToolProvider.blockingFirst().deactivate();
        if (canvas != null) {
            paintToolProvider.blockingFirst().activate(canvas);
        }
    }

    public Subject<PaintTool> getPaintToolProvider() {
        return paintToolProvider;
    }

    public Subject<ToolMode> getToolModeProvider() {
        return toolModeProvider;
    }

    public ToolMode getToolMode() {
        return toolModeProvider.blockingFirst();
    }

    public PaintTool getPaintTool() {
        return paintToolProvider.blockingFirst();
    }

    public void selectAll() {
        SelectionTool tool = (SelectionTool) forceToolSelection(ToolType.SELECT, false);
        tool.createSelection(new Rectangle(0, 0, WyldCard.getInstance().getFocusedCard().getWidth() - 1, WyldCard.getInstance().getFocusedCard().getHeight() - 1));
    }

    public Color getForegroundColor() {
        return foregroundColorProvider.blockingFirst();
    }

    public void setForegroundColor(Color color) {
        foregroundColorProvider.onNext(color);
    }

    public Color getBackgroundColor() {
        return backgroundColorProvider.blockingFirst();
    }

    public void setBackgroundColor(Color color) {
        backgroundColorProvider.onNext(color);
    }

    public Subject<Color> getBackgroundColorProvider() {
        return backgroundColorProvider;
    }

    public Subject<Color> getForegroundColorProvider() {
        return foregroundColorProvider;
    }

    public PaintBrush getSelectedBrush() {
        return brushStrokeProvider.blockingFirst();
    }

    public void setSelectedBrush(PaintBrush brush) {
        brushStrokeProvider.onNext(brush);
    }

    public Subject<PaintBrush> getSelectedBrushProvider() {
        return brushStrokeProvider;
    }

    public void toggleDrawCentered() {
        setDrawCentered(!isDrawCentered());
    }

    public boolean isDrawCentered() {
        return this.drawCenteredProvider.blockingFirst();
    }

    public void setDrawCentered(boolean drawCentered) {
        this.drawCenteredProvider.onNext(drawCentered);
    }

    public Subject<Boolean> getDrawCenteredProvider() {
        return drawCenteredProvider;
    }

    public void toggleDrawMultiple() {
        setDrawMultiple(!isDrawMultiple());
    }

    public boolean isDrawMultiple() {
        return this.drawMultipleProvider.blockingFirst();
    }

    public void setDrawMultiple(boolean drawMultiple) {
        this.drawMultipleProvider.onNext(drawMultiple);
    }

    public Subject<Boolean> getDrawMultipleProvider() {
        return drawMultipleProvider;
    }

    public Subject<Optional<BufferedImage>> getSelectedImageProvider() {
        return selectedImageProvider;
    }

    public BufferedImage getSelectedImage() {
        return selectedImageProvider.blockingFirst().orElse(null);
    }

    private void setSelectedImage(Observable<Optional<BufferedImage>> imageProvider) {
        if (selectedImageSubscription != null) {
            selectedImageSubscription.dispose();
        }
        selectedImageSubscription = imageProvider.subscribe(selectedImageProvider::onNext);
    }

    public int getShapeSides() {
        return shapeSidesProvider.blockingFirst();
    }

    public void setShapeSides(int shapeSides) {
        shapeSidesProvider.onNext(shapeSides);
    }

    public Subject<Integer> getShapeSidesProvider() {
        return shapeSidesProvider;
    }

    public Subject<Integer> getFillPatternProvider() {
        return fillPatternProvider;
    }

    public int getFillPattern() {
        return fillPatternProvider.blockingFirst();
    }

    public void setFillPattern(int pattern) {
        fillPatternProvider.onNext(pattern);
    }

    public double getIntensity() {
        return intensityProvider.blockingFirst();
    }

    public void setIntensity(double intensity) {
        intensityProvider.onNext(intensity);
    }

    public Ditherer getDitherer() {
        return dithererProvider.blockingFirst();
    }

    public void setDitherer(Ditherer ditherer) {
        dithererProvider.onNext(ditherer);
    }

    public Subject<ImageAntiAliasingMode> getAntiAliasingProvider() {
        return antiAliasingProvider;
    }

    public void setAntiAliasingMode(ImageAntiAliasingMode antiAliasingMode) {
        this.antiAliasingProvider.onNext(antiAliasingMode);
    }

    public Observable<Ditherer> getDithererProvider() {
        return dithererProvider;
    }

    public void toggleMagnifier() {
        if (getPaintTool().getToolType() == PaintToolType.MAGNIFIER) {
            WyldCard.getInstance().getFocusedCard().getCanvas().setScale(1.0);
            forceToolSelection(ToolType.fromPaintTool(lastToolType), false);
        } else if (WyldCard.getInstance().getFocusedCard().getCanvas().getScale() != 1.0) {
            WyldCard.getInstance().getFocusedCard().getCanvas().setScale(1.0);
        } else {
            forceToolSelection(ToolType.MAGNIFIER, false);
        }
    }

    public int getLineWidth() {
        return Math.round((int) ((BasicStroke) lineStrokeProvider.blockingFirst()).getLineWidth());
    }

    public void setLineWidth(int width) {
        lineStrokeProvider.onNext(StrokeBuilder.withBasicStroke().ofWidth(width).withRoundCap().withRoundJoin().build());
    }

    public void setPattern(int patternId) {
        fillPatternProvider.onNext(patternId);
    }

    public boolean isEditingBackground() {
        return isEditingBackground.blockingFirst();
    }

    public Subject<Boolean> isEditingBackgroundProvider() {
        return isEditingBackground;
    }

    public void toggleIsEditingBackground() {
        getPaintTool().deactivate();
        isEditingBackground.onNext(!isEditingBackground.blockingFirst());
        reactivateTool(WyldCard.getInstance().getFocusedCard().getCanvas());
        WindowManager.getInstance().getFocusedStackWindow().invalidateWindowTitle();
    }

    public void setIsEditingBackground(boolean isEditingBackground) {
        getPaintTool().deactivate();
        this.isEditingBackground.onNext(isEditingBackground);
        reactivateTool(WyldCard.getInstance().getFocusedCard().getCanvas());
    }

    public boolean isShapesFilled() {
        return shapesFilledProvider.blockingFirst();
    }

    public void setShapesFilled(boolean shapesFilled) {
        this.shapesFilledProvider.onNext(shapesFilled);
    }

    public void toggleShapesFilled() {
        shapesFilledProvider.onNext(!shapesFilledProvider.blockingFirst());
        forceToolSelection(ToolType.fromPaintTool(paintToolProvider.blockingFirst().getToolType()), false);
    }

    public Subject<Boolean> getShapesFilledProvider() {
        return shapesFilledProvider;
    }

    /**
     * Attempts to make the given tool active on the card. Sends a {@link SystemMessage#CHOOSE} message to the
     * current card. If any script in the message passing hierarchy traps the message, then the request is ignored
     * and the tool is not changed.
     * <p>
     * For programmatic tool changes that should not be trappable, see {@link #forceToolSelection(ToolType, boolean)}.
     *
     * @param toolType The requested tool selection.
     */
    public void chooseTool(ToolType toolType) {
        WyldCard.getInstance().getFocusedCard().getCardModel().receiveMessage(ExecutionContext.unboundInstance(), SystemMessage.CHOOSE.messageName, ListExp.fromValues(null, new Value(toolType.getPrimaryToolName()), new Value(toolType.getToolNumber())), (command, wasTrapped, err) -> {
            if (!wasTrapped) {
                SwingUtilities.invokeLater(() -> forceToolSelection(toolType, false));
            }
        });
    }

    /**
     * Make the given tool the active tool on the card. Does not generate a HyperTalk {@link SystemMessage#CHOOSE}
     * message, and therefore is not overridable in script.
     * <p>
     * This method should be used for "programmatic" tool selection where the user/card should have no ability to "see"
     * the change or prevent it. User-driven changes to the tool selection should use {@link #chooseTool(ToolType)}
     * instead.
     *
     * @param tool          The tool selection
     * @param keepSelection Attempt to maintain / morph the active selection as the tool changes. Has no effect if
     *                      the tool transition does not operate on a selection or does not support selection morphing.
     * @return The instance of the newly activated tool.
     */
    public PaintTool forceToolSelection(ToolType tool, boolean keepSelection) {
        PaintTool selected = activatePaintTool(tool.toPaintTool(), keepSelection);

        switch (tool) {
            case BROWSE:
                toolModeProvider.onNext(ToolMode.BROWSE);
                break;
            case BUTTON:
                toolModeProvider.onNext(ToolMode.BUTTON);
                break;
            case FIELD:
                toolModeProvider.onNext(ToolMode.FIELD);
                break;

            default:
                toolModeProvider.onNext(ToolMode.PAINT);
                break;
        }

        return selected;
    }

    public ToolType getSelectedTool() {
        return ToolType.fromToolMode(getToolMode(), getPaintTool().getToolType());
    }

    /**
     * Gets an observable of whether an image selection exists using a tool that conforms to
     * {@link TransformableImageSelection}.
     *
     * @return The observable
     */
    public Observable<Boolean> hasTransformableImageSelectionProvider() {
        return Observable.combineLatest(
                ToolsContext.getInstance().getPaintToolProvider(),
                ToolsContext.getInstance().getSelectedImageProvider(),
                (paintTool, bufferedImage) -> paintTool instanceof TransformableImageSelection && bufferedImage.isPresent()
        );
    }

    /**
     * Gets an observable of whether an image selection exists using a tool that conforms to
     * {@link TransformableSelection}.
     *
     * @return The observable
     */
    public Observable<Boolean> hasTransformableSelectionProvider() {
        return Observable.combineLatest(
                ToolsContext.getInstance().getPaintToolProvider(),
                ToolsContext.getInstance().getSelectedImageProvider(),
                (paintTool, bufferedImage) -> paintTool instanceof TransformableSelection && bufferedImage.isPresent()
        );
    }

    /**
     * Gets an observable of whether an image selection exists using a tool that conforms to
     * {@link TransformableCanvasSelection}.
     *
     * @return The observable
     */
    public Observable<Boolean> hasTransformableCanvasSelectionProvider() {
        return Observable.combineLatest(
                ToolsContext.getInstance().getPaintToolProvider(),
                ToolsContext.getInstance().getSelectedImageProvider(),
                (paintTool, bufferedImage) -> paintTool instanceof TransformableCanvasSelection && bufferedImage.isPresent()
        );
    }

    private PaintTool activatePaintTool(PaintToolType selectedToolType, boolean keepSelection) {

        // Create and activate new paint tool
        PaintTool selectedTool = PaintToolBuilder.create(selectedToolType)
                .withStrokeObservable(getStrokeProviderForTool(selectedToolType))
                .withStrokePaintObservable(getStrokePaintProviderForTool(selectedToolType))
                .withFillPaintObservable(fillPatternProvider.map(t -> isShapesFilled() || !selectedToolType.isShapeTool() ? Optional.of(HyperCardPatternFactory.getInstance().getPattern(t)) : Optional.empty()))
                .withFontObservable(FontContext.getInstance().getPaintFontProvider())
                .withFontColorObservable(foregroundColorProvider)
                .withShapeSidesObservable(shapeSidesProvider)
                .withIntensityObservable(intensityProvider)
                .withDrawCenteredObservable(drawCenteredProvider)
                .withDrawMultipleObservable(drawMultipleProvider)
                .makeActiveOnCanvas(WyldCard.getInstance().getFocusedCard().getCanvas())
                .build();

        // TODO: Add to paint tool builder
        selectedTool.setAntiAliasingObservable(antiAliasingProvider);

        // When requested, move current selection over to the new tool
        if (keepSelection) {
            PaintTool lastTool = paintToolProvider.blockingFirst();
            if (lastTool instanceof AbstractSelectionTool && selectedTool instanceof AbstractSelectionTool) {
                ((AbstractSelectionTool) lastTool).morphSelection((AbstractSelectionTool) selectedTool);
            }
        }

        // Deactivate current tool
        lastToolType = paintToolProvider.blockingFirst().getToolType();
        paintToolProvider.blockingFirst().deactivate();

        // Update selected image provider (so UI can tell when a selection exists)
        if (selectedTool instanceof AbstractSelectionTool) {
            setSelectedImage(((AbstractSelectionTool) selectedTool).getSelectedImageObservable());
        }

        paintToolProvider.onNext(selectedTool);
        return selectedTool;
    }

    private Observable<Stroke> getStrokeProviderForTool(PaintToolType type) {
        switch (type) {
            case PAINTBRUSH:
            case AIRBRUSH:
            case ERASER:
                return brushStrokeProvider.map(value -> value.stroke);

            default:
                return lineStrokeProvider;
        }
    }

    private Observable<Paint> getStrokePaintProviderForTool(PaintToolType type) {
        switch (type) {
            case PAINTBRUSH:
            case AIRBRUSH:
                return fillPatternProvider.map(patternId -> HyperCardPatternFactory.getInstance().getPattern(patternId));

            default:
                return linePaintProvider;
        }
    }

}
