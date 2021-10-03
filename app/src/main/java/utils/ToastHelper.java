package utils;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hlv.cute.todo.App;
import hlv.cute.todo.R;

public class ToastHelper {

    private static final ArrayList<Toast> TOASTS = new ArrayList<>();
    private static ToastHelper toastHelper;

    private ToastHelper() {
    }

    public static ToastHelper get() {
        if (toastHelper == null)
            toastHelper = new ToastHelper();

        return toastHelper;
    }

    /**
     * show toast in normal / long duration
     *
     * @param message        message that show in toast
     * @param isLongDuration default is false, to show toast longer that normal mode you must set it true
     */
    public void toast(final String message, final boolean isLongDuration) {
        new Handler().post(() -> {
            for (Toast toast : TOASTS) {
                toast.cancel();
            }
            TOASTS.clear();

            Toast toast = new Toast(App.get().applicationContext);
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(App.get().applicationContext).inflate(R.layout.toast, null, false);
            TextView txtToast = view.findViewById(R.id.toast_txt);
            txtToast.setText(message);
            txtToast.setTypeface(Typeface.createFromAsset(App.get().applicationContext.getAssets(), "font/vazir_regular.ttf"));
            toast.setDuration(isLongDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.setView(view);
            toast.show();

            TOASTS.add(toast);
        });
    }

    /**
     * show toast with normal duration
     *
     * @param message message that show in toast
     */
    public void toast(String message) {
        toast(message, false);
    }

}
