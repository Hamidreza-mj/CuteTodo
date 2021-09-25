package utils;


import android.graphics.Paint;
import android.widget.TextView;

public class TextHelper {

    public static void addLineThrough(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void removeLineThrough(TextView textView) {
        textView.setPaintFlags(Paint.HINTING_OFF);
    }

}