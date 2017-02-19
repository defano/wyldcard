/*
 * PartToolContext
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.context;

import com.defano.hypercard.HyperCard;
import com.defano.jmonet.model.Provider;
import com.defano.hypercard.parts.ButtonPart;
import com.defano.hypercard.parts.FieldPart;
import com.defano.hypercard.parts.ToolEditablePart;

public class PartToolContext {

    private final static PartToolContext instance = new PartToolContext();

    private Provider<ToolEditablePart> selectedPart = new Provider<>(null);

    private PartToolContext() {
        ToolsContext.getInstance().getToolModeProvider().addObserver((o, arg) -> deselectAllParts());
    }

    public static PartToolContext getInstance() {
        return instance;
    }

    public void setSelectedPart(ToolEditablePart button) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON || ToolsContext.getInstance().getToolMode() == ToolMode.FIELD) {
            deselectAllParts();
            selectedPart.set(button);
            selectedPart.get().setBeingEdited(true);
        }
    }

    public void deselectAllParts() {
        for (ButtonPart thisButton : HyperCard.getInstance().getCard().getButtons()) {
            thisButton.setBeingEdited(false);
        }

        for (FieldPart thisField : HyperCard.getInstance().getCard().getFields()) {
            thisField.setBeingEdited(false);
        }

        selectedPart.set(null);
    }

    public void bringCloser() {
        if (selectedPart.get() != null) {
            selectedPart.get().bringCloser();
        }
    }

    public void sendFurther() {
        if (selectedPart.get() != null) {
            selectedPart.get().sendFurther();
        }
    }

    public Provider<ToolEditablePart> getSelectedPartProvider() {
        return selectedPart;
    }
}
