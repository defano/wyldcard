package com.defano.wyldcard.search;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface SearchManager extends SearchResultHighlighter {
    /**
     * Performs a search of the given query. Search results in field text are highlighted (boxed) and the foundLine,
     * foundText, foundChunk and foundField HyperCard properties are updated accordingly.
     *
     * If the given query is the same as the last query, this method "continues" the previous search, finding and
     * highlighting the next matching string.
     *
     *
     * @param context The execution context.
     * @param query The query to perform
     * @throws HtException Thrown if the query refers to a bogus field
     */
    void find(ExecutionContext context, SearchQuery query) throws HtException;

    /**
     * Reset the search context to its default, no-query state. Removes all search highlights and resets HyperCard
     * properties to their default, empty state.
     */
    void reset();

    Value getFoundChunk();

    Value getFoundField();

    Value getFoundLine();

    Value getFoundText();
}
