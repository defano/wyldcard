package com.defano.hypertalk.utils;

import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Chunk;
import com.defano.hypertalk.ast.model.CompositeChunk;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class ChunkPropertiesDelegate {

    private static final String PROP_TEXTSIZE = "textsize";
    private static final String PROP_TEXTFONT = "textfont";
    private static final String PROP_TEXTSTYLE = "textstyle";

    public static Value getProperty(ExecutionContext context, String property, Chunk chunk, PartSpecifier part) throws HtException {

        PartModel partModel = context.getPart(part);

        if (!(partModel instanceof FieldModel)) {
            throw new HtSemanticException("Can't get that property from this part.");
        }

        FieldModel fieldModel = (FieldModel) partModel;
        Range range = (chunk instanceof CompositeChunk) ?
                RangeUtils.getRange(context, partModel.getValue(context).stringValue(), (CompositeChunk) chunk) :
                RangeUtils.getRange(context, partModel.getValue(context).stringValue(), chunk);

        switch (property.toLowerCase()) {
            case PROP_TEXTSIZE:
                return fieldModel.getTextFontSize(context, range.start, range.length());
            case PROP_TEXTFONT:
                return fieldModel.getTextFontFamily(context, range.start, range.length());
            case PROP_TEXTSTYLE:
                return fieldModel.getTextFontStyle(context, range.start, range.length());
            default:
                throw new HtSemanticException("Can't get that property from this part.");
        }
    }

    public static void setProperty(ExecutionContext context, String property, Value value, Chunk chunk, PartSpecifier part) throws HtException {

        PartModel partModel = context.getPart(part);

        if (!(partModel instanceof FieldModel)) {
            throw new HtSemanticException("Can't set that property on this part.");
        }

        FieldModel fieldModel = (FieldModel) partModel;
        Range range = (chunk instanceof CompositeChunk) ?
                RangeUtils.getRange(context, partModel.getValue(context).stringValue(), (CompositeChunk) chunk) :
                RangeUtils.getRange(context, partModel.getValue(context).stringValue(), chunk);

        switch (property.toLowerCase()) {
            case PROP_TEXTSIZE:
                if (!value.isInteger()) {
                    throw new HtSemanticException("The value '" + value.stringValue() + "' is not a valid font size.");
                }
                fieldModel.setTextFontSize(context, range.start, range.length(), value);
                break;
            case PROP_TEXTFONT:
                fieldModel.setTextFontFamily(context, range.start, range.length(), value);
                break;
            case PROP_TEXTSTYLE:
                fieldModel.setTextFontStyle(context, range.start, range.length(), value);
                break;
            default:
                throw new HtSemanticException("Can't set that property on this part.");
        }
    }

}
