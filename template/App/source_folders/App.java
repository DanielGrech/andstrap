package {package_name};

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import {package_name}.module.AppModule;
import {package_name}.module.AppServicesModule;
import {package_name}.module.ApiModule;
import {package_name}.module.DaoModule;
import {package_name}.util.ReleaseLogger;
import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * 
 */
public class {app_class_prefix}App extends Application implements Application.ActivityLifecycleCallbacks {

    private ObjectGraph mObjectGraph;

    /**
     * The number of activities the app is showing
     */
    private int mActivityCounter = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            //Default logger
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseLogger(getClass().getSimpleName()));
        }

        mObjectGraph = ObjectGraph.create(getDiModules());
        mObjectGraph.injectStatics();

        registerActivityLifecycleCallbacks(this);
    }

    /**
     * The base set of DI modules to inject app components with
     */
    private Object[] getDiModules() {
        return new Object[]{
                new AppModule(this),
                new AppServicesModule(),
                new ApiModule(),
                new DaoModule(),
        };
    }

    /**
     * Inject the given object
     *
     * @param obj          The obejct to inject
     * @param extraModules Any additional modules to include in the injection
     */
    public void inject(Object obj, Object... extraModules) {
        ObjectGraph og = mObjectGraph;
        if (extraModules != null && extraModules.length > 0) {
            og = mObjectGraph.plus(extraModules);
        }
        og.inject(obj);
    }

    /**
     * @return <code>true</code> if the app has at least 1 activity visible,
     * <code>false otherwise</code>
     */
    public boolean isAppInForeground() {
        return mActivityCounter > 0;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivityCounter++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityCounter = Math.max(0, mActivityCounter - 1);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
