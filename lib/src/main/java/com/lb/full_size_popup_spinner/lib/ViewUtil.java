package com.lb.full_size_popup_spinner.lib;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.ViewPropertyAnimator;

class ViewUtil {
    static Drawable getRotateDrawable(final Drawable d, final int angle) {
        return new LayerDrawable(new Drawable[]{d}) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, d.getBounds().width() / 2, d.getBounds().height() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static ViewPropertyAnimator runOnAnimationEnd(final ViewPropertyAnimator animator, final Runnable runnable) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
            animator.withEndAction(runnable);
        else
            animator.setListener(new android.animation.Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(final android.animation.Animator animation) {
                }

                @Override
                public void onAnimationRepeat(final android.animation.Animator animation) {
                }

                @Override
                public void onAnimationEnd(final android.animation.Animator animation) {
                    animator.setListener(null);
                    runnable.run();
                }

                @Override
                public void onAnimationCancel(final android.animation.Animator animation) {
                }
            });
        return animator;
    }
}
