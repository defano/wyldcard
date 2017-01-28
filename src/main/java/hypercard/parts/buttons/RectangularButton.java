package hypercard.parts.buttons;

import hypercard.parts.ToolEditablePart;
import hypercard.parts.model.ButtonModel;

import java.awt.*;

public class RectangularButton extends AbstractLabelButton {

    public RectangularButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected void drawBorder(boolean isDisabled, Graphics2D g) {
        g.setPaint(textColor(isDisabled));
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    @Override
    protected void setName(boolean isDisabled, String name) {
        setForeground(textColor(isDisabled));
        RectangularButton.super.setText(name);
    }

    @Override
    protected void setHilite(boolean isDisabled, boolean isHilited) {
        if (!isDisabled) {
            setOpaque(isHilited);
            setForeground(isHilited ? Color.WHITE : textColor(isDisabled));
        }
    }

}
