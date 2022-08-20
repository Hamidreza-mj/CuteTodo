package repo.dbRepoController

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import model.Todo
import repo.dao.SearchDao
import javax.inject.Inject

@ViewModelScoped
class SearchDBRepository @Inject constructor(
    private val dao: SearchDao
) {

    fun getAllTodos(): Flow<List<Todo>?> =
        dao.getAllTodos().distinctUntilChanged()

    fun getAllTodosInSpecificCategory(categoryId: Int): Flow<List<Todo>?> =
        dao.getTodosWithCategory(categoryId).distinctUntilChanged()

    fun searchTodo(term: String?): Flow<List<Todo>?> =
        dao.searchTodo(term).distinctUntilChanged()

    fun searchTodoInSpecificCategory(term: String?, categoryId: Int): Flow<List<Todo>?> =
        dao.searchTodoWithCategoryId(term, categoryId).distinctUntilChanged()

    fun searchInCategories(term: String?): Flow<List<Todo>?> =
        dao.searchCategory(term).distinctUntilChanged()

    fun searchInTodosAndCategories(term: String?): Flow<List<Todo>?> =
        dao.searchTodoWithCategory(term).distinctUntilChanged()

}