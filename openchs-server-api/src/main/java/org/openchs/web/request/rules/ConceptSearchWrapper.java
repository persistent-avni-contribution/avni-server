package org.openchs.web.request.rules;

import java.util.List;

public class ConceptSearchWrapper {

    List<SearchFilter> searchFilters;

    public List<SearchFilter> getSearchFilters() {
        return searchFilters;
    }

    public void setSearchFilters(List<SearchFilter> searchFilters) {
        this.searchFilters = searchFilters;
    }

    @Override
    public String toString() {
        return "ConceptSearchWrapper{" +
                "searchFilters=" + searchFilters +
                '}';
    }
}
