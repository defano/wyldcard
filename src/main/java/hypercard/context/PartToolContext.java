package hypercard.context;

import hypercard.HyperCard;
import hypercard.parts.ButtonPart;
import hypercard.parts.FieldPart;
import hypercard.parts.ToolEditablePart;

import java.util.Observable;
import java.util.Observer;

public class PartToolContext {

    private final static PartToolContext instance = new PartToolContext();

    private ToolEditablePart selectedPart;

    private PartToolContext() {
        ToolsContext.getInstance().getToolModeProvider().addObserver((o, arg) -> deselectAllParts());
    }

    public static PartToolContext getInstance() {
        return instance;
    }

    public void setSelectedPart(ToolEditablePart button) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON || ToolsContext.getInstance().getToolMode() == ToolMode.FIELD) {
            selectedPart = button;
            deselectAllParts();
            selectedPart.setBeingEdited(true);
        }
    }

    public void deselectAllParts() {
        for (ButtonPart thisButton : HyperCard.getInstance().getCard().getButtons()) {
            thisButton.setBeingEdited(false);
        }

        for (FieldPart thisField : HyperCard.getInstance().getCard().getFields()) {
            thisField.setBeingEdited(false);
        }
    }

}
