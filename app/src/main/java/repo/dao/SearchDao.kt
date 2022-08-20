package repo.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.Todo

@Dao
interface SearchDao {

    @Query("SELECT * FROM todos ORDER BY is_done, id DESC;")
    fun getAllTodos(): Flow<List<Todo>?>

    @Query("SELECT * FROM todos WHERE category_id = :categoryId ORDER BY is_done, id DESC;")
    fun getTodosWithCategory(categoryId: Int): Flow<List<Todo>?>

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    fun searchTodo(term: String?): Flow<List<Todo>?>

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' AND category_id = :categoryId ORDER BY is_done, id DESC;")
    fun searchTodoWithCategoryId(term: String?, categoryId: Int): Flow<List<Todo>?>

    @Query("SELECT * FROM todos WHERE category LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    fun searchCategory(term: String?): Flow<List<Todo>?>

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' OR category LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    fun searchTodoWithCategory(term: String?): Flow<List<Todo>?>
}