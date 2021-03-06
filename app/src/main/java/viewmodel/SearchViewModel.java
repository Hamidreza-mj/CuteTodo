package viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import model.Search;
import model.Todo;
import repo.dbRepoController.SearchDBRepository;

public class SearchViewModel extends ViewModel {

    private final SearchDBRepository dbRepository;
    private final LiveData<List<Todo>> todosLive;
    private final MutableLiveData<Search> searchLiveData;

    public SearchViewModel() {
        dbRepository = new SearchDBRepository();
        todosLive = dbRepository.getTodosLive();
        searchLiveData = new MutableLiveData<>();
    }

    public void fetch(Search search) {
        if (search == null)
            dbRepository.initFetch();
        else
            search(search);
    }

    public void fetch(int categoryId) {
        dbRepository.initFetch(categoryId);
    }

    public void fetch() {
        fetch(getSearch());
    }

    public void search(Search search) {
        searchLiveData.setValue(search);

        switch (search.getSearchMode()) {
            case TODO:
            default:
                if (search.getCategoryId() == 0)
                    dbRepository.searchTodo(search.getTerm());
                else
                    dbRepository.searchTodo(search.getTerm(), search.getCategoryId());
                break;

            case CATEGORY:
                dbRepository.searchCategory(search.getTerm());
                break;

            case BOTH:
                dbRepository.searchTodoWithCategory(search.getTerm());
                break;
        }
    }

    public LiveData<List<Todo>> getTodosLiveData() {
        return todosLive;
    }

    public LiveData<Search> getSearchLiveData() {
        return searchLiveData;
    }

    public Search getSearch() {
        if (getSearchLiveData().getValue() != null)
            return getSearchLiveData().getValue();

        return null;
    }

    public String getCurrentTerm() {
        if (getSearch() != null)
            return getSearch().getTerm().trim();

        return "";
    }

    public Search.SearchMode getSearchMode() {
        if (getSearch() != null)
            return getSearch().getSearchMode();

        return Search.SearchMode.TODO;
    }

    public void release() {
        searchLiveData.setValue(null);
    }

    public String getTitleTerm() {
        String title;
        switch (getSearchMode()) {
            case TODO:
            default:
                title = "??????????";
                break;

            case CATEGORY:
                title = "???????????????????";
                break;

            case BOTH:
                title = "?????????? ?? ???????????????????";
                break;
        }

        return title;
    }

    public String getTitleTermResult() {
        String title;
        switch (getSearchMode()) {
            case TODO:
            default:
                title = "?????????? ??????";
                break;

            case CATEGORY:
                title = "?????????? ???????????????????";
                break;

            case BOTH:
                title = "?????????? ?????? ?? ???????????????????";
                break;
        }

        return title;
    }

}
