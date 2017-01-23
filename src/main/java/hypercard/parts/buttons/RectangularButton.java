package hypercard.parts.buttons;

import com.defano.jmonet.tools.util.MarchingAnts;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.model.ButtonModel;

import javax.swing.*;
import java.awt.*;

public class RectangularButton extends JLabel {

    private final ToolEditablePart toolEditablePart;
    private boolean isHilited = false;

    public RectangularButton(ToolEditablePart toolEditablePart) {
        super("", SwingConstants.CENTER);
        setBackground(Color.BLACK);

        this.toolEditablePart = toolEditablePart;
        super.setEnabled(true);

        MarchingAnts.getInstance().addObserver(this::repaint);
        super.addMouseListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);

        // Draw the button highlight as the property changes
        toolEditablePart.getModel().addPropertyChangedObserver((property, oldValue, newValue) -> {
            if (property.equalsIgnoreCase(ButtonModel.PROP_HILITE)) {
                isHilited = newValue.booleanValue();
                setOpaque(isHilited);
                setForeground(isHilited ? Color.WHITE : Color.BLACK);
            }
        });

        toolEditablePart.addTextChangeObserver(super::setText);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.BLACK);
        g2d.drawRect(1, 1, getWidth() - 2, getHeight() - 2);

        toolEditablePart.drawSelectionRectangle(g);
    }
}
