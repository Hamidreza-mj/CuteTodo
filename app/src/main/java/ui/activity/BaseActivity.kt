package ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import utils.ResourceProvider
import java.text.MessageFormat
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    protected lateinit var provideResource: ResourceProvider

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setBackgroundDrawable(null)
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

    override fun attachBaseContext(newBase: Context?) {
        val newConfiguration = Configuration(newBase?.resources?.configuration)

        //to ignore scaling font when changed from settings device
        newConfiguration.apply {
            if (fontScale > 1.35f)
                fontScale = 1.35f

            applyOverrideConfiguration(this)
        }

        super.attachBaseContext(newBase)
    }

}