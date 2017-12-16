package com.defano.hypercard.runtime.context;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.FontContext;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.ButtonPart;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.field.FieldPart;
import com.defano.hypertalk.ast.common.Value;
import com.defano.jmonet.model.Provider;

import java.util.Observable;
import java.util.Observer;

/**
 * Management of the selection context/state of the field and button tools. Tracks which part on the displayed card
 * are presently selected, if any.
 */
public class PartToolContext {

    private final static PartToolContext instance = new PartToolContext();

    private final Provider<ToolEditablePart> selectedPart = new Provider<>(null);
    private final TextFontObserver fontObserver = new TextFontObserver();
    private final TextStyleObserver styleObserver = new TextStyleObserver();
    private final TextSizeObserver sizeObserver = new TextSizeObserver();

    private PartToolContext() {
        // Deselect all parts when user changes tool mode
        ToolsContext.getInstance().getToolModeProvider().addObserver((o, arg) -> deselectAllParts());

        // Change part font when user chooses a font/style from the menubar
        FontContext.getInstance().getSelectedFontFamilyProvider().addObserver(fontObserver);
        FontContext.getInstance().getSelectedFontSizeProvider().addObserver(sizeObserver);
        FontContext.getInstance().getSelectedFontStyleProvider().addObserver(styleObserver);
    }

    public static PartToolContext getInstance() {
        return instance;
    }

    public void setSelectedPart(ToolEditablePart part) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON || ToolsContext.getInstance().getToolMode() == ToolMode.FIELD) {
            deselectAllParts();
            selectedPart.set(part);
            part.setSelectedForEditing(true);
        }
    }

    public void deselectAllParts() {
        for (ButtonPart thisButton : HyperCard.getInstance().getDisplayedCard().getButtons()) {
            thisButton.setSelectedForEditing(false);
        }

        for (FieldPart thisField : HyperCard.getInstance().getDisplayedCard().getFields()) {
            thisField.setSelectedForEditing(false);
        }

        selectedPart.set(null);
    }

    public void bringSelectedPartCloser() {
        if (selectedPart.get() != null) {
            selectedPart.get().bringCloser();
        }
    }

    public void sendSelectedPartFurther() {
        if (selectedPart.get() != null) {
            selectedPart.get().sendFurther();
        }
    }

    public void deleteSelectedPart() {
        ToolEditablePart selectedPart = this.selectedPart.get();
        if (selectedPart != null) {
            HyperCard.getInstance().getDisplayedCard().getCardModel().removePartModel(selectedPart.getPartModel());
            this.selectedPart.set(null);
        }
    }

    public Provider<ToolEditablePart> getSelectedPartProvider() {
        return selectedPart;
    }

    private class TextStyleObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            ToolEditablePart selectedPart = PartToolContext.this.selectedPart.get();
            if (selectedPart != null) {
                selectedPart.getPartModel().setKnownProperty(CardLayerPartModel.PROP_TEXTSTYLE, new Value(arg));
            }
        }
    }

    private class TextSizeObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            ToolEditablePart selectedPart = PartToolContext.this.selectedPart.get();
            if (selectedPart != null) {
                selectedPart.getPartModel().setKnownProperty(CardLayerPartModel.PROP_TEXTSIZE, new Value(arg));
            }
        }
    }

    private class TextFontObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            ToolEditablePart selectedPart = PartToolContext.this.selectedPart.get();
            if (selectedPart != null) {
                selectedPart.getPartModel().setKnownProperty(CardLayerPartModel.PROP_TEXTFONT, new Value(arg));
            }
        }
    }

}
