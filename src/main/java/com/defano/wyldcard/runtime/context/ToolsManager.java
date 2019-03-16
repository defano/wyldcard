package com.defano.wyldcard.runtime.context;

import com.defano.hypertalk.ast.model.SystemMessage;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.jmonet.transform.dither.Ditherer;
import com.defano.jmonet.canvas.PaintCanvas;
import com.defano.jmonet.model.Interpolation;
import com.defano.jmonet.tools.base.Tool;
import com.defano.jmonet.tools.selection.TransformableCanvasSelection;
import com.defano.jmonet.tools.selection.TransformableImageSelection;
import com.defano.jmonet.tools.selection.TransformableSelection;
import com.defano.wyldcard.paint.PaintBrush;
import com.defano.wyldcard.paint.ToolMode;
import io.reactivex.Observable;
import io.reactivex.subjects.Subject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public interface ToolsManager {

    int getGridSpacing();

    void setGridSpacing(int spacing);

    Subject<Integer> getGridSpacingProvider();

    Subject<Stroke> getLineStrokeProvider();

    Subject<Paint> getLinePaintProvider();

    void setLinePaint(Paint p);

    void reactivateTool(PaintCanvas canvas);

    Subject<Tool> getPaintToolProvider();

    Subject<ToolMode> getToolModeProvider();

    ToolMode getToolMode();

    Tool getPaintTool();

    void selectAll();

    void select();

    Color getForegroundColor();

    void setForegroundColor(Color color);

    Color getBackgroundColor();

    void setBackgroundColor(Color color);

    Subject<Color> getBackgroundColorProvider();

    Subject<Color> getForegroundColorProvider();

    PaintBrush getSelectedBrush();

    void setSelectedBrush(PaintBrush brush);

    Subject<PaintBrush> getSelectedBrushProvider();

    void toggleDrawCentered();

    boolean isDrawCentered();

    void setDrawCentered(boolean drawCentered);

    Subject<Boolean> getDrawCenteredProvider();

    void toggleDrawMultiple();

    boolean isDrawMultiple();

    void setDrawMultiple(boolean drawMultiple);

    Subject<Boolean> getDrawMultipleProvider();

    Subject<Optional<BufferedImage>> getSelectedImageProvider();

    BufferedImage getSelectedImage();

    int getShapeSides();

    void setShapeSides(int shapeSides);

    Subject<Integer> getShapeSidesProvider();

    Subject<Integer> getFillPatternProvider();

    /**
     * Gets the index of selected pattern, counting from 0.
     * @return The fill pattern.
     */
    int getFillPattern();

    /**
     * Sets the index of the selected pattern, counting from 0. Valid values are between 0 and 39.
     * @param pattern The pattern index, 0..39
     */
    void setFillPattern(int pattern);

    double getIntensity();

    void setIntensity(double intensity);

    boolean getPathInterpolation();

    void setPathInterpolation(boolean enabled);

    Ditherer getDitherer();

    void setDitherer(Ditherer ditherer);

    Subject<Interpolation> getAntiAliasingProvider();

    void setAntiAliasingMode(Interpolation antiAliasingMode);

    Observable<Ditherer> getDithererProvider();

    void toggleMagnifier();

    int getLineWidth();

    void setLineWidth(int width);

    void setPattern(int patternId);

    boolean isEditingBackground();

    Subject<Boolean> isEditingBackgroundProvider();

    void toggleIsEditingBackground();

    void setIsEditingBackground(boolean isEditingBackground);

    boolean isShapesFilled();

    void setShapesFilled(boolean shapesFilled);

    void toggleShapesFilled();

    Subject<Boolean> getShapesFilledProvider();

    /**
     * Attempts to make the given tool active on the card. Sends a {@link SystemMessage#CHOOSE} message to the
     * current card. If any script in the message passing hierarchy traps the message, then the request is ignored
     * and the tool is not changed.
     * <p>
     * For programmatic tool changes that should not be trappable, see {@link #forceToolSelection(ToolType, boolean)}.
     *
     * @param toolType The requested tool selection.
     */
    void chooseTool(ToolType toolType);

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
    Tool forceToolSelection(ToolType tool, boolean keepSelection);

    ToolType getSelectedTool();

    /**
     * Gets an observable of whether an image selection exists using a tool that conforms to
     * {@link TransformableImageSelection}.
     *
     * @return The observable
     */
    Observable<Boolean> hasTransformableImageSelectionProvider();

    /**
     * Gets an observable of whether an image selection exists using a tool that conforms to
     * {@link TransformableSelection}.
     *
     * @return The observable
     */
    Observable<Boolean> hasTransformableSelectionProvider();

    /**
     * Gets an observable of whether an image selection exists using a tool that conforms to
     * {@link TransformableCanvasSelection}.
     *
     * @return The observable
     */
    Observable<Boolean> hasTransformableCanvasSelectionProvider();
}
