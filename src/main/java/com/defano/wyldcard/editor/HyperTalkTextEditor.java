package com.defano.wyldcard.editor;

import com.defano.wyldcard.aspect.RunOnDispatch;
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
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HyperTalkTextEditor extends RTextScrollPane {

    private final static Color TRACE_HILITE_COLOR = new Color(0xff, 0x00, 0x00, 0x40);
    private final static String LANGUAGE_KEY = "text/hypertalk";
    private final static String LANGUAGE_TOKENIZER = "com.defano.wyldcard.editor.HyperTalkTokenMaker";
    private final static String EDITOR_THEME = "/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml";
    private final static CompletionProvider COMPLETION_PROVIDER = new HyperTalkCompletionProvider();

    private final RSyntaxTextArea scriptField;
    private final HyperTalkSyntaxParser scriptParser;
    private final AutoCompletion ac;
    private Object traceHighlightTag;

    private GutterIconInfo[] bookmarks;
    private ArrayList<BreakpointToggleObserver> breakpointToggleObservers = new ArrayList<>();

    public HyperTalkTextEditor(SyntaxParserDelegate parserObserver) {
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
                    if (e.isShiftDown()) {
                        scriptField.setCaretPosition(currentIndex);
                        scriptField.moveCaretPosition(scriptField.getLineStartOffsetOfCurrentLine());
                    } else {
                        scriptField.setCaretPosition(scriptField.getLineStartOffsetOfCurrentLine());
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_END) {
                    e.consume();

                    int endIndex = scriptField.getText().length() > scriptField.getLineEndOffsetOfCurrentLine() && scriptField.getText().charAt(scriptField.getLineEndOffsetOfCurrentLine()) == '\n' ?
                            scriptField.getLineEndOffsetOfCurrentLine() - 1 :
                            scriptField.getLineEndOffsetOfCurrentLine();

                    if (e.isShiftDown()) {
                        scriptField.moveCaretPosition(endIndex);
                    } else {
                        scriptField.setCaretPosition(endIndex);
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

    @RunOnDispatch
    public List<Integer> getBreakpoints() {
        ArrayList<Integer> breakpoints = new ArrayList<>();
        for (GutterIconInfo thisMark : getGutter().getBookmarks()) {
            try {
                breakpoints.add(scriptField.getLineOfOffset(thisMark.getMarkedOffset()));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        return breakpoints;
    }

    @RunOnDispatch
    public void clearBreakpoints() {
        getGutter().removeAllTrackingIcons();
    }

    @RunOnDispatch
    public void toggleBreakpoint() {
        try {
            getGutter().toggleBookmark(scriptField.getCaretLineNumber());
            fireBookmarkToggleListener();
        } catch (BadLocationException e) {
            // Impossible
        }
    }

    @RunOnDispatch
    public void startDebugging() {
        scriptField.setEnabled(false);
        scriptField.setHighlightCurrentLine(false);
    }

    @RunOnDispatch
    public void finishDebugging() {
        scriptField.setEnabled(true);
        scriptField.setHighlightCurrentLine(true);
        clearTraceHighlights();
    }

    @RunOnDispatch
    public void showTraceHighlight(int line) {
        startDebugging();
        clearTraceHighlights();
        try {
            traceHighlightTag = scriptField.addLineHighlight(line, TRACE_HILITE_COLOR);
            scriptField.setCaretPosition(scriptField.getLineStartOffset(line));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @RunOnDispatch
    public void clearTraceHighlights() {
        if (traceHighlightTag != null) {
            scriptField.removeLineHighlight(traceHighlightTag);
        }
    }

    public RSyntaxTextArea getScriptField() {
        return scriptField;
    }

    public Parser getScriptParser() {
        return scriptParser;
    }

    @RunOnDispatch
    public void showAutoComplete() {
        ac.doCompletion();
    }

    @RunOnDispatch
    public void addBreakpointToggleObserver(BreakpointToggleObserver observer) {
        if (breakpointToggleObservers.size() == 0) {
            installBreakpointToggleObserver();
        }
        breakpointToggleObservers.add(observer);
    }

    @RunOnDispatch
    public void removeBreakpointToggleObserver(BreakpointToggleObserver observer) {
        breakpointToggleObservers.remove(observer);
    }

    @RunOnDispatch
    private void installBreakpointToggleObserver() {
        for (Component c : getGutter().getComponents()) {
            if (c instanceof IconRowHeader) {
                IconRowHeader irh = (IconRowHeader) c;
                irh.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        SwingUtilities.invokeLater(() -> {
                            if (breakpointToggleObservers != null && (bookmarks == null || bookmarks.length != irh.getBookmarks().length)) {
                                fireBookmarkToggleListener();
                            }
                            bookmarks = irh.getBookmarks();
                        });
                    }
                });
            }
        }
    }

    @RunOnDispatch
    private void fireBookmarkToggleListener() {
        for (BreakpointToggleObserver thisObserver : breakpointToggleObservers) {
            thisObserver.onBookmarkToggle(getBreakpoints());
        }
    }
}
