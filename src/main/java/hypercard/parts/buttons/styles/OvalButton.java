package hypercard.parts.buttons.styles;

import hypercard.parts.ToolEditablePart;

import java.awt.*;

public class OvalButton extends AbstractLabelButton {

    private final static int OUTLINE_SROKE = 2;
    private boolean isHilited = false;

    public OvalButton(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);
    }

    @Override
    protected void drawBorder(boolean isDisabled, Graphics2D g) {
        g.setPaint(Color.WHITE);
        g.fillOval(OUTLINE_SROKE / 2, OUTLINE_SROKE / 2, getWidth() - OUTLINE_SROKE, getHeight() - OUTLINE_SROKE);

        g.setPaint(textColor(isDisabled));
        g.setStroke(new BasicStroke(OUTLINE_SROKE));
        g.drawOval(OUTLINE_SROKE / 2, OUTLINE_SROKE / 2, getWidth() - OUTLINE_SROKE, getHeight() - OUTLINE_SROKE);

        if (isHilited) {
            g.fillOval(OUTLINE_SROKE / 2 + OUTLINE_SROKE,OUTLINE_SROKE / 2 + OUTLINE_SROKE,getWidth() - OUTLINE_SROKE * 2 - OUTLINE_SROKE, getHeight() - OUTLINE_SROKE * 2 - OUTLINE_SROKE);
        } else {
            g.setPaint(Color.WHITE);
            g.fillOval(OUTLINE_SROKE / 2 + OUTLINE_SROKE,OUTLINE_SROKE / 2 + OUTLINE_SROKE,getWidth() - OUTLINE_SROKE * 2 - OUTLINE_SROKE, getHeight() - OUTLINE_SROKE * 2 - OUTLINE_SROKE);
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
