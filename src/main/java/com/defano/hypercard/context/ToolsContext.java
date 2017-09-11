/*
 * ToolsContext
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.context;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.fonts.FontFactory;
import com.defano.hypercard.patterns.HyperCardPatternFactory;
import com.defano.jmonet.canvas.PaintCanvas;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.model.Provider;
import com.defano.jmonet.tools.SelectionTool;
import com.defano.jmonet.tools.base.AbstractBoundsTool;
import com.defano.jmonet.tools.base.AbstractSelectionTool;
import com.defano.jmonet.tools.base.PaintTool;
import com.defano.jmonet.tools.brushes.BasicBrush;
import com.defano.jmonet.tools.builder.PaintToolBuilder;
import com.defano.hypertalk.ast.common.Tool;

import java.awt.*;
import java.awt.image.BufferedImage;


public class ToolsContext {

    private final static ToolsContext instance = new ToolsContext();

    // Tool mode properties
    private final Provider<ToolMode> toolModeProvider = new Provider<>(ToolMode.BROWSE);

    // Properties that the tools provide to us...
    private final ImmutableProvider<BufferedImage> selectedImageProvider = new ImmutableProvider<>();

    // Properties that we provide the tools...
    private final Provider<Boolean> shapesFilled = new Provider<>(false);
    private final Provider<Boolean> isEditingBackground = new Provider<>(false);
    private final Provider<Stroke> lineStrokeProvider = new Provider<>(new BasicStroke(2));
    private final Provider<BasicBrush> eraserStrokeProvider = new Provider<>(BasicBrush.SQUARE_12X12);
    private final Provider<BasicBrush> brushStrokeProvider = new Provider<>(BasicBrush.ROUND_12X12);
    private final Provider<Paint> linePaintProvider = new Provider<>(Color.black);
    private final Provider<Integer> fillPatternProvider = new Provider<>(0);
    private final Provider<Integer> shapeSidesProvider = new Provider<>(5);
    private final Provider<PaintTool> paintToolProvider = new Provider<>(PaintToolBuilder.create(PaintToolType.ARROW).build());
    private final Provider<Boolean> drawMultiple = new Provider<>(false);
    private final Provider<Boolean> drawCentered = new Provider<>(false);
    private final Provider<Color> foregroundColorProvider = new Provider<>(Color.BLACK);
    private final Provider<Color> backgroundColorProvider = new Provider<>(Color.WHITE);

    // Properties that we provide the canvas
    private final Provider<Integer> gridSpacingProvider = new Provider<>(1);

    // Last font explicitly chosen by the user from the Font/Style menus
    private final Provider<Font> selectedFontProvider = new Provider<>(new Font("Ariel", Font.PLAIN, 24));

    // Last hilited font in text field or active button
    private final Provider<Font> hilitedFontProvider = new Provider<>(new Font("Ariel", Font.PLAIN, 24));

    private PaintToolType lastToolType;

    private ToolsContext() {
        selectedFontProvider.addObserver((o, arg) -> hilitedFontProvider.set((Font) arg));
        gridSpacingProvider.addObserver((o, arg) -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing((Integer) arg));
    }

    public static ToolsContext getInstance() {
        return instance;
    }

    public void setGridSpacing(int spacing) {
        gridSpacingProvider.set(spacing);
    }

    public Provider<Integer> getGridSpacingProvider() {
        return gridSpacingProvider;
    }

    public Provider<Stroke> getLineStrokeProvider() {
        return lineStrokeProvider;
    }

    public Provider<Paint> getLinePaintProvider() {
        return linePaintProvider;
    }

    public void setLinePaint(Paint p) {
        linePaintProvider.set(p);
    }

    public void reactivateTool(PaintCanvas canvas) {
        paintToolProvider.get().deactivate();
        paintToolProvider.get().activate(canvas);
    }

    public Provider<PaintTool> getPaintToolProvider() {
        return paintToolProvider;
    }

    public Provider<ToolMode> getToolModeProvider() {
        return toolModeProvider;
    }

    public ToolMode getToolMode() {
        return toolModeProvider.get();
    }

    public PaintTool getPaintTool() {
        return paintToolProvider.get();
    }

    public void selectAll() {
        SelectionTool tool = (SelectionTool) selectPaintTool(PaintToolType.SELECTION, false);
        tool.createSelection(new Rectangle(0, 0, HyperCard.getInstance().getCard().getWidth() - 1, HyperCard.getInstance().getCard().getHeight() - 1));
    }

    public void setForegroundColor(Color color) {
        foregroundColorProvider.set(color);
    }

    public void setBackgroundColor(Color color) {
        backgroundColorProvider.set(color);
    }

    public Color getForegroundColor() {
        return foregroundColorProvider.get();
    }

    public Color getBackgroundColor() {
        return backgroundColorProvider.get();
    }

    public Provider<Color> getBackgroundColorProvider() {
        return backgroundColorProvider;
    }

    public Provider<Color> getForegroundColorProvider() {
        return foregroundColorProvider;
    }

    public void setSelectedBrush(BasicBrush brush) {
        brushStrokeProvider.set(brush);
    }

    public Provider<BasicBrush> getSelectedBrushProvider() {
        return brushStrokeProvider;
    }

    public void toggleDrawCentered() {
        drawCentered.set(!drawCentered.get());
    }

    public Provider<Boolean> getDrawCenteredProvider() {
        return drawCentered;
    }

    public void toggleDrawMultiple() {
        drawMultiple.set(!drawMultiple.get());
    }

    public Provider<Boolean> getDrawMultipleProvider() {
        return drawMultiple;
    }

    public ImmutableProvider<BufferedImage> getSelectedImageProvider() {
        return selectedImageProvider;
    }

    public void setShapeSides(int shapeSides) {
        shapeSidesProvider.set(shapeSides);
    }

    public Provider<Integer> getShapeSidesProvider() {
        return shapeSidesProvider;
    }

    public Provider<Integer> getFillPatternProvider() {
        return fillPatternProvider;
    }

    public void toggleMagnifier() {
        if (getPaintTool().getToolType() == PaintToolType.MAGNIFIER) {
            HyperCard.getInstance().getCard().getCanvas().setScale(1.0);
            selectPaintTool(lastToolType, false);
        } else if (HyperCard.getInstance().getCard().getCanvas().getScale() != 1.0) {
            HyperCard.getInstance().getCard().getCanvas().setScale(1.0);
        }
        else {
            selectPaintTool(PaintToolType.MAGNIFIER, false);
        }
    }

    public void setVisibleFont(Font font) {
        hilitedFontProvider.set(font);
    }

    public void setFont(Font font) {
        if (font != null) {
            selectedFontProvider.set(font);
        }
    }

    public void setFontSize(int size) {
        String currentFamily = hilitedFontProvider.get().getFamily();
        int currentStyle = hilitedFontProvider.get().getStyle();

        selectedFontProvider.set(FontFactory.byNameStyleSize(currentFamily, currentStyle, size));
    }

    public void setFontStyle(int style) {
        String currentFamily = hilitedFontProvider.get().getFamily();
        int currentSize = hilitedFontProvider.get().getSize();

        selectedFontProvider.set(FontFactory.byNameStyleSize(currentFamily, style, currentSize));
    }

    public void setFontFamily(String fontName) {
        int currentSize = hilitedFontProvider.get().getSize();
        int currentStyle = hilitedFontProvider.get().getStyle();

        selectedFontProvider.set(FontFactory.byNameStyleSize(fontName, currentStyle, currentSize));
    }

    public Provider<Font> getSelectedFontProvider() {
        return selectedFontProvider;
    }

    public Provider<Font> getHilitedFontProvider() {
        return hilitedFontProvider;
    }

    public void setLineWidth(int width) {
        lineStrokeProvider.set(new BasicStroke(width));
    }

    public void setPattern(int patternId) {
            fillPatternProvider.set(patternId);
    }

    public boolean isEditingBackground() {
        return isEditingBackground.get();
    }

    public ImmutableProvider<Boolean> isEditingBackgroundProvider() {
        return ImmutableProvider.from(isEditingBackground);
    }

    public void toggleIsEditingBackground() {
        getPaintTool().deactivate();
        isEditingBackground.set(!isEditingBackground.get());
        reactivateTool(HyperCard.getInstance().getCard().getCanvas());
    }

    public void setIsEditingBackground(boolean isEditingBackground) {
        getPaintTool().deactivate();
        this.isEditingBackground.set(isEditingBackground);
        reactivateTool(HyperCard.getInstance().getCard().getCanvas());
    }

    public boolean isShapesFilled() {
        return shapesFilled.get();
    }

    public void toggleShapesFilled() {
        shapesFilled.set(!shapesFilled.get());
        selectPaintTool(paintToolProvider.get().getToolType(), false);
    }

    public Provider<Boolean> getShapesFilledProvider() {
        return shapesFilled;
    }

    public PaintTool setSelectedTransform(PaintToolType transformTool) {
        return selectPaintTool(transformTool, true);
    }

    public PaintTool setSelectedTool (Tool tool, boolean keepSelection) {
        switch (tool) {
            case BROWSE:
                toolModeProvider.set(ToolMode.BROWSE);
                return selectPaintTool(PaintToolType.ARROW, keepSelection);
            case OVAL:
                return selectPaintTool(PaintToolType.OVAL, keepSelection);
            case BRUSH:
                return selectPaintTool(PaintToolType.PAINTBRUSH, keepSelection);
            case PENCIL:
                return selectPaintTool(PaintToolType.PENCIL, keepSelection);
            case BUCKET:
                return selectPaintTool(PaintToolType.FILL, keepSelection);
            case POLYGON:
                return selectPaintTool(PaintToolType.POLYGON, keepSelection);
            case BUTTON:
                toolModeProvider.set(ToolMode.BUTTON);
                return selectPaintTool(PaintToolType.ARROW, keepSelection);
            case RECTANGLE:
                return selectPaintTool(PaintToolType.RECTANGLE, keepSelection);
            case CURVE:
                return selectPaintTool(PaintToolType.FREEFORM, keepSelection);
            case SHAPE:
                return selectPaintTool(PaintToolType.SHAPE, keepSelection);
            case ERASER:
                return selectPaintTool(PaintToolType.ERASER, keepSelection);
            case ROUNDRECT:
                return selectPaintTool(PaintToolType.ROUND_RECTANGLE, keepSelection);
            case FIELD:
                toolModeProvider.set(ToolMode.FIELD);
                return selectPaintTool(PaintToolType.ARROW, keepSelection);
            case SELECT:
                return selectPaintTool(PaintToolType.SELECTION, keepSelection);
            case LASSO:
                return selectPaintTool(PaintToolType.LASSO, keepSelection);
            case SPRAY:
                return selectPaintTool(PaintToolType.AIRBRUSH, keepSelection);
            case LINE:
                return selectPaintTool(PaintToolType.LINE, keepSelection);
            case TEXT:
                return selectPaintTool(PaintToolType.TEXT, keepSelection);
        }

        throw new IllegalStateException("Bug! Unimplemented tool.");
    }

    public Tool getSelectedTool() {
        return Tool.fromToolMode(getToolMode(), getPaintTool().getToolType());
    }

    private PaintTool selectPaintTool(PaintToolType selectedToolType, boolean keepSelection) {

        PaintTool selectedTool = PaintToolBuilder.create(selectedToolType)
                .withStrokeProvider(getStrokeProviderForTool(selectedToolType))
                .withStrokePaintProvider(linePaintProvider)
                .withFillPaintProvider(Provider.derivedFrom(fillPatternProvider, t -> isShapesFilled() || !selectedToolType.isShapeTool() ? HyperCardPatternFactory.create(t) : (Paint) null))
                .withFontProvider(selectedFontProvider)
                .withShapeSidesProvider(shapeSidesProvider)
                .makeActiveOnCanvas(HyperCard.getInstance().getCard().getCanvas())
                .build();

        if (keepSelection) {
            PaintTool lastTool = paintToolProvider.get();
            if (lastTool instanceof AbstractSelectionTool && selectedTool instanceof AbstractSelectionTool) {
                ((AbstractSelectionTool) lastTool).morphSelection((AbstractSelectionTool) selectedTool);
            }
        }

        lastToolType = paintToolProvider.get().getToolType();
        paintToolProvider.get().deactivate();

        if (selectedTool instanceof AbstractSelectionTool) {
            selectedImageProvider.setSource(((AbstractSelectionTool) selectedTool).getSelectedImageProvider());
        }

        if (selectedTool instanceof AbstractBoundsTool) {
            ((AbstractBoundsTool)selectedTool).setDrawMultiple(drawMultiple);
            ((AbstractBoundsTool)selectedTool).setDrawCentered(drawCentered);
        }

        if (selectedToolType != PaintToolType.ARROW) {
            toolModeProvider.set(ToolMode.PAINT);
        }

        paintToolProvider.set(selectedTool);
        return selectedTool;
    }

    private Provider<Stroke> getStrokeProviderForTool(PaintToolType type) {
        switch (type) {
            case PAINTBRUSH:
            case AIRBRUSH:
                return new Provider<>(brushStrokeProvider, value -> value.stroke);

            case ERASER:
                return new Provider<>(eraserStrokeProvider, value -> value.stroke);

            default:
                return lineStrokeProvider;
        }
    }

}
