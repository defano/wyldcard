package hypercard.parts.buttons;

import com.defano.jmonet.tools.util.MarchingAnts;
import hypercard.parts.ToolEditablePart;

import javax.swing.*;
import java.awt.*;

public class MenuButton extends JMenu {

    private final ToolEditablePart toolEditablePart;

    public MenuButton(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        MarchingAnts.getInstance().addObserver(this::repaint);
        super.addActionListener(toolEditablePart);
        super.addMouseListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        toolEditablePart.drawSelectionRectange(g);
    }
}
