package com.defano.wyldcard;

import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.ast.model.SystemMessage;
import com.defano.hypertalk.ast.model.specifiers.StackPartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.parts.stack.StackNavigationObserver;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.PartToolContext;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.runtime.serializer.Serializer;
import com.defano.wyldcard.util.ProxyObservable;
import com.defano.wyldcard.window.WindowDock;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.WyldCardFrame;
import com.defano.wyldcard.window.layouts.StackWindow;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages opening, closing, saving and focusing stacks.
 */
public class StackManager implements StackNavigationObserver {

    private final BehaviorSubject<StackPart> focusedStack = BehaviorSubject.create();
    private final ProxyObservable<Integer> cardCount = new ProxyObservable<>(BehaviorSubject.createDefault(1));
    private final ProxyObservable<Optional<CardPart>> cardClipboard = new ProxyObservable<>(BehaviorSubject.createDefault(Optional.empty()));
    private final ProxyObservable<Optional<File>> savedStackFile = new ProxyObservable<>(BehaviorSubject.createDefault(Optional.empty()));
    private final ProxyObservable<Boolean> isUndoable = new ProxyObservable<>(BehaviorSubject.createDefault(false));
    private final ProxyObservable<Boolean> isRedoable = new ProxyObservable<>(BehaviorSubject.createDefault(false));

    /**
     * Creates and opens a new stack in a new stack window.
     *
     * @param context The current execution context
     */
    public void newStack(ExecutionContext context) {
        displayStack(context, StackPart.newStack(context), true);
    }

    /**
     * Attempts to locate a specified stack, prompting the user to locate the stack on disk if necessary.
     *
     * @param context   The current execution context
     * @param specifier A specifier identifying the stack we're looking for
     * @param options   Navigation options (like 'in a new window' or 'without dialog')
     * @return The located stack, or null if the stack could not be located.
     */
    public StackModel locateStack(ExecutionContext context, StackPartSpecifier specifier, RemoteNavigationOptions options) {
        try {
            // Try to find the requested stack among those already opened...
            return WyldCard.getInstance().findStackPart(context, specifier);
        } catch (PartException e) {

            // ... if that fails, interpret the specifier as a file name
            StackPart foundStack = openStack(context, new File(String.valueOf(specifier.getValue())), options.inNewWindow);
            if (foundStack != null) {
                return foundStack.getStackModel();
            }

            // ... and finally tonight, prompt the user to choose the stack
            if (!options.withoutDialog) {
                StackPart openedStack = openStack(context, options.inNewWindow, "Where is stack " + specifier.getValue() + "?");
                if (openedStack != null) {
                    return openedStack.getStackModel();
                }
            }
        }

        return null;
    }

    /**
     * Prompts the user to select a stack file from disk for opening.
     *
     * @param context     The current execution context
     * @param inNewWindow When true, the stack will open in a new stack window. When false, the opened stack will
     *                    replace the stack active in the execution context (i.e., the focused stack or stack requesting
     *                    to open a new stack).
     */
    public StackPart openStack(ExecutionContext context, boolean inNewWindow, String title) {
        FileDialog fd = new FileDialog(WindowManager.getInstance().getWindowForStack(context, context.getCurrentStack()).getWindow(), title, FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setFilenameFilter((dir, name) -> name.endsWith(StackModel.FILE_EXTENSION));
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            return openStack(context, fd.getFiles()[0], inNewWindow);
        }

        return null;
    }

