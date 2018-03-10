package com.defano.wyldcard.awt;

import com.defano.wyldcard.window.WindowManager;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

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
    private final int commandKey;

    private Robot robot;

    private RoboticTypist() {
        commandKey = WindowManager.getInstance().isMacOs() ? KeyEvent.VK_META : KeyEvent.VK_CONTROL;

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
        for (int i = (int) '0'; i <= (int) '9'; i++) {
            keystrokeMap.put((char) i, new KeyStroke(i, false));
        }
        keystrokeMap.put(':', new KeyStroke(KeyEvent.VK_SEMICOLON, true));
        keystrokeMap.put(';', new KeyStroke(KeyEvent.VK_SEMICOLON, false));
        keystrokeMap.put('<', new KeyStroke(KeyEvent.VK_COMMA, true));
        keystrokeMap.put('=', new KeyStroke(KeyEvent.VK_EQUALS, false));
        keystrokeMap.put('>', new KeyStroke(KeyEvent.VK_PERIOD, true));
        keystrokeMap.put('?', new KeyStroke(KeyEvent.VK_SLASH, true));
        keystrokeMap.put('@', new KeyStroke(KeyEvent.VK_2, true));
        for (int i = (int) 'A'; i <= (int) 'Z'; i++) {
            keystrokeMap.put((char) i, new KeyStroke(i, true));
        }
        keystrokeMap.put('[', new KeyStroke(KeyEvent.VK_OPEN_BRACKET, false));
        keystrokeMap.put('\\', new KeyStroke(KeyEvent.VK_BACK_SLASH, false));
        keystrokeMap.put(']', new KeyStroke(KeyEvent.VK_CLOSE_BRACKET, false));
        keystrokeMap.put('^', new KeyStroke(KeyEvent.VK_6, true));
        keystrokeMap.put('_', new KeyStroke(KeyEvent.VK_MINUS, true));
        keystrokeMap.put('`', new KeyStroke(KeyEvent.VK_BACK_QUOTE, false));
        for (int i = (int) 'A'; i <= (int) 'Z'; i++) {
            keystrokeMap.put((char) (i + ((int) 'a' - (int) 'A')), new KeyStroke(i, false));
        }
        keystrokeMap.put('{', new KeyStroke(KeyEvent.VK_OPEN_BRACKET, true));
        keystrokeMap.put('|', new KeyStroke(KeyEvent.VK_BACK_SLASH, true));
        keystrokeMap.put('}', new KeyStroke(KeyEvent.VK_CLOSE_BRACKET, true));
        keystrokeMap.put('~', new KeyStroke(KeyEvent.VK_BACK_QUOTE, true));
    }

    public static RoboticTypist getInstance() {
        return instance;
    }

    public void type(String string, boolean withCommandKey) throws HtException {
        for (Character thisChar : string.toCharArray()) {
            if (keystrokeMap.containsKey(thisChar)) {
                keystrokeMap.get(thisChar).type(withCommandKey);
            } else {
                throw new HtSemanticException("Sorry, don't know type this character: " + thisChar);
            }
        }
    }

    private class KeyStroke {
        private final int code;
        private final boolean isShifted;

        KeyStroke(int keyCode, boolean shift) {
            code = keyCode;
            isShifted = shift;
        }

        public void type(boolean withCommandKey) throws HtException {

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

            if (withCommandKey) {
                robot.keyPress(commandKey);
            }

            robot.keyPress(code);
            robot.keyRelease(code);

            if (isShifted) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }

            if (withCommandKey) {
                robot.keyRelease(commandKey);
            }

            if (code == KeyEvent.VK_ENTER) {
                robot.keyPress(KeyEvent.VK_HOME);
                robot.keyRelease(KeyEvent.VK_HOME);
            }

            try {
                robot.waitForIdle();
            } catch (IllegalThreadStateException e) {
                // Nothing to do; thrown if executing on dispatch thread
            }
        }
    }

}

