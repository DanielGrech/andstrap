package {package_name}.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import butterknife.Views;
import {package_name}.R;
import {package_name}.service.ApiExecutorService;
import com.squareup.otto.Subscribe;

/**
 *
 */
public class MainActivity extends BaseActivity {

    // public static final String KEY_HAS_MADE_FORECAST_REQUEST = "has_made_request";

    /**
     * Tracks whether or not we have made our network request for new forecasts
     */
    private boolean mHasMadeRequest;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        Views.inject(this);


        // mHasMadeRequest = savedInstanceState == null ?
        //         false : savedInstanceState.getBoolean(KEY_HAS_MADE_FORECAST_REQUEST, false);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putBoolean(KEY_HAS_MADE_FORECAST_REQUEST, mHasMadeRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);

        if (!mHasMadeRequest) {
            //TODO: Sydney is hardcoded at the moment .. need to support picking multiple cities
            // registerForApi(ApiExecutorService.AsyncRequest.getForecasts(this,
            //         "Sydney", 5));
            // mHasMadeRequest = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }
}
