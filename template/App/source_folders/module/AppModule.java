package {package_name}.module;

import android.content.Context;
import {package_name}.{app_class_prefix}App;
import dagger.Module;
import dagger.Provides;

/**
 * Provides injection of our global {@link android.app.Application} object
 */
@Module(
        library = true
)
public class AppModule {

    private {app_class_prefix}App mApp;

    public AppModule({app_class_prefix}App app) {
        mApp = app;
    }

    @Provides
    @ForApplication
    public Context providesApplicationContext() {
        return mApp;
    }
}
