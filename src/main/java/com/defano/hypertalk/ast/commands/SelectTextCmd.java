package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.card.CardLayerPart;
import com.defano.hypercard.parts.field.styles.AbstractTextField;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.ast.containers.Preposition;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;
import com.defano.hypertalk.utils.RangeUtils;

public class SelectTextCmd extends Command {

    private final Preposition preposition;
    private final Chunk chunk;
    private final PartExp partExp;

    public SelectTextCmd(Preposition preposition, Chunk chunkExp, PartExp partExp) {
        super("select");

        this.preposition = preposition;
        this.chunk = chunkExp;
        this.partExp = partExp;
    }

    public SelectTextCmd(Preposition preposition, PartExp partExp) {
        this(preposition, null, partExp);
    }

    @Override
    public void onExecute() throws HtException {
        PartSpecifier specifier = this.partExp.evaluateAsSpecifier();

        if (specifier.type() == null || (specifier.type() != PartType.FIELD)) {
            throw new HtSemanticException("Expected a field here.");
        }

        PartModel partModel = HyperCard.getInstance().getDisplayedCard().findPart(specifier);
        CardLayerPart part = HyperCard.getInstance().getDisplayedCard().getPart(partModel);
        AbstractTextField fieldPart = (AbstractTextField) part.getComponent();

        Range range = chunk == null ?
                new Range(0, fieldPart.getText().length()) :            // Entire contents
                RangeUtils.getRange(fieldPart.getText(), chunk);        // Chunk of contents

        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            switch (preposition) {
                case BEFORE:
                    fieldPart.setSelection(range.start, range.start);
                    break;
                case AFTER:
                    fieldPart.setSelection(range.end, range.end);
                    break;
                case INTO:
                    fieldPart.setSelection(range.start, range.end);
                    break;
            }
        });
    }

}
