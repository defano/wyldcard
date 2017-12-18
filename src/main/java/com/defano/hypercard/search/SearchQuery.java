package com.defano.hypercard.search;

import com.defano.hypertalk.ast.model.SearchType;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;

public class SearchQuery {
    public final SearchType searchType;
    public final String searchTerm;
    public final PartSpecifier searchField;
    public final boolean searchOnlyMarkedCards;

    public SearchQuery(SearchType searchType, String searchTerm, PartSpecifier fieldSpecifier) {
        this.searchType = searchType;
        this.searchTerm = searchTerm;
        this.searchField = fieldSpecifier;
        this.searchOnlyMarkedCards = false;
    }

    public SearchQuery(SearchType searchType, String searchTerm, boolean searchOnlyMarkedCards) {
        this.searchType = searchType;
        this.searchTerm = searchTerm;
        this.searchField = null;
        this.searchOnlyMarkedCards = searchOnlyMarkedCards;
    }

    public boolean isSingleFieldSearch() {
        return searchField != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchQuery that = (SearchQuery) o;

        if (searchOnlyMarkedCards != that.searchOnlyMarkedCards) return false;
        if (searchType != that.searchType) return false;
        if (searchTerm != null ? !searchTerm.equals(that.searchTerm) : that.searchTerm != null) return false;
        return searchField != null ? searchField.equals(that.searchField) : that.searchField == null;
    }

    @Override
    public int hashCode() {
        int result = searchType != null ? searchType.hashCode() : 0;
        result = 31 * result + (searchTerm != null ? searchTerm.hashCode() : 0);
        result = 31 * result + (searchField != null ? searchField.hashCode() : 0);
        result = 31 * result + (searchOnlyMarkedCards ? 1 : 0);
        return result;
    }
}