    /**
     * Attempts to open the identified stack file.
     *
     * @param context     The current execution context
     * @param stackFile   The stack file to open
     * @param inNewWindow When true, the stack will open in a new stack window. When false, the opened stack will
     *                    replace the stack active in the execution context (i.e., the focused stack or stack requesting
     *                    to open a new stack).
     * @return The opened StackPart or null if the stack could not be opened for any reason.
     */
    private StackPart openStack(ExecutionContext context, File stackFile, boolean inNewWindow) {
        try {
            StackModel model = Serializer.deserialize(stackFile, StackModel.class);
            StackPart part = StackPart.fromStackModel(context, model);

            model.setSavedStackFile(context, stackFile);
            displayStack(context, part, inNewWindow);

            return part;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets a list of all currently open stacks (those which are displayed inside of a window).
     *
     * @return The list of open stacks.
     */
    public List<StackPart> getOpenStacks() {
        ArrayList<StackPart> stacks = new ArrayList<>();
        for (WyldCardFrame thisWindow : WindowManager.getInstance().getFrames(false)) {
            if (thisWindow instanceof StackWindow && thisWindow.getWindow().isVisible()) {
                stacks.add(((StackWindow) thisWindow).getStack());
            }
        }
        return stacks;
    }

    /**
     * Saves the given stack model to a file/path chosen by the user.
     *
     * @param context    The execution context
     * @param stackModel The stack to be saved
     */
    public void saveStackAs(ExecutionContext context, StackModel stackModel) {
        String defaultName = "Untitled";

        if (WyldCard.getInstance().getSavedStackFileProvider().blockingFirst().isPresent()) {
            defaultName = WyldCard.getInstance().getSavedStackFileProvider().blockingFirst().get().getName();
        } else if (stackModel.getStackName(context) != null && !stackModel.getStackName(context).isEmpty()) {
            defaultName = stackModel.getStackName(context);
        }

        FileDialog fd = new FileDialog(WindowManager.getInstance().getWindowForStack(context, context.getCurrentStack()).getWindow(), "Save Stack", FileDialog.SAVE);
        fd.setFile(defaultName);
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            File f = fd.getFiles()[0];
            String path = f.getAbsolutePath().endsWith(StackModel.FILE_EXTENSION) ?
                    f.getAbsolutePath() :
                    f.getAbsolutePath() + StackModel.FILE_EXTENSION;

            saveStack(context, stackModel, new File(path));
        }
    }

    /**
     * Saves the given stack model to its associated file (acts like "Save as" if no file is associated).
     *
     * @param context    The execution context.
     * @param stackModel The StackModel to save; when null the focused stack is assumed.
     */
    public void saveStack(ExecutionContext context, StackModel stackModel) {
        if (stackModel == null) {
            stackModel = getFocusedStack().getStackModel();
        }

        Optional<File> saveFile = savedStackFile.getObservable().blockingFirst();
        saveStack(context, stackModel, saveFile.orElse(null));
    }

    /**
     * Writes the serialized stack data into the given file. Prompts the "Save as..." dialog if the given file is null.
     *
     * @param context The execution context.
     * @param file    The file where the stack should be saved
     */
    private void saveStack(ExecutionContext context, StackModel stackModel, File file) {
        if (file != null) {
            try {
                Serializer.serialize(file, stackModel);
                stackModel.setSavedStackFile(context, file);
            } catch (IOException e) {
                WyldCard.getInstance().showErrorDialog(new HtSemanticException("An error occurred saving the file " + file.getAbsolutePath()));
            }
        } else {
            saveStackAs(context, stackModel);
        }
    }

    /**
     * Closes all of the open stack windows (prompting as required to save); when all stacks have closed, the
     * application exits.
     * @param context The execution context
     */
    public void closeAllStacks(ExecutionContext context) {
        for (StackPart thisOpenStack : getOpenStacks()) {
            closeStack(context, thisOpenStack);
        }
    }

    /**
     * Attempts to close the requested stack, prompting the user to save it (when necessary) before closing. Note that
     * the when prompted to save, the user can cancel, resulting in the stack not being closed.
     *
     * @param context The execution context
     * @param stackPart The stack to be closed
     */
    public void closeStack(ExecutionContext context, StackPart stackPart) {
        if (stackPart == null) {
            stackPart = getFocusedStack();
        }

        // Prompt user to save if stack is dirty
        if (stackPart.getStackModel().isStackDirty()) {
            int dialogResult = JOptionPane.showConfirmDialog(
                    stackPart.getDisplayedCard(),
                    "Save changes to " + stackPart.getStackModel().getStackName(null) + "?",
                    "Save",
                    JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.CLOSED_OPTION) {
                return;
            } else if (dialogResult == JOptionPane.YES_OPTION) {
                saveStack(new ExecutionContext(), stackPart.getStackModel());
            }
        }

        stackPart.partClosed(context);

        // Dispose the stack's frame (if one exists)
        if (stackPart.getOwningStackWindow() != null) {
            stackPart.getOwningStackWindow().dispose();
        }

        // Finally, quit application when last stack window has closed
        if (getOpenStacks().size() == 0) {
            System.exit(0);
        }
    }

    /**
     * Make the given stack the focused, or active, stack. That is, the stack that is acted upon by the menus, message
     * box and other "unbound" execution contexts.
     * <p>
     * Note that this method does not actually attempt to bring the specified stack's window to the front or otherwise
     * adjust the focus subsystem's view of the world..
     *
     * @param stackPart The stack part to focus.
     */
    public StackPart focusStack(StackPart stackPart) {

        // Don't refocus the focused stack
        if (focusedStack.hasValue() && focusedStack.blockingFirst() == stackPart) {
            return stackPart;
        }

        // Remove focus from last-focused stack when applicable
        if (focusedStack.hasValue()) {
            PartToolContext.getInstance().deselectAllParts();
            focusedStack.blockingFirst().removeNavigationObserver(this);

            // Send suspendStack/resumeStack messages
            focusedStack.blockingFirst().getDisplayedCard().getCardModel().receiveMessage(new ExecutionContext(focusedStack.blockingFirst()), SystemMessage.SUSPEND_STACK.messageName);
            stackPart.getDisplayedCard().getCardModel().receiveMessage(new ExecutionContext(stackPart), SystemMessage.RESUME_STACK.messageName);
        }

        // Make the focused stack the window dock
        WindowDock.getInstance().setDock(stackPart.getOwningStackWindow());

        // Make the selected tool active on the focused card
        ToolsContext.getInstance().reactivateTool(stackPart.getDisplayedCard().getCanvas());

        focusedStack.onNext(stackPart);
        cardCount.setSource(stackPart.getCardCountProvider());
        cardClipboard.setSource(stackPart.getCardClipboardProvider());
        savedStackFile.setSource(stackPart.getStackModel().getSavedStackFileProvider());
        isUndoable.setSource(stackPart.getDisplayedCard().getCanvas().isUndoableObservable());
        isRedoable.setSource(stackPart.getDisplayedCard().getCanvas().isRedoableObservable());
        stackPart.addNavigationObserver(this);

        return stackPart;
    }

    /**
     * Gets the stack that currently has focus.
     *
     * @return The active stack
     */
    public StackPart getFocusedStack() {
        if (focusedStack.hasValue()) {
            return focusedStack.blockingFirst();
        } else {
            return null;
        }
    }

    /**
     * Gets an observable of the focused stack.
     *
     * @return The focused stack observable.
     */
    public Observable<StackPart> getFocusedStackProvider() {
        return focusedStack;
    }

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
    public CardPart getFocusedCard() {
        if (getFocusedStack() == null) {
            return null;
        } else {
            return getFocusedStack().getDisplayedCard();
        }
    }

    /**
     * Gets an observable count of the number of cards present in the active stack.
     *
     * @return An observable count of cards in the active stack.
     */
    public Observable<Integer> getFocusedStackCardCountProvider() {
        return cardCount.getObservable();
    }

    /**
     * Gets an observable "clipboard" of cards from the active stack. This clipboard is non-empty when a card in the
     * active stack has been cut or copied from the stack.
     *
     * @return The card clipboard associated with the active stack.
     */
    public Observable<Optional<CardPart>> getFocusedStackCardClipboardProvider() {
        return cardClipboard.getObservable();
    }

    /**
     * Gets an observable optional file representing the file to which the active stack is current bound (that is, the
     * file where the stack was last saved or opened from).
     * <p>
     * An empty optional indicates the stack has never been saved and has no associated file.
     *
     * @return An observable optional file.
     */
    public Observable<Optional<File>> getSavedStackFileProvider() {
        return savedStackFile.getObservable();
    }

    public Observable<Boolean> getIsUndoableProvider() {
        return isUndoable.getObservable();
    }

    public Observable<Boolean> getIsRedoableProvider() {
        return isRedoable.getObservable();
    }

    @Override
    public void onCardOpened(CardPart newCard) {
        isUndoable.setSource(newCard.getCanvas().isUndoableObservable());
        isRedoable.setSource(newCard.getCanvas().isRedoableObservable());
    }

    /**
     * Displays the given stack part in a window. If the stack is already displayed in a window, this method simply
     * focuses the existing window (it is not legal to have the same stack opened twice in two windows).
     *
     * @param context     The current execution context
     * @param stackPart   The stack to display in a window
     * @param inNewWindow When true, the stack will open in a new stack window. When false, the opened stack will
     *                    replace the stack active in the execution context (i.e., the focused stack or stack requesting
     *                    to open a new stack). Has no effect when attempting to open a stack already displayed in a
     *                    window.
     */
    private void displayStack(ExecutionContext context, StackPart stackPart, boolean inNewWindow) {
        StackWindow existingWindow = WindowManager.getInstance().findWindowForStack(stackPart.getStackModel());

        // Special case: Stack is already open, simply focus it
        if (existingWindow != null) {
            existingWindow.requestFocus();
        }

        // Stack is not already open
        else {
            if (inNewWindow) {
                stackPart.bindToWindow(WindowManager.getInstance().getWindowForStack(context, stackPart));
            } else {
                stackPart.bindToWindow(context.getCurrentStack().getOwningStackWindow());
            }

            stackPart.addNavigationObserver(this);
            stackPart.partOpened(context);
        }

        focusStack(stackPart);
    }
}
