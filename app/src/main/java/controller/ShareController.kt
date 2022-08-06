package controller

import android.app.Activity
import android.content.Intent
import dagger.hilt.android.scopes.FragmentScoped
import hlv.cute.todo.R
import ir.hamsaa.persiandatepicker.date.PersianDateImpl
import model.Priority
import model.Todo
import utils.DateHelper
import utils.ResourceProvider
import javax.inject.Inject

@FragmentScoped
class ShareController @Inject constructor(
    private val provideResource: ResourceProvider
) {

    fun shareString(activity: Activity?, shareContent: String) {
        activity?.let {
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareContent)
            }

            it.startActivity(
                Intent.createChooser(sharingIntent, provideResource.getString(R.string.share_using))
            )
        }
    }

    fun prepareShareTodoContent(todo: Todo?): String {
        todo?.let {
            var content = provideResource.getString(R.string.todo_title) + "\n" + it.title + "\n\n"

            if (it.category != null)
                content += provideResource.getString(R.string.todo_category) + " " + it.category + "\n\n"

            content += provideResource.getString(R.string.todo_priority) + " " + getCurrentPriority(
                it
            ) + "\n\n"

            content += if (it.isDone) "انجام شده ✅" else "انجام نشده ❌️"
            content += "\n\n"

            if (it.arriveDate != 0L)
                content += "🔔" + provideResource.getString(R.string.todo_reminder) + "\n" + getCompleteDate(
                    it.arriveDate
                ) + "\n\n"

            content += "\n" + provideResource.getString(R.string.app_name) + "\n" + "اپلیکیشن مدیریت کارهای روزانه"

            return content
        }

        return provideResource.getString(R.string.app_name)
    }

    private fun getCurrentPriority(todo: Todo?): String {
        var priority = provideResource.getString(R.string.low)

        if (todo != null) {
            priority = when (todo.priority) {
                Priority.LOW -> provideResource.getString(R.string.low)
                Priority.NORMAL -> provideResource.getString(R.string.normal)
                Priority.HIGH -> provideResource.getString(R.string.high)
                else -> provideResource.getString(R.string.low)
            }
        }

        return priority
    }

    private fun getCompleteDate(timeMillis: Long): String {
        val dateHelper = DateHelper(timeMillis)

        val hour = dateHelper.hourString
        val minute = dateHelper.minuteString

        val persianDate = PersianDateImpl().apply {
            setDate(timeMillis)
        }

        return persianDate.persianDayOfWeekName +
                "، " + persianDate.persianDay +
                " " + persianDate.persianMonthName +
                " " + persianDate.persianYear +
                "، ساعت " + hour + ":" + minute
    }

}