package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private var fetchJob: Job? = null
    private var searchStateJob: Job? = null
    private var searchTodoJob: Job? = null
    private var searchCategoryJob: Job? = null
    private var searchBothJob: Job? = null

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
        searchStateJob?.cancel()

        searchStateJob = viewModelScope.launch {
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
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch(Dispatchers.IO) {
            _searchedTodosStateFlow.emitAll(dbRepository.getAllTodos())
        }
    }

    suspend fun search(search: Search): List<Todo>? {
        var filteredTodos: List<Todo>? = emptyList()

        searchTodoJob?.cancel()
        searchCategoryJob?.cancel()
        searchBothJob?.cancel()

        when (search.searchMode) {
            SearchMode.TODO -> {
                search.categoryId.let { cid ->
                    if (cid == null || cid == 0) {
                        searchTodoJob = viewModelScope.launch(Dispatchers.IO) {
                            filteredTodos = dbRepository.searchTodo(search.term)
                        }

                        searchTodoJob?.join()
                    } else {
                        searchTodoJob = viewModelScope.launch(Dispatchers.IO) {
                            filteredTodos =
                                dbRepository.searchTodoInSpecificCategory(search.term, cid)
                        }

                        searchTodoJob?.join()
                    }
                }
            }

            SearchMode.CATEGORY -> {
                searchCategoryJob = viewModelScope.launch(Dispatchers.IO) {
                    filteredTodos = dbRepository.searchInCategories(search.term)
                }

                searchCategoryJob?.join()
            }

            SearchMode.BOTH -> {
                searchBothJob = viewModelScope.launch(Dispatchers.IO) {
                    filteredTodos = dbRepository.searchInTodosAndCategories(search.term)
                }

                searchBothJob?.join()
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