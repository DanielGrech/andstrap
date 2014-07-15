package {package_name}.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import {package_name}.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static {package_name}.fragment.AppDrawerFragment.DrawerItem;

public class AppMenuListItemView extends CheckableLinearLayout {

    @InjectView(R.id.text)
    TextView mText;

    @InjectView(R.id.image)
    ImageView mImage;

    private DrawerItem mItem;

    private final Paint mPaint;

    private final int mColorDisplayWidth;

    private final int mPrimaryColor;

    public static AppMenuListItemView inflate(ViewGroup parent) {
        return (AppMenuListItemView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.li_app_drawer_item, parent, false);
    }

    public AppMenuListItemView(final Context context) {
        this(context, null);
    }

    public AppMenuListItemView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppMenuListItemView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPrimaryColor = getResources().getColor(R.color.primary);
        mColorDisplayWidth = getResources()
                .getDimensionPixelSize(R.dimen.app_menu_list_item_color_display_width);
    }

    @Override
    public void setChecked(final boolean checked) {
        super.setChecked(checked);
        populate(mItem);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (isChecked()) {
            mPaint.setColor(mPrimaryColor);
            canvas.drawRect(0, 0, mColorDisplayWidth, canvas.getHeight(), mPaint);
        }
    }

    public void populate(DrawerItem item) {
        mItem = item;

        final int textRes;
        final int drawableRes;

        switch (item) {
			{app_drawer_switch_code}
			default:
				throw new IllegalStateException("Unknown drawer item :" + item);
        }

        // mText.setText(textRes);
        // mImage.setImageResource(drawableRes);
        setStyle(item);

        invalidate();
    }

    private void setStyle(DrawerItem item) {
        final Resources res = getResources();
        final int extraPadding = res.getDimensionPixelSize(R.dimen.default_padding);

        final int pL = mText.getPaddingLeft();
        final int pR = mText.getPaddingRight();

        if (item.isMajorItem()) {
            mText.setPadding(pL, extraPadding, pR, extraPadding);
            mText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    res.getDimensionPixelSize(R.dimen.app_drawer_major_text_size));
            if (isChecked()) {
                mText.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                mText.setTypeface(Typeface.DEFAULT);
            }

            // setBackgroundResource(R.drawable.selectable_background_white_);
        } else {
            mText.setPadding(pL, 0, pR, 0);
            mText.setTextColor(getResources().getColor(R.color.dark_text));
            mText.setTypeface(Typeface.DEFAULT);
            mText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    res.getDimensionPixelSize(R.dimen.app_drawer_minor_text_size));
            // setBackgroundResource(R.drawable.selectable_background_);
        }

        // Bug in older versions .. need to reapply padding
        setPadding(extraPadding, 0, extraPadding, 0);
    }
}