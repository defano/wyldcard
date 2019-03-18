package com.defano.wyldcard.parts.card;

import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.Collection;

public interface PartOwner {

    PartModel getParentPartModel();

    PartType getType();

    Collection<FieldModel> getFieldModels();

    Collection<ButtonModel> getButtonModels();

    /**
     * Removes the specified part (button or field). Has no effect if the part doesn't exist on this card.
     *
     * @param context   The execution context.
     * @param partModel The part to remove from this card.
     */
    void removePartModel(ExecutionContext context, PartModel partModel);

    /**
     * Adds a part (button or field) to this card.
     *
     * @param partModel The part to add.
     */
    void addPartModel(PartModel partModel);
}
