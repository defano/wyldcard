package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.search.SearchContext;
import com.defano.hypercard.search.SearchQuery;
import com.defano.hypertalk.ast.common.SearchType;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class FindCmd extends Command {

    private final SearchType searchType;
    private final Expression term;
    private final Expression field;
    private final boolean onlyMarkedCards;

    public FindCmd(ParserRuleContext context, SearchType searchType, Expression term, boolean onlyMarkedCards) {
        this(context, searchType, term, null, onlyMarkedCards);
    }

    public FindCmd(ParserRuleContext context, SearchType searchType, Expression term, Expression field, boolean onlyMarkedCards) {
        super(context, "find");

        this.searchType = searchType;
        this.term = term;
        this.field = field;
        this.onlyMarkedCards = onlyMarkedCards;
    }

    @Override
    protected void onExecute() throws HtException {


        PartSpecifier fieldSpecifier = field == null ?
                null :
                field.factor(PartContainerExp.class, new HtSemanticException("Can't search that.")).evaluateAsSpecifier();

        SearchQuery query = fieldSpecifier == null ?
                new SearchQuery(searchType, term.evaluate().stringValue(), onlyMarkedCards) :
                new SearchQuery(searchType, term.evaluate().stringValue(), fieldSpecifier);

        SearchContext.getInstance().find(query);
    }
}
