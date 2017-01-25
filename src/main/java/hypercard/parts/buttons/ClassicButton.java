package hypercard.parts.buttons;

import hypercard.parts.ToolEditablePart;

import java.awt.*;

public class ClassicButton extends AbstractLabelButton implements ButtonComponent {

    private boolean isHilited = false;

    public ClassicButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected void drawBorder(boolean isDisabled, Graphics2D g) {
        g.setPaint(textColor(isDisabled));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        if (isHilited) {
            g.fillRoundRect(0,0,getWidth() - 1, getHeight() - 1, 10, 10);
        }
    }

    @Override
    protected void setName(boolean isDisabled, String name) {
        setForeground(textColor(isDisabled));
        ClassicButton.super.setText(name);
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
