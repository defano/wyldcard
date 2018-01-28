package com.defano.hypercard.runtime.context;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.patterns.HyperCardPatternFactory;
import com.defano.hypertalk.ast.model.ExpressionList;
import com.defano.hypertalk.ast.model.SystemMessage;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.jmonet.canvas.PaintCanvas;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.SelectionTool;
import com.defano.jmonet.tools.base.AbstractBoundsTool;
import com.defano.jmonet.tools.base.AbstractSelectionTool;
import com.defano.jmonet.tools.base.PaintTool;
import com.defano.jmonet.tools.brushes.BasicBrush;
import com.defano.jmonet.tools.builder.PaintToolBuilder;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * A singleton representation of paint tool context. Responsible for all configurable properties of the paint subsystem
 * including line width, brush selection, pattern, poly sides, draw multiple, draw centered, grid spacing, etc.
 */
public class ToolsContext {

    private final static ToolsContext instance = new ToolsContext();

    // Tool mode properties
    private final Subject<ToolMode> toolModeProvider = BehaviorSubject.createDefault(ToolMode.BROWSE);

    // Properties that the tools provide to us...
    private final Subject<Optional<BufferedImage>> selectedImageProvider = BehaviorSubject.createDefault(Optional.empty());

    // Properties that we provide the tools...
    private final Subject<Boolean> shapesFilled = BehaviorSubject.createDefault(false);
    private final Subject<Boolean> isEditingBackground = BehaviorSubject.createDefault(false);
    private final Subject<Stroke> lineStrokeProvider = BehaviorSubject.createDefault(new BasicStroke(2));
    private final Subject<BasicBrush> eraserStrokeProvider = BehaviorSubject.createDefault(BasicBrush.SQUARE_12X12);
    private final Subject<BasicBrush> brushStrokeProvider = BehaviorSubject.createDefault(BasicBrush.ROUND_12X12);
    private final Subject<Paint> linePaintProvider = BehaviorSubject.createDefault(Color.black);
    private final Subject<Integer> fillPatternProvider = BehaviorSubject.createDefault(0);
    private final Subject<Integer> shapeSidesProvider = BehaviorSubject.createDefault(5);
    private final Subject<PaintTool> paintToolProvider = BehaviorSubject.createDefault(PaintToolBuilder.create(PaintToolType.ARROW).build());
    private final Subject<Boolean> drawMultiple = BehaviorSubject.createDefault(false);
    private final Subject<Boolean> drawCentered = BehaviorSubject.createDefault(false);
    private final Subject<Color> foregroundColorProvider = BehaviorSubject.createDefault(Color.BLACK);
    private final Subject<Color> backgroundColorProvider = BehaviorSubject.createDefault(Color.WHITE);

    // Properties that we provide the canvas
    private final Subject<Integer> gridSpacingProvider = BehaviorSubject.createDefault(1);

    private PaintToolType lastToolType;
    private Disposable selectedImageSubscription;
    private Disposable gridSpacingSubscription;

    public static ToolsContext getInstance() {
        return instance;
    }

    public void setGridSpacing(int spacing) {
        if (instance.gridSpacingSubscription == null) {
            instance.gridSpacingSubscription = instance.gridSpacingProvider.subscribe(integer -> HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas().setGridSpacing(integer));
        }
        gridSpacingProvider.onNext(spacing);
    }

