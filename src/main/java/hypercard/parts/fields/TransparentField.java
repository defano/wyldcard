package hypercard.parts.fields;

import hypercard.context.ToolMode;
import hypercard.context.ToolsContext;
import hypercard.parts.ToolEditablePart;

import javax.swing.*;
import java.awt.*;

public class TransparentField extends AbstractTextPaneField {

    private final static Color TRANSPARENT = new Color(0,0,0,0);

    public TransparentField(ToolEditablePart toolEditablePart) {
        super(toolEditablePart);

        setOpaque(false);
        textPane.setOpaque(false);
        getViewport().setOpaque(false);

        setBorder(BorderFactory.createEmptyBorder());
        textPane.setBorder(BorderFactory.createEmptyBorder());

        getViewport().setBackground(TRANSPARENT);
        setBackground(TRANSPARENT);
        textPane.setBackground(TRANSPARENT);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (ToolsContext.getInstance().getToolMode() == ToolMode.FIELD) {
            ((Graphics2D) g).setPaint(Color.GRAY);
            ((Graphics2D) g).drawRect(0, 0, getWidth(), getHeight());
        }
    }
}
