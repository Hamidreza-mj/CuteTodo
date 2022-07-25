package utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class DateHelper(timeMillisecond: Long) {

    val date: Date

    private fun yearFormatter(): SimpleDateFormat = SimpleDateFormat("yyyy")
    private fun monthFormatter(): SimpleDateFormat = SimpleDateFormat("MM")
    private fun dayFormatter(): SimpleDateFormat = SimpleDateFormat("dd")

    private fun hourFormatter(): SimpleDateFormat = SimpleDateFormat("HH")
    private fun minuteFormatter(): SimpleDateFormat = SimpleDateFormat("mm")

    init {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillisecond
        date = calendar.time
    }

    fun getYear(): Int {
        var year = 0
        try {
            year = yearFormatter().format(date).toInt()
        } catch (ignored: Exception) {
        }
        return year
    }

    fun getMonth(): Int {
        var month = 0
        try {
            month = monthFormatter().format(date).toInt()
        } catch (ignored: Exception) {
        }
        return month
    }

    fun getDay(): Int {
        var day = 0
        try {
            day = dayFormatter().format(date).toInt()
        } catch (ignored: Exception) {
        }
        return day
    }

    fun getHour(): Int {
        var hour = 0
        try {
            hour = hourFormatter().format(date).toInt()
        } catch (ignored: Exception) {
        }
        return hour
    }

    fun getMinute(): Int {
        var minute = 0
        try {
            minute = minuteFormatter().format(date).toInt()
        } catch (ignored: Exception) {
        }
        return minute
    }

    val hourString: String
        get() = normalizeTime(getHour())

    val minuteString: String
        get() = normalizeTime(getMinute())

    private fun normalizeTime(clock: Int): String = if (clock < 10) "0$clock" else clock.toString()

    fun getClock(): String = "$hourString:$minuteString"

}