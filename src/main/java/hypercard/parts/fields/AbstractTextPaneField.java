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
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTextPaneField extends JScrollPane implements FieldComponent {

    protected final JTextPane textPane;
    private ToolEditablePart toolEditablePart;

    public AbstractTextPaneField(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        MarchingAnts.getInstance().addObserver(this::repaint);

        textPane = new JTextPane(new DefaultStyledDocument(new StyleContext()));

        this.addMouseListener(toolEditablePart);
        this.addKeyListener(toolEditablePart);
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
                    replaceText(newValue.stringValue());
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

    private void replaceText(String withText) {
        Map<Span, AttributeSet> styleSpans = getStyleSpans();
        textPane.setText(withText);

        for (Span thisSpan : styleSpans.keySet()) {
            textPane.getStyledDocument().setCharacterAttributes(thisSpan.start, thisSpan.end - thisSpan.start, styleSpans.get(thisSpan), true);
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

    @Override
    public void partOpened() {
    }

    private void setSelectedTextAttribute (Object attribute, Object value) {
        MutableAttributeSet attributeSet = new SimpleAttributeSet();
        attributeSet.addAttribute(attribute, value);
        textPane.setCharacterAttributes(attributeSet, false);
    }

    private Map<Span,AttributeSet> getStyleSpans() {
        int lastIndex = 0;
        AttributeSet last = null;
        AttributeSet attributeSet = new SimpleAttributeSet();

        HashMap<Span,AttributeSet> map = new HashMap<>();

        for (int index = 0; index < textPane.getText().length(); index++) {
            attributeSet = textPane.getStyledDocument().getCharacterElement(index).getAttributes();

            if (last != null && last != attributeSet) {
                map.put(new Span(lastIndex, index), last);
                lastIndex = index;
            }

            last = attributeSet;
        }

        map.put(new Span(lastIndex, textPane.getText().length()), attributeSet);
        return map;
    }

    private class Span {
        final int start, end;

        Span(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
