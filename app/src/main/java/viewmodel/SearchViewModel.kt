package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import model.Search
import model.Search.SearchMode
import model.Todo
import repo.dbRepoController.SearchDBRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dbRepository: SearchDBRepository
) : ViewModel() {

    val todosLiveData: LiveData<List<Todo>?> = dbRepository.todosLive
    private val _searchLiveData: MutableLiveData<Search?> = MutableLiveData()
    val searchLiveData: LiveData<Search?> = _searchLiveData

    @JvmOverloads
    fun fetch(search: Search? = this.search) {
        if (search == null)
            dbRepository.initFetch()
        else
            search(search)
    }

    fun fetch(categoryId: Int) {
        dbRepository.initFetch(categoryId)
    }

    fun search(search: Search) {
        _searchLiveData.value = search

        when (search.searchMode) {
            SearchMode.TODO -> {
                if (search.categoryId == 0)
                    dbRepository.searchTodo(search.term)
                else
                    dbRepository.searchTodo(search.term, search.categoryId)
            }

            SearchMode.CATEGORY -> dbRepository.searchCategory(search.term)

            SearchMode.BOTH -> dbRepository.searchTodoWithCategory(search.term)
        }
    }

    val search: Search?
        get() = if (_searchLiveData.value != null) _searchLiveData.value else null

    val currentTerm: String
        get() = if (search != null) search!!.term!!.trim() else ""

    val searchMode: SearchMode
        get() = if (search != null) search!!.searchMode else SearchMode.TODO

    fun release() {
        _searchLiveData.value = null
    }

    val titleTerm: String
        get() {
            val title: String = when (searchMode) {
                SearchMode.TODO -> "عنوان"

                SearchMode.CATEGORY -> "دسته‌بندی"

                SearchMode.BOTH -> "عنوان و دسته‌بندی"
            }

            return title
        }

    val titleTermResult: String
        get() {
            val title: String = when (searchMode) {
                SearchMode.TODO -> "عنوان کار"
                SearchMode.CATEGORY -> "عنوان دسته‌بندی"
                SearchMode.BOTH -> "عنوان کار و دسته‌بندی"
            }

            return title
        }

}