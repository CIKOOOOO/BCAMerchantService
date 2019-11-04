package com.andrew.bcamerchantservice.utils;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;

public class BottomNavigationViewBehavior extends CoordinatorLayout.Behavior<View> {

    private int height;

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        height = child.getHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        child.setTranslationY(Math.max(0f,
                Math.min(Float.parseFloat(String.valueOf(child.getHeight())),child.getTranslationY()+dy)));
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       View child, @NonNull
                                               View directTargetChild, @NonNull View target,
                                       int axes, int type)
    {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
                               @NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed,
                               @ViewCompat.NestedScrollType int type)
    {
        if (dyConsumed > 0) {
            slideDown(child);
        } else if (dyConsumed < 0) {
            slideUp(child);
        }
    }

    private void slideUp(View child) {
        child.clearAnimation();
        child.animate().translationY(0).setDuration(200);
    }

    private void slideDown(View child) {
        child.clearAnimation();
        child.animate().translationY(height).setDuration(200);
    }
}
