package com.defano.wyldcard;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.ast.model.SystemMessage;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.StackPartSpecifier;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.parts.stack.StackNavigationObserver;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.patterns.WyldCardPatternFactory;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.serializer.Serializer;
import com.defano.wyldcard.util.ImageLayerUtils;
import com.defano.wyldcard.util.LimitedDepthStack;
import com.defano.wyldcard.util.ProxyObservable;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.wyldcard.window.WindowDock;
import com.defano.wyldcard.window.layouts.StackWindow;
import com.google.inject.Singleton;
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
@Singleton
public class WyldCardStackManager implements StackNavigationObserver, StackManager {

    private final ArrayList<StackPart> openedStacks = new ArrayList<>();
    private final LimitedDepthStack<Destination> backstack = new LimitedDepthStack<>(20);

    private final BehaviorSubject<StackPart> focusedStack = BehaviorSubject.create();
    private final ProxyObservable<Integer> cardCount = new ProxyObservable<>(BehaviorSubject.createDefault(1));
    private final ProxyObservable<Optional<CardPart>> cardClipboard = new ProxyObservable<>(BehaviorSubject.createDefault(Optional.empty()));
    private final ProxyObservable<Optional<File>> savedStackFile = new ProxyObservable<>(BehaviorSubject.createDefault(Optional.empty()));
    private final ProxyObservable<Boolean> isUndoable = new ProxyObservable<>(BehaviorSubject.createDefault(false));
    private final ProxyObservable<Boolean> isRedoable = new ProxyObservable<>(BehaviorSubject.createDefault(false));
    private final ProxyObservable<Boolean> isSelectable = new ProxyObservable<>(BehaviorSubject.createDefault(false));
    private final ProxyObservable<Double> canvasScale = new ProxyObservable<>(BehaviorSubject.createDefault(1.0));

    @Override
    public void newStack(ExecutionContext context) {
        StackPart newStack = StackPart.newStack(context);
        ThreadUtils.invokeAndWaitAsNeeded(() -> displayStack(context, newStack, true));
    }

