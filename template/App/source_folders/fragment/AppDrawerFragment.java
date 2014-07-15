package {package_name}.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import {package_name}.R;
import {package_name}.activity.BaseActivity;
import {package_name}.util.UiUtils;
import {package_name}.view.AppMenuListItemView;
import com.squareup.otto.Subscribe;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class AppDrawerFragment extends BaseFragment {

    public static enum DrawerItem {
        
        {app_drawer_enum_code}

        private final boolean mIsMajorItem;

        private DrawerItem(boolean isMajorItem) {
            mIsMajorItem = isMajorItem;
        }

        public boolean isMajorItem() {
            return mIsMajorItem;
        }
    }

    @InjectView(R.id.list)
    ListView mList;

    private DrawerItem mCurrentItem;

    private OnDrawerItemSelected mItemSelectedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof OnDrawerItemSelected)) {
            throw new IllegalStateException("Activity needs to implement OnDrawerItemSelected");
        }

        mItemSelectedListener = (OnDrawerItemSelected) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v
                = inflater.inflate(R.layout.frag_app_drawer, container, false);
        ButterKnife.inject(this, v);

        mList.setAdapter(new MenuAdapter());

        final int navBarAffordance = getBaseActivity().getNavigationBarAffordance();
        if (navBarAffordance > 0) {
            UiUtils.increaseBottomPadding(v, navBarAffordance);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mEventBus.register(this);
        updateDrawerItems();
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    @SuppressWarnings("unused")
    @OnItemClick(R.id.list)
    public void onDrawerItemClick(int position) {
        final DrawerItem item = (DrawerItem) mList.getAdapter().getItem(position);
        mAnalytics.click("app_drawer_item_" + item.name().toLowerCase());
        selectItem(item);
    }

    private void updateDrawerItems() {
        final MenuAdapter adapter = (MenuAdapter) mList.getAdapter();
        adapter.notifyDataSetChanged();
    }

    public void selectItem(DrawerItem item) {
        if (item.isMajorItem()) {
            mList.setItemChecked(item.ordinal(), true);
            if (mCurrentItem != item) {
                mCurrentItem = item;
                mEventBus.post(item);
                mItemSelectedListener.onItemSelected(item);
            }
        } else {
            mItemSelectedListener.onItemSelected(item);
            if (mCurrentItem != null) {
                // We still want the same item to be selected
                mList.setItemChecked(mCurrentItem.ordinal(), true);
            }
        }
    }

    private class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return DrawerItem.values().length;
        }

        @Override
        public DrawerItem getItem(final int position) {
            return DrawerItem.values()[position];
        }

        @Override
        public long getItemId(final int position) {
            return getItem(position).ordinal();
        }

        @Override
        public View getView(final int pos, View convertView, final ViewGroup parent) {
            final AppMenuListItemView view;
            if (convertView == null) {
                view = AppMenuListItemView.inflate(parent);
            } else {
                view = (AppMenuListItemView) convertView;
            }

            view.populate(getItem(pos));

            return view;
        }
    }

    public static interface OnDrawerItemSelected {
        public void onItemSelected(DrawerItem item);
    }
}
