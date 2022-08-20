package viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hlv.cute.todo.R
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.date.PersianDateImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import model.Category
import model.DateTime
import model.Priority
import model.Todo
import utils.DateHelper
import utils.ResourceProvider
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val provideResource: ResourceProvider
) : ViewModel() {

    private val _categoryStateFlow: MutableStateFlow<Category?> = MutableStateFlow(null)
    val categoryStateFlow: StateFlow<Category?> = _categoryStateFlow

    private val _dateTimeStateFlow: MutableStateFlow<DateTime?> = MutableStateFlow(null)
    val dateTimeStateFlow: StateFlow<DateTime?> = _dateTimeStateFlow

    private val oldDateTimeStateFlow: MutableStateFlow<DateTime?> = MutableStateFlow(null)

    var todo: Todo? = null
        set(value) {
            field = value
            handleCategory()
        }

    var priority: Priority = Priority.NORMAL

    var tempDateTime = DateTime()
        private set

    var isEditMode = false
    var isShareMode = false
    private var isCleared = false

    var shareTitle: String? = null

    val category: Category?
        get() = categoryStateFlow.value

    private val dateTime: DateTime?
        get() = dateTimeStateFlow.value

    val oldDateTime: DateTime?
        get() = oldDateTimeStateFlow.value

    fun firstInitDateTime() {
        val firstDateTime: DateTime?

        if (isEditMode) {
            firstDateTime = todo!!.dateTime
            if (todo!!.dateTime == null) {
                val persianDate = PersianDateImpl()
                persianDate.setDate(
                    PersianDatePickerDialog.THIS_YEAR,
                    PersianDatePickerDialog.THIS_MONTH,
                    PersianDatePickerDialog.THIS_DAY
                )
                firstDateTime!!.date = persianDate
            }
        } else {
            firstDateTime = DateTime()
        }

        oldDateTimeStateFlow.value = firstDateTime
        _dateTimeStateFlow.value = firstDateTime
    }

    private fun oldDateTimeIsValid(): Boolean = oldDateTime != null && oldDateTime!!.date != null

    fun commitCategory(category: Category?) {
        _categoryStateFlow.value = category
    }

    fun commitDateTime(dateTime: DateTime?) {
        _dateTimeStateFlow.value = dateTime ?: DateTime()
    }

    fun commitOldDateTime(dateTime: DateTime?) {
        oldDateTimeStateFlow.value = dateTime ?: DateTime()
    }

    fun releaseAll() {
        isCleared = true
        commitDateTime(null)
        commitOldDateTime(null)
    }

    fun configTempDateTime() {
        tempDateTime = DateTime() //release temp for reseting values
        val dateHelper: DateHelper

        if (isEditMode && todo!!.arriveDate != 0L) {
            //edit mode & has date
            if (oldDateTimeIsValid()) {
                //old date is valid (set before)
                tempDateTime.hour = oldDateTime!!.hour
                tempDateTime.minute = oldDateTime!!.minute
            } else {
                //old date invalid
                dateHelper = if (isCleared) //if clear old date
                    DateHelper(System.currentTimeMillis())
                else  //if old date not found and it exists without any change (normal edit mode)
                    DateHelper(todo!!.arriveDate)

                tempDateTime.hour = dateHelper.getHour()
                tempDateTime.minute = dateHelper.getMinute()
            }

        } else {
            //add mode or hasn't date (in edit only)
            if (oldDateTimeIsValid()) { //old is valid, set with old datas
                tempDateTime.hour = oldDateTime!!.hour
                tempDateTime.minute = oldDateTime!!.minute
            } else { //old date is invalid set now current time (edit mode or add [add without date])
                dateHelper = DateHelper(System.currentTimeMillis())
                tempDateTime.hour = dateHelper.getHour()
                tempDateTime.minute = dateHelper.getMinute()
            }
        }
    }

    fun setDateTemp(date: PersianPickerDate?) {
        tempDateTime.date = date
    }

    private fun setHourTemp(hour: Int) {
        tempDateTime.hour = hour
    }

    private fun setMinuteTemp(minute: Int) {
        tempDateTime.minute = minute
    }

    /**
     * set init date value for DatePicker & TimePicker
     *
     * @param persianDate PersianPickerDate
     * @return int[] of date: <br></br> index[0] -> year <br></br> index[1] -> month <br></br> index[2] -> day
     */
    fun setInitDateValue(persianDate: PersianPickerDate?): IntArray {
        val initValues: IntArray

        if (persianDate == null) {
            initValues = intArrayOf(
                PersianDatePickerDialog.THIS_YEAR,
                PersianDatePickerDialog.THIS_MONTH,
                PersianDatePickerDialog.THIS_DAY
            )

            initTimeEditModeWithUnsetDate()
        } else {
            initValues = intArrayOf(
                persianDate.persianYear,
                persianDate.persianMonth,
                persianDate.persianDay
            )
        }

        return initValues
    }

    private fun initTimeEditModeWithUnsetDate() {
        //if is edit mode and date not set (hasn't date)
        if (isEditMode) {
            val dateHelper = DateHelper(System.currentTimeMillis())
            setHourTemp(dateHelper.getHour())
            setMinuteTemp(dateHelper.getMinute())
        }
    }

    fun categoryIsValid(): Boolean {
        return category != null && category!!.id != 0 && category!!.name != null
    }

    fun addTodo(title: String?): Todo {
        todo = Todo().also {
            it.title = title
            it.priority = priority
            it.createdAt = System.currentTimeMillis()
        }

        if (dateTime != null && dateTime!!.date != null) {
            todo!!.dateTime = dateTime

            val calendar = Calendar.getInstance().also {

                it.timeInMillis = dateTime!!.date!!.timestamp

                it[Calendar.HOUR_OF_DAY] = dateTime!!.hour //HOUR_OF_DAY is 24 hours format
                it[Calendar.MINUTE] = dateTime!!.minute
                it[Calendar.SECOND] = 0
            }

            todo!!.arriveDate = calendar.timeInMillis
        }

        if (category != null) {
            todo!!.categoryId = category!!.id
            todo!!.category = category!!.name
        } else {
            todo!!.categoryId = 0
            todo!!.category = null
        }

        return todo as Todo
    }

    fun editTodo(newTitle: String?): Todo {
        val mustBeEditTodo = Todo().also {
            it.id = todo!!.id
            it.title = newTitle
            it.priority = priority
            it.isDone = todo!!.isDone
            it.createdAt = todo!!.createdAt
        }

        if (dateTime != null && dateTime!!.date != null) {
            mustBeEditTodo.dateTime = dateTime

            val calendar = Calendar.getInstance().also {
                it.timeInMillis = dateTime!!.date!!.timestamp
                it[Calendar.HOUR_OF_DAY] = dateTime!!.hour //HOUR_OF_DAY is 24 hours format
                it[Calendar.MINUTE] = dateTime!!.minute
                it[Calendar.SECOND] = 0
            }

            mustBeEditTodo.arriveDate = calendar.timeInMillis

        } else {
            mustBeEditTodo.arriveDate = 0
        }

        if (category != null) {
            mustBeEditTodo.categoryId = category!!.id
            mustBeEditTodo.category = category!!.name
        } else {
            mustBeEditTodo.categoryId = 0
            mustBeEditTodo.category = null
        }

        if (mustBeEditTodo.compareTo(todo!!) != 0) //if is not same, update the value of updated_at in db
            mustBeEditTodo.updatedAt = System.currentTimeMillis()
        else //keep current
            mustBeEditTodo.updatedAt = todo!!.updatedAt

        return mustBeEditTodo
    }

    fun todayTtimePassed(dateTime: DateTime?): Boolean {
        if (dateTime == null)
            return false

        val pickedYear = dateTime.date!!.persianYear
        val pickedMonth = dateTime.date!!.persianMonth
        val pickedDay = dateTime.date!!.persianDay

        val pickedHour = dateTime.hour
        val pickedMinute = dateTime.minute


        val persianDate: PersianPickerDate = PersianDateImpl().apply {
            setDate(System.currentTimeMillis())
        }

        val dateHelper = DateHelper(System.currentTimeMillis())

        val nowYear = persianDate.persianYear
        val nowMonth = persianDate.persianMonth
        val nowDay = persianDate.persianDay

        val nowHour = dateHelper.getHour()
        val nowMinute = dateHelper.getMinute()

        return if (pickedYear == nowYear && pickedMonth == nowMonth && pickedDay == nowDay) {
            if (pickedHour < nowHour) {
                true
            } else if (pickedHour == nowHour) {
                if (pickedMinute < nowMinute)
                    true
                else
                    pickedMinute == nowMinute
            } else {
                false
            }

        } else false
    }

    //region: get text and strings
    val titleFragment: String
        get() = provideResource.getString(if (isEditMode) R.string.edit_todo else R.string.add_new_todo)

    val buttonPrimaryText: String
        get() = provideResource.getString(if (isEditMode) R.string.edit else R.string.save)

    //add mode or share mode
    val editTextTitle: String?
        get() {
            return if (isEditMode) {
                todo!!.title
            } else {
                //add mode or share mode
                if (isShareMode && shareTitle != null)
                    shareTitle
                else
                    ""
            }
        }

    val categoryTitleText: String?
        get() = if (categoryIsValid()) category!!.name else provideResource.getString(R.string.enter_category_name)

    //endregion

    private fun handleCategory() {
        if (todo?.categoryId != 0 && todo?.category != null) {
            val category = Category().apply {
                id = todo!!.categoryId
                name = todo!!.category
            }

            commitCategory(category)
        }
    }

}