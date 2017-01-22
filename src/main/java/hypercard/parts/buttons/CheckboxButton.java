package hypercard.parts.buttons;

import com.defano.jmonet.tools.util.MarchingAnts;

import javax.swing.*;
import java.awt.*;

public class CheckboxButton extends JCheckBox {

    private final ToolEditablePart toolEditablePart;

    public CheckboxButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        MarchingAnts.getInstance().addObserver(this::repaint);
        super.addActionListener(toolEditablePart);
        super.addMouseListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (toolEditablePart.isSelected()) {
            ((Graphics2D) g).setStroke(MarchingAnts.getInstance().getMarchingAnts());
            g.drawRect(0, 0, getWidth(), getHeight());
        }
    }

}
