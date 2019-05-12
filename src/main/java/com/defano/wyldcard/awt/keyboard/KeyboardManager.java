package com.defano.wyldcard.awt.keyboard;

import com.defano.wyldcard.runtime.ExecutionContext;

import java.awt.event.KeyListener;

public interface KeyboardManager {

    /**
     * Starts the KeyboardManager.
     * <p>
     * This method should be invoked only once at application start. Key listeners and other methods provided by the
     * manager will not function property until the manager has been started.
     */
    void start();

    /**
     * Registers a listener of all key events received by the WyldCard application, irrespective of which component
     * was the target of the event.
     * <p>
     * Note that global key events apply only to key events received while WyldCard has focus in the host operating
     * system. Listeners are NOT fired when the application is not in focus.
     *
     * @param observer The global key event listener. Has no effect if the listener is already registered.
     */
    void addGlobalKeyListener(KeyListener observer);

    /**
     * Unregisters a global key event listener that was previously registered via a call to
     * {@link #addGlobalKeyListener(KeyListener)}. Has no effect if the listener is not registered.
     *
     * @param observer The global event listener that should be unregistered.
     * @return True if observer was succesfully unregistered; false otherwise (indicating the observer was not
     * registered)
     */
    boolean removeGlobalKeyListener(KeyListener observer);

    /**
     * Gets the time returned by {@link System#currentTimeMillis()} at which the break sequence (ctrl-.) was last typed,
     * or null if it has never been typed.
     *
     * @return The last time that ctrl-. was typed, or null if never.
     */
    Long getBreakTime();

    /**
     * Gets the state of the shift key as tracked by WyldCard. Note that the true state of the shift key cannot be
     * determined when WyldCard loses focus in the host OS. See the note associated with {@link #resetKeyStates()}.
     *
     * @return True if the shift key is down, false otherwise.
     */
    boolean isShiftDown();

    /**
     * Gets the state of the alt or option key as tracked by WyldCard. Note that the true state of the key cannot
     * be determined when WyldCard loses focus in the host OS. See the note associated with {@link #resetKeyStates()}.
     *
     * @return True if the alt or option key is down, false otherwise.
     */
    boolean isAltOptionDown();

    /**
     * Gets the state of the ctrl or command key as tracked by WyldCard. Note that the true state of the key cannot
     * be determined when WyldCard loses focus in the host OS. See the note associated with {@link #resetKeyStates()}.
     *
     * @return True if the ctrl or command key is down, false otherwise.
     */
    boolean isCtrlCommandDown();

    /**
     * Determines if the user is holding the "peek" keyboard sequence (command-option) and that the stack in focus is
     * currently allowing peeking.
     *
     * @param context The execution context
     * @return True if user is peeking, false otherwise.
     */
    boolean isPeeking(ExecutionContext context);

    /**
     * Resets the state returned by {@link #isShiftDown()}, {@link #isAltOptionDown()}, and {@link #isCtrlCommandDown()}
     * to false.
     * <p>
     * This method should be invoked whenever the app regains system focus to assure modifier key states are reset to
     * 'up'.
     * <p>
     * Because Java cannot track key events that occur when another process on the host OS has focus, and because there
     * is no API for querying the current state of the keyboard, we track the state of modifier keys locally in a key
     * event listener. However, if this application loses system focus while shift, option, command or control is down
     * and the user releases those keys while the app is out of focus, they will appear "stuck" in down state until the
     * user presses and releases them while WyldCard is focused. Since we cannot detect this state, its safer to assume
     * that these modifier keys are in the up state whenever WyldCard re-gains focus.
     */
    void resetKeyStates();
}
