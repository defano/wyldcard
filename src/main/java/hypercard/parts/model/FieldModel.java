package hypercard.parts.model;

import hypercard.parts.fields.FieldStyle;
import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Value;

import java.awt.*;

public class FieldModel extends AbstractPartModel {

    public static final String PROP_TEXT = "text";
    public static final String PROP_WRAPTEXT = "wraptext";
    public static final String PROP_VISIBLE = "visible";
    public static final String PROP_LOCKTEXT = "locktext";
    public static final String PROP_STYLE = "style";

    public FieldModel () {
        super(PartType.FIELD);
    }

    public static FieldModel newFieldModel(int id, Rectangle geometry) {
        FieldModel partModel = new FieldModel();

        partModel.defineProperty(PROP_SCRIPT, new Value(), false);
        partModel.defineProperty(PROP_TEXT, new Value(), false);
        partModel.defineProperty(PROP_ID, new Value(id), true);
        partModel.defineProperty(PROP_NAME, new Value("Text Field " + id), false);
        partModel.defineProperty(PROP_LEFT, new Value(geometry.x), false);
        partModel.defineProperty(PROP_TOP, new Value(geometry.y), false);
        partModel.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
        partModel.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
        partModel.defineProperty(PROP_WRAPTEXT, new Value(true), false);
        partModel.defineProperty(PROP_VISIBLE, new Value(true), false);
        partModel.defineProperty(PROP_LOCKTEXT, new Value(false), false);
        partModel.defineProperty(PROP_STYLE, new Value(FieldStyle.TRANSPARENT.getName()), false);

        return partModel;
    }
}
