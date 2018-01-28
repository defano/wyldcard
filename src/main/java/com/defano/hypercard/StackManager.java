package com.defano.hypercard;

import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.parts.stack.StackPart;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.serializer.Serializer;
import com.defano.hypercard.util.ProxyObservable;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.exception.HtSemanticException;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class StackManager {

    private StackPart activeStack = StackPart.newStack();

    private final ProxyObservable<Integer> cardCount = new ProxyObservable<>(BehaviorSubject.createDefault(1));
    private final ProxyObservable<Optional<CardPart>> cardClipboard = new ProxyObservable<>(BehaviorSubject.createDefault(Optional.empty()));
    private final ProxyObservable<Optional<File>> savedStackFile = new ProxyObservable<>(BehaviorSubject.createDefault(Optional.empty()));

    public File getSavedStackFile() {
        return savedStackFile.getObservable().blockingFirst().orElse(null);
    }

    public StackPart getActiveStack() {
        return activeStack;
    }

    /**
     * Prompts the user to choose a stack file to open. If a valid stack file is chosen, the stack is opened and made
     * the active stack.
     */
    public void open() {
        FileDialog fd = new FileDialog(WindowManager.getStackWindow().getWindow(), "Open Stack", FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setFilenameFilter((dir, name) -> name.endsWith(StackModel.FILE_EXTENSION));
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            StackModel model = Serializer.deserialize(fd.getFiles()[0], StackModel.class);
            model.setSavedStackFile(fd.getFiles()[0]);
            setActiveStack(StackPart.fromStackModel(model));
        }
    }

    public void setActiveStack(StackPart stackPart) {
        activeStack = stackPart;
        activeStack.bindToWindow(WindowManager.getStackWindow());
        cardCount.setSource(activeStack.getCardCountProvider());
        cardClipboard.setSource(activeStack.getCardClipboardProvider());
        savedStackFile.setSource(activeStack.getStackModel().getSavedStackFileProvider());
    }

    public void saveActiveStackAs() {
        saveAs(activeStack.getStackModel());
    }

    /**
     * Prompts the user to choose a file in which to save the current stack; provided user chooses a file (doesn't
     * cancel), the stack is saved to this file.
     */
    public void saveAs(StackModel stackModel) {
        String defaultName = "Untitled";

        if (HyperCard.getInstance().getSavedStackFileProvider().blockingFirst().isPresent()) {
            defaultName = HyperCard.getInstance().getSavedStackFileProvider().blockingFirst().get().getName();
        } else if (stackModel.getStackName() != null && !stackModel.getStackName().isEmpty()) {
            defaultName = stackModel.getStackName();
        }

        FileDialog fd = new FileDialog(WindowManager.getStackWindow().getWindow(), "Save Stack", FileDialog.SAVE);
        fd.setFile(defaultName);
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            File f = fd.getFiles()[0];
            String path = f.getAbsolutePath().endsWith(StackModel.FILE_EXTENSION) ?
                    f.getAbsolutePath() :
                    f.getAbsolutePath() + StackModel.FILE_EXTENSION;

            save(stackModel, new File(path));
        }
    }

    public void saveActiveStack() {
        if (savedStackFile.getObservable().blockingFirst().isPresent()) {
            save(activeStack.getStackModel(), savedStackFile.getObservable().blockingFirst().get());
        } else {
            saveAs(activeStack.getStackModel());
        }
    }

    public void save(StackModel stackModel) {
        Optional<File> saveFile = savedStackFile.getObservable().blockingFirst();
        save(stackModel, saveFile.orElse(null));
    }

    /**
     * Writes the serialized stack data into the given file. Prompts the "Save as..." dialog if the given file is null.
     * @param file The file where the stack should be saved
     */
    private void save(StackModel stackModel, File file) {
        if (file != null) {
            try {
                Serializer.serialize(file, stackModel);
                stackModel.setSavedStackFile(file);
            } catch (IOException e) {
                HyperCard.getInstance().showErrorDialog(new HtSemanticException("An error occurred saving the file " + file.getAbsolutePath()));
            }
        } else {
            saveAs(stackModel);
        }
    }

    /**
     * A cheesy and expensive mechanism to determine if the user has made a change to the stack since it was last opened.
     * @return True if the stack has changes; false otherwise
     */
    public boolean isActiveStackDirty() {
        try {
            String savedStack = new String(Files.readAllBytes(getSavedStackFileProvider().blockingFirst().get().toPath()), StandardCharsets.UTF_8);
            String currentStack = Serializer.serialize(getActiveStack().getStackModel());
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
        return activeStack.getDisplayedCard();
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

    public Observable<Optional<File>> getSavedStackFileProvider() {
        return savedStackFile.getObservable();
    }
}
