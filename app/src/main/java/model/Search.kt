package model;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class Search implements Serializable {

    private String term;
    private SearchMode searchMode;
    private int categoryId = 0;

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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public enum SearchMode {
        TODO, CATEGORY, BOTH
    }

}


