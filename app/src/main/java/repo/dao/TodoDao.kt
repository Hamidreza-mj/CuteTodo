package repo.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import model.Priority
import model.Todo

@Dao
interface TodoDao {

    @Insert(entity = Todo::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(todo: Todo?): Long

    @Update(entity = Todo::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(todo: Todo?)

    @Delete(entity = Todo::class)
    suspend fun delete(todo: Todo?)

    @Query("SELECT * FROM todos ORDER BY is_done, id DESC;")
    fun getAllTodos(): Flow<List<Todo>?>

    @Query("SELECT * FROM todos WHERE id = :todoID;")
    suspend fun getTodo(todoID: Long): Todo?

    @Query("DELETE FROM todos;")
    suspend fun deleteAllTodos()

    @Query("DELETE FROM todos WHERE is_done = 1;")
    suspend fun deleteAllDoneTodo()

    @Query("SELECT COUNT(*) FROM todos;")
    suspend fun getTodosCount(): Long

    @Query("SELECT COUNT(*) FROM todos WHERE is_done = 1;")
    suspend fun getDoneTodosCount(): Long

    @Query("UPDATE todos SET is_done = not is_done WHERE id = :todoID")
    suspend fun setDoneTodo(todoID: Long)

    @Query("UPDATE todos SET is_done = 1 WHERE id = :todoID")
    suspend fun setTodoIsDone(todoID: Long)

    @Query("SELECT * FROM todos WHERE is_done = :isDone AND priority IN (:priorities) ORDER BY is_done, id DESC;")
    suspend fun filterByDoneTodos(isDone: Boolean, priorities: List<Priority?>?): List<Todo>?

    @Query("SELECT * FROM todos WHERE priority IN (:priorities) ORDER BY is_done, id DESC;")
    suspend fun filterByAllTodos(priorities: List<Priority?>?): List<Todo>?

}