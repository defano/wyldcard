package hypercard.parts.fields.styles;

import com.defano.jmonet.tools.util.MarchingAnts;
import hypercard.context.ToolMode;
import hypercard.context.ToolsContext;
import hypercard.parts.ToolEditablePart;
import hypercard.parts.fields.FieldView;
import hypercard.parts.model.FieldModel;
import hypertalk.ast.common.Value;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTextPaneField extends JScrollPane implements FieldView, DocumentListener {

    protected final JTextPane textPane;
    private ToolEditablePart toolEditablePart;

    public AbstractTextPaneField(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        // And listen for ants to march
        MarchingAnts.getInstance().addObserver(this::repaint);

        // Create the editor component
        textPane = new JTextPane(new DefaultStyledDocument());
        textPane.setEditorKit(new RTFEditorKit());
        this.setViewportView(textPane);

        // Add mouse and keyboard listeners
        this.addMouseListener(toolEditablePart);
        this.addKeyListener(toolEditablePart);
        textPane.addMouseListener(toolEditablePart);
        textPane.addKeyListener(toolEditablePart);

        // Get notified when font styles change
        ToolsContext.getInstance().getFontProvider().addObserverAndUpdate((o, arg) -> {
            Font font = (Font) arg;
            setSelectedTextAttribute(StyleConstants.FontFamily, font.getFamily());
            setSelectedTextAttribute(StyleConstants.Size, font.getSize());
            setSelectedTextAttribute(StyleConstants.Bold, font.isBold());
            setSelectedTextAttribute(StyleConstants.Italic, font.isItalic());
        });

        // Get notified when field tool is selected
        ToolsContext.getInstance().getToolModeProvider().addObserver((o, arg) -> {
            setHorizontalScrollBarPolicy(ToolMode.FIELD == arg ? ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER : ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            setVerticalScrollBarPolicy(ToolMode.FIELD == arg ? ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER : ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            setEnabled(ToolMode.FIELD != arg);
            setEditable(ToolMode.FIELD != arg && !toolEditablePart.getPartModel().getKnownProperty(FieldModel.PROP_LOCKTEXT).booleanValue());
        });

        // Listen for changes to the field's contents
        textPane.getStyledDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();
        model.setKnownProperty(FieldModel.PROP_TEXT, new Value(getText()), true);
        model.setStyleData(getRtf());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();
        model.setKnownProperty(FieldModel.PROP_TEXT, new Value(getText()), true);
        model.setStyleData(getRtf());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();
        model.setKnownProperty(FieldModel.PROP_TEXT, new Value(getText()), true);
        model.setStyleData(getRtf());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        SwingUtilities.invokeLater(() -> {
            switch (property) {
                case FieldModel.PROP_TEXT:
                    if (!newValue.toString().equals(getText())) {
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
        });
    }

    private void replaceText(String withText) {

        StyledDocument styledDocument = textPane.getStyledDocument();
        List<StyleSpan> styleSpans = getStyleSpans(styledDocument);

        // Do not fire listeners during this operation
        styledDocument.removeDocumentListener(this);

        try {
            // Remove current text
            styledDocument.remove(0, getText().length());

            // Remove all styles
            Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

            // Insert new text
            styledDocument.insertString(0, withText, defaultStyle);

            applyStyleSpans(styledDocument, styleSpans);

            ((FieldModel) toolEditablePart.getPartModel()).setStyleData(getRtf());

        } catch (BadLocationException e) {
            throw new RuntimeException("An error occurred when replacing field text.", e);
        } finally {
            styledDocument.addDocumentListener(this);
        }
    }

    @Override
    public String getText() {
        try {
            String text = textPane.getStyledDocument().getText(0, textPane.getStyledDocument().getLength());
            return (text == null) ? "" : text;
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JTextComponent getTextComponent() {
        return textPane;
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEnabled(editable);
        textPane.setEnabled(editable);
    }

    @Override
    public void partOpened() {
        setRtf(((FieldModel) toolEditablePart.getPartModel()).getStyleData());
    }

    private void setSelectedTextAttribute(Object attribute, Object value) {
        MutableAttributeSet attributeSet = new SimpleAttributeSet();
        attributeSet.addAttribute(attribute, value);
        textPane.setCharacterAttributes(attributeSet, false);
    }

    private void setRtf(byte[] rtfData) {

        if (rtfData != null && rtfData.length != 0) {
            ByteArrayInputStream bais = new ByteArrayInputStream(rtfData);
            try {
                StyledDocument doc = new DefaultStyledDocument();

                textPane.getEditorKit().read(bais, doc, 0);
                bais.close();

                textPane.setStyledDocument(doc);
                doc.addDocumentListener(this);

                // RTFEditorKit appears to (erroneously) append a newline when we deserialize; get rid of that.
                textPane.getStyledDocument().remove(doc.getLength() - 1, 1);

            } catch (IOException | BadLocationException e) {
                throw new RuntimeException("An error occurred while restoring field contents.", e);
            }
        }
    }

    private byte[] getRtf() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document doc = textPane.getStyledDocument();
            textPane.getEditorKit().write(baos, doc, 0, doc.getLength());
            baos.close();

            return baos.toByteArray();

        } catch (IOException | BadLocationException e) {
            throw new RuntimeException("An error occurred while saving field contents.", e);
        }
    }

    private List<StyleSpan> getStyleSpans(StyledDocument styledDocument) {

        int lastIndex = 0;
        AttributeSet lastAttribute = null, thisAttribute = null;
        List<StyleSpan> styleSpans = new ArrayList<>();
        String text = getText();

        for (int index = 0; index < text.length(); index++) {
            thisAttribute = styledDocument.getCharacterElement(index).getAttributes();

            if (lastAttribute != null && lastAttribute != thisAttribute) {
                StyleSpan thisSpan = new StyleSpan(text, lastIndex, index, lastAttribute);
                if (thisSpan.getSpanLength(text) > 0) {
                    styleSpans.add(thisSpan);
                    lastIndex = index;
                }
            }

            lastAttribute = thisAttribute;
        }

        styleSpans.add(new StyleSpan(text, lastIndex, text.length(), thisAttribute));

        return styleSpans;
    }

    private void applyStyleSpans(StyledDocument styledDocument, List<StyleSpan> styleSpans) {

        // Reapply old style to new text
        for (StyleSpan thisSpan : styleSpans) {
            int startChar = thisSpan.getStartOfSpan(getText());
            int endChar = thisSpan.getEndOfSpan(getText());

            styledDocument.setCharacterAttributes(startChar, endChar - startChar, thisSpan.getStyleSet(), true);
        }
    }

}
