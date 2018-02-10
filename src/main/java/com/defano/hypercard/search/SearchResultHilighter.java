package com.defano.hypercard.search;

import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.field.FieldPart;
import com.defano.hypercard.runtime.context.ExecutionContext;

public interface SearchResultHilighter {

    default void clearSearchHighlights() {
        for (FieldPart parts : ExecutionContext.getContext().getCurrentCard().getFields()) {
            parts.getHyperCardTextPane().clearSearchHilights();
        }
    }

    default void highlightSearchResult(SearchResult result) {

        clearSearchHighlights();

        // Search result is on a different card; go there
        if (result.getCardIndex() != ExecutionContext.getContext().getCurrentCard().getCardModel().getCardIndexInStack()) {
            ExecutionContext.getContext().getCurrentStack().goCard(result.getCardIndex(), null, true);
        }

        // Box the found text
        try {
            FieldModel foundFieldModel = (FieldModel) ExecutionContext.getContext().getCurrentCard().getCardModel().findPart(result.getLocalPartSpecifier());
            FieldPart foundField = (FieldPart) ExecutionContext.getContext().getCurrentCard().getPart(foundFieldModel);

            foundField.applySearchHilight(result.getRange());
            foundField.getHyperCardTextPane().setCaretPosition(result.getRange().start);
        } catch (Exception e) {
            throw new IllegalStateException("Bug! Search result refers to a bogus part.", e);
        }
    }
}
