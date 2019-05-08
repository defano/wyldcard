package com.defano.wyldcard.runtime.manager;

import com.defano.hypertalk.ast.model.ToolType;
import com.defano.jmonet.canvas.PaintCanvas;
import com.defano.jmonet.model.Interpolation;
import com.defano.jmonet.tools.base.Tool;
import com.defano.jmonet.tools.selection.TransformableCanvasSelection;
import com.defano.jmonet.tools.selection.TransformableImageSelection;
import com.defano.jmonet.tools.selection.TransformableSelection;
import com.defano.jmonet.transform.dither.Ditherer;
import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.paint.PaintBrush;
import com.defano.wyldcard.paint.ToolMode;
import io.reactivex.Observable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * Manages the state of paint subsystem and all related properties.
 */
public interface PaintManager {

    /**
     * Activates the marquee (selection) tool and creates a selection from the top-left corner of the card to the
     * bottom-right corner.
     */
    void selectAll();

    /**
     * Activates the marquee selection tool and creates a selection of the last committed paint change.
     */
    void select();

    /**
     * Deactivates the current tool and re-activates it on the given canvas. This method makes the given canvas the
     * active, painted upon canvas.
     *
     * @param canvas The canvas to make active for the current paint tool.
     */
    void reactivateTool(PaintCanvas canvas);

    /**
     * Gets the current grid spacing, in pixels. When painting, the coordinate on the canvas that tools operate on is
     * rounded to this nearest multiple.
     *
     * @return The grid spacing, in pixels
     */
    int getGridSpacing();

    /**
     * Sets the current grid spacing, in pixels. When painting, the coordinate on the canvas that tools operate on is
     * rounded to this nearest multiple.
     *
     * @param spacing The grid spacing, in pixels
     */
    void setGridSpacing(int spacing);

    /**
     * Gets a ReactiveX observable indicating changes to the grid spacing attribute.
     *
     * @return An observable of grid spacing changes.
     */
    Observable<Integer> getGridSpacingProvider();

    /**
     * Gets a ReactiveX observable indicating changes to the line stroke attribute.
     *
     * @return An observable of line stroke changes.
     */
    Observable<Stroke> getLineStrokeProvider();

    /**
     * Gets a ReactiveX observable indicating changes to the line paint attribute.
     *
     * @return An observable of line paint changes.
     */
    Observable<Paint> getLinePaintProvider();

    /**
     * Specifies the paint used to render lines and shape outlines.
     *
     * @param p The paint used for lines.
     */
    void setLinePaint(Paint p);

    /**
     * Gets a ReactiveX observable indicating changes to the current paint tool.
     *
     * @return An observable of paint tool changes.
     */
    Observable<Tool> getPaintToolProvider();

    /**
     * Gets a ReactiveX observable indicating changes to the current tool mode.
     *
     * @return An observable of tool mode changes.
     */
    Observable<ToolMode> getToolModeProvider();

    /**
     * Gets the active tool mode.
     *
     * @return The active tool mode.
     */
    ToolMode getToolMode();

    /**
     * Gets the active paint tool.
     *
     * @return The active paint tool.
     */
    Tool getPaintTool();

    /**
     * Gets the current paint foreground color.
     *
     * @return The foreground color.
     */
    Color getForegroundColor();

    /**
     * Sets the current paint foreground color.
     *
     * @param color The foreground color.
     */
    void setForegroundColor(Color color);

    /**
     * Gets a ReactiveX observable of the foreground color attribute.
     *
     * @return An observable of the foreground color.
     */
    Observable<Color> getForegroundColorProvider();

    /**
     * Gets the current paint background color.
     *
     * @return The background color.
     */
    Color getBackgroundColor();

    /**
     * Sets the current paint background color.
     *
     * @param color The paint background color.
     */
    void setBackgroundColor(Color color);

    /**
     * Gets a ReactiveX observable of the background color attribute.
     *
     * @return An observable of the background color.
     */
    Observable<Color> getBackgroundColorProvider();

    /**
     * Gets the current paint brush.
     *
     * @return The active paint brush.
     */
    PaintBrush getSelectedBrush();

    /**
     * Sets the current paint brush.
     *
     * @param brush The paint brush.
     */
    void setSelectedBrush(PaintBrush brush);

    /**
     * Gets a ReactiveX observable of the current paint brush.
     *
     * @return An observable of the paint brush.
     */
    Observable<PaintBrush> getSelectedBrushProvider();

    /**
     * Toggles the state of the draw centered attribute (affects shape tools).
     */
    void toggleDrawCentered();

    /**
     * Determines whether paint tools draw centered (affects shape tools).
     *
     * @return True if draw centered is enabled, false otherwise.
     */
    boolean isDrawCentered();

    /**
     * Sets whether paint tools draw centered (affects shape tools).
     *
     * @param drawCentered True to draw centered, false to draw from corner.
     */
    void setDrawCentered(boolean drawCentered);

    /**
     * A ReactiveX observable of the draw centered attribute.
     *
     * @return An observable of the draw centered attribute.
     */
    Observable<Boolean> getDrawCenteredProvider();

    /**
     * Toggle the state of the draw multiple attribute
     */
    void toggleDrawMultiple();

    /**
     * Determines whether paint tools draw multiple.
     *
     * @return True if draw multiple is enabled
     */
    boolean isDrawMultiple();

    /**
     * Sets whether paint tools draw multiple.
     *
     * @param drawMultiple True if paint tools draw multiple.
     */
    void setDrawMultiple(boolean drawMultiple);

