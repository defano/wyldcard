package hypercard.parts.buttons;

import hypercard.parts.ToolEditablePart;
import hypercard.parts.model.ButtonModel;

import java.awt.*;

public class OvalButton extends AbstractLabelButton {

    private boolean isHilited = false;

    public OvalButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected void drawBorder(boolean isDisabled, Graphics2D g) {
        g.setPaint(textColor(isDisabled));
        g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

        if (isHilited) {
            g.fillOval(0,0,getWidth() - 1, getHeight() - 1);
        }
    }

    @Override
    protected void setName(boolean isDisabled, String name) {
        setForeground(textColor(isDisabled));
        OvalButton.super.setText(name);
    }

    @Override
    protected void setHilite(boolean isDisabled, boolean isHilited) {
        if (!isDisabled) {
            this.isHilited = isHilited;
            setForeground(isHilited ? Color.WHITE : textColor(isDisabled));
            repaint();
        }
    }
}
