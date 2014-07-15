package {package_name}.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Property;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import {package_name}.R;

public class UiUtils {

    private enum PaddingType {
        TOP, LEFT, BOTTOM, RIGHT;
    }

    private UiUtils(){
        // No instances
    }

    public static void setMenuItemVisible(MenuItem item, boolean visible) {
        item.setVisible(visible);
        item.setEnabled(visible);
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.is_tablet);
    }

    public static boolean isLargeTablet(Context context) {
        return context.getResources().getBoolean(R.bool.is_large_tablet);
    }

    public static boolean isPortrait(Context context) {
        return context.getResources().getBoolean(R.bool.is_portrait);
    }

    public static float dpToPx(Context context, int dps) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps,
                context.getResources().getDisplayMetrics());
    }

    public static void setTextOrHide(TextView tv, CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setText(text);
            tv.setVisibility(View.VISIBLE);
        }
    }

    public static void onPreDraw(final View v, final Runnable r) {
        ViewTreeObserver vto = v.getViewTreeObserver();
        vto.addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        ViewTreeObserver vto = v.getViewTreeObserver();
                        vto.removeOnPreDrawListener(this);
                        r.run();
                        return false;
                    }
                }
        );
    }

    public static void increaseLeftPadding(View view, int increase) {
        increasePadding(view, increase, PaddingType.LEFT);
    }

    public static void increaseTopPadding(View view, int increase) {
        increasePadding(view, increase, PaddingType.TOP);
    }

    public static void increaseRightPadding(View view, int increase) {
        increasePadding(view, increase, PaddingType.RIGHT);
    }

    public static void increaseBottomPadding(View view, int increase) {
        increasePadding(view, increase, PaddingType.BOTTOM);
    }

    private static void increasePadding(View view, int increase, PaddingType type) {
        int pL = view.getPaddingLeft();
        int pT = view.getPaddingTop();
        int pB = view.getPaddingBottom();
        int pR = view.getPaddingRight();

        switch (type)  {
            case TOP:
                pT += increase;
                break;
            case LEFT:
                pL += increase;
                break;
            case BOTTOM:
                pB += increase;
                break;
            case RIGHT:
                pR += increase;
                break;
        }

        view.setPadding(pL, pT, pR, pB);
    }

    public static void fadeIn(final View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setAlpha(1f);
        } else {
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);

            view.animate().cancel();
            view.animate().alpha(1f).setListener(null);
        }
    }

    public static void fadeOut(final View view) {
        view.animate().cancel();
        view.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
                view.setAlpha(1f);
            }
        });
    }

    @TargetApi(Api.KITKAT)
    public static void animateTransition(ViewGroup view) {
        if (Api.isMin(Api.KITKAT)) {
            TransitionManager.beginDelayedTransition(view);
        }
    }

    public static void animateTextColor(TextView tv, int color) {
        animateColor(tv, TEXT_VIEW_COLOR_PROPERTY, color);
    }

    public static void animateBackgroundColor(View view, int color) {
        animateColor(view, BACKGROUND_COLOR_PROPERTY, color);
    }

    public static <T extends View> void animateColor(T view, Property<T, Integer> prop, int color) {
        final ObjectAnimator anim = ObjectAnimator.ofInt(view, prop, color);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setInterpolator(AnimUtils.DECEL_INTERPOLATOR);
        anim.setDuration(800);
        anim.start();
    }

    private static final Property<View, Integer> BACKGROUND_COLOR_PROPERTY
            = new Property<View, Integer>(int.class, "backgroundColor") {

        @Override
        public Integer get(View object) {
            final Drawable d = object.getBackground();
            if (d != null && d instanceof ColorDrawable) {
                return ((ColorDrawable) d).getColor();
            }

            return Color.TRANSPARENT;
        }

        @Override
        public void set(View object, Integer value) {
            object.setBackgroundColor(value.intValue());
        }
    };

    private static final Property<TextView, Integer> TEXT_VIEW_COLOR_PROPERTY
            = new Property<TextView, Integer>(int.class, "textColor") {
        @Override
        public Integer get(TextView object) {
            return object.getCurrentTextColor();
        }

        @Override
        public void set(TextView object, Integer value) {
            object.setTextColor(value);
        }
    };
}
