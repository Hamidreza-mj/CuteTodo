package model;

import androidx.annotation.Keep;

import java.io.Serializable;

import ir.hamsaa.persiandatepicker.api.PersianPickerDate;

@Keep
public class DateTime implements Serializable {

    private PersianPickerDate date;
    private int hour;
    private int minute;

    public PersianPickerDate getDate() {
        return date;
    }

    public void setDate(PersianPickerDate date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getHourString() {
        return normalizeTime(hour);
    }

    public String getMinuteString() {
        return normalizeTime(minute);
    }

    public String normalizeTime(int clock) {
        return clock < 10 ? "0" + clock : String.valueOf(clock);
    }

    public String getClock() {
        return getHourString() + ":" + getMinuteString();
    }

    public String getPersianDate() {
        return date.getPersianDayOfWeekName() +
                "، " + date.getPersianDay() +
                " " + date.getPersianMonthName() +
                " " + date.getPersianYear();
    }
}
