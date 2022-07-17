package ui.component;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class CustomItemAnimator extends DefaultItemAnimator {
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        float toXDelta = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels;
        Animation animation = new TranslateAnimation(0, toXDelta, 0, 0);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        holder.itemView.startAnimation(animation);
        return super.animateRemove(holder);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        return super.animateAdd(holder);
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
    }

    @Override
    public long getAddDuration() {
        return 500;
    }

    @Override
    public long getRemoveDuration() {
        return 800;
    }
}
