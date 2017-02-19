/*
 * Tool
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.common;

import com.defano.jmonet.model.PaintToolType;
import com.google.common.collect.Lists;
import com.defano.hypercard.context.ToolMode;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;

public enum Tool {

    BROWSE(1, "browse"),
    OVAL(14, "oval"),
    BRUSH(7, "brush"),
    PENCIL(6, "pencil"),
    BUCKET(13, "bucket"),
    POLYGON(18, "poly", "polygon"),
    BUTTON(2, "button"),
    RECTANGLE(11, "rect", "rectangle"),
    CURVE(15, "curve"),
    SHAPE(17, "reg poly", "regular poly", "reg polygon", "regular polygon"),
    ERASER(8, "eraser"),
    ROUNDRECT(12, "round rect", "round rectangle"),
    FIELD(3, "field"),
    SELECT(4, "select"),
    LASSO(5, "lasso"),
    SPRAY(10, "spray", "spray can"),
    LINE(9, "line"),
    TEXT(16, "text");

    private final int toolNumber;
    private final List<String> toolNames;

    Tool(int toolNumber, String... names) {
        this.toolNumber = toolNumber;
        this.toolNames = Lists.newArrayList(names);
    }

    public static Tool fromToolMode(ToolMode mode, PaintToolType paintTool) {
        switch (mode) {
            case BROWSE: return BROWSE;
            case FIELD: return FIELD;
            case PAINT: return fromPaintTool(paintTool);
        }

        throw new IllegalArgumentException("Bug! Unimplemented tool mode: " + mode);
    }

    public static Tool fromPaintTool(PaintToolType paintTool) {
        switch (paintTool) {
            case ARROW: return BROWSE;
            case PENCIL: return PENCIL;
            case RECTANGLE: return RECTANGLE;
            case ROUND_RECTANGLE: return ROUNDRECT;
            case OVAL: return OVAL;
            case PAINTBRUSH: return BRUSH;
            case ERASER: return ERASER;
            case LINE: return LINE;
            case POLYGON: return POLYGON;
            case SHAPE: return SHAPE;
            case FREEFORM: return CURVE;
            case SELECTION: return SELECT;
            case LASSO: return LASSO;
            case TEXT: return TEXT;
            case FILL: return BUCKET;
            case AIRBRUSH: return SPRAY;
            case CURVE: return CURVE;
            case SLANT: return SELECT;
            case ROTATE: return SELECT;
            case SCALE: return SELECT;
            case MAGNIFIER: return SELECT;
        }

        throw new IllegalArgumentException("Bug! Unimplemented paint tool: " + paintTool);
    }

    public static Tool byName(String toolName) throws HtSemanticException {
        for (Tool thisTool : Tool.values()) {
            if (thisTool.toolNames.contains(toolName.toLowerCase())) {
                return thisTool;
            }
        }

        throw new HtSemanticException("No such tool named " + toolName);
    }

    public static Tool byNumber(int toolNumber) throws HtSemanticException {
        for (Tool thisTool : Tool.values()) {
            if (thisTool.toolNumber == toolNumber) {
                return thisTool;
            }
        }

        throw new HtSemanticException("No tool number " + toolNumber +". Tools are numbered 1-18.");
    }
}
