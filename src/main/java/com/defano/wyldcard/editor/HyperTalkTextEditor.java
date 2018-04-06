package com.defano.wyldcard.editor;

import com.defano.wyldcard.awt.KeyListenable;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import javax.swing.text.BadLocationException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class HyperTalkTextEditor extends RTextScrollPane {

    private final static String LANGUAGE_KEY = "text/hypertalk";
    private final static String LANGUAGE_TOKENIZER = "com.defano.wyldcard.editor.HyperTalkTokenMaker";
    private final static String EDITOR_THEME = "/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml";
    private final static CompletionProvider COMPLETION_PROVIDER = new HyperTalkCompletionProvider();

    private final RSyntaxTextArea scriptField;
    private final HyperTalkSyntaxParser scriptParser;
    private final AutoCompletion ac;

    public HyperTalkTextEditor(SyntaxParserObserver parserObserver) {
        super(new RSyntaxTextArea());

        this.scriptField = (RSyntaxTextArea) super.getTextArea();
        this.scriptParser = new HyperTalkSyntaxParser(parserObserver);

        // Install the syntax highlighter token factory
        AbstractTokenMakerFactory tokenFactory = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        tokenFactory.putMapping(LANGUAGE_KEY, LANGUAGE_TOKENIZER);

        // Install the code folding provider
        FoldParserManager.get().addFoldParserMapping(LANGUAGE_KEY, new HyperTalkFoldParser());

        scriptField.setSyntaxEditingStyle(LANGUAGE_KEY);
        scriptField.setCodeFoldingEnabled(true);
        scriptField.addParser(scriptParser);
        scriptField.setParserDelay(50);
        scriptField.setTabSize(2);
        scriptField.setBracketMatchingEnabled(true);
        scriptField.setAnimateBracketMatching(true);
        scriptField.setPaintTabLines(true);

        // Install custom home/end key behavior
        scriptField.addKeyListener(new KeyListenable() {
            @Override
            public void keyPressed(KeyEvent e) {
                int currentIndex = scriptField.getCaretPosition();
                if (e.getKeyCode() == KeyEvent.VK_HOME) {
                    e.consume();
                    scriptField.setCaretPosition(scriptField.getLineStartOffsetOfCurrentLine());
                    if (e.isShiftDown()) {
                        scriptField.select(scriptField.getCaretPosition(), currentIndex);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_END) {
                    e.consume();
                    scriptField.setCaretPosition(scriptField.getLineEndOffsetOfCurrentLine() - 1);
                    if (e.isShiftDown()) {
                        scriptField.select(currentIndex, scriptField.getCaretPosition());
                    }
                }
            }
        });

        // Install the code-coloring theme
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(EDITOR_THEME));
            theme.apply(scriptField);
        } catch (IOException e) {
            // Nothing to do
        }

        // Install the completion provider
        ac = new AutoCompletion(COMPLETION_PROVIDER);
        ac.setTriggerKey(KeyStroke.getKeyStroke(' ', InputEvent.CTRL_MASK));
        ac.setAutoCompleteSingleChoices(false);
        ac.setParameterAssistanceEnabled(true);
        ac.setShowDescWindow(true);
        ac.setChoicesWindowSize(150, 250);
        ac.setDescriptionWindowSize(600, 250);
        ac.install(scriptField);

        getGutter().setBookmarkIcon(new ImageIcon(getClass().getResource("/icons/breakpoint.png")));
        getGutter().setBookmarkingEnabled(true);

    }

    public void clearBreakpoints() {
        getGutter().removeAllTrackingIcons();
    }

    public void toggleBreakpoint() {
        try {
            getGutter().toggleBookmark(scriptField.getCaretLineNumber());
        } catch (BadLocationException e) {
            // Impossible
        }
    }

    public RSyntaxTextArea getScriptField() {
        return scriptField;
    }

    public Parser getScriptParser() {
        return scriptParser;
    }

    public void showAutoComplete() {
        ac.doCompletion();
    }
}
