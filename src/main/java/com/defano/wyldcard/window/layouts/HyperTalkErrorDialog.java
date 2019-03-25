package com.defano.wyldcard.window.layouts;

import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.Breadcrumb;
import com.defano.wyldcard.runtime.context.ExecutionContext;
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
                // Suppress further error messages while user is editing script of this part
                ScriptEditor scriptEditor = WyldCard.getInstance().getWindowManager().findScriptEditorForPart(e.getBreadcrumb().getPartModel());
                if (scriptEditor == null || !scriptEditor.isVisible()) {
                    showEditableError(e.getMessage(), e.getBreadcrumb().getContext().getCurrentStack(), e.getBreadcrumb().getPartModel(), e.getBreadcrumb().getToken());
                }
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
                WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getWindowPanel(),
                message,
                "HyperTalk Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @RunOnDispatch
    private void showEditableError(String message, StackPart stackPart, PartModel offendingPart, Token offendingToken) {
        Object[] options = {"OK", "Script..."};
        int selection = JOptionPane.showOptionDialog(
                WyldCard.getInstance().getWindowManager().getWindowForStack(new ExecutionContext(), stackPart),
                message,
                "HyperTalk Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);

        if (selection == 1) {
            offendingPart.editScript(new ExecutionContext(), offendingToken.getStartIndex());
        }
    }

    private boolean isEditable(HtException e) {
        Breadcrumb breadcrumb = e.getBreadcrumb();

        return breadcrumb != null &&
                breadcrumb.getPartModel() != null &&
                breadcrumb.getToken() != null &&
                breadcrumb.getPart().getType() != null &&
                breadcrumb.getPart().getType() != PartType.MESSAGE_BOX;
    }
}
