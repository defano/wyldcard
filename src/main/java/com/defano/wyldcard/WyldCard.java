package com.defano.wyldcard;

import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import com.defano.wyldcard.awt.keyboard.KeyboardManager;
import com.defano.wyldcard.awt.keyboard.WyldCardKeyboardManager;
import com.defano.wyldcard.awt.mouse.MouseManager;
import com.defano.wyldcard.awt.mouse.WyldCardMouseManager;
import com.defano.wyldcard.cursor.CursorManager;
import com.defano.wyldcard.cursor.WyldCardCursorManager;
import com.defano.wyldcard.menu.main.MainWyldCardMenuBar;
import com.defano.wyldcard.menu.main.WyldCardMenuBar;
import com.defano.wyldcard.part.editor.PartEditManager;
import com.defano.wyldcard.part.editor.WyldCardPartEditManager;
import com.defano.wyldcard.part.finder.PartFinder;
import com.defano.wyldcard.part.wyldcard.WyldCardPart;
import com.defano.wyldcard.part.wyldcard.WyldCardProperties;
import com.defano.wyldcard.pattern.PatternManager;
import com.defano.wyldcard.pattern.WyldCardPatternManager;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.runtime.manager.*;
import com.defano.wyldcard.search.SearchManager;
import com.defano.wyldcard.search.WyldCardSearchManager;
import com.defano.wyldcard.sound.SoundManager;
import com.defano.wyldcard.sound.SpeechPlaybackManager;
import com.defano.wyldcard.sound.WyldCardSoundManager;
import com.defano.wyldcard.sound.WyldCardSpeechPlaybackManager;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.WyldCardWindowManager;
import com.defano.wyldcard.window.layout.HyperTalkErrorDialog;
import com.google.inject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The WyldCard application object.
 * <p>
 * A singleton object holding references to each "manager" class (a subsystem responsible for some area of
 * functionality).
 * <p>
 * This class (as well as all manager classes) are assembled using Google Guide dependency injection. Dependent classes
 * can get a reference to this object via the {@link #getInstance()} method, or simply by injecting the singleton using
 * a Guice annotation.
 */
@SuppressWarnings("unused")
@Singleton
public class WyldCard implements PartFinder {

    private static Logger LOG = LoggerFactory.getLogger(WyldCard.class);

    private static WyldCard instance;                                   // Application object graph
    private static Injector injector;                                   // Google Guice injector that built the app

    @Inject private StackManager stackManager;                          // Open, find and address stacks
    @Inject private NavigationManager navigationManager;                // Card and stack navigation methods
    @Inject private MouseManager mouseManager;                          // AWT mouse state handler (mouseLoc, etc.)
    @Inject private KeyboardManager keyboardManager;                    // AWT keyboard state handler (keyDown, etc.)
    @Inject private WindowManager windowManager;                        // Window and palette management
    @Inject private PaintManager paintManager;                          // Paint and part tools state management
    @Inject private FileManager fileManager;                            // Manages files for reading/writing in scripts
    @Inject private FontManager fontManager;                            // Font style selection state management
    @Inject private SelectionManager selectionManager;                  // Management of "the selection" text
    @Inject private SoundManager soundManager;                          // Sound playback management
    @Inject private SearchManager searchManager;                        // Search context management
    @Inject private PartEditManager partEditManager;                    // AWT management of button/field editing
    @Inject private PatternManager patternManager;                      // Automatically color patterns when color changes
    @Inject private PeriodicMessageManager periodicMessageManager;      // Send within and idle messages repeatedly
    @Inject private CursorManager cursorManager;                        // Mouse cursor management
    @Inject private PartToolManager partToolManager;                    // Button/field tool selection state
    @Inject private SpeechPlaybackManager speechPlaybackManager;        // Text to speech management
    @Inject private WyldCardMenuBar wyldCardMenuBar;                    // Main menubar
    @Inject private WyldCardPart wyldCardPart;                          // WyldCard script-addressable properties

    /**
     * Returns the singleton instance of the WyldCard application.
     * <p>
     * Note that this singleton is Guice-managed object and can be automatically injected into classes that depend on it
     * in lieu of retrieving it through this static method.
     *
     * @return The WyldCard singleton application instance.
     */
    public static WyldCard getInstance() {
        return instance;
    }

    /**
     * The application's main method. Invoked initially by the JVM on launch. Responsible for assembling the
     * application's object graph and setting system-level properties that must be provisioned before any AWT calls are
     * made.
     *
     * @param argv Arguments passed to WyldCard (not used)
     */
    public static void main(String[] argv) {

        // Configure macOS environment (must occur before any AWT calls are made)
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WyldCard");
            System.setProperty("apple.awt.application.name", "WyldCard");
            System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        } catch (Exception e) {
            LOG.error("An error occurred setting system properties.", e);
        }

        // Assemble WyldCard application object graph
        injector = Guice.createInjector(new WyldCardAssembly());
        instance = injector.getInstance(WyldCard.class);

        // We're ready to go... startup all the managers
        instance.startup();
    }

    /**
     * Starts the WyldCard application and all its managers after the application object graph has been initialized by
     * Guice.
     */
    private void startup() {

        Invoke.onDispatch(() -> {
            keyboardManager.start();                            // Global key event handler
            mouseManager.start();                               // Global mouse event and mouseLoc handler
            partEditManager.start();                            // Button field movement and resize management
            windowManager.start();                              // Window and palette management
            patternManager.start();                             // Update pattern palette on color changes
            periodicMessageManager.start();                     // Idle and mouseWithin periodic message generation
            cursorManager.start();                              // Mouse cursor assignment
            partToolManager.start();                            // Button and field tool selection state
            stackManager.start();                               // Stack opening, closing and disposal

            // Create and open a default stack
            stackManager.newStack(new ExecutionContext());

            // Need to have an open stack before showing the menu bar
            getWyldCardMenuBar().reset();

            getWindowManager().restoreDefaultLayout();          // Apply default palette layout
            getWindowManager().toggleDockPalettes();            // Dock palettes to stack window

            // Show tools and patterns palettes. Tool palette must be fully packed and rendered before showing
            // patterns, otherwise patterns palette may display in the wrong location.
            windowManager.getPaintToolsPalette().setVisible(true);
            windowManager.getPaintToolsPalette().addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    super.windowOpened(e);
                    windowManager.getPatternsPalette().setVisible(true);
                }
            });
        });

        // Close all open files before we die
        Runtime.getRuntime().addShutdownHook(new Thread(() -> fileManager.closeAll()));
    }

    /**
     * Display a syntax error dialog. When a breadcrumb is available, dialog displays an "edit script" button that
     * launches a script editor with the offending line highlighted.
     *
     * @param e The syntax error to display
     */
    public void showErrorDialogAndAbort(HtUncheckedSemanticException e) throws HtException {
        showErrorDialog(e.getHtCause());
        throw e.getHtCause();
    }

    public void showErrorDialog(HtException e) {
        SwingUtilities.invokeLater(() -> HyperTalkErrorDialog.getInstance().showError(e));
    }

    /**
     * Returns the {@link MouseManager} object (singleton). The {@link MouseManager} is responsible for AWT-level
     * handling of global mouse events (like 'the mouseLoc' and 'mouseDown').
     *
     * @return The mouse manager object.
     */
    public MouseManager getMouseManager() {
        return mouseManager;
    }

    /**
     * Returns the {@link KeyboardManager} object (singleton). The {@link KeyboardManager} is responsible for AWT-level
     * handling of global keyboard events (like tracking the state of modifier keys and command-. abort sequences).
     *
     * @return The keyboard manager object.
     */
    public KeyboardManager getKeyboardManager() {
        return keyboardManager;
    }

    /**
     * Returns the {@link WindowManager} object (singleton). The {@link WindowManager} holds references to tool palettes
     * and provides routines for finding the window object bound to stacks.
     *
     * @return The window manager object.
     */
    public WindowManager getWindowManager() {
        return windowManager;
    }

    /**
     * Returns the {@link PaintManager} object (singleton). The {@link PaintManager} maintains state for all the paint
     * tools (i.e., line size, selected pattern, etc.)
     *
     * @return The manager object.
     */
    public PaintManager getPaintManager() {
        return paintManager;
    }

    /**
     * Returns the {@link FileManager} object (singleton). The {@link FileManager} provides a facade on top of which
     * scripts open, read, write, and close text files.
     *
     * @return The file manager object.
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Returns the {@link FontManager} object (singleton). The {@link FontManager} maintains the font style state
     * associated with selected text and selections in the menu bar.
     *
     * @return The font manager object.
     */
    public FontManager getFontManager() {
        return fontManager;
    }

    /**
     * Returns the {@link SelectionManager} object (singleton). The {@link SelectionManager} maintains the state of text
     * and objects referencable as "selected" in script.
     *
     * @return The selection manager object.
     */
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    /**
     * Returns the {@link SoundManager} object (singleton). The {@link SoundManager} is responsible for managing sound
     * playback and queries about active playing sounds (i.e., 'the sound' function).
     *
     * @return The sound manager object.
     */
    public SoundManager getSoundManager() {
        return soundManager;
    }

    /**
     * Returns the {@link SearchManager} object (singleton). The {@link SearchManager} maintains the state and context
     * of the WyldCard search engine (i.e., active searches, found field highlights, etc.)
     *
     * @return The search manager object.
     */
    public SearchManager getSearchManager() {
        return searchManager;
    }

    /**
     * Returns the {@link PartToolManager} object (singleton). The {@link PartToolManager} manages the selection state
     * of buttons and fields (i.e., which part is selected).
     *
     * @return The part tool manager object.
     */
    public PartToolManager getPartToolManager() {
        return partToolManager;
    }

    /**
     * Returns the {@link PartEditManager} object (singleton). The {@link PartEditManager} provides the ability to move,
     * resize and edit button and field parts when the appropriate tool is active.
     *
     * @return The part edit manager object.
     */
    public PartEditManager getPartEditManager() {
        return partEditManager;
    }

    /**
     * Returns the {@link PatternManager} object (singleton). The {@link PatternManager} is responsible for updating
     * pattern renderings on the pattern palette when the color selection changes.
     *
     * @return The pattern manager object.
     */
    public PatternManager getPatternManager() {
        return patternManager;
    }

    /**
     * Returns the {@link PeriodicMessageManager} object (singleton). The {@link PeriodicMessageManager} is responsible
     * for sending "periodic" HyperTalk messages (those, like 'idle' and 'mouseWithin', that are sent repeatedly while
     * some condition is met).
     *
     * @return The periodic message manager object.
     */
    public PeriodicMessageManager getPeriodicMessageManager() {
        return periodicMessageManager;
    }

    /**
     * Returns the {@link CursorManager} object (singleton). The {@link CursorManager} provides a facade for querying
     * and setting the mouse cursor icon.
     *
     * @return The cursor manager object.
     */
    public CursorManager getCursorManager() {
        return cursorManager;
    }

    /**
     * Returns the {@link SpeechPlaybackManager} object (singleton). The {@link SpeechPlaybackManager} provides a
     * facade for text-to-speech related script functions.
     *
     * @return The speech playback manager object.
     */
    public SpeechPlaybackManager getSpeechPlaybackManager() {
        return speechPlaybackManager;
    }

    /**
     * Returns the {@link StackManager} object (singleton). The {@link StackManager} is responsible for managing the
     * state of open stacks, proving methods for finding, opening, focusing and closing stacks.
     *
     * @return The stack manager object.
     */
    public StackManager getStackManager() {
        return stackManager;
    }

    /**
     * Returns the {@link NavigationManager} object (singleton). The {@link NavigationManager} is responsible for
     * handling requests to navigate between cards and stacks.
     *
     * @return The navigation manager object.
     */
    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    /**
     * Returns the {@link WyldCardPart} object (singleton). The {@link WyldCardPart} object maintains the
     * state of every script-addressable systemwide property (like 'the itemDelimiter').
     *
     * @return The properties object.
     */
    public WyldCardPart getWyldCardPart() {
        return wyldCardPart;
    }

    /**
     * Returns the {@link WyldCardMenuBar} object (singleton). The {@link WyldCardMenuBar} object represents the normal
     * system menu bar. That is, the set of menus displayed when viewing a stack (distinct from the menus shown when
     * editing or debugging a script).
     *
     * @return The menubar object.
     */
    public WyldCardMenuBar getWyldCardMenuBar() {
        return wyldCardMenuBar;
    }

    /**
     * Returns the Google Guice injector used to assemble this object.
     *
     * @return The Guice injector
     */
    public static Injector getInjector() {
        return injector;
    }

    /**
     * Sets the Guice Injector used to assemble WyldCard, then creates the singleton WyldCard application instance
     * using the Injector.
     *
     * Primarily intended for test use to create a WyldCard instance injected with mock objects. Typically this method
     * should only be invoked once, prior to executing any code which depends on the WyldCard singleton.
     *
     * @param injector The Google Guice injector to use when assembling this managed Singleton.
     */
    public static void setInjector(Injector injector) {
        WyldCard.injector = injector;
        WyldCard.instance = injector.getInstance(WyldCard.class);
    }

    /**
     * The "normal" assembly of this managed singleton; binds manager classes and other managed singletons to their
     * default implementations.
     */
    private static class WyldCardAssembly extends AbstractModule {
        @Override
        protected void configure() {
            bind(StackManager.class).to(WyldCardStackManager.class);
            bind(MouseManager.class).to(WyldCardMouseManager.class);
            bind(KeyboardManager.class).to(WyldCardKeyboardManager.class);
            bind(WindowManager.class).to(WyldCardWindowManager.class);
            bind(PaintManager.class).to(WyldCardPaintManager.class);
            bind(FileManager.class).to(WyldCardFileManager.class);
            bind(FontManager.class).to(WyldCardFontManager.class);
            bind(SelectionManager.class).to(WyldCardSelectionManager.class);
            bind(SoundManager.class).to(WyldCardSoundManager.class);
            bind(SearchManager.class).to(WyldCardSearchManager.class);
            bind(PartEditManager.class).to(WyldCardPartEditManager.class);
            bind(PatternManager.class).to(WyldCardPatternManager.class);
            bind(PeriodicMessageManager.class).to(WyldCardPeriodicMessageManager.class);
            bind(CursorManager.class).to(WyldCardCursorManager.class);
            bind(PartToolManager.class).to(WyldCardPartToolManager.class);
            bind(SpeechPlaybackManager.class).to(WyldCardSpeechPlaybackManager.class);
            bind(WyldCardMenuBar.class).to(MainWyldCardMenuBar.class);
            bind(WyldCardProperties.class).to(WyldCardPart.class);
            bind(NavigationManager.class).to(WyldCardNavigationManager.class);
        }
    }
}
