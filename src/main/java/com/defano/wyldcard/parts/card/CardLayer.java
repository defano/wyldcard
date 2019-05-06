package com.defano.wyldcard.parts.card;

import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.Collection;

public interface CardLayer {

    /**
     * Gets the {@link PartModel} of the part's parent object. Buttons and fields belong to the card or background on
     * which they appear; cards and background belong to the stack they comprise; stacks have no parent.
     *
     * @return The parent object's part model.
     */
    PartModel getParentPartModel();

    /**
     * Gets the {@link PartType} of this part.
     *
     * @return The type of this part.
     */
    PartType getType();

    /**
     * Gets the collection of fields on this layer.
     *
     * @return The fields on this layer.
     */
    Collection<FieldModel> getFieldModels();

    /**
     * Gets the collection of button on this layer.
     *
     * @return The buttons on this layer.
     */
    Collection<ButtonModel> getButtonModels();

    /**
     * Removes the specified part (button or field) from this layer. Has no effect if the part doesn't exist.
     *
     * @param context   The execution context.
     * @param partModel The part to remove from this card.
     */
    void removePartModel(ExecutionContext context, PartModel partModel);

    /**
     * Adds a part (button or field) to this layer.
     *
     * @param partModel The part to add.
     */
    void addPartModel(PartModel partModel);
}
