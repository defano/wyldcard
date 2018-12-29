package com.defano.wyldcard;

import com.defano.hypertalk.ast.preemptions.ExitToHyperCardPreemption;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.DefaultKeyboardManager;
import com.defano.wyldcard.awt.DefaultMouseManager;
import com.defano.wyldcard.awt.KeyboardManager;
import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.cursor.CursorManager;
import com.defano.wyldcard.cursor.DefaultCursorManager;
import com.defano.wyldcard.menubar.main.DefaultWyldCardMenuBar;
import com.defano.wyldcard.menubar.main.WyldCardMenuBar;
import com.defano.wyldcard.parts.editor.DefaultPartEditManager;
import com.defano.wyldcard.parts.editor.PartEditManager;
import com.defano.wyldcard.parts.finder.PartFinder;
import com.defano.wyldcard.patterns.DefaultPatternManager;
import com.defano.wyldcard.patterns.PatternManager;
import com.defano.wyldcard.runtime.DefaultPeriodicMessageManager;
import com.defano.wyldcard.runtime.DefaultWyldCardProperties;
import com.defano.wyldcard.runtime.WyldCardProperties;
import com.defano.wyldcard.runtime.PeriodicMessageManager;
import com.defano.wyldcard.runtime.context.*;
import com.defano.wyldcard.search.DefaultSearchManager;
import com.defano.wyldcard.search.SearchManager;
import com.defano.wyldcard.sound.DefaultSoundManager;
import com.defano.wyldcard.sound.DefaultSpeechPlaybackManager;
import com.defano.wyldcard.sound.SoundManager;
import com.defano.wyldcard.sound.SpeechPlaybackManager;
import com.defano.wyldcard.window.DefaultWindowManager;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.layouts.HyperTalkErrorDialog;
import com.google.inject.*;

