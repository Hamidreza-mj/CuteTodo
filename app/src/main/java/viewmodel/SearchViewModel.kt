package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Search
import model.Search.SearchMode
import model.Todo
import repo.dbRepoController.SearchDBRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dbRepository: SearchDBRepository
) : ViewModel() {

    private var _searchedTodosStateFlow: MutableSharedFlow<List<Todo>?> = MutableSharedFlow()
    var searchedTodosStateFlow: SharedFlow<List<Todo>?> = _searchedTodosStateFlow.asSharedFlow()

    private val _searchStateFlow: MutableStateFlow<Search?> = MutableStateFlow(null)
    val searchStateFlow: Flow<Search?> = _searchStateFlow.asStateFlow()

    val currentSearch: Search?
        get() = _searchStateFlow.value

    val currentTerm: String
        get() = currentSearch?.term?.trim() ?: ""

    val searchMode: SearchMode
        get() = currentSearch?.searchMode ?: SearchMode.TODO

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

    /**
     * need for switching search mode from any to SearchMode.Todo
     * for remember what category are selected before switching
     * to navigate to this with cid
     */
    var lastSelectedCategory: Int = 0

    fun applySearchState(
        term: String? = currentSearch?.term,
        searchMode: SearchMode = currentSearch?.searchMode ?: SearchMode.TODO,
        categoryId: Int? = currentSearch?.categoryId
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //need to copy for sequence emiting, if not copy the same value not emit
                val newSearch = currentSearch?.copy()?.also {
                    it.term = term
                    it.searchMode = searchMode
                    it.categoryId = categoryId
                } ?: run {
                    Search(term, searchMode, categoryId)
                }

                _searchStateFlow.emit(newSearch)
            }
        }
    }

    fun fetch() {
        viewModelScope.launch(Dispatchers.IO) {
            _searchedTodosStateFlow.emitAll(dbRepository.getAllTodos())
        }
    }

    suspend fun search(search: Search): List<Todo>? {
        var filteredTodos: List<Todo>? = emptyList()

        when (search.searchMode) {
            SearchMode.TODO -> {
                search.categoryId.let { cid ->
                    if (cid == null || cid == 0) {
                        viewModelScope.launch(Dispatchers.IO) {
                            filteredTodos = dbRepository.searchTodo(search.term)
                        }.join()
                    } else {
                        viewModelScope.launch(Dispatchers.IO) {
                            filteredTodos =
                                dbRepository.searchTodoInSpecificCategory(search.term, cid)
                        }.join()
                    }
                }
            }

            SearchMode.CATEGORY -> {
                viewModelScope.launch(Dispatchers.IO) {
                    filteredTodos = dbRepository.searchInCategories(search.term)
                }.join()
            }

            SearchMode.BOTH -> {
                viewModelScope.launch(Dispatchers.IO) {
                    filteredTodos = dbRepository.searchInTodosAndCategories(search.term)
                }.join()
            }
        }

        return filteredTodos
    }

    fun release() {
        viewModelScope.launch(Dispatchers.IO) {
            _searchStateFlow.emit(null)
        }
    }

    fun getCategoryIdAfterSwitchMode(newMode: SearchMode): Int? {
        return if (newMode == SearchMode.CATEGORY)
            null //to reset
        else
            lastSelectedCategory
    }

}