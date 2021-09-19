package utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DisplayUtils {

    private static DisplayUtils displayUtils;

    private DisplayUtils() {
    }

    public static DisplayUtils getDisplay() {
        if (displayUtils == null)
            displayUtils = new DisplayUtils();

        return displayUtils;
    }

    public float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public float dpToFloat(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    public int getWidth(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        return displaymetrics.widthPixels;
    }

    public int getHeight(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        return displaymetrics.heightPixels;
    }

}
