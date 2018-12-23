package com.defano.hypertalk;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.awt.KeyboardManager;
import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.cursor.CursorManager;
import com.defano.wyldcard.parts.editor.PartEditManager;
import com.defano.wyldcard.patterns.PatternManager;
import com.defano.wyldcard.runtime.PeriodicMessageManager;
import com.defano.wyldcard.runtime.context.*;
import com.defano.wyldcard.search.SearchManager;
import com.defano.wyldcard.sound.SoundManager;
import com.defano.wyldcard.window.WindowManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.antlr.v4.runtime.ParserRuleContext;
import org.mockito.Answers;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

public class GuiceTest<T> {

    protected Injector injector;        // The Guice injector
    protected T uut;                    // The unit under test

    // Mock WyldCard components
    @Mock protected MouseManager mockMouseManager;
    @Mock protected KeyboardManager mockKeyboardManager;
    @Mock protected WindowManager mockWindowManager;
    @Mock protected ToolsManager mockToolsManager;
    @Mock protected FileManager mockFileManager;
    @Mock protected FontManager mockFontManager;
    @Mock protected SelectionManager mockSelectionManager;
    @Mock protected SoundManager mockSoundManager;
    @Mock protected SearchManager mockSearchManager;
    @Mock protected PartEditManager mockPartEditManager;
    @Mock protected PatternManager mockPatternManager;
    @Mock protected PeriodicMessageManager mockPeriodicMessageManager;
    @Mock protected CursorManager mockCursorManager;
    @Mock protected PartToolManager mockPartToolManager;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    protected ExecutionContext mockExecutionContext;
    @Mock protected ParserRuleContext mockParserRuleContext;
    @Mock protected WyldCard wyldCard;

    /**
     * Initializes this testcase by performing two functions:
     *
     * 1. Creates mock objects for all fields annotated with {@link Mock}.
     * 2. Injects Guice dependencies into the unit under test (all components are bound to mocks).
     *
     * GOTCHA: This method (obviously) executes after the unit under test has been instantiated. Therefore, if any
     * arguments passed to the UUT require mocks or Guice injections themselves, that must be handled manually and
     * without the use of {@link Mock} annotation (which will not yet have been processed).
     *
     * @param unitUnderTest The unit to be tested
     */
    public void initialize(T unitUnderTest) {
        this.uut = unitUnderTest;

        initMocks(this);

        injector = Guice.createInjector(new TestAssembly());
        injector.injectMembers(uut);
    }

    private class TestAssembly extends AbstractModule {
        @Override
        protected void configure() {
            bind(MouseManager.class).toInstance(mockMouseManager);
            bind(KeyboardManager.class).toInstance(mockKeyboardManager);
            bind(WindowManager.class).toInstance(mockWindowManager);
            bind(ToolsManager.class).toInstance(mockToolsManager);
            bind(FileManager.class).toInstance(mockFileManager);
            bind(FontManager.class).toInstance(mockFontManager);
            bind(SelectionManager.class).toInstance(mockSelectionManager);
            bind(SoundManager.class).toInstance(mockSoundManager);
            bind(SearchManager.class).toInstance(mockSearchManager);
            bind(PartEditManager.class).toInstance(mockPartEditManager);
            bind(PatternManager.class).toInstance(mockPatternManager);
            bind(PeriodicMessageManager.class).toInstance(mockPeriodicMessageManager);
            bind(CursorManager.class).toInstance(mockCursorManager);
            bind(PartToolManager.class).toInstance(mockPartToolManager);

            bind(WyldCard.class).toInstance(wyldCard);
        }
    }

}
