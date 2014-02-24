package {package_name}.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import butterknife.ButterKnife;
import {package_name}.R;
import com.squareup.otto.Subscribe;

/**
 *
 */
public class MainActivity extends BaseActivity {


    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        ButterKnife.inject(this);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }
}
