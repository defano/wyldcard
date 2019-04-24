package com.defano.wyldcard.parts.button.styles;

import com.defano.wyldcard.fonts.FontUtils;
import com.defano.wyldcard.icons.ButtonIcon;
import com.defano.wyldcard.icons.IconDatabase;
import com.defano.wyldcard.parts.ContainerWrappedPart;
import com.defano.wyldcard.parts.button.HyperCardButton;
import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.button.IconAlignable;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractLabelButton extends JPanel implements ContainerWrappedPart, HyperCardButton, IconAlignable {

    protected final ToolEditablePart toolEditablePart;
    private boolean isHilited = false;
    private JLabel label;
    private ButtonIcon icon;

    protected abstract void paintHilite(boolean isHilited, Graphics2D g);

    public AbstractLabelButton(ToolEditablePart toolEditablePart) {
        label = new JLabel("", SwingConstants.CENTER);
        setLayout(new BorderLayout());
        add(label);

        this.toolEditablePart = toolEditablePart;
        super.setEnabled(true);
    }

    @Override
    public void onStart() {
        super.addMouseListener(toolEditablePart);
        super.addKeyListener(toolEditablePart);
    }

    @Override
    public void onStop() {
        super.removeMouseListener(toolEditablePart);
        super.removeKeyListener(toolEditablePart);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (icon == null) {
            paintHilite(paintHilited(), (Graphics2D) g);
            label.setForeground(getLabelColor());
        }

        label.paintComponents(g);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public ToolEditablePart getToolEditablePart() {
        return toolEditablePart;
    }

    @Override
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_NAME:
            case ButtonModel.PROP_SHOWNAME:
                boolean showName = toolEditablePart.getPartModel().get(context, ButtonModel.PROP_SHOWNAME).booleanValue();
                label.setText(showName ? toolEditablePart.getPartModel().get(context, ButtonModel.PROP_NAME).toString() : "");
                break;

            case ButtonModel.PROP_HILITE:
                isHilited = newValue.booleanValue();
                updateIconHiliteState();
                break;

            case ButtonModel.PROP_ENABLED:
                setEnabled(newValue.booleanValue());
                updateIconHiliteState();
                break;

            case ButtonModel.PROP_TEXTSIZE:
                label.setFont(FontUtils.getFontByNameStyleSize(label.getFont().getFamily(), label.getFont().getStyle(), newValue.integerValue()));
                break;

            case ButtonModel.PROP_TEXTFONT:
                label.setFont(FontUtils.getFontByNameStyleSize(newValue.toString(), label.getFont().getStyle(), label.getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTSTYLE:
                label.setFont(FontUtils.getFontByNameStyleSize(label.getFont().getFamily(), FontUtils.getFontStyleForValue(context, newValue), label.getFont().getSize()));
                break;

            case ButtonModel.PROP_TEXTALIGN:
                label.setHorizontalAlignment(FontUtils.getAlignmentForValue(newValue));
                break;

            case ButtonModel.PROP_ICON:
                icon = IconDatabase.getInstance().findIconForValue(newValue);
                label.setIconTextGap(1);
                break;

            case ButtonModel.PROP_ICONALIGN:
                setIconAlignment(newValue);
                break;
        }

        revalidate();
        repaint();
    }

    @Override
    public JComponent getIconComponent() {
        return label;
    }

    @Override
    public JComponent getWrappedComponent() {
        return this;
    }

    private void updateIconHiliteState() {
        if (icon != null) {
            label.setIcon(paintHilited() ? icon.getInvertedIcon() : icon.getIcon());
        }
    }

    private Color getLabelColor() {
        return paintHilited() && icon == null ? Color.WHITE :
                isEnabled() ? Color.BLACK : Color.GRAY;
    }

    private boolean paintHilited() {
        return isHilited && isEnabled();
    }
}
