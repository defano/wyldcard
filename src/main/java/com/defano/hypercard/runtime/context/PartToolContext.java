/*
 * PartToolContext
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.runtime.context;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.jmonet.model.Provider;
import com.defano.hypercard.parts.button.ButtonPart;
import com.defano.hypercard.parts.field.FieldPart;
import com.defano.hypercard.parts.ToolEditablePart;

import java.awt.*;

public class PartToolContext {

    private final static PartToolContext instance = new PartToolContext();

    private final Provider<ToolEditablePart> selectedPart = new Provider<>(null);

    private PartToolContext() {
        // Deselect all parts when user changes tool mode
        ToolsContext.getInstance().getToolModeProvider().addObserver((o, arg) -> deselectAllParts());

        // Change part font when user chooses a font/style from the menubar
        ToolsContext.getInstance().getSelectedFontProvider().addObserver((o, arg) -> setSelectedPartFont((Font) arg));
    }

    public static PartToolContext getInstance() {
        return instance;
    }

    public void setSelectedPart(ToolEditablePart part) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON || ToolsContext.getInstance().getToolMode() == ToolMode.FIELD) {
            deselectAllParts();
            selectedPart.set(part);
            part.setSelectedForEditing(true);
        }
    }

    public void deselectAllParts() {
        for (ButtonPart thisButton : HyperCard.getInstance().getDisplayedCard().getButtons()) {
            thisButton.setSelectedForEditing(false);
        }

        for (FieldPart thisField : HyperCard.getInstance().getDisplayedCard().getFields()) {
            thisField.setSelectedForEditing(false);
        }

        selectedPart.set(null);
    }

    public void bringSelectedPartCloser() {
        if (selectedPart.get() != null) {
            selectedPart.get().bringCloser();
        }
    }

    public void sendSelectedPartFurther() {
        if (selectedPart.get() != null) {
            selectedPart.get().sendFurther();
        }
    }

    public void deleteSelectedPart() {
        ToolEditablePart selectedPart = this.selectedPart.get();
        if (selectedPart != null) {
            HyperCard.getInstance().getDisplayedCard().removePart(selectedPart.getPartModel());
            this.selectedPart.set(null);
        }
    }

    public void setSelectedPartFont(Font font) {
        ToolEditablePart selectedPart = this.selectedPart.get();
        if (selectedPart != null) {
            ((CardLayerPartModel)selectedPart.getPart().getPartModel()).setFont(font);
        }
    }

    public Provider<ToolEditablePart> getSelectedPartProvider() {
        return selectedPart;
    }
}
