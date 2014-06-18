package {package_name}.module;

import android.content.Context;
import {package_name}.BuildConfig;
import {package_name}.{app_class_prefix}App;
import {package_name}.api.*;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides access to the underlying API
 */
@Module(
        complete = false
)
public class ApiModule {

}
