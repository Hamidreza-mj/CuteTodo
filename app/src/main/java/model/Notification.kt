package model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Keep
@Entity(tableName = "notifications")
data class Notification(
    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: Int = 0,

    @ColumnInfo(name = "content")
    var content: String? = null,

    @ColumnInfo(name = "is_done")
    var isDone: Boolean = false,

    @ColumnInfo(name = "is_shown", defaultValue = "false")
    var isShown: Boolean = false,

    @ColumnInfo(name = "arrive_date", defaultValue = "0")
    var arriveDate: Long = 0,

    @ColumnInfo(name = "priority")
    var priority: Priority? = null,

    @ColumnInfo(name = "category")
    var category: String? = null,

    @ColumnInfo(name = "category_id", defaultValue = "0")
    var categoryId: Int = 0
) : Serializable {

    fun initWith(initTodo: Todo?) {
        if (initTodo == null)
            return

        id = initTodo.id
        content = initTodo.title
        isDone = initTodo.isDone
        isShown = false
        arriveDate = initTodo.arriveDate
        priority = initTodo.priority
        category = initTodo.category
        categoryId = initTodo.categoryId
    }
}