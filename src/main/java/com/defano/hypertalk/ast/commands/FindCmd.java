package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.search.SearchContext;
import com.defano.hypercard.search.SearchQuery;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.common.SearchType;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class FindCmd extends Command {

    private final SearchType searchType;
    private final Expression term;
    private final PartContainerExp field;
    private final boolean onlyMarkedCards;

    public FindCmd(ParserRuleContext context, SearchType searchType, Expression term, boolean onlyMarkedCards) {
        this(context, searchType, term, null, onlyMarkedCards);
    }

    public FindCmd(ParserRuleContext context, SearchType searchType, Expression term, PartContainerExp field, boolean onlyMarkedCards) {
        super(context, "find");

        this.searchType = searchType;
        this.term = term;
        this.field = field;
        this.onlyMarkedCards = onlyMarkedCards;
    }

    @Override
    protected void onExecute() throws HtException, Breakpoint {
        PartSpecifier fieldSpecifier = field == null ? null : field.evaluateAsSpecifier();

        SearchQuery query = fieldSpecifier == null ?
                new SearchQuery(searchType, term.evaluate().stringValue(), onlyMarkedCards) :
                new SearchQuery(searchType, term.evaluate().stringValue(), fieldSpecifier);

        SearchContext.getInstance().find(query);
    }
}
