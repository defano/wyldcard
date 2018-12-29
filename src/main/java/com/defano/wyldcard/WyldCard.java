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
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */
@Singleton
public class WyldCard implements PartFinder {

    private static WyldCard instance;
    private static Injector injector;

    @Inject private StackManager stackManager;
    @Inject private MouseManager mouseManager;
    @Inject private KeyboardManager keyboardManager;
    @Inject private WindowManager windowManager;
    @Inject private ToolsManager toolsManager;
    @Inject private FileManager fileManager;
    @Inject private FontManager fontManager;
    @Inject private SelectionManager selectionManager;
    @Inject private SoundManager soundManager;
    @Inject private SearchManager searchManager;
    @Inject private PartEditManager partEditManager;
    @Inject private PatternManager patternManager;
    @Inject private PeriodicMessageManager periodicMessageManager;
    @Inject private CursorManager cursorManager;
    @Inject private PartToolManager partToolManager;
    @Inject private SpeechPlaybackManager speechPlaybackManager;
    @Inject private WyldCardMenuBar wyldCardMenuBar;
    @Inject private WyldCardProperties wyldCardProperties;

    WyldCard() {}

    public static WyldCard getInstance() {
        return instance;
    }

    public static void main(String[] argv) {

        try {
            // Configure macOS environment
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WyldCard");
            System.setProperty("apple.awt.application.name", "WyldCard");
            System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");

        } catch (Exception e) {
            e.printStackTrace();
        }

        injector = Guice.createInjector(new WyldCardAssembly());
        instance = injector.getInstance(WyldCard.class);

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
     * Display a syntax error dialog containing, when a breadcrumb is available, an "edit script" button that launches
     * a script editor with the offending line highlighted.
     *
     * @param e
     */
    public void showErrorDialog(HtException e) {
        SwingUtilities.invokeLater(() -> HyperTalkErrorDialog.getInstance().showError(e));

        // Abort further script execution
        throw new ExitToHyperCardPreemption();
    }

    public MouseManager getMouseManager() {
        return mouseManager;
    }

    public KeyboardManager getKeyboardManager() {
        return keyboardManager;
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public ToolsManager getToolsManager() {
        return toolsManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public SearchManager getSearchManager() {
        return searchManager;
    }

    public PartEditManager getPartEditManager() {
        return partEditManager;
    }

    public PatternManager getPatternManager() {
        return patternManager;
    }

    public PeriodicMessageManager getPeriodicMessageManager() {
        return periodicMessageManager;
    }

    public CursorManager getCursorManager() {
        return cursorManager;
    }

    public PartToolManager getPartToolManager() {
        return partToolManager;
    }

    public SpeechPlaybackManager getSpeechPlaybackManager() {
        return speechPlaybackManager;
    }

    public StackManager getStackManager() {
        return stackManager;
    }

    public WyldCardProperties getWyldCardProperties() {
        return wyldCardProperties;
    }

    public WyldCardMenuBar getWyldCardMenuBar() {
        return wyldCardMenuBar;
    }

    /**
     * Returns the Google Guice injector used to assemble this object.
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
