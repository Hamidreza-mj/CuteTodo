package model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.date.PersianDateImpl
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import utils.DateHelper

@Keep
@Parcelize
@Entity(tableName = "todos")
data class Todo(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "category_id")
    var categoryId: Int = 0,

    @ColumnInfo(name = "category")
    var category: String? = null,

    @ColumnInfo(name = "priority")
    var priority: Priority? = null,

    @ColumnInfo(name = "is_done")
    var isDone: Boolean = false,

    @ColumnInfo(name = "arrive_date", defaultValue = "0")
    var arriveDate: Long = 0,

    @ColumnInfo(name = "created_at", defaultValue = "0")
    var createdAt: Long = 0,

    @ColumnInfo(name = "updated_at", defaultValue = "0")
    var updatedAt: Long = 0,
) : Comparable<Todo>, Parcelable {

    @Ignore
    @IgnoredOnParcel
    var dateTime: DateTime? = null
        get() {
            field = DateTime()
            if (arriveDate == 0L) return field

            val persianDate: PersianPickerDate = PersianDateImpl()
            persianDate.setDate(arriveDate)

            field!!.date = persianDate

            val dateHelper = DateHelper(arriveDate)
            field!!.hour = dateHelper.getHour()
            field!!.minute = dateHelper.getMinute()

            return field
        }

    val clock: String
        get() {
            val dateHelper = DateHelper(arriveDate)
            return dateHelper.getClock()
        }

    override fun compareTo(other: Todo): Int {
        val isSame = id == other.id &&
                title == other.title &&
                category == other.category &&
                categoryId == other.categoryId &&
                arriveDate == other.arriveDate &&
                priority == other.priority &&
                isDone == other.isDone

        return if (isSame) 0 else 1
    }
}