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


    @Query("SELECT * FROM todos ORDER By id DESC;")
    List<Todo> getAllTodos();

    @Query("SELECT * FROM todos WHERE id = :todoID;")
    Todo getTodo(long todoID);

    @Query("DELETE FROM todos;")
    void deleteAllTodos();

    @Query("SELECT COUNT(*) FROM todos;")
    long getTodosCount();

    @Query("UPDATE todos SET is_done = not is_done WHERE id = :todoID")
    void setDoneTodo(long todoID);
}