    @Override
    public StackModel loadStack(ExecutionContext context, File stackFile) {
        try {
            return Serializer.deserialize(stackFile, StackModel.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public StackModel findStack(ExecutionContext context, StackPartSpecifier specifier, RemoteNavigationOptions options) {
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

    @Override
    public StackPart openStack(ExecutionContext context, boolean inNewWindow, String title) {
        FileDialog fd = new FileDialog(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow(), title, FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setFilenameFilter((dir, name) -> name.endsWith(StackModel.FILE_EXTENSION));
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            return openStack(context, fd.getFiles()[0], inNewWindow);
        }

        return null;
    }

    public StackPart openStack(ExecutionContext context, StackModel model, boolean inNewWindow) {
        try {
            StackPart part = StackPart.fromStackModel(context, model);
            displayStack(context, part, inNewWindow);
            return part;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
    private StackPart openStack(ExecutionContext context, File stackFile, boolean inNewWindow) {
        try {
            StackModel model = Serializer.deserialize(stackFile, StackModel.class);
            model.setSavedStackFile(context, stackFile);

            return openStack(context, model, inNewWindow);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<StackPart> getOpenStacks() {
        return this.openedStacks;
    }

    @Override
    public StackPart getOpenStack(StackModel model) {
        return getOpenStacks().stream()
                .filter(stack -> stack.getStackModel().equals(model))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void saveStackAs(ExecutionContext context, StackModel stackModel) {
        String defaultName = "Untitled";

        if (getSavedStackFileProvider().blockingFirst().isPresent()) {
            defaultName = getSavedStackFileProvider().blockingFirst().get().getName();
        } else if (stackModel.getStackName(context) != null && !stackModel.getStackName(context).isEmpty()) {
            defaultName = stackModel.getStackName(context);
        }

        FileDialog fd = new FileDialog(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow(), "Save Stack", FileDialog.SAVE);
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

    @Override
    public void saveStack(ExecutionContext context, StackModel stackModel) {
        if (stackModel == null) {
            stackModel = getFocusedStack().getStackModel();
        }

        Optional<File> saveFile = savedStackFile.getObservable().blockingFirst();
        saveStack(context, stackModel, saveFile.orElse(null));
    }

    @Override
    public void saveStack(ExecutionContext context, StackModel stackModel, File file) {
        if (file != null) {
            try {
                Serializer.serialize(file, stackModel);
                stackModel.setSavedStackFile(context, file);
                context.setResult(new Value());
            } catch (IOException e) {
                context.setResult(new Value("An error occurred saving the file " + file.getAbsolutePath()));
            }
        } else {
            saveStackAs(context, stackModel);
        }
    }

    @Override
    public void closeAllStacks(ExecutionContext context) {
        for (StackPart thisOpenStack : getOpenStacks()) {
            closeStack(context, thisOpenStack);
        }
    }

    @Override
    public void closeStack(ExecutionContext context, StackPart stackPart) {
        if (stackPart == null) {
            stackPart = getFocusedStack();
        }

        if (promptToSave(context, stackPart)) {
            return;     // User wants to cancel
        }

        disposeStack(context, stackPart, true);
    }

    @Override
    public void focusStack(StackPart stackPart) {

        // Don't refocus the focused stack
        if (focusedStack.hasValue() && focusedStack.blockingFirst() == stackPart) {
            return;
        }

        // Remove focus from last-focused stack when applicable
        if (focusedStack.hasValue()) {
            WyldCard.getInstance().getPartToolManager().deselectAllParts();
            focusedStack.blockingFirst().removeNavigationObserver(this);

            // Send suspendStack/resumeStack messages
            focusedStack.blockingFirst().getDisplayedCard().getPartModel().receiveMessage(new ExecutionContext(focusedStack.blockingFirst()), SystemMessage.SUSPEND_STACK.messageName);
            stackPart.getDisplayedCard().getPartModel().receiveMessage(new ExecutionContext(stackPart), SystemMessage.RESUME_STACK.messageName);
        }

        // Make the focused stack the window dock
        WindowDock.getInstance().setDock(stackPart.getOwningStackWindow());

        focusedStack.onNext(stackPart);

        // Update patterns to show user-created patterns
        WyldCardPatternFactory.getInstance().invalidatePatternCache();

        // Make the selected tool active on the focused card
        WyldCard.getInstance().getToolsManager().reactivateTool(stackPart.getDisplayedCard().getActiveCanvas());

        // Update proxied observables (so that they reference newly focused stack)
        cardCount.setSource(stackPart.getCardCountProvider());
        cardClipboard.setSource(stackPart.getCardClipboardProvider());
        savedStackFile.setSource(stackPart.getStackModel().getSavedStackFileProvider());
        isUndoable.setSource(stackPart.getDisplayedCard().getActiveCanvas().isUndoableObservable());
        isRedoable.setSource(stackPart.getDisplayedCard().getActiveCanvas().isRedoableObservable());
        canvasScale.setSource(stackPart.getDisplayedCard().getActiveCanvas().getScaleObservable());

        isSelectable.setSource(Observable.combineLatest(
                isUndoable.getObservable(),
                WyldCard.getInstance().getToolsManager().getSelectedImageProvider(),

                // Select command is available when an undoable change is present; the user does not have an active
                // graphic selection; there are no re-doable changes; and the last graphic change in the undo buffer
                // did not "remove" paint from the canvas (i.e., eraser changes are not selectable)
                (hasUndoableChanges, selection) ->
                        hasUndoableChanges &&
                                !selection.isPresent() &&
                                getFocusedCard().getActiveCanvas().getRedoBufferDepth() == 0 &&
                                !ImageLayerUtils.layersRemovesPaint(getFocusedCard().getActiveCanvas().peek(0).getImageLayers()))
        );

        stackPart.addNavigationObserver(this);
    }

    @Override
    public StackPart getFocusedStack() {
        if (focusedStack.hasValue()) {
            return focusedStack.blockingFirst();
        } else {
            return null;
        }
    }

    @Override
    public Observable<StackPart> getFocusedStackProvider() {
        return focusedStack;
    }

    @Override
    public CardPart getFocusedCard() {
        if (getFocusedStack() == null) {
            return null;
        } else {
            return getFocusedStack().getDisplayedCard();
        }
    }

    @Override
    public Observable<Integer> getFocusedStackCardCountProvider() {
        return cardCount.getObservable();
    }

    @Override
    public Observable<Optional<CardPart>> getFocusedStackCardClipboardProvider() {
        return cardClipboard.getObservable();
    }

    @Override
    public Observable<Optional<File>> getSavedStackFileProvider() {
        return savedStackFile.getObservable();
    }

    @Override
    public Observable<Boolean> getIsUndoableProvider() {
        return isUndoable.getObservable();
    }

    @Override
    public Observable<Boolean> getIsRedoableProvider() {
        return isRedoable.getObservable();
    }

    @Override
    public Observable<Boolean> getIsSelectableProvider() {
        return isSelectable.getObservable();
    }

    @Override
    public Observable<Double> getScaleProvider() {
        return canvasScale.getObservable();
    }

    @Override
    public void onCardOpened(CardPart newCard) {
        isUndoable.setSource(newCard.getActiveCanvas().isUndoableObservable());
        isRedoable.setSource(newCard.getActiveCanvas().isRedoableObservable());
        canvasScale.setSource(newCard.getActiveCanvas().getScaleObservable());
    }

    @Override
    public LimitedDepthStack<Destination> getBackstack() {
        return backstack;
    }

    /**
     * Displays the given stack in a window. If the stack is already displayed in a window, this method simply
     * focuses the existing window (HyperCard does not allow having the same stack opened twice in two windows).
     *
     * @param context     The current execution context
     * @param stack       The stack to display in a window
     * @param inNewWindow When true, the stack will open in a new stack window. When false, the opened stack will
     *                    replace the stack active in the execution context (i.e., the focused stack or stack requesting
     *                    to open a new stack). Has no effect when attempting to open a stack already displayed in a
     *                    window.
     */
    @RunOnDispatch
    private void displayStack(ExecutionContext context, StackPart stack, boolean inNewWindow) {

        // Special case: Stack is already open, simply focus it
        StackWindow existingWindow = WyldCard.getInstance().getWindowManager().findWindowForStack(stack.getStackModel());
        if (existingWindow != null) {
            existingWindow.requestFocus();
        }

        // Stack is not already open
        else {
            if (inNewWindow) {
                stack.bindToWindow(WyldCard.getInstance().getWindowManager().getWindowForStack(context, stack));
                openedStacks.add(stack);
            } else {
                // Stack displayed in window we're about to open new stack inside of
                StackPart oldStack = context.getCurrentStack();
                StackWindow oldStackWindow = oldStack.getOwningStackWindow();

                if (promptToSave(context, oldStack)) {
                    return;     // User cancelled saving existing stack; abort open process
                }

                stack.bindToWindow(oldStackWindow);

                openedStacks.add(stack);
                context.bind(stack);

                disposeStack(context, oldStack, false);
            }

            stack.addNavigationObserver(this);
            stack.partOpened(context);
        }

        focusStack(stack);
    }

    /**
     * Disposes resources associated with the given stack (including its bound window, when requested). Causes WyldCard
     * to quit when the last stack window is disposed.
     *
     * @param context       The execution context
     * @param stack         The stack to dispose
     * @param disposeWindow When true, dispose the stack's window
     */
    private void disposeStack(ExecutionContext context, StackPart stack, boolean disposeWindow) {
        // Clean up stack resources
        stack.partClosed(context);

        // Dispose the stack's frame when requested
        if (disposeWindow && stack.getOwningStackWindow() != null) {
            stack.getOwningStackWindow().dispose();
        }

        // Forget about it...
        openedStacks.remove(stack);

        // Finally, quit application when last stack window has closed
        if (openedStacks.size() == 0) {
            System.exit(0);
        }
    }

    /**
     * If the given stack is dirty, prompts the user to save it (further performing the save operation if confirmed by
     * the user).
     *
     * @param context The current execution context
     * @param stack   The stack part to prompt to save
     * @return True if the user canceled the prompt; false if the user chose to save or not save the stack.
     */
    private boolean promptToSave(ExecutionContext context, StackPart stack) {
        // Prompt user to save if stack is dirty
        if (stack.getStackModel().isDirty()) {
            int dialogResult = JOptionPane.showConfirmDialog(
                    stack.getDisplayedCard(),
                    "Save changes to " + stack.getStackModel().getStackName(null) + "?",
                    "Save",
                    JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.CLOSED_OPTION) {
                return true;
            } else if (dialogResult == JOptionPane.YES_OPTION) {
                saveStack(context, stack.getStackModel());
            }
        }

        return false;
    }

}
