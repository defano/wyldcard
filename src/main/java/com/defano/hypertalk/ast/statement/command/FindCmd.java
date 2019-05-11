package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.search.SearchQuery;
import com.defano.hypertalk.ast.model.enums.SearchType;
import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class FindCmd extends Command {

    private final Expression type;
    private final Expression term;
    private final Expression field;
    private final boolean onlyMarkedCards;

    public FindCmd(ParserRuleContext context, Expression searchType, Expression term, boolean onlyMarkedCards) {
        this(context, searchType, term, null, onlyMarkedCards);
    }

    public FindCmd(ParserRuleContext context, Expression searchType, Expression term, Expression field, boolean onlyMarkedCards) {
        super(context, "find");

        this.type = searchType;
        this.term = term;
        this.field = field;
        this.onlyMarkedCards = onlyMarkedCards;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        PartSpecifier fieldSpecifier = field == null ?
                null :
                field.factor(context, PartExp.class, new HtSemanticException("Can't search that.")).evaluateAsSpecifier(context);

        SearchType searchType = type == null ?
                SearchType.WHOLE :
                SearchType.fromHyperTalk(type.evaluate(context).toString());

        SearchQuery query = fieldSpecifier == null ?
                new SearchQuery(searchType, term.evaluate(context).toString(), onlyMarkedCards) :
                new SearchQuery(searchType, term.evaluate(context).toString(), fieldSpecifier);

        WyldCard.getInstance().getSearchManager().find(context, query);
    }
}
