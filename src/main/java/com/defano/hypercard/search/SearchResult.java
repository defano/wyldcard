package com.defano.hypercard.search;

import com.defano.hypercard.parts.util.FieldUtilities;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.utils.Range;

public class SearchResult {

    private final String searchedText;
    private final int cardIndex;
    private final Range range;
    private final int fieldId;
    private final Owner fieldLayer;

    public SearchResult(String searchedText, Range range, Owner fieldLayer, int fieldId, int cardIndex) {
        this.searchedText = searchedText;
        this.range = range;
        this.fieldId = fieldId;
        this.cardIndex = cardIndex;
        this.fieldLayer = fieldLayer;
    }

    public PartSpecifier getLocalPartSpecifier() {
        return new PartIdSpecifier(fieldLayer, PartType.FIELD, fieldId);
    }

    public String getFoundText() {
        return searchedText.substring(range.start, range.end);
    }

    public String getFoundField() {
        return new PartIdSpecifier(fieldLayer, PartType.FIELD, fieldId).getHyperTalkIdentifier();
    }

    public String getFoundLine() {
        int line = FieldUtilities.getLineOfChar(range.start, searchedText);
        return "line " + line + " of " + getFoundField();
    }

    public String getFoundChunk() {
        return "char " + (range.start + 1) + " to " + range.end + " of " + getFoundField();
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public Range getRange() {
        return range;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "cardIndex=" + cardIndex +
                ", range=" + range +
                ", fieldId=" + fieldId +
                ", fieldLayer=" + fieldLayer +
                '}';
    }
}
