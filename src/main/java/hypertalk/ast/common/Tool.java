package hypertalk.ast.common;

import com.google.common.collect.Lists;
import hypertalk.exception.HtSemanticException;

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
