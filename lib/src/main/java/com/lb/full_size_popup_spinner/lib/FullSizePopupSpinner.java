package com.lb.full_size_popup_spinner.lib;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

public class FullSizePopupSpinner extends android.support.v7.widget.AppCompatTextView {
    private static final long ANIMATION_DURATION = 150;
    private int[] mItemsTextsResIds, mItemsIconsResIds;
    private int mSelectedItemPosition = -1;
    private SpinnerPopupWindow mPopupWindow;
    private boolean mInitialized = false;
    private OnItemSelectedListener mOnItemSelectedListener;
    private Drawable mClosedDrawable;
    private Drawable mOpenedDrawable;

    public interface OnItemSelectedListener {
        void onItemSelected(FullSizePopupSpinner parent, int position, String item, int previousSelectedPosition);

        void onNothingSelected(FullSizePopupSpinner parent);
    }

    public FullSizePopupSpinner(final Context context) {
        super(context);
        init(context);
    }

    public FullSizePopupSpinner(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FullSizePopupSpinner(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mSelectedItemPosition = this.mSelectedItemPosition;
        ss.mItemsTextsResIds = mItemsTextsResIds;
        ss.mItemsIconsResIds = mItemsIconsResIds;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setItems(ss.mItemsTextsResIds, ss.mItemsIconsResIds);
        setSelectedItemPosition(ss.mSelectedItemPosition);
    }

    public void setItems(final int[] itemsTextsResIds, final int[] itemsIconsResIds) {
        mItemsTextsResIds = itemsTextsResIds;
        mItemsIconsResIds = itemsIconsResIds;
        if (mItemsTextsResIds != null && mSelectedItemPosition >= 0 && mSelectedItemPosition < mItemsTextsResIds.length)
            setText(mItemsTextsResIds[mSelectedItemPosition]);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(this, null, null, isPopupShown() ? mOpenedDrawable : mClosedDrawable, null);
    }

    public boolean isPopupShown() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    public void setSelectedItemPosition(final int selectedItemPosition) {
        int lastSelectedItemPosition = mSelectedItemPosition;
        mSelectedItemPosition = selectedItemPosition;
        final String itemText = mItemsTextsResIds != null && mSelectedItemPosition >= 0 && mSelectedItemPosition < mItemsTextsResIds.length ?
                getResources().getString(mItemsTextsResIds[mSelectedItemPosition]) : null;
        setText(itemText);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(FullSizePopupSpinner.this, null, null, mClosedDrawable, null);
        if (mOnItemSelectedListener != null)
            mOnItemSelectedListener.onItemSelected(FullSizePopupSpinner.this, selectedItemPosition, itemText, lastSelectedItemPosition);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPopupWindow != null)
            mPopupWindow.dismissRightAway();
    }