import javax.swing.*;

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

    private static WyldCard instance;                                   // Application object graph
    private static Injector injector;                                   // Google Guice injector that built the app

    @Inject private StackManager stackManager;                          // Open, find and address stacks
    @Inject private MouseManager mouseManager;                          // AWT mouse state handler (mouseLoc, etc.)
    @Inject private KeyboardManager keyboardManager;                    // AWT keyboard state handler (keyDown, etc.)
    @Inject private WindowManager windowManager;                        // Window and palette management
    @Inject private ToolsManager toolsManager;                          // Paint and part tools state management
    @Inject private FileManager fileManager;                            // Manages files for reading/writing in scripts
    @Inject private FontManager fontManager;                            // Font style selection state management
    @Inject private SelectionManager selectionManager;                  // Management of "the selection" text
    @Inject private SoundManager soundManager;                          // Sound playback management
    @Inject private SearchManager searchManager;                        // Search context management
    @Inject private PartEditManager partEditManager;                    // AWT management of button/field editing
    @Inject private PatternManager patternManager;                      // Paint pattern management
    @Inject private PeriodicMessageManager periodicMessageManager;      // Send within and idle messages repeatedly
    @Inject private CursorManager cursorManager;                        // Mouse cursor management
    @Inject private PartToolManager partToolManager;                    // Button/field tool selection state
    @Inject private SpeechPlaybackManager speechPlaybackManager;        // Text to speech management
    @Inject private WyldCardMenuBar wyldCardMenuBar;                    // Main menubar
    @Inject private WyldCardProperties wyldCardProperties;              // WyldCard script-addressable properties

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
     * The application's main method. Invoked initially by the JVM on launch.
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
            e.printStackTrace();        // Should never occur
        }

        // Assemble WyldCard application object graph
        injector = Guice.createInjector(new WyldCardAssembly());
        instance = injector.getInstance(WyldCard.class);

        // We're ready to go... startup all the managers
        instance.startup();
    }

    private void startup() {

        SwingUtilities.invokeLater(() -> {
            keyboardManager.start();                            // Global key event handler
            mouseManager.start();                               // Global mouse event and mouseLoc handler
            partEditManager.start();                            // Button field movement and resize management
            windowManager.start();                              // Window and palette management
            patternManager.start();                             // Update pattern palette on color changes
            periodicMessageManager.start();                     // Idle and mouseWithin periodic message generation
            cursorManager.start();                              // Mouse cursor assignment
            partToolManager.start();                            // Button and field tool selection state

            // Create and open a default stack
            stackManager.newStack(new ExecutionContext());

            // Need to have an open stack before showing the menu bar
            WyldCard.getInstance().getWyldCardMenuBar().reset();

            // Apply default palette layout
            WyldCard.getInstance().getWindowManager().restoreDefaultLayout();
            WyldCard.getInstance().getWindowManager().getPaintToolsPalette().toggleVisible();
            WyldCard.getInstance().getWindowManager().getPatternsPalette().toggleVisible();
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
    public void showErrorDialog(HtException e) {
        SwingUtilities.invokeLater(() -> HyperTalkErrorDialog.getInstance().showError(e));

        // Abort further script execution
        throw new ExitToHyperCardPreemption();
    }

    /**
     * Returns the {@link MouseManager} object (singleton). The {@link MouseManager} is responsible for AWT-level
     * handling of global mouse events (like 'the mouseLoc' and 'mouseDown').
     *
     * @return The manager object.
     */
    public MouseManager getMouseManager() {
        return mouseManager;
    }

    /**
     * Returns the {@link KeyboardManager} object (singleton). The {@link KeyboardManager} is responsible for AWT-level
     * handling of global keyboard events (like tracking the state of modifier keys and command-. abort sequences).
     *
     * @return The manager object.
     */
    public KeyboardManager getKeyboardManager() {
        return keyboardManager;
    }

    /**
     * Returns the {@link WindowManager} object (singleton). The {@link WindowManager} holds references to tool palettes
     * and provides routines for finding the window object bound to stacks.
     *
     * @return The manager object.
     */
    public WindowManager getWindowManager() {
        return windowManager;
    }

    /**
     * Returns the {@link ToolsManager} object (singleton). The {@link ToolsManager} maintains state for all the paint
     * tools (i.e., line size, selected pattern, etc.)
     *
     * @return The manager object.
     */
    public ToolsManager getToolsManager() {
        return toolsManager;
    }

    /**
     * Returns the {@link FileManager} object (singleton). The {@link FileManager} provides a facade on top of which
     * scripts open, read, write, and close text files.
     *
     * @return The manager object.
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Returns the {@link FontManager} object (singleton). The {@link FontManager} maintains the font style state
     * associated with selected text and selections in the menu bar.
     *
     * @return The manager object.
     */
    public FontManager getFontManager() {
        return fontManager;
    }

    /**
     * Returns the {@link SelectionManager} object (singleton). The {@link SelectionManager} maintains the state of text
     * and objects referencable as "selected" in script.
     *
     * @return The manager object.
     */
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    /**
     * Returns the {@link SoundManager} object (singleton). The {@link SoundManager} is responsible for managing sound
     * playback and queries about active playing sounds (i.e., 'the sound' function).
     *
     * @return The manager object.
     */
    public SoundManager getSoundManager() {
        return soundManager;
    }

    /**
     * Returns the {@link SearchManager} object (singleton). The {@link SearchManager} maintains the state and context
     * of the WyldCard search engine (i.e., active searches, found field highlights, etc.)
     *
     * @return The manager object.
     */
    public SearchManager getSearchManager() {
        return searchManager;
    }

    /**
     * Returns the {@link PartToolManager} object (singleton). The {@link PartToolManager} manages the selection state
     * of buttons and fields (i.e., which part is selected).
     *
     * @return The manager object.
     */
    public PartToolManager getPartToolManager() {
        return partToolManager;
    }

    /**
     * Returns the {@link PartEditManager} object (singleton). The {@link PartEditManager} provides the ability to move,
     * resize and edit button and field parts when the appropriate tool is active.
     *
     * @return The manager object.
     */
    public PartEditManager getPartEditManager() {
        return partEditManager;
    }

    /**
     * Returns the {@link PatternManager} object (singleton). The {@link PatternManager} provides a facade for getting
     * and editing paint patterns.
     *
     * @return The manager object.
     */
    public PatternManager getPatternManager() {
        return patternManager;
    }

    /**
     * Returns the {@link PeriodicMessageManager} object (singleton). The {@link PeriodicMessageManager} is responsible
     * for sending "periodic" HyperTalk messages (those, like 'idle' and 'mouseWithin', that are sent repeatedly while
     * some condition is met).
     *
     * @return The manager object.
     */
    public PeriodicMessageManager getPeriodicMessageManager() {
        return periodicMessageManager;
    }

    /**
     * Returns the {@link CursorManager} object (singleton). The {@link CursorManager} provides a facade for querying
     * and setting the mouse cursor icon.
     *
     * @return The manager object.
     */
    public CursorManager getCursorManager() {
        return cursorManager;
    }

    /**
     * Returns the {@link SpeechPlaybackManager} object (singleton). The {@link SpeechPlaybackManager} provides a
     * facade for text-to-speech related script functions.
     *
     * @return The manager object.
     */
    public SpeechPlaybackManager getSpeechPlaybackManager() {
        return speechPlaybackManager;
    }

    /**
     * Returns the {@link StackManager} object (singleton). The {@link StackManager} is responsible for managing the
     * state of open stacks, proving methods for finding, opening, focusing and closing stacks.
     *
     * @return The manager object.
     */
    public StackManager getStackManager() {
        return stackManager;
    }

    /**
     * Returns the {@link WyldCardProperties} object (singleton). The {@link WyldCardProperties} object maintains the
     * state of every script-addressable systemwide property (like 'the itemDelimiter').
     *
     * @return The properties object.
     */
    public WyldCardProperties getWyldCardProperties() {
        return wyldCardProperties;
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
     * Sets the Guice Injector used to assemble this object, then creates the singleton instance using this Injector.
     *
     * Intended for test use to create a WyldCard instance injected with mock managed objects. Typically this method
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
            bind(StackManager.class).to(DefaultStackManager.class);
            bind(MouseManager.class).to(DefaultMouseManager.class);
            bind(KeyboardManager.class).to(DefaultKeyboardManager.class);
            bind(WindowManager.class).to(DefaultWindowManager.class);
            bind(ToolsManager.class).to(DefaultToolsManager.class);
            bind(FileManager.class).to(DefaultFileManager.class);
            bind(FontManager.class).to(DefaultFontManager.class);
            bind(SelectionManager.class).to(DefaultSelectionManager.class);
            bind(SoundManager.class).to(DefaultSoundManager.class);
            bind(SearchManager.class).to(DefaultSearchManager.class);
            bind(PartEditManager.class).to(DefaultPartEditManager.class);
            bind(PatternManager.class).to(DefaultPatternManager.class);
            bind(PeriodicMessageManager.class).to(DefaultPeriodicMessageManager.class);
            bind(CursorManager.class).to(DefaultCursorManager.class);
            bind(PartToolManager.class).to(DefaultPartToolManager.class);
            bind(SpeechPlaybackManager.class).to(DefaultSpeechPlaybackManager.class);
            bind(WyldCardMenuBar.class).to(DefaultWyldCardMenuBar.class);
            bind(WyldCardProperties.class).to(DefaultWyldCardProperties.class);
        }
    }
}
