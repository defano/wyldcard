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

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Provides methods to open, close, save and focus stacks.
 */
public class StackManager implements StackNavigationObserver {

    private final ProxyObservable<Integer> cardCount = new ProxyObservable<>(BehaviorSubject.createDefault(1));
    private final ProxyObservable<Optional<CardPart>> cardClipboard = new ProxyObservable<>(BehaviorSubject.createDefault(Optional.empty()));
    private final ProxyObservable<Optional<File>> savedStackFile = new ProxyObservable<>(BehaviorSubject.createDefault(Optional.empty()));
    private final ProxyObservable<Boolean> isUndoable = new ProxyObservable<>(BehaviorSubject.createDefault(false));
    private final ProxyObservable<Boolean> isRedoable = new ProxyObservable<>(BehaviorSubject.createDefault(false));

    /**
     * Creates and activates a new, empty stack with default dimensions.
     */
    public void newStack(ExecutionContext context) {
        displayStack(context, StackPart.newStack(context), true);
    }

    /**
     * Prompts the user to choose a stack file to open. If a valid stack file is chosen, the stack is opened and made
     * the active stack.
     */
    public void open(ExecutionContext context, boolean inNewWindow) {
        FileDialog fd = new FileDialog(WindowManager.getInstance().getStackWindow(context).getWindow(), "Open Stack", FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setFilenameFilter((dir, name) -> name.endsWith(StackModel.FILE_EXTENSION));
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            StackModel model = Serializer.deserialize(fd.getFiles()[0], StackModel.class);
            model.setSavedStackFile(context, fd.getFiles()[0]);
            displayStack(context, StackPart.fromStackModel(context, model), inNewWindow);
        }
    }

    public void displayStack(ExecutionContext context, StackPart stackPart, boolean inNewWindow) {
        StackWindow existingWindow = WindowManager.getInstance().findWindowForStack(stackPart);

        // Special case: Stack is already open, simply focus it
        if (existingWindow != null) {
            existingWindow.requestFocus();
        }

        // Stack is not open
        else {
            stackPart.addNavigationObserver(this);
            if (inNewWindow) {
                stackPart.displayInWindow(context, WindowManager.getInstance().getStackWindow(stackPart));
            } else {
                stackPart.displayInWindow(context, context.getCurrentStack().getOwningStackWindow());
            }
        }

        focusStack(stackPart);
    }

    public void unfocusStack(StackPart stackPart) {
        PartToolContext.getInstance().deselectAllParts();
    }

    public void focusStack(StackPart stackPart) {
        if (stackPart == null) {
            throw new IllegalArgumentException("Focused stack cannot be null.");
        }

        WindowManager.getInstance().setFocusedStack(stackPart);

        cardCount.setSource(stackPart.getCardCountProvider());
        cardClipboard.setSource(stackPart.getCardClipboardProvider());
        savedStackFile.setSource(stackPart.getStackModel().getSavedStackFileProvider());

        ToolsContext.getInstance().reactivateTool(stackPart.getDisplayedCard().getCanvas());
    }

    public List<StackPart> getOpenStacks() {
        ArrayList<StackPart> stacks = new ArrayList<>();
        for (Window thisWindow : WindowManager.getInstance().getWindows()) {
            if (thisWindow instanceof StackWindow) {
                stacks.add(((StackWindow) thisWindow).getStack());
            }
        }
        return stacks;
    }

