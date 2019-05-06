package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.SearchType;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.search.SearchIndexer;
import com.defano.wyldcard.search.SearchQuery;
import com.defano.wyldcard.thread.Invoke;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.*;

public class MarkCmd extends Command {

    private final boolean mark;
    private final Expression cardExpr;
    private final Expression logicalExpr;
    private final Expression strategyExpr;
    private final Expression textExpr;
    private final Expression fieldExpr;

    public MarkCmd(ParserRuleContext context, boolean mark, Expression cardExpr, Expression logicalExpr, Expression strategyExpr, Expression textExpr, Expression fieldExpr) {
        super(context, "mark");
        this.mark = mark;
        this.cardExpr = cardExpr;
        this.logicalExpr = logicalExpr;
        this.strategyExpr = strategyExpr;
        this.textExpr = textExpr;
        this.fieldExpr = fieldExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {

        // Mark all cards matching a logical evaluation (filter)
        if (logicalExpr != null) {
            setCardsMark(context, findCardsByLogicalExpression(context, logicalExpr), mark);
        }

        // Mark a specific card
        else if (cardExpr != null) {
            cardExpr.partFactor(context, CardModel.class, new HtSemanticException("No such card."))
                    .setMarked(context, mark);
        }

        // Mark cards containing found text
        else if (textExpr != null) {

            PartSpecifier searchField = fieldExpr == null ?
                    null :
                    fieldExpr.factor(context, PartExp.class).evaluateAsSpecifier(context);

            SearchType searchType = strategyExpr == null ?
                    SearchType.WHOLE :
                    SearchType.fromHyperTalk(strategyExpr.evaluate(context).toString());

            String searchTerm = textExpr.evaluate(context).toString();

            SearchQuery query = new SearchQuery(searchType, searchTerm, searchField);
            setCardsMark(context, findsCardsBySearchResults(context, query), mark);
        }

        // Mark all cards
        else {
            setCardsMark(context, context.getCurrentStack().getStackModel().getCardModels(), mark);
        }
    }

    private void setCardsMark(ExecutionContext context, Collection<CardModel> cards, boolean markValue) {
        for (CardModel thisCard : cards) {
            thisCard.setMarked(context, markValue);
        }
    }

    private Set<CardModel> findsCardsBySearchResults(ExecutionContext context, SearchQuery query) {
        Set<CardModel> models = new HashSet<>();
        List<CardPart> cards = getAllCards(context);

        for (int cardIndex = 0; cardIndex < context.getCurrentStack().getStackModel().getCardCount(); cardIndex++) {
            if (!SearchIndexer.indexResults(context, query, cardIndex).isEmpty()) {
                models.add(cards.get(cardIndex).getPartModel());
            }
        }

        return models;
    }

    private Set<CardModel> findCardsByLogicalExpression(ExecutionContext context, Expression logicalExpr) throws HtException {
        Set<CardModel> models = new HashSet<>();
        CardPart currentCard = context.getCurrentCard();

        // Walk cards and determine which match expression
        for (CardPart card : getAllCards(context)) {
            context.setCurrentCard(card);
            if (logicalExpr.evaluate(context).booleanValue()) {
                models.add(card.getPartModel());
            }
        }

        // Be sure to reset the current card
        context.setCurrentCard(currentCard);
        return models;
    }

    private List<CardPart> getAllCards(ExecutionContext context) {
        ArrayList<CardPart> cards = new ArrayList<>();

        // Create skeleton card parts for every card in the stack
        Invoke.onDispatch(() -> {
            for (CardModel model : context.getCurrentStack().getStackModel().getCardModels()) {
                cards.add(CardPart.fromModel(context, model));
            }
        });

        return cards;
    }
}