    /**
     * Gets a ReactiveX observable of changes to the draw multiple attribute.
     *
     * @return An observable of changes to the draw multiple attribute.
     */
    Observable<Boolean> getDrawMultipleProvider();

    /**
     * Gets the image currently selected by the active selection tool, or null, if there is no selection.
     *
     * @return The selected image or null if there is not selected image.
     */
    BufferedImage getSelectedImage();

    /**
     * Gets a ReactiveX observable of changes to the selected image.
     *
     * @return An observable of changes to the selected image.
     */
    Observable<Optional<BufferedImage>> getSelectedImageProvider();

    /**
     * Gets the number of sides drawn by the shape tool.
     *
     * @return The number of sides of the shape drawn by the shape tool.
     */
    int getShapeSides();

    /**
     * Sets the number of sides drawn by the shape tool.
     *
     * @param shapeSides The number of sides drawn by the shape tool.
     */
    void setShapeSides(int shapeSides);

    /**
     * Gets a ReactiveX observable of changes to the number of sides drawn by the shape tool.
     *
     * @return An observable of changes to the number of sides drawn by the shape tool.
     */
    Observable<Integer> getShapeSidesProvider();

    /**
     * Gets a ReactiveX observable of changes to the fill pattern.
     *
     * @return An observable of changes to the fill pattern.
     */
    Observable<Integer> getFillPatternProvider();

    /**
     * Gets the index of selected pattern, counting from 0.
     *
     * @return The fill pattern.
     */
    int getFillPattern();

    /**
     * Sets the index of the selected pattern, counting from 0. Valid values are between 0 and 39.
     *
     * @param pattern The pattern index, 0..39
     */
    void setFillPattern(int pattern);

    /**
     * Gets the spray intensity of the spray can tool, a value between 0.0 and 1.0 where higher values represent higher
     * spray intensity.
     *
     * @return The spray can spray intensity.
     */
    double getIntensity();

    /**
     * Sets the spray intensity of the spray can tool, a value between 0.0 and 1.0 where higher values represent higher
     * spray intensity.
     *
     * @param intensity The spray can spray intensity
     */
    void setIntensity(double intensity);

    /**
     * Determines whether path interpolation is enabled for the spray can tool. When enabled the tool paints a smooth
     * path on the canvas; when disabled, produces a more spotty effect.
     *
     * @return True if path interpolation is enabled.
     */
    boolean isSmoothSpray();

    /**
     * Sets whether path interpolation is enabled for the spray can tool. When enabled the tool paints a smooth
     * * path on the canvas; when disabled, produces a more spotty effect.
     *
     * @param enabled True to enable smooth spray (path interpolation); false to disable
     */
    void setSmoothSpray(boolean enabled);

    /**
     * Gets the dithering algorithm used when reducing color depth.
     *
     * @return The dithering algorithm
     */
    Ditherer getDitherer();

    /**
     * Sets the dithering algorithm used when reducing color depth.
     *
     * @param ditherer The dithering algorithm to use when reducing color depth.
     */
    void setDitherer(Ditherer ditherer);

    /**
     * Gets a ReactiveX observable of changes to the dithering algorithm.
     *
     * @return An observable of dithering algorithm changes.
     */
    Observable<Ditherer> getDithererProvider();

    /**
     * Sets the antialiasing interpolation mode.
     *
     * @param antiAliasingMode The antialiasing interpolation mode.
     */
    void setAntiAliasingMode(Interpolation antiAliasingMode);

    /**
     * Gets a ReactiveX observable of changes to the antialiasing mode.
     *
     * @return An observable of changes to the antialiasing mode.
     */
    Observable<Interpolation> getAntiAliasingProvider();

    /**
     * Gets the width, in pixels, of lines drawn by the paint tools.
     *
     * @return The line width, in pixels.
     */
    int getLineWidth();

    /**
     * Sets the width of lines, in pixels, drawn by the paint tools.
     *
     * @param width The line width.
     */
    void setLineWidth(int width);

    /**
     * Sets the pattern id; a value between 0 and 39 describing the active fill pattern.
     *
     * @param patternId The pattern id, a value between 0 and 39.
     */
    void setPattern(int patternId);

    /**
     * Determines if the card background layer is currently being edited.
     *
     * @return True when editing the background, false otherwise.
     */
    boolean isEditingBackground();

    /**
     * Gets a ReactiveX observable of changes to editing the background layer.
     *
     * @return An observable of changes to editing the background.
     */
    Observable<Boolean> isEditingBackgroundProvider();

    /**
     * Toggles the state of the editing background attribute.
     */
    void toggleIsEditingBackground();

    /**
     * Sets whether the card background layer is being edited.
     *
     * @param isEditingBackground True to edit the card background, false otherwise.
     */
    void setIsEditingBackground(boolean isEditingBackground);

    /**
     * Determines if shapes are drawn filled.
     *
     * @return True when shapes are being drawn filled, false otherwise.
     */
    boolean isShapesFilled();

    /**
     * Sets whether shapes are being drawn filled.
     *
     * @param shapesFilled True to draw shapes filled, false to draw shapes outlined.
     */
    void setShapesFilled(boolean shapesFilled);

    /**
     * Toggles the state of the shapes filled attribute.
     */
    void toggleShapesFilled();

    /**
     * Gets a ReactiveX observable of changes to the shapes filled attribute.
     *
     * @return An observable of changes to the shapes filled attribute.
     */
    Observable<Boolean> getShapesFilledProvider();

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

    /**
     * Gets the type of tool currently selected.
     *
     * @return The type of tool currently selected.
     */
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
