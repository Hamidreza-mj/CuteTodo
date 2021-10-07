package utils;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import hlv.cute.todo.App;
import hlv.cute.todo.R;

public class ResourceUtils {

    private static ResourceUtils resourceUtils;

    private ResourceUtils() {
    }

    public static ResourceUtils get() {
        if (resourceUtils == null)
            resourceUtils = new ResourceUtils();

        return resourceUtils;
    }

    public String getString(@StringRes int stringRes) {
        return App.get().getString(stringRes);
    }

    public int getColor(@ColorRes int colorRes) {
        return App.get().getResources().getColor(colorRes);
    }
}
