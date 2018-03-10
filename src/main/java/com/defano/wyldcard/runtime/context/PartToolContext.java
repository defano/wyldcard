package com.defano.wyldcard.runtime.context;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.button.ButtonPart;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.field.FieldPart;
import com.defano.hypertalk.ast.model.Value;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import java.util.Optional;

/**
 * Management of the selection context/state of the field and button tools. Tracks which part on the displayed card
 * is presently selected, if any.
 */
public class PartToolContext {

    private final static PartToolContext instance = new PartToolContext();

    private final Subject<Optional<ToolEditablePart>> selectedPart = BehaviorSubject.createDefault(Optional.empty());
    private final TextFontObserver fontObserver = new TextFontObserver();
    private final TextStyleObserver styleObserver = new TextStyleObserver();
    private final TextSizeObserver sizeObserver = new TextSizeObserver();

    private PartToolContext() {
        // Deselect all parts when user changes tool mode
        ToolsContext.getInstance().getToolModeProvider().subscribe(toolMode -> deselectAllParts());

        // Change part font when user chooses a font/style from the menubar
        FontContext.getInstance().getSelectedFontFamilyProvider().subscribe(fontObserver);
        FontContext.getInstance().getSelectedFontSizeProvider().subscribe(sizeObserver);
        FontContext.getInstance().getSelectedFontStyleProvider().subscribe(styleObserver);
    }

    public static PartToolContext getInstance() {
        return instance;
    }

    public void setSelectedPart(ToolEditablePart part) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON || ToolsContext.getInstance().getToolMode() == ToolMode.FIELD) {
            deselectAllParts();
            part.setSelectedForEditing(true);
            selectedPart.onNext(Optional.of(part));
        }
    }

    public void deselectAllParts() {
        for (ButtonPart thisButton : WyldCard.getInstance().getActiveStackDisplayedCard().getButtons()) {
            thisButton.setSelectedForEditing(false);
        }

        for (FieldPart thisField : WyldCard.getInstance().getActiveStackDisplayedCard().getFields()) {
            thisField.setSelectedForEditing(false);
        }

        selectedPart.onNext(Optional.empty());
    }

    public void bringSelectedPartCloser() {
        selectedPart.blockingFirst().ifPresent(ToolEditablePart::bringCloser);
    }

    public void sendSelectedPartFurther() {
        selectedPart.blockingFirst().ifPresent(ToolEditablePart::sendFurther);
    }

    public void deleteSelectedPart() {
        Optional<ToolEditablePart> selectedPart = this.selectedPart.blockingFirst();
        selectedPart.ifPresent(part -> {
            WyldCard.getInstance().getActiveStackDisplayedCard().getCardModel().removePartModel(part.getPartModel());
            this.selectedPart.onNext(Optional.empty());
        });
    }

    public Observable<Optional<ToolEditablePart>> getSelectedPartProvider() {
        return selectedPart;
    }

    public ToolEditablePart getSelectedPart() {
        return selectedPart.blockingFirst().orElse(null);
    }

    private class TextStyleObserver implements Consumer<Value> {
        @Override
        public void accept(Value value) {
            Optional<ToolEditablePart> selectedPart = PartToolContext.this.selectedPart.blockingFirst();
            selectedPart.ifPresent(part -> part.getPartModel().setKnownProperty(CardLayerPartModel.PROP_TEXTSTYLE, value));
        }
    }

    private class TextSizeObserver implements Consumer<Value> {
        @Override
        public void accept(Value value) {
            Optional<ToolEditablePart> selectedPart = PartToolContext.this.selectedPart.blockingFirst();
            selectedPart.ifPresent(toolEditablePart -> toolEditablePart.getPartModel().setKnownProperty(CardLayerPartModel.PROP_TEXTSIZE, value));
        }
    }

    private class TextFontObserver implements Consumer<Value> {
        @Override
        public void accept(Value value) {
            Optional<ToolEditablePart> selectedPart = PartToolContext.this.selectedPart.blockingFirst();
            selectedPart.ifPresent(toolEditablePart -> toolEditablePart.getPartModel().setKnownProperty(CardLayerPartModel.PROP_TEXTFONT, value));
        }
    }

}
