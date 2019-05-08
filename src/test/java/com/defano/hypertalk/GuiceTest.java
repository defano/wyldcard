package com.defano.hypertalk;

import com.defano.wyldcard.NavigationManager;
import com.defano.wyldcard.StackManager;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.awt.keyboard.KeyboardManager;
import com.defano.wyldcard.awt.mouse.MouseManager;
import com.defano.wyldcard.cursor.CursorManager;
import com.defano.wyldcard.menubar.main.WyldCardMenuBar;
import com.defano.wyldcard.parts.editor.PartEditManager;
import com.defano.wyldcard.parts.wyldcard.WyldCardPart;
import com.defano.wyldcard.patterns.PatternManager;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.runtime.manager.PeriodicMessageManager;
import com.defano.wyldcard.runtime.manager.*;
import com.defano.wyldcard.search.SearchManager;
import com.defano.wyldcard.sound.SoundManager;
import com.defano.wyldcard.sound.SpeechPlaybackManager;
import com.defano.wyldcard.window.WindowManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.antlr.v4.runtime.ParserRuleContext;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.MockitoAnnotations.initMocks;

public class GuiceTest<T> implements TestDataGenerator {

    protected Injector injector;        // The Guice injector
    protected T uut;                    // The unit under test

    // Mock all managed WyldCard components
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected StackManager mockStackManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected MouseManager mockMouseManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected KeyboardManager mockKeyboardManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected WindowManager mockWindowManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected PaintManager mockPaintManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected FileManager mockFileManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected FontManager mockFontManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected SelectionManager mockSelectionManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected SoundManager mockSoundManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected SearchManager mockSearchManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected PartEditManager mockPartEditManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected PatternManager mockPatternManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected PeriodicMessageManager mockPeriodicMessageManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected CursorManager mockCursorManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected PartToolManager mockPartToolManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected SpeechPlaybackManager mockSpeechPlaybackManager;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected WyldCardMenuBar mockWyldCardMenuBar;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected WyldCardPart mockWyldCardPart;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) protected NavigationManager mockNavigationManager;

    // Statically initialize these so they can be referenced before call to initialize()
    protected ExecutionContext mockExecutionContext = Mockito.mock(ExecutionContext.class, Mockito.RETURNS_DEEP_STUBS);
    protected ParserRuleContext mockParserRuleContext = Mockito.mock(ParserRuleContext.class, Mockito.RETURNS_DEEP_STUBS);

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
        // Keep a reference of the UUT for convenience
        this.uut = unitUnderTest;

        // Process @Mock annotations found in the testcase
        initMocks(this);

        // Create an injector, and assemble a mock WyldCard
        injector = Guice.createInjector(new TestAssembly());
        WyldCard.setInjector(injector);

        // Inject Guice dependencies in the UUT
        if (uut != null) {
            inject(uut);
        }
    }

    public void initialize() {
        initialize(null);
    }

    public T inject(T uut) {
        injector.injectMembers(uut);
        return uut;
    }

    private class TestAssembly extends AbstractModule {
        @Override
        protected void configure() {
            bind(StackManager.class).toInstance(mockStackManager);
            bind(MouseManager.class).toInstance(mockMouseManager);
            bind(KeyboardManager.class).toInstance(mockKeyboardManager);
            bind(WindowManager.class).toInstance(mockWindowManager);
            bind(PaintManager.class).toInstance(mockPaintManager);
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
            bind(SpeechPlaybackManager.class).toInstance(mockSpeechPlaybackManager);
            bind(WyldCardMenuBar.class).toInstance(mockWyldCardMenuBar);
            bind(WyldCardPart.class).toInstance(mockWyldCardPart);
            bind(NavigationManager.class).toInstance(mockNavigationManager);
        }
    }

}
