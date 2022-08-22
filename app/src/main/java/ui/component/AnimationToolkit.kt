package ui.component

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class AnimationToolkit @Inject constructor() {

    fun fadeAnimation(
        fromAlpha: Float = 0f,
        toAlpha: Float = 1f,
        durationInMillis: Long = 1_000L,
    ): Animation {
        val anim = AlphaAnimation(fromAlpha, toAlpha).apply {
            //interpolator = DecelerateInterpolator()
            duration = durationInMillis
        }

        return anim
    }

}