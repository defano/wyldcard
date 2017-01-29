package hypercard.context;

import com.defano.jmonet.model.Provider;
import hypercard.HyperCard;
import hypercard.parts.ButtonPart;
import hypercard.parts.FieldPart;
import hypercard.parts.ToolEditablePart;

public class PartToolContext {

    private final static PartToolContext instance = new PartToolContext();

    private Provider<ToolEditablePart> selectedPart = new Provider<>(null);

    private PartToolContext() {
        ToolsContext.getInstance().getToolModeProvider().addObserver((o, arg) -> deselectAllParts());
    }

    public static PartToolContext getInstance() {
        return instance;
    }

    public void setSelectedPart(ToolEditablePart button) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON || ToolsContext.getInstance().getToolMode() == ToolMode.FIELD) {
            deselectAllParts();
            selectedPart.set(button);
            selectedPart.get().setBeingEdited(true);
        }
    }

    public void deselectAllParts() {
        for (ButtonPart thisButton : HyperCard.getInstance().getCard().getButtons()) {
            thisButton.setBeingEdited(false);
        }

        for (FieldPart thisField : HyperCard.getInstance().getCard().getFields()) {
            thisField.setBeingEdited(false);
        }

        selectedPart.set(null);
    }

    public void bringCloser() {
        if (selectedPart.get() != null) {
            selectedPart.get().bringCloser();
        }
    }

    public void sendFurther() {
        if (selectedPart.get() != null) {
            selectedPart.get().sendFurther();
        }
    }

    public Provider<ToolEditablePart> getSelectedPartProvider() {
        return selectedPart;
    }
}
