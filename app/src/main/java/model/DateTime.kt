package model

import android.os.Parcelable
import androidx.annotation.Keep
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class DateTime(
    //if field of serializable class not implement Serializable,
    // must be add `transient` keyword to that
    @Transient
    @IgnoredOnParcel
    var date: PersianPickerDate? = null,

    var hour: Int = 0,

    var minute: Int = 0,

    ) : Parcelable {

    val hourString: String
        get() = normalizeTime(hour)

    val minuteString: String
        get() = normalizeTime(minute)

    fun normalizeTime(clock: Int): String {
        return if (clock < 10)
            "0$clock"
        else
            clock.toString()
    }

    val clock: String
        get() = "$hourString:$minuteString"

    val persianDate: String
        get() = date!!.persianDayOfWeekName +
                "، " + date!!.persianDay +
                " " + date!!.persianMonthName +
                " " + date!!.persianYear
}