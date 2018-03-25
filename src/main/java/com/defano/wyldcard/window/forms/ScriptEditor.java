package com.defano.wyldcard.window.forms;

import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.SystemMessage;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.editor.EditorStatus;
import com.defano.wyldcard.fonts.FontUtils;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.HyperCardProperties;
import com.defano.wyldcard.util.HandlerComboBox;
import com.defano.wyldcard.window.HyperCardDialog;
import com.defano.wyldcard.editor.HyperTalkTextEditor;
import com.defano.wyldcard.editor.SyntaxParserObserver;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ScriptEditor extends HyperCardDialog implements HandlerComboBox.HandlerComboBoxDelegate, SyntaxParserObserver {

    private PartModel model;
    private Script compiledScript;
    private final HyperTalkTextEditor editor;

    private JPanel scriptEditor;
    private JButton saveButton;
    private HandlerComboBox handlersMenu;
    private HandlerComboBox functionsMenu;
    private JLabel charCount;
    private EditorStatus status;
    private JPanel textArea;

    public ScriptEditor() {

        editor = new HyperTalkTextEditor(this);

        handlersMenu.setDelegate(this);
        functionsMenu.setDelegate(this);

        saveButton.addActionListener(e -> {
            updateProperties();
            dispose();
        });

        editor.getScriptField().addCaretListener(e -> updateActiveHandler());
        editor.getScriptField().addCaretListener(e -> updateCaretPositionLabel());

        editor.getScriptField().setFont(FontUtils.getFontByNameStyleSize(
                HyperCardProperties.getInstance().getKnownProperty(HyperCardProperties.PROP_SCRIPTTEXTFONT).stringValue(),
                Font.PLAIN,
                HyperCardProperties.getInstance().getKnownProperty(HyperCardProperties.PROP_SCRIPTTEXTSIZE).integerValue()
        ));

        textArea.add(editor);
        editor.requestFocus();
    }

    @RunOnDispatch
    public void moveCaretToPosition(int position) {
        try {
            editor.getScriptField().setCaretPosition(position);
            editor.requestFocus();
        } catch (Exception e) {
            // Ignore bogus caret positions
        }
    }

    @Override
    public JButton getDefaultButton() {
        return saveButton;
    }

    @Override
    public JPanel getWindowPanel() {
        return scriptEditor;
    }

    @Override
    @RunOnDispatch
    public void bindModel(Object properties) {
        if (properties instanceof PartModel) {
            this.model = (PartModel) properties;
            String script = this.model.getKnownProperty("script").stringValue();
            editor.getScriptField().setText(script.trim());
            moveCaretToPosition(model.getScriptEditorCaretPosition());
            editor.getScriptField().addCaretListener(e -> saveCaretPosition());
            editor.getScriptField().forceReparsing(0);
        } else {
            throw new RuntimeException("Bug! Don't know how to bind data class to window: " + properties);
        }
    }

    private void updateProperties() {
        model.setKnownProperty("script", new Value(editor.getScriptField().getText()));
    }

    @Override
    @RunOnDispatch
    public Collection<String> getImplementedHandlers(HandlerComboBox theComboBox) {
        if (theComboBox == functionsMenu) {
            return compiledScript == null ? new ArrayList<>() : compiledScript.getFunctions();
        } else {
            return compiledScript == null ? new ArrayList<>() : compiledScript.getHandlers();
        }
    }

    @Override
    @RunOnDispatch
    public Collection<String> getSystemMessages(HandlerComboBox theComboBox) {
        ArrayList<String> messages = new ArrayList<>();
        if (theComboBox == functionsMenu) {
            return Collections.singletonList("New function...");
        } else {
            if (model != null) {
                for (SystemMessage message : SystemMessage.messagesSentTo(model.getType())) {
                    messages.add(message.messageName);
                }
            }
        }
        return messages;
    }

    @Override
    @RunOnDispatch
    public void jumpToHandler(HandlerComboBox theComboBox, String handler) {

        // Script is empty, add selected handler
        if (compiledScript == null) {
            if (theComboBox == functionsMenu) {
                appendFunctionTemplate();
            } else {
                appendHandler(handler);
            }
        }

        // Script is not empty; see if the selected handler exists
        else {
            Integer lineNumber = compiledScript.getLineNumberForNamedBlock(handler);
            if (lineNumber == null) {
                if (theComboBox == functionsMenu) {
                    appendFunctionTemplate();
                } else {
                    appendHandler(handler);
                }
            } else {
                editor.requestFocus();
                jumpToLine(lineNumber);
            }
        }
    }

    @RunOnDispatch
    private void saveCaretPosition() {
        model.setScriptEditorCaretPosition(editor.getScriptField().getCaretPosition());
    }

    @RunOnDispatch
    private void updateCaretPositionLabel() {
        try {
            int caretpos = editor.getScriptField().getCaretPosition();
            int row = editor.getScriptField().getLineOfOffset(caretpos);
            int column = caretpos - editor.getScriptField().getLineStartOffset(row);

            charCount.setText("Line " + (row + 1) + ", column " + column);

        } catch (BadLocationException e1) {
            charCount.setText("");
        }
    }

    @RunOnDispatch
    private void updateActiveHandler() {
        if (compiledScript != null) {
            handlersMenu.setActiveHandler(compiledScript.getNamedBlockForLine(currentLine()));
            functionsMenu.setActiveHandler(compiledScript.getNamedBlockForLine(currentLine()));
        }
    }

    @RunOnDispatch
    private void appendFunctionTemplate() {
        appendNamedBlock("function", null, "myFunction", new String[]{"arg1", "arg2"});
    }

    /**
     * Appends the handler to the script, including, when available, a description and argument list.
     *
     * @param handlerName The name of the handler to append.
     */
    @RunOnDispatch
    private void appendHandler(String handlerName) {
        SystemMessage message = SystemMessage.fromHandlerName(handlerName);
        if (message != null) {
            appendNamedBlock("on", message.description, message.messageName, message.arguments);
        }
    }

    @RunOnDispatch
    private void appendNamedBlock(String blockOpener, String description, String blockName, String[] arguments) {
        int lastIndex = editor.getScriptField().getDocument().getLength();
        StringBuilder builder = new StringBuilder();

        if (editor.getScriptField().getText().length() > 0 && !editor.getScriptField().getText().endsWith("\n")) {
            builder.append("\n\n");
        }

        // Add the handler description if one exists
        if (description != null) {
            builder.append("--\n-- ");
            builder.append(description);
            builder.append("\n--");
        }

        // Add the 'on handler' text
        builder.append("\n");
        builder.append(blockOpener);
        builder.append(" ");
        builder.append(blockName);

        // Add the handler arguments, if they exist
        if (arguments != null) {
            builder.append(" ");
            for (int index = 0; index < arguments.length; index++) {
                builder.append(arguments[index]);
                if (index < arguments.length - 1) {
                    builder.append(", ");
                }
            }
        }

        // Add the 'end handler' text
        builder.append("\n\nend ");
        builder.append(blockName);

        try {
            editor.getScriptField().getDocument().insertString(lastIndex, builder.toString(), null);
        } catch (BadLocationException e) {
            // Nothing to do
        }
    }

    @RunOnDispatch
    private void jumpToLine(int lineIndex) {
        editor.getScriptField().setCaretPosition(editor.getScriptField().getDocument().getDefaultRootElement().getElement(lineIndex).getStartOffset());
    }

    @RunOnDispatch
    private int currentLine() {
        try {
            return editor.getScriptField().getLineOfOffset(editor.getScriptField().getCaretPosition()) + 1;
        } catch (BadLocationException e) {
            return 0;
        }
    }

    @Override
    public void onRequestParse(Parser syntaxParser) {
        editor.getScriptField().forceReparsing(syntaxParser);
    }

    @Override
    public void onCompileStarted() {
        status.setStatusPending();
    }

    @Override
    public void onCompileCompleted(Script compiledScript, String resultMessage) {
        if (compiledScript != null) {
            this.compiledScript = compiledScript;
            handlersMenu.invalidateDataset();
            functionsMenu.invalidateDataset();

            status.setStatusOkay();
        } else {
            status.setStatusError(resultMessage);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        scriptEditor = new JPanel();
        scriptEditor.setLayout(new GridLayoutManager(3, 4, new Insets(10, 10, 10, 10), 0, -1));
        scriptEditor.setPreferredSize(new Dimension(640, 480));
        functionsMenu = new HandlerComboBox();
        scriptEditor.add(functionsMenu, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        charCount = new JLabel();
        Font charCountFont = this.$$$getFont$$$(null, -1, -1, charCount.getFont());
        if (charCountFont != null) charCount.setFont(charCountFont);
        charCount.setText("Line 0, column 0");
        scriptEditor.add(charCount, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setHideActionText(false);
        saveButton.setOpaque(true);
        saveButton.setText("Save");
        scriptEditor.add(saveButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textArea = new JPanel();
        textArea.setLayout(new BorderLayout(0, 0));
        scriptEditor.add(textArea, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        handlersMenu = new HandlerComboBox();
        handlersMenu.setName("Handlers:");
        scriptEditor.add(handlersMenu, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        status = new EditorStatus();
        scriptEditor.add(status, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        scriptEditor.add(spacer1, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return scriptEditor;
    }
}
