package {package_name}.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import {package_name}.{app_class_prefix}App;
import {package_name}.api.IApiManager;
import {package_name}.api.IPersistenceManager;
import {package_name}.data.{app_class_prefix}ContentProvider;
import {package_name}.modules.ApiModule;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.List;

/**
 * Dispatches API calls on a background thread
 */
public class ApiExecutorService extends BaseApiService {

    @Inject
    IApiManager mApiManager;

    @Inject
    IPersistenceManager mPersistenceManager;

    @Override
    public void onCreate() {
        super.onCreate();

        {app_class_prefix}App app = ({app_class_prefix}App) getApplication();
        app.inject(this, new ApiModule());
    }

    @Override
    protected void handleApiRequest(final String token, final Bundle extras) {
        switch (token) {
            case AsyncRequest.ACTION_:

                break;
        }
    }

    /**
     * Fires off asynchronous requests for API calls
     */
    public static class AsyncRequest {

        public static final String ACTION_ = "async_request_";

    }


}
