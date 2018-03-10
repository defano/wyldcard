package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PictureVisibleCmd extends Command {

    private final Owner owningLayer;
    private final Expression layerExpression;
    private final Value visibility;

    public PictureVisibleCmd(ParserRuleContext context, Owner owningLayer, boolean visibility) {
        this(context, owningLayer, null, visibility);
    }

    public PictureVisibleCmd(ParserRuleContext context, Owner owningLayer, Expression layerExpression, boolean visibility) {
        super(context, visibility ? "show" : "hide");
        this.owningLayer = owningLayer;
        this.layerExpression = layerExpression;
        this.visibility = new Value(visibility);
    }

    @Override
    protected void onExecute() throws HtException {

        // Hide/show picture of current card/bkgnd
        if (layerExpression == null) {
            CardModel currentCard = ExecutionContext.getContext().getCurrentCard().getCardModel();
            if (owningLayer == Owner.CARD) {
                currentCard.setKnownProperty(CardModel.PROP_SHOWPICT, visibility);
            } else if (owningLayer == Owner.BACKGROUND) {
                currentCard.getBackgroundModel().setKnownProperty(BackgroundModel.PROP_SHOWPICT, visibility);
            } else {
                throw new IllegalArgumentException("Bug! Invalid picture layer.");
            }
        }

        // Hide/show picture of specified card/bkgnd
        else {
            CardModel cardModel = layerExpression.partFactor(CardModel.class);
            if (cardModel != null) {
                cardModel.setKnownProperty(CardModel.PROP_SHOWPICT, visibility);
                return;
            }

            BackgroundModel backgroundModel = layerExpression.partFactor(BackgroundModel.class);
            if (backgroundModel != null) {
                backgroundModel.setKnownProperty(BackgroundModel.PROP_SHOWPICT, visibility);
                return;
            }

            throw new HtSemanticException("No such card or background.");
        }

    }
}
