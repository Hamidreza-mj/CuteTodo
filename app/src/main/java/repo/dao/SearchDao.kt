package repo.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.Todo

@Dao
interface SearchDao {

    @Query("SELECT * FROM todos ORDER BY is_done, id DESC;")
    fun getAllTodos(): Flow<List<Todo>?>

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    suspend fun searchTodo(term: String?): List<Todo>?

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' AND category_id = :categoryId ORDER BY is_done, id DESC;")
    suspend fun searchTodoWithCategoryId(term: String?, categoryId: Int): List<Todo>?

    @Query("SELECT * FROM todos WHERE category LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    suspend fun searchCategory(term: String?): List<Todo>?

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' OR category LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    suspend fun searchTodoWithCategory(term: String?): List<Todo>?
}