    protected void init(final Context context) {
        if (mInitialized)
            return;
        mInitialized = true;
        setSaveEnabled(true);
        mClosedDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.drop_down_menu_ic_arrow_down, null);
        mOpenedDrawable = ViewUtil.getRotateDrawable(mClosedDrawable, 180);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(FullSizePopupSpinner.this, null, null, mClosedDrawable, null);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mItemsTextsResIds == null)
                    return;
                if (mPopupWindow != null)
                    mPopupWindow.dismissRightAway();
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(FullSizePopupSpinner.this, null, null, mOpenedDrawable, null);
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                final View popupView = layoutInflater.inflate(R.layout.spinner_drop_down_popup, null, false);
                final LinearLayout linearLayout = (LinearLayout) popupView.findViewById(R.id.spinner_drop_down_popup__itemsContainer);
                final View overlayView = popupView.findViewById(R.id.spinner_drop_down_popup__overlay);
                linearLayout.setPivotY(0);
                linearLayout.setScaleY(0);
                linearLayout.animate().scaleY(1).setDuration(ANIMATION_DURATION).start();
                mPopupWindow = new SpinnerPopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true, overlayView, linearLayout);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setTouchable(true);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
                //PopupWindowCompat.setOverlapAnchor(mPopupWindow, false);
                //if (VERSION.SDK_INT >= VERSION_CODES.M)
                //    mPopupWindow.setOverlapAnchor(false);
                final AtomicBoolean isItemSelected = new AtomicBoolean(false);
                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                    popupView.findViewById(R.id.spinner_drop_down_popup__preLollipopShadow).setVisibility(View.GONE);
                    linearLayout.setBackgroundColor(0xFFffffff);
                }
                for (int i = 0; i < mItemsTextsResIds.length; ++i) {
                    final String itemText = getResources().getString(mItemsTextsResIds[i]);
                    final int position = i;
                    View itemView = layoutInflater.inflate(R.layout.spinner_drop_down_popup_item, linearLayout, false);
                    final TextView textView = (TextView) itemView.findViewById(android.R.id.text1);
                    textView.setText(itemText);
                    if (mItemsIconsResIds != null)
                        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, mItemsIconsResIds[position], 0,
                                position == mSelectedItemPosition ? R.drawable.drop_down_menu_ic_v : 0, 0);
                    else
                        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, 0, 0, position == mSelectedItemPosition ? R.drawable.drop_down_menu_ic_v : 0, 0);

                    linearLayout.addView(itemView, linearLayout.getChildCount() - 2);
                    itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            isItemSelected.set(true);
                            mPopupWindow.dismiss();
                            setSelectedItemPosition(position);
                        }
                    });
                }
                overlayView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mPopupWindow.dismiss();
                    }
                });
                overlayView.setAlpha(0);
                overlayView.animate().alpha(1).setDuration(ANIMATION_DURATION).start();
                mPopupWindow.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(FullSizePopupSpinner.this, null, null, mClosedDrawable, null);
                        if (!isItemSelected.get() && mOnItemSelectedListener != null)
                            mOnItemSelectedListener.onNothingSelected(FullSizePopupSpinner.this);
                    }
                });
                // optional: set animation style. look here for more info: http://stackoverflow.com/q/9648797/878126
                mPopupWindow.setAnimationStyle(0);
                //PopupWindowCompat.showAsDropDown(mPopupWindow, v, 0, 0, Gravity.TOP);
                //mPopupWindow.showAsDropDown(v, 0, 0, Gravity.TOP);
                mPopupWindow.showAsDropDown(v, 0, 0);
            }
        });

    }

    static class SpinnerPopupWindow extends PopupWindow {
        private final View mOverlayView;
        private final View mLayout;

        public SpinnerPopupWindow(final View contentView, final int width, final int height, final boolean focusable, View overlayView, View layout) {
            super(contentView, width, height, focusable);
            mOverlayView = overlayView;
            mLayout = layout;
        }

        public void dismissRightAway() {
            super.dismiss();
        }


        @Override
        public void dismiss() {
            final ViewPropertyAnimator animator = mOverlayView.animate().alpha(0);
            mLayout.setPivotY(0);
            mLayout.animate().scaleY(0).setDuration(ANIMATION_DURATION);
            ViewUtil.runOnAnimationEnd(animator, new Runnable() {
                @Override
                public void run() {
                    dismissRightAway();
                }
            });
            animator.start();
        }
    }

    //////////////////////////////////////
    //SavedState//
    //////////////
    static class SavedState extends BaseSavedState {
        private int[] mItemsTextsResIds;
        private int mSelectedItemPosition = -1;
        public int[] mItemsIconsResIds;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(@NonNull Parcel in) {
            super(in);
            this.mItemsTextsResIds = in.createIntArray();
            mSelectedItemPosition = in.readInt();
            mItemsIconsResIds = in.createIntArray();
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeIntArray(mItemsTextsResIds);
            out.writeInt(mSelectedItemPosition);
            out.writeIntArray(mItemsIconsResIds);
        }

        //required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}

