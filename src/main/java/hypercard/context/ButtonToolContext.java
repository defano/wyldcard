package hypercard.context;

import hypercard.HyperCard;
import hypercard.parts.ButtonPart;
import hypercard.parts.ToolEditablePart;

import java.util.Observable;
import java.util.Observer;

public class ButtonToolContext {

    private final static ButtonToolContext instance = new ButtonToolContext();

    private ToolEditablePart selectedButton;

    private ButtonToolContext() {
        ToolsContext.getInstance().getToolModeProvider().addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg != ToolMode.BUTTON) {
                    deselectAllButtons();
                }
            }
        });
    }

    public static ButtonToolContext getInstance() {
        return instance;
    }

    public void setSelectedButton(ToolEditablePart button) {
        if (ToolsContext.getInstance().getToolModeProvider().get() == ToolMode.BUTTON) {
            selectedButton = button;
            deselectAllButtons();
            selectedButton.setSelected(true);
        }
    }

    public void deselectAllButtons() {
        for (ButtonPart thisButton : HyperCard.getInstance().getCard().getButtons().getParts()) {
            thisButton.setSelected(false);
        }
    }

}