    public int getGridSpacing() {
        return gridSpacingProvider.blockingFirst();
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
        paintToolProvider.blockingFirst().activate(canvas);
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
        tool.createSelection(new Rectangle(0, 0, HyperCard.getInstance().getActiveStackDisplayedCard().getWidth() - 1, HyperCard.getInstance().getActiveStackDisplayedCard().getHeight() - 1));
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

    public void setSelectedBrush(BasicBrush brush) {
        brushStrokeProvider.onNext(brush);
    }

    public BasicBrush getSelectedBrush() {
        return brushStrokeProvider.blockingFirst();
    }

    public Subject<BasicBrush> getSelectedBrushProvider() {
        return brushStrokeProvider;
    }

    public void toggleDrawCentered() {
        setDrawCentered(!isDrawCentered());
    }

    public void setDrawCentered(boolean drawCentered) {
        this.drawCentered.onNext(drawCentered);
    }

    public boolean isDrawCentered() {
        return this.drawCentered.blockingFirst();
    }

    public Subject<Boolean> getDrawCenteredProvider() {
        return drawCentered;
    }

    public void toggleDrawMultiple() {
        setDrawMultiple(!isDrawMultiple());
    }

    public void setDrawMultiple(boolean drawMultiple) {
        this.drawMultiple.onNext(drawMultiple);
    }

    public boolean isDrawMultiple() {
        return this.drawMultiple.blockingFirst();
    }

    public Subject<Boolean> getDrawMultipleProvider() {
        return drawMultiple;
    }

    public Subject<Optional<BufferedImage>> getSelectedImageProvider() {
        return selectedImageProvider;
    }

    public BufferedImage getSelectedImage() {
        return selectedImageProvider.blockingFirst().orElse(null);
    }

    public void setShapeSides(int shapeSides) {
        shapeSidesProvider.onNext(shapeSides);
    }

    public int getShapeSides() {
        return shapeSidesProvider.blockingFirst();
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

    public void toggleMagnifier() {
        if (getPaintTool().getToolType() == PaintToolType.MAGNIFIER) {
            HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas().setScale(1.0);
            forceToolSelection(ToolType.fromPaintTool(lastToolType), false);
        } else if (HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas().getScale() != 1.0) {
            HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas().setScale(1.0);
        } else {
            forceToolSelection(ToolType.MAGNIFIER, false);
        }
    }

    public void setLineWidth(int width) {
        lineStrokeProvider.onNext(new BasicStroke(width));
    }

    public int getLineWidth() {
        return Math.round((int) ((BasicStroke) lineStrokeProvider.blockingFirst()).getLineWidth());
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
        reactivateTool(HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas());
    }

    public void setIsEditingBackground(boolean isEditingBackground) {
        getPaintTool().deactivate();
        this.isEditingBackground.onNext(isEditingBackground);
        reactivateTool(HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas());
    }

    public boolean isShapesFilled() {
        return shapesFilled.blockingFirst();
    }

    public void setShapesFilled(boolean shapesFilled) {
        this.shapesFilled.onNext(shapesFilled);
    }

    public void toggleShapesFilled() {
        shapesFilled.onNext(!shapesFilled.blockingFirst());
        forceToolSelection(ToolType.fromPaintTool(paintToolProvider.blockingFirst().getToolType()), false);
    }

    public Subject<Boolean> getShapesFilledProvider() {
        return shapesFilled;
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
        HyperCard.getInstance().getActiveStackDisplayedCard().getCardModel().receiveMessage(SystemMessage.CHOOSE.messageName, new ExpressionList(null, toolType.getPrimaryToolName(), String.valueOf(toolType.getToolNumber())), (command, wasTrapped, err) -> {
            if (!wasTrapped) {
                forceToolSelection(toolType, false);
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

    private PaintTool activatePaintTool(PaintToolType selectedToolType, boolean keepSelection) {

        // Create and activate new paint tool
        PaintTool selectedTool = PaintToolBuilder.create(selectedToolType)
                .withStrokeSubject(getStrokeProviderForTool(selectedToolType))
                .withStrokePaintSubject(linePaintProvider)
                .withFillPaintSubject(fillPatternProvider.map(t -> isShapesFilled() || !selectedToolType.isShapeTool() ? Optional.of(HyperCardPatternFactory.create(t)) : Optional.empty()))
                .withFontSubject(FontContext.getInstance().getPaintFontProvider())
                .withShapeSidesSubject(shapeSidesProvider)
                .makeActiveOnCanvas(HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas())
                .build();

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
            setSelectedImage(((AbstractSelectionTool) selectedTool).getSelectedImageSubject());
        }

        // Setup "Draw Multiple" and "Draw Centered" options on tools that support them
        if (selectedTool instanceof AbstractBoundsTool) {
            ((AbstractBoundsTool) selectedTool).setDrawMultiple(drawMultiple);
            ((AbstractBoundsTool) selectedTool).setDrawCentered(drawCentered);
        }

        paintToolProvider.onNext(selectedTool);
        return selectedTool;
    }

    private Observable<Stroke> getStrokeProviderForTool(PaintToolType type) {
        switch (type) {
            case PAINTBRUSH:
            case AIRBRUSH:
                return brushStrokeProvider.map(value -> value.stroke);

            case ERASER:
                return eraserStrokeProvider.map(value -> value.stroke);

            default:
                return lineStrokeProvider;
        }
    }

    private void setSelectedImage(Observable<Optional<BufferedImage>> imageProvider) {
        if (selectedImageSubscription != null) {
            selectedImageSubscription.dispose();
        }
        selectedImageSubscription = imageProvider.subscribe(selectedImageProvider::onNext);
    }

}
