package com.defano.wyldcard.runtime.manager;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.button.ButtonPart;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.field.FieldPart;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.google.inject.Singleton;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import java.util.Optional;

/**
 * Management of the selection context/state of the field and button tools. Tracks which part on the displayed card
 * is presently selected, if any.
 */
@Singleton
public class WyldCardPartToolManager implements PartToolManager {

    private final Subject<Optional<ToolEditablePart>> selectedPart = BehaviorSubject.createDefault(Optional.empty());
    private final TextFontObserver fontObserver = new TextFontObserver();
    private final TextStyleObserver styleObserver = new TextStyleObserver();
    private final TextSizeObserver sizeObserver = new TextSizeObserver();
    private final TextAlignObserver alignObserver = new TextAlignObserver();

    @Override
    public void start() {
        // Deselect all parts when user changes tool mode
        WyldCard.getInstance().getToolsManager().getToolModeProvider().subscribe(toolMode -> deselectAllParts());

        // Change part font when user chooses a font/style from the menubar
        WyldCard.getInstance().getFontManager().getSelectedFontFamilyProvider().subscribe(fontObserver);
        WyldCard.getInstance().getFontManager().getSelectedFontSizeProvider().subscribe(sizeObserver);
        WyldCard.getInstance().getFontManager().getSelectedFontStyleProvider().subscribe(styleObserver);
        WyldCard.getInstance().getFontManager().getSelectedTextAlignProvider().subscribe(alignObserver);
    }

    @Override
    public void setSelectedPart(ToolEditablePart part) {
        Invoke.onDispatch(() -> {
            WyldCard.getInstance().getToolsManager().forceToolSelection(part.getEditTool(), false);

            deselectAllParts();
            part.setSelectedForEditing(new ExecutionContext(), true);
            selectedPart.onNext(Optional.of(part));
        });
    }

    @Override
    public void deselectAllParts() {
        Invoke.onDispatch(() -> {
            if (WyldCard.getInstance().getStackManager().getFocusedCard() != null) {
                for (ButtonPart thisButton : WyldCard.getInstance().getStackManager().getFocusedCard().getButtons()) {
                    thisButton.setSelectedForEditing(new ExecutionContext(), false);
                }

                for (FieldPart thisField : WyldCard.getInstance().getStackManager().getFocusedCard().getFields()) {
                    thisField.setSelectedForEditing(new ExecutionContext(), false);
                }
            }

            selectedPart.onNext(Optional.empty());
        });
    }

    @Override
    public void bringSelectedPartCloser() {
        selectedPart.blockingFirst().ifPresent(toolEditablePart -> toolEditablePart.bringCloser(new ExecutionContext()));
    }

    @Override
    public void sendSelectedPartFurther() {
        selectedPart.blockingFirst().ifPresent(toolEditablePart -> toolEditablePart.sendFurther(new ExecutionContext()));
    }

    @Override
    public void deleteSelectedPart() {
        Optional<ToolEditablePart> selectedPart = this.selectedPart.blockingFirst();
        selectedPart.ifPresent(part -> {
            WyldCard.getInstance().getStackManager().getFocusedCard().getPartModel().removePartModel(new ExecutionContext(), part.getPartModel());
            this.selectedPart.onNext(Optional.empty());
        });
    }

    @Override
    public Observable<Optional<ToolEditablePart>> getSelectedPartProvider() {
        return selectedPart;
    }

    @Override
    public ToolEditablePart getSelectedPart() {
        return selectedPart.blockingFirst().orElse(null);
    }

    private class TextStyleObserver implements Consumer<Value> {
        @Override
        public void accept(Value value) {
            Optional<ToolEditablePart> selectedPart = WyldCardPartToolManager.this.selectedPart.blockingFirst();
            selectedPart.ifPresent(part -> part.getPartModel().set(new ExecutionContext(), CardLayerPartModel.PROP_TEXTSTYLE, value));
        }
    }

    private class TextSizeObserver implements Consumer<Value> {
        @Override
        public void accept(Value value) {
            Optional<ToolEditablePart> selectedPart = WyldCardPartToolManager.this.selectedPart.blockingFirst();
            selectedPart.ifPresent(toolEditablePart -> toolEditablePart.getPartModel().set(new ExecutionContext(), CardLayerPartModel.PROP_TEXTSIZE, value));
        }
    }

    private class TextFontObserver implements Consumer<Value> {
        @Override
        public void accept(Value value) {
            Optional<ToolEditablePart> selectedPart = WyldCardPartToolManager.this.selectedPart.blockingFirst();
            selectedPart.ifPresent(toolEditablePart -> toolEditablePart.getPartModel().set(new ExecutionContext(), CardLayerPartModel.PROP_TEXTFONT, value));
        }
    }

    private class TextAlignObserver implements Consumer<Value> {
        @Override
        public void accept(Value value) {
            Optional<ToolEditablePart> selectedPart = WyldCardPartToolManager.this.selectedPart.blockingFirst();
            selectedPart.ifPresent(toolEditablePart -> toolEditablePart.getPartModel().set(new ExecutionContext(), CardLayerPartModel.PROP_TEXTALIGN, value));
        }
    }

}
