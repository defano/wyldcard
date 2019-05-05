package com.defano.wyldcard;

import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.ast.model.specifiers.StackPartSpecifier;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import io.reactivex.Observable;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface StackManager {

    /**
     * Start the stack manager. The behavior of the manager is n
     */
    void start();

    /**
     * Creates and opens a new stack in a new stack window.
     *
     * @param context The current execution context
     */
    void newStack(ExecutionContext context);

    /**
     * Attempts to locate a specified stack, prompting the user to locate the stack on disk if necessary.
     *
     * @param context   The current execution context
     * @param specifier A specifier identifying the stack we're looking for
     * @param options   Navigation options (like 'in a new window' or 'without dialog')
     * @return The located stack, or null if the stack could not be located.
     */
    StackModel findStack(ExecutionContext context, StackPartSpecifier specifier, RemoteNavigationOptions options);

    /**
     * Attempts to load the specified stack file into memory and demoralize it into a StackModel object. Does not
     * "open", display, or otherwise make the stack available to WyldCard.
     *
     * @param context   The execution context.
     * @param stackFile The serialized stack file.
     * @return The deserialized stack model loaded from the given file, or null if the file cannot be loaded for any
     * reason.
     */
    StackModel loadStack(ExecutionContext context, File stackFile);

    /**
     * Prompts the user to select a stack file from disk for opening.
     *
     * @param context     The current execution context
     * @param inNewWindow When true, the stack will open in a new stack window. When false, the opened stack will
     *                    replace the stack active in the execution context (i.e., the focused stack or stack requesting
     *                    to open a new stack).
     * @param title       The title to be displayed in the Open/Find file dialog.
     */
    StackPart findAndOpenStack(ExecutionContext context, boolean inNewWindow, String title);

    StackPart openStack(ExecutionContext context, StackModel model, boolean inNewWindow);

    /**
     * Gets a list of all currently open stacks (those which are displayed inside of a window).
     *
     * @return The list of open stacks.
     */
    List<StackPart> getOpenStacks();

    /**
     * Gets the opened stack associated with the given model, or null if no such stack exists.
     *
     * @param model The stack model whose stack should be located
     * @return The opened stack associated with this model.
     */
    StackPart getOpenStack(StackModel model);

    /**
     * Saves the given stack model to a file/path chosen by the user.
     *
     * @param context    The execution context
     * @param stackModel The stack to be saved
     */
    void saveStackAs(ExecutionContext context, StackModel stackModel);

    /**
     * Saves the given stack model to its associated file (acts like "Save as" if no file is associated).
     *
     * @param context    The execution context.
     * @param stackModel The StackModel to save; when null the focused stack is assumed.
     */
    void saveStack(ExecutionContext context, StackModel stackModel);

    /**
     * Writes the serialized stack data into the given file. Prompts the "Save as..." dialog if the given file is null.
     * Sets the result to empty if stack is saved successfully, otherwise, puts the error message into the result.
     *
     * @param context    The execution context.
     * @param stackModel The model of the stack to be saved
     * @param file       The file where the stack should be saved
     */
    void saveStack(ExecutionContext context, StackModel stackModel, File file);

    /**
     * Closes all of the open stack windows (prompting as required to save); when all stacks have closed, the
     * application exits.
     *
     * @param context The execution context
     */
    void closeAllStacks(ExecutionContext context);

    /**
     * Attempts to close the requested stack, prompting the user to save it (when necessary) before closing. Note that
     * the when prompted to save, the user can cancel, resulting in the stack not being closed.
     *
     * @param context   The execution context
     * @param stackPart The stack to be closed; when null, closes the focused stack.
     */
    void closeStack(ExecutionContext context, StackPart stackPart);

    /**
     * Make the given stack the focused, or active, stack. That is, the stack that is acted upon by the menus, message
     * box and other "unbound" execution contexts.
     * <p>
     * Note that this method does not actually attempt to bring the specified stack's window to the front or otherwise
     * adjust the focus subsystem's view of the world..
     *
     * @param stackPart The stack part to focus.
     */
    void focusStack(StackPart stackPart);

    /**
     * Gets the stack that currently has focus.
     *
     * @return The active stack
     */
    StackPart getFocusedStack();

    /**
     * Gets an observable of the focused stack.
     *
     * @return The focused stack observable.
     */
    Observable<StackPart> getFocusedStackProvider();

    /**
     * Gets the card currently displayed in the focused stack window.
     * <p>
     * Note that HyperTalk-implementing classes should always use {@link ExecutionContext#getCurrentCard()} to retrieve
     * a reference to the current card, since, from the perspective of a script the focused card may differ from the
     * "current" card under certain runtime conditions (like stack sorting or when a script is executing in an un-
     * focused stack window).
     *
     * @return The card currently displayed in the focused stack window.
     */
    CardPart getFocusedCard();

    /**
     * Gets an observable count of the number of cards present in the active stack.
     *
     * @return An observable count of cards in the active stack.
     */
    Observable<Integer> getFocusedStackCardCountProvider();

    /**
     * Gets an observable "clipboard" of cards from the active stack. This clipboard is non-empty when a card in the
     * active stack has been cut or copied from the stack.
     *
     * @return The card clipboard associated with the active stack.
     */
    Observable<Optional<CardPart>> getFocusedStackCardClipboardProvider();

    /**
     * Gets an observable optional file representing the file to which the active stack is current bound (that is, the
     * file where the stack was last saved or opened from).
     * <p>
     * An empty optional indicates the stack has never been saved and has no associated file.
     *
     * @return An observable optional file.
     */
    Observable<Optional<File>> getSavedStackFileProvider();

    Observable<Boolean> getIsUndoableProvider();

    Observable<Boolean> getIsRedoableProvider();

    Observable<Boolean> getIsSelectableProvider();

    Observable<Double> getScaleProvider();
}
