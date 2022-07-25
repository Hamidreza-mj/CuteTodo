package utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateHelper {

    private final Date date;

    public DateHelper(long timeMillisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillisecond);
        this.date = calendar.getTime();
    }

    public SimpleDateFormat yearFormatter() {
        return new SimpleDateFormat("yyyy");
    }

    public SimpleDateFormat monthFormatter() {
        return new SimpleDateFormat("MM");
    }

    public SimpleDateFormat dayFormatter() {
        return new SimpleDateFormat("dd");
    }

    public SimpleDateFormat hourFormatter() {
        return new SimpleDateFormat("HH");
    }

    public SimpleDateFormat minuteFormatter() {
        return new SimpleDateFormat("mm");
    }

    public int getYear() {
        int year = 0;
        try {
            year = Integer.parseInt(yearFormatter().format(date));
        } catch (Exception ignored) {
        }

        return year;
    }

    public int getMonth() {
        int month = 0;
        try {
            month = Integer.parseInt(monthFormatter().format(date));
        } catch (Exception ignored) {
        }

        return month;
    }

    public int getDay() {
        int day = 0;
        try {
            day = Integer.parseInt(dayFormatter().format(date));
        } catch (Exception ignored) {
        }

        return day;
    }

    public int getHour() {
        int hour = 0;
        try {
            hour = Integer.parseInt(hourFormatter().format(date));
        } catch (Exception ignored) {
        }

        return hour;
    }

    public int getMinute() {
        int minute = 0;
        try {
            minute = Integer.parseInt(minuteFormatter().format(date));
        } catch (Exception ignored) {
        }

        return minute;
    }

    public Date getDate() {
        return date;
    }

    public String getHourString() {
        return normalizeTime(getHour());
    }

    public String getMinuteString() {
        return normalizeTime(getMinute());
    }

    public String normalizeTime(int clock) {
        return clock < 10 ? "0" + clock : String.valueOf(clock);
    }

    public String getClock() {
        return getHourString() + ":" + getMinuteString();
    }

}

