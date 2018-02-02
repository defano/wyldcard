package com.defano.hypercard.window;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.Breadcrumb;
import com.defano.hypercard.window.forms.ScriptEditor;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.Token;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class HyperTalkErrorDialog {

    private final static HyperTalkErrorDialog instance = new HyperTalkErrorDialog();
    private AtomicBoolean errorDialogVisible = new AtomicBoolean(false);

    private HyperTalkErrorDialog() {
    }

    public static HyperTalkErrorDialog getInstance() {
        return instance;
    }

    public void showError(HtException e) {
        SwingUtilities.invokeLater(() -> {
            if (!errorDialogVisible.get()) {

                if (isEditable(e)) {
                    showEditableError(e.getMessage(), e.getBreadcrumb().getPartModel(), e.getBreadcrumb().getToken());
                } else {
                    showUneditableError(e.getMessage());
                }

                errorDialogVisible.set(false);
            }
        });
        e.printStackTrace();
    }

    private void showUneditableError(String message) {
        JOptionPane.showMessageDialog(
                WindowManager.getInstance().getStackWindow().getWindowPanel(),
                message,
                "HyperTalk Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showEditableError(String message, PartModel offendingPart, Token offendingToken) {
        Object[] options = {"OK", "Script..."};
        int selection = JOptionPane.showOptionDialog(
                WindowManager.getInstance().getStackWindow().getWindowPanel(),
                message,
                "HyperTalk Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);

        if (selection == 1) {
            ScriptEditor editor = (ScriptEditor) WindowBuilder.make(new ScriptEditor())
                    .withTitle("Script of " + offendingPart.getKnownProperty(PartModel.PROP_NAME).stringValue())
                    .withModel(offendingPart)
                    .resizeable(true)
                    .withLocationStaggeredOver(WindowManager.getInstance().getStackWindow().getWindowPanel())
                    .build();
            editor.moveCaretToPosition(offendingToken.getStartIndex());
        }
    }

    private boolean isEditable(HtException e) {
        Breadcrumb breadcrumb = e.getBreadcrumb();

        return breadcrumb != null &&
                breadcrumb.getPart() != null &&
                breadcrumb.getToken() != null &&
                breadcrumb.getPart().getType() != null &&
                breadcrumb.getPart().getType() != PartType.MESSAGE_BOX;
    }
}
