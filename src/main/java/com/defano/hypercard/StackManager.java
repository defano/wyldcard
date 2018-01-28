package com.defano.hypercard;

import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.parts.stack.StackPart;
import com.defano.hypercard.runtime.serializer.Serializer;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.exception.HtSemanticException;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class StackManager {

    public final static String FILE_EXTENSION = ".stack";

    protected StackPart stackPart;
    protected final Subject<Optional<File>> savedStackFileProvider = BehaviorSubject.createDefault(Optional.empty());

    public Observable<Optional<File>> getSavedStackFileProvider() {
        return savedStackFileProvider;
    }

    public File getSavedStackFile() {
        return savedStackFileProvider.blockingFirst().orElse(null);
    }

    public void setSavedStackFile(File savedStackFileProvider) {
        this.savedStackFileProvider.onNext(Optional.of(savedStackFileProvider));
    }

    /**
     * Prompts the user to choose a stack file to open.
     */
    public StackPart open() {
        FileDialog fd = new FileDialog(WindowManager.getStackWindow().getWindow(), "Open Stack", FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setFilenameFilter((dir, name) -> name.endsWith(FILE_EXTENSION));
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            StackModel model = Serializer.deserialize(fd.getFiles()[0], StackModel.class);
            HyperCard.getInstance().setSavedStackFile(fd.getFiles()[0]);
            stackPart = StackPart.fromStackModel(model).activate();
        }

        return stackPart;
    }

    public void saveAs() {
        saveAs(stackPart.getStackModel());
    }

    /**
     * Prompts the user to choose a file in which to save the current stack.
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
            save(stackModel, new File(f.getAbsolutePath() + FILE_EXTENSION));
        }
    }

    public void save() {
        if (savedStackFileProvider.blockingFirst().isPresent()) {
            save(stackPart.getStackModel(), savedStackFileProvider.blockingFirst().get());
        } else {
            saveAs(stackPart.getStackModel());
        }
    }

    /**
     * Writes the serialized stack data into the given file. Prompts the "Save as..." dialog if the given file is null.
     * @param file The file where the stack should be saved
     */
    public void save(StackModel stackModel, File file) {
        if (file == null) {
            saveAs(stackModel);
        } else {
            try {
                Serializer.serialize(file, stackModel);
                HyperCard.getInstance().setSavedStackFile(file);
            } catch (IOException e) {
                HyperCard.getInstance().showErrorDialog(new HtSemanticException("An error occurred saving the file " + file.getAbsolutePath()));
            }
        }
    }

}
