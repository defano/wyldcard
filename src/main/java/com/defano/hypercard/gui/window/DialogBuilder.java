package com.defano.hypercard.gui.window;

import com.defano.hypercard.gui.HyperCardDialog;

import javax.swing.*;
import java.awt.*;

public class DialogBuilder {

    private final JDialog frame = new JDialog();
    private final HyperCardDialog dialog;
    private Component relativeLocation;

    private DialogBuilder(HyperCardDialog dialog) {
        this.dialog = dialog;

        frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        frame.setContentPane(dialog.getWindowPanel());
    }

    public static DialogBuilder make (HyperCardDialog dialog) {
        return new DialogBuilder(dialog);
    }

    public DialogBuilder withModel(Object model) {
        dialog.bindModel(model);
        return this;
    }

    public DialogBuilder withTitle(String title) {
        frame.setTitle(title);
        return this;
    }

    public DialogBuilder withLocationCenteredOver(Component component) {
        this.relativeLocation = component;
        return this;
    }

    public JDialog build() {
        frame.pack();

        if (relativeLocation == null) {
            frame.setLocationRelativeTo(null);
        } else {
            frame.setLocationRelativeTo(relativeLocation);
        }

        dialog.setWindow(frame);
        frame.setResizable(false);
        frame.setVisible(true);

        return frame;
    }

}
