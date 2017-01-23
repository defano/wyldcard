package hypercard.parts.buttons;

import com.defano.jmonet.tools.util.MarchingAnts;
import hypercard.context.ToolMode;
import hypercard.context.ToolsContext;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class TransparentButton extends JLabel {

    private final ToolEditablePart toolEditablePart;
    private boolean isHilited = false;

    public TransparentButton(ToolEditablePart toolEditablePart) {
        super("", SwingConstants.CENTER);
        setBackground(Color.BLACK);

        this.toolEditablePart = toolEditablePart;
        super.setEnabled(true);

        MarchingAnts.getInstance().addObserver(this::repaint);
        super.addMouseListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);

        // Draw the button highlight as the property changes
        toolEditablePart.getModel().addPropertyChangedObserver((property, oldValue, newValue) -> {
            if (property.equalsIgnoreCase(hypercard.parts.model.ButtonModel.PROP_HILITE)) {
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

        if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON) {
            g.drawRect(1, 1, getWidth() -2 , getHeight() -2);
        }

        toolEditablePart.drawSelectionRectangle(g);
    }
}
