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

    suspend fun searchTodo(term: String?): List<Todo>? {
        return dao.searchTodo(term)
    }

    suspend fun searchTodoInSpecificCategory(term: String?, categoryId: Int): List<Todo>? {
        return dao.searchTodoWithCategoryId(term, categoryId)
    }

    suspend fun searchInCategories(term: String?): List<Todo>? {
        return dao.searchCategory(term)
    }

    suspend fun searchInTodosAndCategories(term: String?): List<Todo>? {
        return dao.searchTodoWithCategory(term)
    }

}