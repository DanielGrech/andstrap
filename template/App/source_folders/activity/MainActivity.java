package {package_name}.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import {package_name}.R;
import com.squareup.otto.Subscribe;
import {package_name}.fragment.AppDrawerFragment;

import static {package_name}.fragment.AppDrawerFragment.DrawerItem;

public class MainActivity extends BaseActivity 
        implements AppDrawerFragment.OnDrawerItemSelected {

    @InjectView(R.id.navigation_drawer)
    DrawerLayout mDrawerLayout;

    ActionBarDrawerToggle mDrawerToggle;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        ButterKnife.inject(this);

        setupDrawerToggle();
        setupNavigationDrawer();
        setupTintManagerForViews(true, false, mDrawerLayout);
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            switch (item.getItemId()) {
               
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(DrawerItem item) {
        if (mDrawerLayout != null) {
            // Null when first setting selected item
            mDrawerLayout.closeDrawers();
        }

        switch (item) {
            {app_drawer_switch_code}
        }
    }

    private void setupNavigationDrawer() {
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
    }

    private void setupDrawerToggle() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View view, float slideOffset) {
                super.onDrawerSlide(view, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mAnalytics.event("app_drawer_opened");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mAnalytics.event("app_drawer_closed");
            }
        };
    }
}
