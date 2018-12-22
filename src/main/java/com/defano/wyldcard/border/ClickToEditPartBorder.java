package com.defano.wyldcard.border;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.awt.DefaultKeyboardManager;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ClickToEditPartBorder extends CompoundBorder implements Border {

    public ClickToEditPartBorder(Border innerBorder) {
        super(new ClickToEditScriptBorder(), innerBorder);
    }

    private static class ClickToEditScriptBorder implements Border, ColorStateBorder {

        private int getClickToEditScriptBorderWidth() {
            return 4;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (WyldCard.getInstance().getKeyboardManager().isPeeking(new ExecutionContext())) {
                int frameWidth = getClickToEditScriptBorderWidth();
                Color frameColor = getBorderColor(c);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(frameColor);
                g2d.setStroke(new BasicStroke(frameWidth));
                g2d.draw(new Rectangle2D.Float(x + frameWidth / 2.0f, y + frameWidth / 2.0f, width - frameWidth, height - frameWidth));
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

}
