package ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.content.pm.ActivityInfo
import kotlin.jvm.JvmOverloads
import android.content.Intent
import android.util.Log
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import java.text.MessageFormat

open class BaseActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * custom method start activity type (type 2)
     *
     * @param targetActivity       the destination activity goes here.
     * @param bundle               if you want to send bundle from current activity to another.
     * @param finishBackActivities if this expression is true, the back activityies and current activity finished and then start new activity suitable for splash and sign in/up activity.
     */
    /**
     * overloaded from the [.runActivity]
     *
     * @param targetActivity destination activity.
     * @param bundle         like before methods
     */
    @JvmOverloads
    fun runActivity(
        targetActivity: Class<*>,
        bundle: Bundle?,
        finishBackActivities: Boolean = false
    ) {
        Log.i(
            "RunActivity",
            MessageFormat.format("{0} => {1}", this.javaClass.name, targetActivity.name)
        )
        val intent = Intent(this, targetActivity)
        if (bundle != null) intent.putExtras(bundle)
        if (finishBackActivities) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
        }
        startActivity(intent)
    }
    /**
     * overloaded from the [.runActivity]
     *
     * @param targetActivity     destination activity.
     * @param finishBackActivity finish back and current activities.
     */
    /**
     * overloaded from the [.runActivity]
     *
     * @param targetActivity destination activity.
     */
    @JvmOverloads
    fun runActivity(targetActivity: Class<*>, finishBackActivity: Boolean = false) {
        runActivity(targetActivity, null, finishBackActivity)
    }

    protected fun getColorRes(@ColorRes color: Int): Int {
        return ContextCompat.getColor(this, color)
    }
}