    /**
     * Prompts the user to choose a file in which to save the current stack; provided user chooses a file (doesn't
     * cancel), the stack is saved to this file.
     */
    private void saveAs(ExecutionContext context, StackModel stackModel) {
        String defaultName = "Untitled";

        if (WyldCard.getInstance().getSavedStackFileProvider().blockingFirst().isPresent()) {
            defaultName = WyldCard.getInstance().getSavedStackFileProvider().blockingFirst().get().getName();
        } else if (stackModel.getStackName(context) != null && !stackModel.getStackName(context).isEmpty()) {
            defaultName = stackModel.getStackName(context);
        }

        FileDialog fd = new FileDialog(WindowManager.getInstance().getStackWindow(context).getWindow(), "Save Stack", FileDialog.SAVE);
        fd.setFile(defaultName);
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            File f = fd.getFiles()[0];
            String path = f.getAbsolutePath().endsWith(StackModel.FILE_EXTENSION) ?
                    f.getAbsolutePath() :
                    f.getAbsolutePath() + StackModel.FILE_EXTENSION;

            save(context, stackModel, new File(path));
        }
    }

    /**
     * Saves the active stack to its associated file (acts like "Save as" if no file is associated).
     * @param context The execution context.
     */
    public void saveActiveStack(ExecutionContext context) {
        save(context, WindowManager.getInstance().getFocusedStack().getStackModel());
    }

    /**
     * Prompts the user to select a file, then saves the active stack to this file.
     * @param context The execution context.
     */
    public void saveActiveStackAs(ExecutionContext context) {
        save(context, WindowManager.getInstance().getFocusedStack().getStackModel(), null);
    }

    /**
     * Saves the given stack model to its associated file (acts like "Save as" if no file is associated).
     * @param context The execution context.
     * @param stackModel The StackModel to save.
     */
    private void save(ExecutionContext context, StackModel stackModel) {
        Optional<File> saveFile = savedStackFile.getObservable().blockingFirst();
        save(context, stackModel, saveFile.orElse(null));
    }

    /**
     * Writes the serialized stack data into the given file. Prompts the "Save as..." dialog if the given file is null.
     * @param context The execution context.
     * @param file The file where the stack should be saved
     */
    private void save(ExecutionContext context, StackModel stackModel, File file) {
        if (file != null) {
            try {
                Serializer.serialize(file, stackModel);
                stackModel.setSavedStackFile(context, file);
            } catch (IOException e) {
                WyldCard.getInstance().showErrorDialog(new HtSemanticException("An error occurred saving the file " + file.getAbsolutePath()));
            }
        } else {
            saveAs(context, stackModel);
        }
    }

    /**
     * A cheesy and expensive mechanism to determine if the user has made a change to the stack since it was last opened.
     * @return True if the stack has changes; false otherwise
     */
    public boolean isActiveStackDirty() {
        try {
            String savedStack = new String(Files.readAllBytes(getSavedStackFileProvider().blockingFirst().get().toPath()), StandardCharsets.UTF_8);
            String currentStack = Serializer.serialize(WindowManager.getInstance().getFocusedStack().getStackModel());
            return !savedStack.equalsIgnoreCase(currentStack);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Gets the card currently displayed in the stack window (which may not be visible if the screen is currently
     * locked).
     *
     * Note that HyperTalk-implementing classes should always use {@link ExecutionContext#getCurrentCard()} to retrieve
     * a reference to the current card, since, from the perspective of a script the active card may differ from the
     * displayed card under certain conditions (like stack sorting).
     *
     * @return The card currently displayed in the active stack window.
     */
    public CardPart getActiveStackDisplayedCard() {
        if (WindowManager.getInstance().getFocusedStack() == null) {
            return null;
        } else {
            return WindowManager.getInstance().getFocusedStack().getDisplayedCard();
        }
    }

    /**
     * Gets an observable count of the number of cards present in the active stack.
     * @return An observable count of cards in the active stack.
     */
    public Observable<Integer> getActiveStackCardCountProvider() {
        return cardCount.getObservable();
    }

    /**
     * Gets an observable "clipboard" of cards from the active stack. This clipboard is non-empty when a card in the
     * active stack has been cut or copied from the stack.
     * @return The card clipboard associated with the active stack.
     */
    public Observable<Optional<CardPart>> getActiveStackCardClipboardProvider() {
        return cardClipboard.getObservable();
    }

    /**
     * Gets an observable optional file representing the file to which the active stack is current bound (that is, the
     * file where the stack was last saved or opened from).
     *
     * An empty optional indicates the stack has never been saved and has no associated file.
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
