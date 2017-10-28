package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.button.ButtonComponent;
import com.defano.hypercard.parts.button.ButtonPart;
import com.defano.hypercard.parts.button.styles.MenuButton;
import com.defano.hypercard.parts.field.AddressableSelection;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.ChunkType;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;
import com.defano.hypertalk.utils.RangeUtils;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectTextCmd extends Command {

    private final Preposition preposition;
    private final Chunk chunk;
    private final PartExp partExp;

    public SelectTextCmd(ParserRuleContext context, Preposition preposition, Chunk chunkExp, PartExp partExp) {
        super(context, "select");

        this.preposition = preposition;
        this.chunk = chunkExp;
        this.partExp = partExp;
    }

    public SelectTextCmd(ParserRuleContext context, Preposition preposition, PartExp partExp) {
        this(context, preposition, null, partExp);
    }

    @Override
    public void onExecute() throws HtException {
        PartSpecifier specifier = this.partExp.evaluateAsSpecifier();

        if (specifier.type() != null && specifier.type() == PartType.BUTTON) {
            selectMenuButtonItem(specifier);
        } else if (specifier.type() == null || (specifier.type() != PartType.FIELD && specifier.type() != PartType.MESSAGE_BOX)) {
            throw new HtSemanticException("Expected a field here.");
        } else {
            selectManagedText(specifier);
        }
    }

    private void selectManagedText(PartSpecifier specifier) throws HtException {
        PartModel partModel = HyperCard.getInstance().getDisplayedCard().findPart(specifier);

        if (! (partModel instanceof AddressableSelection)) {
            throw new IllegalStateException("Bug! Don't know how to select text in part: " + partModel);
        }

        AddressableSelection field = (AddressableSelection) partModel;

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

    private void selectMenuButtonItem(PartSpecifier specifier) throws HtException {
        if (chunk.type != ChunkType.LINE) {
            throw new HtSemanticException("Cannot select " + chunk.type.hyperTalkName() + " of this button.");
        }

        PartModel partModel = HyperCard.getInstance().getDisplayedCard().findPart(specifier);
        ButtonPart part = (ButtonPart) HyperCard.getInstance().getDisplayedCard().getPart(partModel);

        ButtonComponent component = (ButtonComponent) part.getButtonComponent();
        if (component instanceof MenuButton) {
            ((MenuButton) component).selectItem(chunk.start.evaluate().integerValue());
        } else {
            throw new HtSemanticException("Can't select lines of this type of button.");
        }
    }

}
