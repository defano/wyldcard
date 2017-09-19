package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.common.SortDirection;
import com.defano.hypertalk.ast.common.SystemMessage;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.comparator.SortStyle;
import com.defano.hypertalk.exception.HtException;

public class SortCardsCmd extends Command {

    private final boolean markedCards;
    private final SortDirection direction;
    private final SortStyle style;
    private final Expression expression;
    private final PartExp background;

    public SortCardsCmd(boolean markedCards, SortDirection direction, SortStyle style, Expression expression) {
        this(markedCards, null, direction, style, expression);
    }

    public SortCardsCmd(boolean markedCards, PartExp background, SortDirection direction, SortStyle style, Expression expression) {
        super("sort");

        this.markedCards = markedCards;
        this.direction = direction;
        this.style = style;
        this.expression = expression;
        this.background = background;
    }

    @Override
    public void onExecute() throws HtException {
        // TODO
    }

    @Override
    public String toString() {
        return "SortCardsCmd{" +
                "markedCards=" + markedCards +
                ", direction=" + direction +
                ", style=" + style +
                ", expression=" + expression +
                ", background=" + background +
                '}';
    }
}
