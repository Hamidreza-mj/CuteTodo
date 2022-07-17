package ui.component

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import android.view.animation.Animation
import android.view.animation.TranslateAnimation

class CustomItemAnimator : DefaultItemAnimator() {
    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {

        val toXDelta = holder.itemView.context.resources.displayMetrics.widthPixels.toFloat()
        val animation: Animation = TranslateAnimation(0f, toXDelta, 0f, 0f).apply {
            duration = 1000
            fillAfter = true
        }

        holder.itemView.startAnimation(animation)
        return super.animateRemove(holder)
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        return super.animateAdd(holder)
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY)
    }

    override fun getAddDuration(): Long {
        return 500
    }

    override fun getRemoveDuration(): Long {
        return 800
    }
}