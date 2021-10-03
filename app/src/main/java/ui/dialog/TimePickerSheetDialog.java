package ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.SheetTimePickerBinding;
import ui.component.CustomNumberPicker;

public class TimePickerSheetDialog {

    private final BottomSheetDialog sheetDialog;

    private final CustomNumberPicker hourPicker;
    private final CustomNumberPicker minutePicker;
    private final int hour;
    private final int minute;

    private OnClickApply onClickApply;

    public TimePickerSheetDialog(Context context, boolean cancelable, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;

        sheetDialog = new BottomSheetDialog(context, R.style.TranslucentDialog);
        sheetDialog.setCancelable(cancelable);
        SheetTimePickerBinding binding = SheetTimePickerBinding.inflate(LayoutInflater.from(context), null, false);
        sheetDialog.setContentView(binding.getRoot());

        hourPicker = binding.nPickerHour;
        minutePicker = binding.nPickerMinute;
        MaterialButton btnSet = binding.mBtnSet;

        btnSet.setOnClickListener(view -> dismiss());
        btnSet.setOnClickListener(view -> {
            if (onClickApply == null) {
                dismiss();
                return;
            }

            onClickApply.onClick();
        });

        binding.mBtnCancel.setOnClickListener(view -> dismiss());

        //need to set post delay for updating number pickers
        hourPicker.postDelayed(this::handleHourNumPicker, 100);
        minutePicker.postDelayed(this::handleMinuteNumPicker, 100);

        sheetDialog.create();
    }

    private void handleHourNumPicker() {
        hourPicker.setDisplayedValues(getAllHours());
//        hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hourPicker.setWrapSelectorWheel(true);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(hour);
    }

    private void handleMinuteNumPicker() {
        minutePicker.setDisplayedValues(getAllMinutes());
//        minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutePicker.setWrapSelectorWheel(true);

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(minute);
    }

    private String[] getAllHours() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 24; i++)
            arrayList.add(i < 10 ? "0" + i : String.valueOf(i)); // i < 0 -> 00 01 02 03 04 05 ...

        String[] hours = new String[arrayList.size()];
        hours = arrayList.toArray(hours);

        return hours;
    }

    private String[] getAllMinutes() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 60; i++)
            arrayList.add(i < 10 ? "0" + i : String.valueOf(i)); // i < 0 -> 00 01 02 03 04 05 ...

        String[] minutes = new String[arrayList.size()];
        minutes = arrayList.toArray(minutes);

        return minutes;
    }

    public TimePickerSheetDialog(Context context, int hour, int minute) {
        this(context, true, hour, minute);
    }

    public void setOnClickApply(OnClickApply onClickApply) {
        this.onClickApply = onClickApply;
    }

    public void dismiss() {
        if (sheetDialog != null)
            sheetDialog.dismiss();

    }

    public void show() {
        if (sheetDialog != null)
            sheetDialog.show();

    }

    public interface OnClickApply {
        void onClick();
    }

}
