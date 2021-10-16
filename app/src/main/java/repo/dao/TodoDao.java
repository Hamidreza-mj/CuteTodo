package repo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import model.Todo;

@Dao
public interface TodoDao {

    @Insert(entity = Todo.class, onConflict = OnConflictStrategy.REPLACE)
    void create(Todo todo);

    @Update(entity = Todo.class, onConflict = OnConflictStrategy.REPLACE)
    void update(Todo todo);

    @Delete(entity = Todo.class)
    void delete(Todo todo);


    @Query("SELECT * FROM todos ORDER BY is_done, id DESC;")
    List<Todo> getAllTodos();

    @Query("SELECT * FROM todos WHERE id = :todoID;")
    Todo getTodo(long todoID);

    @Query("DELETE FROM todos;")
    void deleteAllTodos();

    @Query("DELETE FROM todos WHERE is_done = 1;")
    void deleteAllDoneTodo();

    @Query("SELECT COUNT(*) FROM todos;")
    long getTodosCount();

    @Query("SELECT COUNT(*) FROM todos WHERE is_done = 1;")
    long getDoneTodosCount();

    @Query("UPDATE todos SET is_done = not is_done WHERE id = :todoID")
    void setDoneTodo(long todoID);

    @Query("SELECT * FROM todos WHERE is_done = :isDone AND priority IN (:priorities) ORDER BY is_done, id DESC;")
    List<Todo> filterByDoneTodos(boolean isDone, List<Todo.Priority> priorities);

    @Query("SELECT * FROM todos WHERE priority IN (:priorities) ORDER BY is_done, id DESC;")
    List<Todo> filterByAllTodos(List<Todo.Priority> priorities);

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    List<Todo> searchTodo(String term);

    @Query("SELECT * FROM todos WHERE category LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    List<Todo> searchCategory(String term);

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :term || '%' OR category LIKE '%' || :term || '%' ORDER BY is_done, id DESC;")
    List<Todo> searchTodoWithCategory(String term);
}
