package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.field.ManagedSelection;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.msgbox.MsgBoxModel;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypercard.window.WindowManager;
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

        if (specifier.type() == null || (specifier.type() != PartType.FIELD && specifier.type() != PartType.MESSAGE_BOX)) {
            throw new HtSemanticException("Expected a field here.");
        }

        PartModel partModel = HyperCard.getInstance().getDisplayedCard().findPart(specifier);
        ManagedSelection field;

        if (partModel instanceof FieldModel) {
            field = (ManagedSelection) HyperCard.getInstance().getDisplayedCard().getPart(partModel);
        } else if (partModel instanceof MsgBoxModel) {
            field = WindowManager.getMessageWindow();
        } else {
            throw new IllegalStateException("Bug! Don't know how to select text in part: " + partModel);
        }

        Range range = chunk == null ?
                new Range(0, field.getSelectableText().length()) :            // Entire contents
                RangeUtils.getRange(field.getSelectableText(), chunk);        // Chunk of contents

        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            switch (preposition) {
                case BEFORE:
                    field.setSelection(range.start, range.start);
                    break;
                case AFTER:
                    field.setSelection(range.end, range.end);
                    break;
                case INTO:
                    field.setSelection(range.start, range.end);
                    break;
            }
        });
    }

}
