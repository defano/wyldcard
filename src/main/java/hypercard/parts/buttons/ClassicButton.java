package hypercard.parts.buttons;

import hypercard.parts.ToolEditablePart;

import java.awt.*;

public class ClassicButton extends AbstractLabelButton implements ButtonComponent {

    private final static int OUTLINE_SROKE = 2;

    private boolean isHilited = false;

    public ClassicButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected void drawBorder(boolean isDisabled, Graphics2D g) {
        g.setPaint(textColor(isDisabled));
        g.setStroke(new BasicStroke(OUTLINE_SROKE));
        g.drawRoundRect(OUTLINE_SROKE / 2, OUTLINE_SROKE / 2, getWidth() - OUTLINE_SROKE, getHeight() - OUTLINE_SROKE, 10, 10);

        if (isHilited) {
            g.fillRoundRect(OUTLINE_SROKE / 2 + OUTLINE_SROKE,OUTLINE_SROKE / 2 + OUTLINE_SROKE,getWidth() - OUTLINE_SROKE * 2 - OUTLINE_SROKE, getHeight() - OUTLINE_SROKE * 2 - OUTLINE_SROKE, 10, 10);
        }
    }

    @Override
    protected void setName(boolean isDisabled, String name) {
        setForeground(textColor(isDisabled));
        ClassicButton.super.setText(name);
    }

    @Override
    protected void setHilite(boolean isDisabled, boolean isHilited) {
        if (!isDisabled && !toolEditablePart.isPartToolActive()) {
            this.isHilited = isHilited;
            setForeground(isHilited ? Color.WHITE : textColor(isDisabled));
            repaint();
        }
    }

}
