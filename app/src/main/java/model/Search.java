package model;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class Search implements Serializable {

    private String term;
    private SearchMode searchMode;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public SearchMode getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(SearchMode searchMode) {
        this.searchMode = searchMode;
    }

    public enum SearchMode {
        TODO, CATEGORY, BOTH
    }

}


