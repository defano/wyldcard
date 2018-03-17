package com.defano.wyldcard.window;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.Breadcrumb;
import com.defano.wyldcard.window.forms.ScriptEditor;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.Token;

import javax.swing.*;

public class HyperTalkErrorDialog {

    private final static HyperTalkErrorDialog instance = new HyperTalkErrorDialog();
    private boolean errorDialogVisible = false;

    private HyperTalkErrorDialog() {
    }

    public static HyperTalkErrorDialog getInstance() {
        return instance;
    }

    @RunOnDispatch
    public void showError(HtException e) {
        if (!errorDialogVisible) {
            errorDialogVisible = true;

            if (isEditable(e)) {
                showEditableError(e.getMessage(), e.getBreadcrumb().getPartModel(), e.getBreadcrumb().getToken());
            } else {
                showUneditableError(e.getMessage());
            }

            errorDialogVisible = false;
        }
        e.printStackTrace();
    }

    @RunOnDispatch
    private void showUneditableError(String message) {
        JOptionPane.showMessageDialog(
                WindowManager.getInstance().getStackWindow().getWindowPanel(),
                message,
                "HyperTalk Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @RunOnDispatch
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
            offendingPart.editScript(offendingToken.getStartIndex());
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
