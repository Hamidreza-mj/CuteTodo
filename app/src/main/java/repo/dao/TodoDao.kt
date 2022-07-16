package repo.dao

import androidx.room.*
import model.Priority
import model.Todo

@Dao
interface TodoDao {

    @Insert(entity = Todo::class, onConflict = OnConflictStrategy.REPLACE)
    fun create(todo: Todo?): Long

    @Update(entity = Todo::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(todo: Todo?)

    @Delete(entity = Todo::class)
    fun delete(todo: Todo?)

    @Query("SELECT * FROM todos ORDER BY is_done, id DESC;")
    fun getAllTodos(): List<Todo>?

    @Query("SELECT * FROM todos WHERE category_id = :categoryId ORDER BY is_done, id DESC;")
    fun getTodosWithCategory(categoryId: Int): List<Todo>?

    @Query("SELECT * FROM todos WHERE id = :todoID;")
    fun getTodo(todoID: Long): Todo?

    @Query("DELETE FROM todos;")
    fun deleteAllTodos()

    @Query("DELETE FROM todos WHERE is_done = 1;")
    fun deleteAllDoneTodo()

    @Query("SELECT COUNT(*) FROM todos;")
    fun getTodosCount(): Long

    @Query("SELECT COUNT(*) FROM todos WHERE is_done = 1;")
    fun getDoneTodosCount(): Long

    @Query("UPDATE todos SET is_done = not is_done WHERE id = :todoID")
    fun setDoneTodo(todoID: Long)

    @Query("UPDATE todos SET is_done = 1 WHERE id = :todoID")
    fun setTodoIsDone(todoID: Long)

    @Query("SELECT * FROM todos WHERE is_done = :isDone AND priority IN (:priorities) ORDER BY is_done, id DESC;")
    fun filterByDoneTodos(isDone: Boolean, priorities: List<Priority?>?): List<Todo>?

    @Query("SELECT * FROM todos WHERE priority IN (:priorities) ORDER BY is_done, id DESC;")
    fun filterByAllTodos(priorities: List<Priority?>?): List<Todo>?

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    fun searchTodo(term: String?): List<Todo>?

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' AND category_id = :categoryId ORDER BY is_done, id DESC;")
    fun searchTodoWithCategoryId(term: String?, categoryId: Int): List<Todo>?

    @Query("SELECT * FROM todos WHERE category LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    fun searchCategory(term: String?): List<Todo>?

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' OR category LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    fun searchTodoWithCategory(term: String?): List<Todo>?
}