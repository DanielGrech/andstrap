package {package_name}.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import {package_name}.IAnalytics;
import {package_name}.R;
import {package_name}.{app_class_prefix}App;
import {package_name}.jobs.BaseJob;
import {package_name}.receiver.ApiBroadcastReceiver;
import {package_name}.util.UiUtils;
import {package_name}.util.Api;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Base class for all activities in the app
 */
public abstract class BaseActivity extends Activity {

    @Inject
    protected Bus mEventBus;

    @Inject
    protected IAnalytics mAnalytics;

    protected SystemBarTintManager mTintManager;

    protected {app_class_prefix}App mApp;

    /**
     * Catches API-related broadcasts
     */
    private ApiBroadcastReceiver mApiReceiver = new ApiBroadcastReceiver() {

        @Override
        protected <T extends BaseJob> void onStart(Class<T> jobCls, String token) {
//            setProgressBarIndeterminateVisibility(getRunningCounter() > 0);
            onJobRequestStart(jobCls, token);
        }

        @Override
        protected <T extends BaseJob> void onFinish(Class<T> jobCls, String token, Bundle results) {
//            setProgressBarIndeterminateVisibility(getRunningCounter() > 0);
            onJobRequestFinish(jobCls, token, results);
        }

        @Override
        protected <T extends BaseJob> void onError(Class<T> jobCls, String token, String errorMsg) {
            onJobRequestError(jobCls, token, errorMsg);
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        mTintManager = new SystemBarTintManager(this);

        mApp = ({app_class_prefix}App) getApplication();
        mApp.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApiReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mApiReceiver.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected <T extends BaseJob> void onJobRequestStart(Class<T> cls, String action) {
        //No-op
    }

    protected <T extends BaseJob> void onJobRequestFinish(Class<T> cls, String action,
                                                          Bundle results) {
        //No-op
    }

    protected <T extends BaseJob> void onJobRequestError(
            Class<T> cls, String action, String errorMsg) {
        //No-op
    }

    /**
     * Listen out for API broadcasts of type <code>token</code>
     *
     * @param token The token returned from a method in {@link {package_name}.jobs.BaseJob}
     */
    protected void registerForJob(String token) {
        mApiReceiver.addAcceptableToken(token);
    }

    protected <T extends BaseJob> void registerForJobClass(Class<T> cls) {
        mApiReceiver.addAcceptableJobType(cls);
    }

    /**
     * @param id  The id of the fragment to retrieve
     * @param <T> A {@link Fragment} subclass
     * @return The fragment with id <code>id</code>, or null if it doesn't exist
     */
    protected <T extends Fragment> T findFragment(int id) {
        return (T) getFragmentManager().findFragmentById(id);
    }

    public int getNavigationBarAffordance() {
        if (!Api.isMin(Api.KITKAT) || UiUtils.isTablet(this) ||
                !getResources().getBoolean(R.bool.is_portrait) ||
                !mTintManager.getConfig().hasNavigtionBar()) {
            return 0;
        } else {
            return mTintManager.getConfig().getNavigationBarHeight();
        }
    }

    protected void setupTintManagerForViews(boolean clipTop, boolean clipBottom, View... views) {
        setupTintManagerForViews(this, mTintManager, clipTop, clipBottom, views);
    }

    static void setupTintManagerForViews(Context context,
                                         SystemBarTintManager tintManager,
                                         boolean clipTop, boolean clipBottom,
                                         View... views) {
        if (Api.isMin(Api.KITKAT) && views != null) {
            tintManager.setTintColor(context.getResources().getColor(R.color.ab_color));

            if (clipTop) {
                tintManager.setStatusBarTintEnabled(true);
            }

            for (View v : views) {
                ViewGroup.LayoutParams lps = v.getLayoutParams();
                if (lps instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams layoutParams
                            = (ViewGroup.MarginLayoutParams) lps;

                    if (clipTop) {
                        layoutParams.topMargin = tintManager.getConfig().getPixelInsetTop(true);
                    }

                    if (clipBottom) {
                        layoutParams.bottomMargin = tintManager.getConfig().getPixelInsetBottom();
                    }

                    v.setLayoutParams(layoutParams);
                } else {
                    Timber.w("Could not apply tint to view: " + v);
                }
            }
        }
    }
}