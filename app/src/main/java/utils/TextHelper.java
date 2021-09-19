package utils;


import android.graphics.Paint;
import android.widget.TextView;

public class TextHelper {

    public static void lineThrough(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

}