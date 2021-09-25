package utils;


import android.graphics.Paint;
import android.widget.TextView;

public class TextHelper {

    public static void addLineThrough(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void removeLineThrough(TextView textView) {
        //set all text paint flags, except of Paint.STRIKE_THRU_TEXT_FLAG
        textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

}