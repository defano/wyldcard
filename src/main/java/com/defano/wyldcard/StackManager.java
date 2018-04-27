package com.defano.wyldcard;

import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.parts.stack.StackNavigationObserver;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.PartToolContext;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.runtime.serializer.Serializer;
import com.defano.wyldcard.util.ProxyObservable;
import com.defano.wyldcard.window.StackWindow;
import com.defano.wyldcard.window.WindowManager;
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

    protected BehaviorSubject<StackPart> focusedStack = BehaviorSubject.create();
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
     * Prompts the user to select a stack file from disk for opening.
     *
     * @param context     The current execution context
     * @param inNewWindow When true, the stack will open in a new stack window. When false, the opened stack will
     *                    replace the stack active in the execution context (i.e., the focused stack or stack requesting
     *                    to open a new stack).
     */
    public void openStack(ExecutionContext context, boolean inNewWindow) {
        FileDialog fd = new FileDialog(WindowManager.getInstance().getWindowForStack(context.getCurrentStack()).getWindow(), "Open Stack", FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setFilenameFilter((dir, name) -> name.endsWith(StackModel.FILE_EXTENSION));
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            openStack(context, fd.getFiles()[0], inNewWindow);
        }
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
    public StackPart openStack(ExecutionContext context, File stackFile, boolean inNewWindow) {
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
                stackPart.openStack(WindowManager.getInstance().getWindowForStack(stackPart));
            } else {
                stackPart.openStack(context.getCurrentStack().getOwningStackWindow());
            }

            stackPart.addNavigationObserver(this);
        }
    }

    /**
     * Gets a list of all currently open stacks (those which are displayed inside of a window).
     *
     * @return The list of open stacks.
     */
    public List<StackPart> getOpenStacks() {
        ArrayList<StackPart> stacks = new ArrayList<>();
        for (Window thisWindow : WindowManager.getInstance().getWindows()) {
            if (thisWindow instanceof StackWindow && thisWindow.isVisible()) {
                stacks.add(((StackWindow) thisWindow).getStack());
            }
        }
        return stacks;
    }

    private void saveStackAs(ExecutionContext context, StackModel stackModel) {
        String defaultName = "Untitled";

        if (WyldCard.getInstance().getSavedStackFileProvider().blockingFirst().isPresent()) {
            defaultName = WyldCard.getInstance().getSavedStackFileProvider().blockingFirst().get().getName();
        } else if (stackModel.getStackName(context) != null && !stackModel.getStackName(context).isEmpty()) {
            defaultName = stackModel.getStackName(context);
        }

        FileDialog fd = new FileDialog(WindowManager.getInstance().getWindowForStack(context.getCurrentStack()).getWindow(), "Save Stack", FileDialog.SAVE);
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
     * Saves the stack active in the execution context to its associated file, or behaves like "Save as..." if there is
     * no file associated with the stack.
     *
     * @param context The execution context.
     */
    public void saveStack(ExecutionContext context) {
        saveStack(context, getFocusedStack().getStackModel());
    }

    /**
     * Prompts the user to select a file, then saves the stack active in the given execution context to this file.
     *
     * @param context The execution context.
     */
    public void saveStackAs(ExecutionContext context) {
        saveStackAs(context, context.getCurrentStack().getStackModel());
    }

    /**
     * Saves the given stack model to its associated file (acts like "Save as" if no file is associated).
     *
     * @param context    The execution context.
     * @param stackModel The StackModel to save.
     */
    public void saveStack(ExecutionContext context, StackModel stackModel) {
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

    public void closeAllStacksAndExit() {
        for (StackPart thisOpenStack : getOpenStacks()) {
            closeStack(thisOpenStack);
        }
    }

    public void closeFocusedStack() {
        closeStack(getFocusedStack());
    }

    public void closeStack(StackPart stackPart) {

        // Prompt user to save if stack is dirty
        if (stackPart.getStackModel().isStackDirty()) {
            int dialogResult = JOptionPane.showConfirmDialog(
                    stackPart.getDisplayedCard(),
                    "Save changes to stack?",
                    "Save",
                    JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.CLOSED_OPTION) {
                return;
            } else if (dialogResult == JOptionPane.YES_OPTION) {
                saveStack(new ExecutionContext(), stackPart.getStackModel());
            }
        }

        // Dispose the stack's frame (if one exists)
        if (stackPart.getOwningStackWindow() != null) {
            stackPart.getOwningStackWindow().dispose();
        }

        // Finally, quit application when last stack window has closed
        if (getOpenStacks().size() == 0) {
            System.exit(0);
        }
    }

    public void unfocusStack(StackPart stackPart) {
        PartToolContext.getInstance().deselectAllParts();
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
        if (stackPart == null) {
            throw new IllegalArgumentException("Focused stack cannot be null.");
        }

        focusedStack.onNext(stackPart);
        cardCount.setSource(stackPart.getCardCountProvider());
        cardClipboard.setSource(stackPart.getCardClipboardProvider());
        savedStackFile.setSource(stackPart.getStackModel().getSavedStackFileProvider());

        ToolsContext.getInstance().reactivateTool(stackPart.getDisplayedCard().getCanvas());

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
}
