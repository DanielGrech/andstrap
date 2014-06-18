package {package_name}.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import {package_name}.R;

public class UiUtils {

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
}
