package ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.MessageFormat;

public class BaseActivity extends AppCompatActivity {

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * custom method start activity type (type 2)
     *
     * @param targetActivity       the destination activity goes here.
     * @param bundle               if you want to send bundle from current activity to another.
     * @param finishBackActivities if this expression is true, the back activityies and current activity finished and then start new activity suitable for splash and sign in/up activity.
     */
    public void runActivity(Class<?> targetActivity, Bundle bundle, boolean finishBackActivities) {
        Log.i("RunActivity", MessageFormat.format("{0} => {1}", this.getClass().getName(), targetActivity.getName()));
        Intent intent = new Intent(this, targetActivity);

        if (bundle != null)
            intent.putExtras(bundle);

        if (finishBackActivities) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
        }

        startActivity(intent);
    }

    /**
     * overloaded from the {@link #runActivity(Class, Bundle, boolean)}
     *
     * @param targetActivity     destination activity.
     * @param finishBackActivity finish back and current activities.
     */
    public void runActivity(Class<?> targetActivity, boolean finishBackActivity) {
        runActivity(targetActivity, null, finishBackActivity);
    }

    /**
     * overloaded from the {@link #runActivity(Class, Bundle, boolean)}
     *
     * @param targetActivity destination activity.
     * @param bundle         like before methods
     */
    public void runActivity(Class<?> targetActivity, Bundle bundle) {
        runActivity(targetActivity, bundle, false);
    }

    /**
     * overloaded from the {@link #runActivity(Class, Bundle, boolean)}
     *
     * @param targetActivity destination activity.
     */

    public void runActivity(Class<?> targetActivity) {
        runActivity(targetActivity, false);
    }

    protected int getColorRes(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }
}


