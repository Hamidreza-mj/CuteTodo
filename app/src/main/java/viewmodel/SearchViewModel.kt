package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import model.Search
import model.Search.SearchMode
import model.Todo
import repo.dbRepoController.SearchDBRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dbRepository: SearchDBRepository
) : ViewModel() {

    private var _searchedTodosStateFlow: MutableStateFlow<List<Todo>?> = MutableStateFlow(null)
    var searchedTodosStateFlow: StateFlow<List<Todo>?> = _searchedTodosStateFlow.asStateFlow()

    private val _searchStateFlow: MutableStateFlow<Search?> = MutableStateFlow(null)

    fun search(search: Search? = _searchStateFlow.value, categoryId: Int? = null) {
        _searchStateFlow.value = search

        viewModelScope.launch(Dispatchers.IO) {
            categoryId?.let { id ->
                _searchedTodosStateFlow.emitAll(dbRepository.getAllTodosInSpecificCategory(id))
                return@launch
            }

            if (search == null) {
                _searchedTodosStateFlow.emitAll(dbRepository.getAllTodos())
                return@launch
            }

            when (search.searchMode) {
                SearchMode.TODO -> {
                    if (search.categoryId == 0)
                        _searchedTodosStateFlow.emitAll(dbRepository.searchTodo(search.term))
                    else
                        _searchedTodosStateFlow.emitAll(
                            dbRepository.searchTodoInSpecificCategory(
                                search.term,
                                search.categoryId
                            )
                        )
                }

                SearchMode.CATEGORY -> _searchedTodosStateFlow.emitAll(
                    dbRepository.searchInCategories(
                        search.term
                    )
                )

                SearchMode.BOTH -> _searchedTodosStateFlow.emitAll(
                    dbRepository.searchInTodosAndCategories(
                        search.term
                    )
                )
            }
        }
    }

    val search: Search?
        get() = if (_searchStateFlow.value != null) _searchStateFlow.value else null

    val currentTerm: String
        get() = if (search != null) search!!.term!!.trim() else ""

    val searchMode: SearchMode
        get() = if (search != null) search!!.searchMode else SearchMode.TODO

    fun release() {
        _searchStateFlow.value = null
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