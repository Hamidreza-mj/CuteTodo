package utils;


import android.graphics.Paint;
import android.widget.TextView;

public class TextHelper {

    public static void configLineThrough(TextView textView, boolean addLine) {
        if (addLine)
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            textView.setPaintFlags(textView.getPaintFlags());
    }

}