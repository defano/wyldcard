package com.defano.hypertalk.utils;

import com.defano.hypercard.parts.field.FieldComponent;
import com.defano.hypercard.parts.field.FieldPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class ChunkPropertiesDelegate {

    private static final String PROP_TEXTSIZE = "textsize";
    private static final String PROP_TEXTFONT = "textfont";
    private static final String PROP_TEXTSTYLE = "textstyle";

    public static Value getProperty(String property, Chunk chunk, PartSpecifier part) throws HtException {

        PartModel partModel = ExecutionContext.getContext().get(part);

        if (partModel.getType() != PartType.FIELD) {
            throw new HtSemanticException("Can't get that property from this part.");
        }

        Range range = RangeUtils.getRange(partModel.getValue().stringValue(), chunk);
        FieldPart field = (FieldPart) ExecutionContext.getContext().getCurrentCard().getPart(partModel);

        switch (property.toLowerCase()) {
            case PROP_TEXTSIZE:
                return ((FieldComponent) field.getComponent()).getTextFontSize(range.start, range.length());
            case PROP_TEXTFONT:
                return ((FieldComponent) field.getComponent()).getTextFontFamily(range.start, range.length());
            case PROP_TEXTSTYLE:
                return ((FieldComponent) field.getComponent()).getTextFontStyle(range.start, range.length());
            default:
                throw new HtSemanticException("Can't get that property from this part.");
        }
    }

    public static void setProperty(String property, Value value, Chunk chunk, PartSpecifier part) throws HtException {

        PartModel partModel = ExecutionContext.getContext().get(part);

        if (partModel.getType() != PartType.FIELD) {
            throw new HtSemanticException("Can't set that property on this part.");
        }

        Range range = RangeUtils.getRange(partModel.getValue().stringValue(), chunk);
        FieldPart field = (FieldPart) ExecutionContext.getContext().getCurrentCard().getPart(partModel);

        switch (property.toLowerCase()) {
            case PROP_TEXTSIZE:
                if (!value.isInteger()) {
                    throw new HtSemanticException("The value '" + value.stringValue() + "' is not a valid font size.");
                }
                ((FieldComponent) field.getComponent()).setTextFontSize(range.start, range.length(), value);
                break;
            case PROP_TEXTFONT:
                ((FieldComponent) field.getComponent()).setTextFontFamily(range.start, range.length(), value);
                break;
            case PROP_TEXTSTYLE:
                ((FieldComponent) field.getComponent()).setTextFontStyle(range.start, range.length(), value);
                break;
            default:
                throw new HtSemanticException("Can't set that property on this part.");
        }
    }

}
