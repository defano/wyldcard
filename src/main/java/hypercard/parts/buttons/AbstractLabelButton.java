package hypercard.parts.buttons;

import com.defano.jmonet.tools.util.MarchingAnts;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.model.ButtonModel;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractLabelButton extends JLabel implements ButtonComponent {

    private final ToolEditablePart toolEditablePart;
    private boolean isDisabled = false;

    protected abstract void drawBorder(boolean isDisabled, Graphics2D g);
    protected abstract void setName(boolean isDisabled, String name);
    protected abstract void setHilite(boolean isDisabled, boolean isHilited);

    public AbstractLabelButton(ToolEditablePart toolEditablePart) {
        super("", SwingConstants.CENTER);
        setBackground(Color.BLACK);

        this.toolEditablePart = toolEditablePart;
        super.setEnabled(true);

        MarchingAnts.getInstance().addObserver(this::repaint);
        super.addMouseListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);
    }

    @Override
    public void paintComponent(Graphics g) {
        drawBorder(isDisabled, (Graphics2D) g);
        super.paintComponent(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_NAME:
            case ButtonModel.PROP_SHOWNAME:
                boolean showName = toolEditablePart.getPartModel().getKnownProperty(ButtonModel.PROP_SHOWNAME).booleanValue();
                setName(isDisabled, showName ? toolEditablePart.getPartModel().getKnownProperty(ButtonModel.PROP_NAME).stringValue() : "");

            case ButtonModel.PROP_HILITE:
                setHilite(isDisabled, newValue.booleanValue());
                break;

            case ButtonModel.PROP_ENABLED:
                isDisabled = !newValue.booleanValue();
                break;
        }
    }

    protected Color textColor(boolean isDisabled) {
        return isDisabled ? Color.GRAY : Color.BLACK;
    }
}
