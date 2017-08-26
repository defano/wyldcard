/*
 * ToolsContext
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.context;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.patterns.HyperCardPatternFactory;
import com.defano.jmonet.canvas.PaintCanvas;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.model.Provider;
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

    // Last font explicitly chosen by the user from the Font/Style menus
    private final Provider<Font> selectedFontProvider = new Provider<>(new Font("Ariel", Font.PLAIN, 24));

    // Last hilited font in text field or active button
    private final Provider<Font> hilitedFontProvider = new Provider<>(new Font("Ariel", Font.PLAIN, 24));

    private PaintToolType lastToolType;

    private ToolsContext() {
        selectedFontProvider.addObserver((o, arg) -> hilitedFontProvider.set((Font) arg));
    }

    public static ToolsContext getInstance() {
        return instance;
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

    public void setToolMode(ToolMode mode) {
        if (mode != ToolMode.PAINT) {
            selectPaintTool(PaintToolType.ARROW, false);
        }

        toolModeProvider.set(mode);
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

    public PaintTool selectPaintTool(PaintToolType selectedToolType, boolean keepSelection) {

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
            setToolMode(ToolMode.PAINT);
        }

        paintToolProvider.set(selectedTool);
        return selectedTool;
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
        String currentFamily = selectedFontProvider.get().getFamily();
        int currentStyle = selectedFontProvider.get().getStyle();

        selectedFontProvider.set(new Font(currentFamily, currentStyle, size));
    }

    public void setFontStyle(int style) {
        String currentFamily = selectedFontProvider.get().getFamily();
        int currentSize = selectedFontProvider.get().getSize();

        selectedFontProvider.set(new Font(currentFamily, style, currentSize));
    }

    public void setFontFamily(String fontName) {
        int currentSize = selectedFontProvider.get().getSize();
        int currentStyle = selectedFontProvider.get().getStyle();

        selectedFontProvider.set(new Font(fontName, currentStyle, currentSize));
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

    public void setSelectedTool (Tool tool) {
        switch (tool) {
            case BROWSE:
                setToolMode(ToolMode.BROWSE);
                break;
            case OVAL:
                selectPaintTool(PaintToolType.OVAL, false);
                break;
            case BRUSH:
                selectPaintTool(PaintToolType.PAINTBRUSH, false);
                break;
            case PENCIL:
                selectPaintTool(PaintToolType.PENCIL, false);
                break;
            case BUCKET:
                selectPaintTool(PaintToolType.FILL, false);
                break;
            case POLYGON:
                selectPaintTool(PaintToolType.POLYGON, false);
                break;
            case BUTTON:
                toolModeProvider.set(ToolMode.BUTTON);
                break;
            case RECTANGLE:
                selectPaintTool(PaintToolType.RECTANGLE, false);
                break;
            case CURVE:
                selectPaintTool(PaintToolType.FREEFORM, false);
                break;
            case SHAPE:
                selectPaintTool(PaintToolType.SHAPE, false);
                break;
            case ERASER:
                selectPaintTool(PaintToolType.ERASER, false);
                break;
            case ROUNDRECT:
                selectPaintTool(PaintToolType.ROUND_RECTANGLE, false);
                break;
            case FIELD:
                toolModeProvider.set(ToolMode.FIELD);
                break;
            case SELECT:
                selectPaintTool(PaintToolType.SELECTION, false);
                break;
            case LASSO:
                selectPaintTool(PaintToolType.LASSO, false);
                break;
            case SPRAY:
                selectPaintTool(PaintToolType.AIRBRUSH, false);
                break;
            case LINE:
                selectPaintTool(PaintToolType.LINE, false);
                break;
            case TEXT:
                selectPaintTool(PaintToolType.TEXT, false);
                break;
        }
    }

    public Tool getSelectedTool() {
        return Tool.fromToolMode(getToolMode(), getPaintTool().getToolType());
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
