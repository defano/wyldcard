package hypercard.parts.fields;

import com.defano.jmonet.tools.util.MarchingAnts;
import hypercard.context.ToolMode;
import hypercard.context.ToolsContext;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.model.FieldModel;
import hypertalk.ast.common.Value;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public abstract class AbstractTextPaneField extends JScrollPane implements FieldComponent {

    protected final JTextPane textPane;
    protected final DefaultStyledDocument doc;
    protected StyleContext styleContext = new StyleContext();

    private ToolEditablePart toolEditablePart;

    public AbstractTextPaneField(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        MarchingAnts.getInstance().addObserver(this::repaint);

        doc = new DefaultStyledDocument(styleContext);
        textPane = new JTextPane(doc);

        textPane.addMouseListener(toolEditablePart);
        textPane.addKeyListener(toolEditablePart);
        this.setViewportView(textPane);

        ToolsContext.getInstance().getFontProvider().addObserverAndUpdate((o, arg) -> {
            Font font = (Font) arg;
            setSelectedTextAttribute(StyleConstants.FontFamily, font.getFamily());
            setSelectedTextAttribute(StyleConstants.Size, font.getSize());
            setSelectedTextAttribute(StyleConstants.Bold, font.isBold());
            setSelectedTextAttribute(StyleConstants.Italic, font.isItalic());
        });

        ToolsContext.getInstance().getToolModeProvider().addObserver((o, arg) -> {
                setHorizontalScrollBarPolicy(ToolMode.FIELD == arg ? ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER : ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                setVerticalScrollBarPolicy(ToolMode.FIELD == arg ? ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER : ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                setEnabled(ToolMode.FIELD != arg);
                setEditable(ToolMode.FIELD != arg && !toolEditablePart.getPartModel().getKnownProperty(FieldModel.PROP_LOCKTEXT).booleanValue());
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        switch (property) {
            case FieldModel.PROP_TEXT:
                if (!newValue.toString().equals(textPane.getText())) {
                    textPane.setText(newValue.toString());
                }
                break;

            case FieldModel.PROP_WRAPTEXT:
//                text.setLineWrap(newValue.booleanValue());
                break;

            case FieldModel.PROP_LOCKTEXT:
                textPane.setEditable(!newValue.booleanValue());
                break;
        }
    }

    @Override
    public String getText() {
        return textPane.getText();
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEnabled(editable);
        textPane.setEnabled(editable);
    }

    private void setSelectedTextAttribute (Object attribute, Object value) {
        MutableAttributeSet attributeSet = new SimpleAttributeSet();
        attributeSet.addAttribute(attribute, value);
        textPane.setCharacterAttributes(attributeSet, false);
        textPane.getStyledDocument().setCharacterAttributes(textPane.getSelectionStart(), textPane.getSelectionEnd() - textPane.getSelectionStart(), attributeSet, false);
    }
}
