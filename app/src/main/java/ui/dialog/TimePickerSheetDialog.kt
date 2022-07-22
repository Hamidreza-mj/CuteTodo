package ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.NumberPicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import hlv.cute.todo.R
import hlv.cute.todo.databinding.SheetTimePickerBinding
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import model.DateTime
import java.text.MessageFormat

class TimePickerSheetDialog(
    context: Context?,
    cancelable: Boolean,
    private val dateTime: DateTime
) {

    private var binding: SheetTimePickerBinding
    private val sheetDialog: BottomSheetDialog?

    var onClickApply: ((dateTime: DateTime?) -> Unit)? = null
    var onBackClick: ((date: PersianPickerDate?) -> Unit)? = null

    init {
        binding = SheetTimePickerBinding.inflate(LayoutInflater.from(context), null, false)

        sheetDialog = BottomSheetDialog(context!!, R.style.AppBottomSheetDialogTheme).apply {
            setCancelable(cancelable)
            setContentView(binding.root)
            create()
        }

        binding.mBtnCancel.setOnClickListener { dismiss() }

        binding.mBtnSet.setOnClickListener {
            if (onClickApply == null) {
                dismiss()
                return@setOnClickListener
            }

            onClickApply!!(dateTime)
        }

        binding.aImgBack.setOnClickListener {
            if (onBackClick == null) {
                dismiss()
                return@setOnClickListener
            }

            onBackClick!!(dateTime.date)
            dismiss()
        }

        //need to set post delay for updating number pickers
        binding.nPickerHour.postDelayed({ handleHourNumPicker() }, 100)
        binding.nPickerMinute.postDelayed({ handleMinuteNumPicker() }, 100)

        updateValues(dateTime)

        binding.nPickerHour.setOnValueChangedListener { _: NumberPicker?, _: Int, newHour: Int ->
            dateTime.hour = newHour
            updateValues(dateTime)
        }

        binding.nPickerMinute.setOnValueChangedListener { _: NumberPicker?, _: Int, newMinute: Int ->
            dateTime.minute = newMinute
            updateValues(dateTime)
        }
    }

    private fun handleHourNumPicker() {
        binding.nPickerHour.displayedValues = getAllHours()
        //hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.nPickerHour.wrapSelectorWheel = true
        binding.nPickerHour.minValue = 0
        binding.nPickerHour.maxValue = 23
        binding.nPickerHour.value = dateTime.hour
    }

    private fun handleMinuteNumPicker() {
        binding.nPickerMinute.displayedValues = getAllMinutes()
        //minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        binding.nPickerMinute.wrapSelectorWheel = true
        binding.nPickerMinute.minValue = 0
        binding.nPickerMinute.maxValue = 59
        binding.nPickerMinute.value = dateTime.minute
    }

    // i < 0 ==> 00 01 02 03 04 05 ...
    private fun getAllHours(): Array<String?> {
        val arrayList = ArrayList<String>()

        for (i in 0..23)
            arrayList.add(dateTime.normalizeTime(i)) //normalize: i < 0 ==> 00 01 02 03 04 05 ...

        var hours = arrayOfNulls<String>(arrayList.size)
        hours = arrayList.toArray(hours)

        return hours
    }

    // i < 0  ==> 00 01 02 03 04 05 ...
    private fun getAllMinutes(): Array<String?> {
        val arrayList = ArrayList<String>()

        for (i in 0..59)
            arrayList.add(dateTime.normalizeTime(i)) // i < 0  ==> 00 01 02 03 04 05 ...

        var minutes = arrayOfNulls<String>(arrayList.size)
        minutes = arrayList.toArray(minutes)

        return minutes
    }

    private fun updateValues(dateTime: DateTime) {
        binding.txtTitle.text = MessageFormat.format(
            "{0}\n ساعت {1}:{2}",
            dateTime.persianDate,
            dateTime.hourString,
            dateTime.minuteString
        )
    }

    fun show() = sheetDialog?.show()

    fun dismiss() = sheetDialog?.dismiss()

}