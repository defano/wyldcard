package hypercard.parts.buttons;

import hypercard.parts.ToolEditablePart;

import java.awt.*;

public class TransparentButton extends AbstractLabelButton {

    public TransparentButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected void drawBorder(boolean isDisabled, Graphics2D g) {
        if (toolEditablePart.isPartToolActive()) {
            g.setPaint(Color.GRAY);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    @Override
    protected void setName(boolean isDisabled, String name) {
        setForeground(textColor(isDisabled));
        TransparentButton.super.setText(name);
    }

    @Override
    protected void setHilite(boolean isDisabled, boolean isHilited) {
        if (!isDisabled) {
            setOpaque(isHilited);
            setForeground(isHilited ? Color.WHITE : textColor(isDisabled));
        }
    }

}
