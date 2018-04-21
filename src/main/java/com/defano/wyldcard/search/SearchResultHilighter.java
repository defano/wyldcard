package com.defano.wyldcard.search;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.field.FieldPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;

import javax.swing.*;

public interface SearchResultHilighter {

    default void clearSearchHighlights(ExecutionContext context) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (FieldPart parts : context.getCurrentCard().getFields()) {
                parts.getHyperCardTextPane().clearSearchHilights();
            }
        });
    }

    default void highlightSearchResult(ExecutionContext context, SearchResult result) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            // Clear existing highlights
            clearSearchHighlights(context);

            // Search result is on a different card; go there
            if (result.getCardIndex() != context.getCurrentCard().getCardModel().getCardIndexInStack()) {
                context.getActiveStack().goCard(context, result.getCardIndex(), null, false);
            }

            // Box the found text
            SwingUtilities.invokeLater(() -> {
                try {
                    FieldModel foundFieldModel = result.getLocalPartSpecifier().getOwner() == Owner.CARD ?
                            (FieldModel) context.getCurrentCard().getCardModel().findPart(context, result.getLocalPartSpecifier()) :
                            (FieldModel) context.getCurrentCard().getCardModel().getBackgroundModel().findPart(context, result.getLocalPartSpecifier());

                    FieldPart foundField = (FieldPart) context.getCurrentCard().getPart(context, foundFieldModel);

                    foundField.applySearchHilight(result.getRange());
                    foundField.getHyperCardTextPane().setCaretPosition(result.getRange().start);
                } catch (Exception e) {
                    throw new IllegalStateException("Bug! Search result refers to a bogus part.", e);
                }
            });
        });
    }
}
