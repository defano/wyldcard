package com.defano.wyldcard.search;

import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.util.FieldUtilities;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public class SearchResult {

    private final String searchedText;
    private final int cardIndex;
    private final Range range;
    private final FieldModel fieldModel;

    public SearchResult(String searchedText, Range range, FieldModel field, int cardIndex) {
        this.searchedText = searchedText;
        this.range = range;
        this.fieldModel = field;
        this.cardIndex = cardIndex;
    }

    public PartSpecifier getLocalPartSpecifier(ExecutionContext context) {
        return fieldModel.getPartSpecifier(context);
    }

    public String getFoundText() {
        return searchedText.substring(range.start, range.end);
    }

    public String getFoundField(ExecutionContext context) {
        return fieldModel.getHyperTalkAddress(context);
    }

    public String getFoundLine(ExecutionContext context) {
        int line = FieldUtilities.getLineOfChar(range.start, searchedText);
        return "line " + line + " of " + getFoundField(context);
    }

    public String getFoundChunk(ExecutionContext context) {
        return "char " + (range.start + 1) + " to " + range.end + " of " + getFoundField(context);
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public Range getRange() {
        return range;
    }
}
