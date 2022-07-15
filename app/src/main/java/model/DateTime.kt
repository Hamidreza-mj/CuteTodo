package model

import androidx.annotation.Keep
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import java.io.Serializable

@Keep
class DateTime : Serializable {
    //if field of serializable class not implement Serializable,
    // must be add `transient` keyword to that
    @Transient
    var date: PersianPickerDate? = null

    var hour = 0
    var minute = 0

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