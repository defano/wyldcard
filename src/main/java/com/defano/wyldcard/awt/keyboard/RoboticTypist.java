package com.defano.wyldcard.awt.keyboard;

import com.defano.hypertalk.ast.model.enums.ArrowDirection;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.thread.Invoke;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * Utility that can generate a series of KeyEvent sequences from a string. This bit of stupidity is required
 * because there is no built-in Java mechanism for converting a character into a sequence of KevEvents that
 * produce it.
 * <p>
 * Based on a solution suggested here,
 *  https://stackoverflow.com/questions/15260282/converting-a-char-into-java-keyevent-keycode
 */
public class RoboticTypist {

    private final static RoboticTypist instance = new RoboticTypist();
    private final HashMap<Character, KeyStroke> keystrokeMap = new HashMap<>();

    private Robot robot;

    private RoboticTypist() {
        for (int i = (int) '0'; i <= (int) '9'; i++) {
            keystrokeMap.put((char) i, new KeyStroke(i, false));
        }

        for (int i = (int) 'A'; i <= (int) 'Z'; i++) {
            keystrokeMap.put((char) i, new KeyStroke(i, true));
        }

        for (int i = (int) 'A'; i <= (int) 'Z'; i++) {
            keystrokeMap.put((char) (i + ((int) 'a' - (int) 'A')), new KeyStroke(i, false));
        }

        keystrokeMap.put('\n', new KeyStroke(KeyEvent.VK_ENTER, false));
        keystrokeMap.put('\t', new KeyStroke(KeyEvent.VK_TAB, false));
        keystrokeMap.put('\r', new KeyStroke(KeyEvent.VK_HOME, false));
        keystrokeMap.put(' ', new KeyStroke(KeyEvent.VK_SPACE, false));
        keystrokeMap.put('!', new KeyStroke(KeyEvent.VK_1, true));
        keystrokeMap.put('"', new KeyStroke(KeyEvent.VK_QUOTE, true));
        keystrokeMap.put('#', new KeyStroke(KeyEvent.VK_3, true));
        keystrokeMap.put('$', new KeyStroke(KeyEvent.VK_4, true));
        keystrokeMap.put('%', new KeyStroke(KeyEvent.VK_5, true));
        keystrokeMap.put('&', new KeyStroke(KeyEvent.VK_7, true));
        keystrokeMap.put('\'', new KeyStroke(KeyEvent.VK_QUOTE, false));
        keystrokeMap.put('(', new KeyStroke(KeyEvent.VK_9, true));
        keystrokeMap.put(')', new KeyStroke(KeyEvent.VK_0, true));
        keystrokeMap.put('*', new KeyStroke(KeyEvent.VK_8, true));
        keystrokeMap.put('+', new KeyStroke(KeyEvent.VK_EQUALS, true));
        keystrokeMap.put(',', new KeyStroke(KeyEvent.VK_COMMA, false));
        keystrokeMap.put('-', new KeyStroke(KeyEvent.VK_MINUS, false));
        keystrokeMap.put('.', new KeyStroke(KeyEvent.VK_PERIOD, false));
        keystrokeMap.put('/', new KeyStroke(KeyEvent.VK_SLASH, false));
        keystrokeMap.put(':', new KeyStroke(KeyEvent.VK_SEMICOLON, true));
        keystrokeMap.put(';', new KeyStroke(KeyEvent.VK_SEMICOLON, false));
        keystrokeMap.put('<', new KeyStroke(KeyEvent.VK_COMMA, true));
        keystrokeMap.put('=', new KeyStroke(KeyEvent.VK_EQUALS, false));
        keystrokeMap.put('>', new KeyStroke(KeyEvent.VK_PERIOD, true));
        keystrokeMap.put('?', new KeyStroke(KeyEvent.VK_SLASH, true));
        keystrokeMap.put('@', new KeyStroke(KeyEvent.VK_2, true));
        keystrokeMap.put('[', new KeyStroke(KeyEvent.VK_OPEN_BRACKET, false));
        keystrokeMap.put('\\', new KeyStroke(KeyEvent.VK_BACK_SLASH, false));
        keystrokeMap.put(']', new KeyStroke(KeyEvent.VK_CLOSE_BRACKET, false));
        keystrokeMap.put('^', new KeyStroke(KeyEvent.VK_6, true));
        keystrokeMap.put('_', new KeyStroke(KeyEvent.VK_MINUS, true));
        keystrokeMap.put('`', new KeyStroke(KeyEvent.VK_BACK_QUOTE, false));
        keystrokeMap.put('{', new KeyStroke(KeyEvent.VK_OPEN_BRACKET, true));
        keystrokeMap.put('|', new KeyStroke(KeyEvent.VK_BACK_SLASH, true));
        keystrokeMap.put('}', new KeyStroke(KeyEvent.VK_CLOSE_BRACKET, true));
        keystrokeMap.put('~', new KeyStroke(KeyEvent.VK_BACK_QUOTE, true));
    }

    public static RoboticTypist getInstance() {
        return instance;
    }

    public void typeEnter(Component c) {
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(synthesizeEnterKeyEvent(c));
    }

    public void type(ArrowDirection arrowKey) throws HtException {
        new KeyStroke(arrowKey.getKeyEvent(), false).type();
    }

    public void type(String string, ModifierKey... modifierKeys) throws HtException {
        for (Character thisChar : string.toCharArray()) {
            if (keystrokeMap.containsKey(thisChar)) {
                keystrokeMap.get(thisChar).type(modifierKeys);
            } else {
                throw new HtSemanticException("Sorry, don't know type this character: " + thisChar);
            }
        }
    }

    private KeyEvent synthesizeEnterKeyEvent(Component c) {
        return new KeyEvent(
                c,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_ENTER,
                '\n',
                KeyEvent.KEY_LOCATION_NUMPAD
        );
    }

    private class KeyStroke {
        private final int code;
        private final boolean isShifted;

        KeyStroke(int keyCode, boolean shift) {
            code = keyCode;
            isShifted = shift;
        }

        public void type(ModifierKey... modifierKeys) throws HtException {

            if (robot == null) {
                try {
                    robot = new Robot();
                } catch (AWTException e) {
                    throw new HtSemanticException("Sorry, scripted typing is not supported on this system.");
                }
            }

            if (isShifted) {
                robot.keyPress(KeyEvent.VK_SHIFT);
            }

            for (ModifierKey modifierKey : modifierKeys) {
                robot.keyPress(modifierKey.getKeyCode());
            }

            robot.keyPress(code);
            robot.keyRelease(code);

            if (isShifted) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }

            for (ModifierKey modifierKey : modifierKeys) {
                robot.keyRelease(modifierKey.getKeyCode());
            }

            if (code == KeyEvent.VK_ENTER) {
                robot.keyPress(KeyEvent.VK_HOME);
                robot.keyRelease(KeyEvent.VK_HOME);
            }

            Invoke.onDispatch(() -> {
                // Wait for queue to flush; more reliable then robot.waitForIdle()
            });
        }
    }

}

