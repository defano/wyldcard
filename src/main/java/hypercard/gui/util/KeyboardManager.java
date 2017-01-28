package hypercard.gui.util;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class KeyboardManager {

    public static boolean isShiftDown;
    public static boolean isBreakSequence;

    private final static List<GlobalKeyEventObserver> observerList = new ArrayList<>();

    public interface GlobalKeyEventObserver{
        void onKeyEvent(KeyEvent e);
    }

    public static void start() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            isShiftDown = e.isShiftDown();
            isBreakSequence = e.getKeyCode() == KeyEvent.VK_PERIOD && (e.isMetaDown() || e.isControlDown());

            fireGlobalKeyEventObservers(e);

            return false;
        });
    }

    public static void addGlobalKeyEventObserver(GlobalKeyEventObserver observer) {
        observerList.add(observer);
    }

    public static boolean removeGlobalKeyEventObserver(GlobalKeyEventObserver observer) {
        return observerList.remove(observer);
    }

    private static void fireGlobalKeyEventObservers(KeyEvent e) {
        for (GlobalKeyEventObserver thisObserver : observerList) {
            thisObserver.onKeyEvent(e);
        }
    }

}
