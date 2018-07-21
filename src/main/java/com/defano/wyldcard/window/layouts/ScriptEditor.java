package com.defano.wyldcard.window.layouts;

import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.ast.model.SystemMessage;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.debug.DebugContext;
import com.defano.wyldcard.editor.EditorStatus;
import com.defano.wyldcard.editor.HyperTalkTextEditor;
import com.defano.wyldcard.editor.SyntaxParserDelegate;
import com.defano.wyldcard.fonts.FontUtils;
import com.defano.wyldcard.menubar.script.ScriptEditorMenuBar;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.runtime.HyperCardProperties;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.HandlerComboBox;
import com.defano.wyldcard.util.StringUtils;
import com.defano.wyldcard.window.WyldCardWindow;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ScriptEditor extends WyldCardWindow<PartModel> implements HandlerComboBox.HandlerComboBoxDelegate, SyntaxParserDelegate, PropertyChangeObserver {

    private final HyperTalkTextEditor editor;
    private final SearchContext context = new SearchContext();
    private final ScriptEditorMenuBar menuBar = new ScriptEditorMenuBar(this);
    private PartModel model;
    private Script compiledScript;
    private JPanel scriptEditor;
    private HandlerComboBox handlersMenu;
    private HandlerComboBox functionsMenu;
    private JLabel charCount;
    private EditorStatus status;
    private JPanel textArea;
    private JLabel helpIcon;

    public ScriptEditor() {

        editor = new HyperTalkTextEditor(this);

        handlersMenu.setDelegate(this);
        functionsMenu.setDelegate(this);

        editor.getScriptField().addCaretListener(e -> updateActiveHandler());
        editor.getScriptField().addCaretListener(e -> updateCaretPositionLabel());
        editor.addBreakpointToggleObserver(breakpoints -> saveBreakpoints());

        editor.getScriptField().setFont(FontUtils.getFontByNameStyleSize(
                HyperCardProperties.getInstance().getKnownProperty(new ExecutionContext(), HyperCardProperties.PROP_SCRIPTTEXTFONT).stringValue(),
                Font.PLAIN,
                HyperCardProperties.getInstance().getKnownProperty(new ExecutionContext(), HyperCardProperties.PROP_SCRIPTTEXTSIZE).integerValue()
        ));

        textArea.add(editor);
        helpIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                editor.showAutoComplete();
            }
        });

        // Prompt to save when closing window
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ScriptEditor.this.close();
            }
        });
    }

    @Override
    public JPanel getWindowPanel() {
        return scriptEditor;
    }

    @Override
    public JMenuBar getWyldCardMenuBar() {
        return menuBar;
    }

    public PartModel getModel() {
        return model;
    }

    @Override
    @RunOnDispatch
    public void bindModel(PartModel partModel) {
        this.model = partModel;

        // Render the script and breakpoints in the editor
        applyScriptToEditor();

        // Listen for programmatically-applied script changes
        this.model.addPropertyChangedObserver(this);

        restoreCaretPosition();
    }

    @RunOnDispatch
    public boolean save() {

        // Can't save if syntax error present
        if (status.isShowingError()) {
            JOptionPane.showMessageDialog(
                    this,
                    "This script can't be saved because it has syntax errors.",
                    "Close",
                    JOptionPane.ERROR_MESSAGE);

            return false;
        }

        // No syntax error; okay to save
        else {
            saveBreakpoints();
            saveCaretPosition();
            saveScript();
            return true;
        }
    }

    @RunOnDispatch
    private void saveScript() {
        model.setKnownProperty(new ExecutionContext(), PartModel.PROP_SCRIPT, new Value(editor.getScriptField().getText()));
    }

    @RunOnDispatch
    private void saveBreakpoints() {
        model.setKnownProperty(new ExecutionContext(), PartModel.PROP_BREAKPOINTS, Value.ofItems(StringUtils.getValueList(editor.getBreakpoints())));
    }

    @RunOnDispatch
    public void close() {

        if (DebugContext.getInstance().isDebugging(this)) {
            DebugContext.getInstance().resume();
        }

        // User modified script but syntax error is present
        if (isDirty() && status.isShowingError()) {
            int dialogResult = JOptionPane.showConfirmDialog(
                    this,
                    "This script can't be saved because it has syntax errors.\nClose without saving?",
                    "Close",
                    JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.YES_OPTION) {
                dispose();
            }
        }

        // User modified script cleanly
        else if (isDirty()) {
            int dialogResult = JOptionPane.showConfirmDialog(
                    this,
                    "Save changes to this script?",
                    "Close",
                    JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.YES_OPTION) {
                if (save()) {
                    dispose();
                }
            } else if (dialogResult == JOptionPane.NO_OPTION) {
                dispose();
            }
        }

        // User didn't make any changes
        else {
            dispose();
        }
    }

    @RunOnDispatch
    @Override
    public void dispose() {
        this.model.removePropertyChangedObserver(this);
        super.dispose();
    }

    @RunOnDispatch
    public void revertToSaved() {
        if (isDirty()) {
            int dialogResult = JOptionPane.showConfirmDialog(
                    this,
                    "Discard changes to this script?",
                    "Revert",
                    JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.YES_OPTION) {
                editor.getScriptField().setText(model.getKnownProperty(new ExecutionContext(), PartModel.PROP_SCRIPT).stringValue());
            }
        }
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
                editor.getScriptField().requestFocusInWindow();
                jumpToLine(lineNumber);
            }
        }
    }

    public HyperTalkTextEditor getEditor() {
        return editor;
    }

    public SearchContext getContext() {
        return context;
    }

    @RunOnDispatch
    public void find(String text, Boolean wholeWord, Boolean caseSensitive, boolean wrap) {
        RSyntaxTextArea textEditor = editor.getScriptField();

        provisionSearch(wholeWord, caseSensitive);
        context.setSearchFor(text);

        SearchResult result = SearchEngine.find(textEditor, context);
        if (!result.wasFound() && wrap) {
            textEditor.setCaretPosition(0);
            SearchEngine.find(textEditor, context);
        }
    }

    @RunOnDispatch
    public void replaceAll(String findText, String replaceText, Boolean wholeWord, Boolean caseSensitive) {
        provisionSearch(wholeWord, caseSensitive);
        context.setSearchFor(findText);
        context.setReplaceWith(replaceText);

        SearchEngine.replaceAll(editor.getScriptField(), context);
    }

    @RunOnDispatch
    public void replace(String findText, String replaceText, Boolean wholeWord, Boolean caseSensitive, boolean wrap) {
        provisionSearch(wholeWord, caseSensitive);
        context.setSearchFor(findText);
        context.setReplaceWith(replaceText);

        SearchEngine.replace(editor.getScriptField(), context);
        find(findText, wholeWord, caseSensitive, wrap);
    }

    @RunOnDispatch
    public void replace() {
        replace(context.getSearchFor(), context.getReplaceWith(), null, null, true);
    }

    @RunOnDispatch
    public void uncomment() {
        RSyntaxTextArea textArea = editor.getScriptField();

        try {
            int startLine = textArea.getLineOfOffset(textArea.getSelectionStart());
            int endLine = textArea.getLineOfOffset(textArea.getSelectionEnd());

            for (int line = startLine; line <= endLine; line++) {
                int lineStartPos = textArea.getLineStartOffset(line);

                if (textArea.getTokenListForLine(line).getType() == TokenTypes.COMMENT_EOL) {
                    editor.getScriptField().replaceRange("", lineStartPos, lineStartPos + 2);
                }
            }
        } catch (BadLocationException e) {
            // Ignore bogus caret position
        }
    }

    @RunOnDispatch
    public void comment() {
        RSyntaxTextArea textArea = editor.getScriptField();

        try {
            int startLine = textArea.getLineOfOffset(textArea.getSelectionStart());
            int endLine = textArea.getLineOfOffset(textArea.getSelectionEnd());

            for (int line = startLine; line <= endLine; line++) {
                int lineStartPos = textArea.getLineStartOffset(line);
                editor.getScriptField().insert("--", lineStartPos);
            }
        } catch (BadLocationException e) {
            // Ignore bogus caret position
        }
    }

    @RunOnDispatch
    private void provisionSearch(Boolean wholeWord, Boolean caseSensitive) {
        if (wholeWord != null) {
            context.setWholeWord(wholeWord);
        }

        if (caseSensitive != null) {
            context.setMatchCase(caseSensitive);
        }

        context.setMarkAll(false);
        context.setRegularExpression(false);
        context.setSearchForward(true);
    }

    @RunOnDispatch
    public void clearBreakpoints() {
        editor.clearBreakpoints();
    }

    @RunOnDispatch
    public void addBreakpoint() {
        editor.toggleBreakpoint();
    }

    @RunOnDispatch
    public void makeSelectionFindText() {
        if (editor.getScriptField().getSelectedText().length() > 0) {
            context.setSearchFor(editor.getScriptField().getSelectedText());
        }
    }

    @RunOnDispatch
    public void find() {
        find(context.getSearchFor(), null, null, true);
    }

    @RunOnDispatch
    public void findSelection() {
        find(editor.getScriptField().getSelectedText(), null, null, true);
    }

    @RunOnDispatch
    public void checkSyntax() {
        editor.getScriptField().forceReparsing(editor.getScriptParser());
    }

    @RunOnDispatch
    private boolean isDirty() {
        return !editor.getScriptField().getText().equals(model.getKnownProperty(new ExecutionContext(), PartModel.PROP_SCRIPT).stringValue());
    }

    @RunOnDispatch
    private void saveCaretPosition() {
        model.setScriptEditorCaretPosition(editor.getScriptField().getCaretPosition());
    }

    @RunOnDispatch
    private void restoreCaretPosition() {
        try {
            editor.getScriptField().setCaretPosition(model.getScriptEditorCaretPosition());
        } catch (Exception e) {
            // Ignore bogus caret positions
        }
    }

    @RunOnDispatch
    private void updateCaretPositionLabel() {
        try {
            int caretPosition = editor.getScriptField().getCaretPosition();
            int row = editor.getScriptField().getLineOfOffset(caretPosition);
            int column = caretPosition - editor.getScriptField().getLineStartOffset(row);

            charCount.setText("Line " + (row + 1) + ", column " + column);

        } catch (BadLocationException e1) {
            // Ignore bogus caret position
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

            SwingUtilities.invokeLater(() -> {
                handlersMenu.invalidateDataset();
                functionsMenu.invalidateDataset();
                status.setStatusOkay();
            });
        } else {
            status.setStatusError(resultMessage);
        }
    }

    @Override
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property.toLowerCase()) {

            // Special case: Script text was programatically changed
            case PartModel.PROP_SCRIPT:
                saveCaretPosition();
                applyScriptToEditor();
                break;
        }
    }

    /**
     * Applies the script associated with the currently bound model to the script editor replacing the text of the
     * script editor with the script text, applying any breakpoints to the gutter and restoring the caret position to
     * its last saved location and re-focusing the script editor component.
     */
    @RunOnDispatch
    private void applyScriptToEditor() {
        String script = this.model.getKnownProperty(new ExecutionContext(), PartModel.PROP_SCRIPT).stringValue();
        editor.getScriptField().setText(script);
        editor.getScriptField().forceReparsing(0);

        // Reset all breakpoints
        editor.clearBreakpoints();
        for (int thisBreakpoint : model.getBreakpoints()) {
            try {
                editor.getGutter().toggleBookmark(thisBreakpoint);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        restoreCaretPosition();
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
        scriptEditor.setMinimumSize(new Dimension(600, 100));
        scriptEditor.setPreferredSize(new Dimension(640, 480));
        functionsMenu = new HandlerComboBox();
        scriptEditor.add(functionsMenu, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
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
        charCount = new JLabel();
        Font charCountFont = this.$$$getFont$$$(null, -1, -1, charCount.getFont());
        if (charCountFont != null) charCount.setFont(charCountFont);
        charCount.setText("Line 0, column 0");
        scriptEditor.add(charCount, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        helpIcon = new JLabel();
        helpIcon.setIcon(new ImageIcon(getClass().getResource("/icons/help.png")));
        helpIcon.setText("");
        scriptEditor.add(helpIcon, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
