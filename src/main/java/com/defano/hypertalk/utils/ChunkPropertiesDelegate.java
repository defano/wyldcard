package com.defano.hypertalk.utils;

import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Chunk;
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

        if (!(partModel instanceof FieldModel)) {
            throw new HtSemanticException("Can't get that property from this part.");
        }

        FieldModel fieldModel = (FieldModel) partModel;
        Range range = RangeUtils.getRange(partModel.getValue().stringValue(), chunk);

        switch (property.toLowerCase()) {
            case PROP_TEXTSIZE:
                return fieldModel.getTextFontSize(range.start, range.length());
            case PROP_TEXTFONT:
                return fieldModel.getTextFontFamily(range.start, range.length());
            case PROP_TEXTSTYLE:
                return fieldModel.getTextFontStyle(range.start, range.length());
            default:
                throw new HtSemanticException("Can't get that property from this part.");
        }
    }

    public static void setProperty(String property, Value value, Chunk chunk, PartSpecifier part) throws HtException {

        PartModel partModel = ExecutionContext.getContext().get(part);

        if (!(partModel instanceof FieldModel)) {
            throw new HtSemanticException("Can't set that property on this part.");
        }

        FieldModel fieldModel = (FieldModel) partModel;
        Range range = RangeUtils.getRange(partModel.getValue().stringValue(), chunk);

        switch (property.toLowerCase()) {
            case PROP_TEXTSIZE:
                if (!value.isInteger()) {
                    throw new HtSemanticException("The value '" + value.stringValue() + "' is not a valid font size.");
                }
                fieldModel.setTextFontSize(range.start, range.length(), value);
                break;
            case PROP_TEXTFONT:
                fieldModel.setTextFontFamily(range.start, range.length(), value);
                break;
            case PROP_TEXTSTYLE:
                fieldModel.setTextFontStyle(range.start, range.length(), value);
                break;
            default:
                throw new HtSemanticException("Can't set that property on this part.");
        }
    }

}
