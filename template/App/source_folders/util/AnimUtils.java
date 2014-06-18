package {package_name}.util;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class AnimUtils {

    public static final Interpolator DECEL_INTERPOLATOR =
            new DecelerateInterpolator(2f);
}
