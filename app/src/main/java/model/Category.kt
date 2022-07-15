package model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Keep
@Entity(tableName = "categories")
data class Category(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @Ignore
    var isSelectedForFilter: Boolean = false
) : Comparable<Category>, Serializable {

    override fun compareTo(other: Category): Int {
        val isSame = id == other.id &&
                name == other.name && isSelectedForFilter == other.isSelectedForFilter
        return if (isSame) 0 else 1
    }
}