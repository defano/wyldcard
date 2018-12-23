package com.defano.wyldcard;

import com.defano.hypertalk.ast.preemptions.ExitToHyperCardPreemption;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.DefaultKeyboardManager;
import com.defano.wyldcard.awt.DefaultMouseManager;
import com.defano.wyldcard.awt.KeyboardManager;
import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.cursor.CursorManager;
import com.defano.wyldcard.menubar.main.HyperCardMenuBar;
import com.defano.wyldcard.parts.editor.PartEditManager;
import com.defano.wyldcard.parts.finder.PartFinder;
import com.defano.wyldcard.patterns.PatternManager;
import com.defano.wyldcard.runtime.PeriodicMessageManager;
import com.defano.wyldcard.runtime.context.*;
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
public class WyldCard extends StackManager implements PartFinder {

    private static WyldCard instance;
    private static Injector injector;

    @Inject private MouseManager mouseManager;
    @Inject private KeyboardManager keyboardManager;
    @Inject private WindowManager windowManager;
    @Inject private ToolsManager toolsManager;
    @Inject private FileManager fileManager;
    @Inject private FontManager fontManager;
    @Inject private SelectionManager selectionManager;

    WyldCard() {}

    public static WyldCard getInstance() {
        return instance;
    }

    public static void main(String argv[]) {

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
            PartEditManager.getInstance().start();              // Button field movement and resize management
            windowManager.start();                              // Window and palette management
            PatternManager.getInstance().start();               // Update pattern palette on color changes
            PeriodicMessageManager.getInstance().start();       // Idle and mouseWithin periodic message generation
            CursorManager.getInstance().start();                // Mouse cursor assignment
            PartToolContext.getInstance().start();              // Button and field tool selection state

            newStack(new ExecutionContext());

            // Need to have an open stack before showing the menu bar
            HyperCardMenuBar.getInstance().reset();

            // Apply default palette layout
            WyldCard.getInstance().getWindowManager().restoreDefaultLayout();
            WyldCard.getInstance().getWindowManager().getPaintToolsPalette().toggleVisible();
            WyldCard.getInstance().getWindowManager().getPatternsPalette().toggleVisible();
        });

        // Close all open files before we die
        Runtime.getRuntime().addShutdownHook(new Thread(() -> WyldCard.getInstance().getFileManager().closeAll()));
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

    public static Injector getInjector() {
        return injector;
    }

    private static class WyldCardAssembly extends AbstractModule {
        @Override
        protected void configure() {
            bind(MouseManager.class).to(DefaultMouseManager.class);
            bind(KeyboardManager.class).to(DefaultKeyboardManager.class);
            bind(WindowManager.class).to(DefaultWindowManager.class);
            bind(ToolsManager.class).to(DefaultToolsManager.class);
            bind(FileManager.class).to(DefaultFileManager.class);
            bind(FontManager.class).to(DefaultFontManager.class);
            bind(SelectionManager.class).to(DefaultSelectionManager.class);
        }
    }
}
