/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.common.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SlidingTabLayout2 extends RecyclerView {

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;
    private ArrayList<String> strings = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private Paint paint;
    private int mSelectedPosition;
    private float mSelectionOffset;

    public SlidingTabLayout2(Context context) {
        this(context, null, 0);
    }

    public SlidingTabLayout2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout2(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        paint = new Paint();
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        this.setLayoutManager(layoutManager);
        this.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL_LIST));
    }


    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        final int height = getHeight();
        final int childCount = this.getAdapter().getItemCount();

        // Thick colored underline below the current selection
        if (childCount > 0) {
            ViewHolder selectedTitle = this.findViewHolderForAdapterPosition(mSelectedPosition);
            if (selectedTitle != null) {

                System.out.println("childCount:" + childCount + ", mSelectedPosition:" + mSelectedPosition);
                int left = selectedTitle.itemView.getLeft();
                int right = selectedTitle.itemView.getRight();

                if (mSelectionOffset > 0f && mSelectedPosition < (childCount - 1)) {

                    // Draw the selection partway between the tabs
                    ViewHolder nextTitle = this.findViewHolderForAdapterPosition(mSelectedPosition + 1);
                    if (nextTitle != null) {
                        left = (int) (mSelectionOffset * nextTitle.itemView.getLeft() +
                                (1.0f - mSelectionOffset) * left);
                        right = (int) (mSelectionOffset * nextTitle.itemView.getRight() +
                                (1.0f - mSelectionOffset) * right);
                    }
                }

                canvas.drawRect(left, height - 30, right,
                        height, paint);
            }
        }

        // Thin underline along the entire bottom edge
        canvas.drawRect(0, height - 20, getWidth(), height, paint);
    }

    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        strings.clear();
        for (int i = 0; i < adapter.getCount(); i++) {
            strings.add(adapter.getPageTitle(i).toString());
        }
        this.setAdapter(new SimpleTabAdapter(strings));
    }


    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = this.getAdapter().getItemCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        ViewHolder selectedChild = this.findViewHolderForAdapterPosition(tabIndex);
        if (selectedChild != null) {
//            int targetScrollX = selectedChild.itemView.getLeft() + positionOffset;
//
//            int scrollLength = targetScrollX - this.getWidth() / 2 + selectedChild.itemView.getWidth() / 2;
//            smoothScrollBy(scrollLength, 0);
//            if (scrollLength >= 0) {
////                layoutManager.scrollToPositionWithOffset(tabIndex, selectedChild.getLeft() + scrollLength);
//            } else if (positionOffset == 0) {
////                layoutManager.scrollToPositionWithOffset(tabIndex, 55);
//            }
            int left = selectedChild.itemView.getLeft();
            int right = selectedChild.itemView.getRight();

            if (mSelectionOffset > 0f && mSelectedPosition < (this.getAdapter().getItemCount() - 1)) {

                ViewHolder nextTitle = this.findViewHolderForAdapterPosition(mSelectedPosition + 1);
                if (nextTitle != null) {
                    left = (int) (mSelectionOffset * nextTitle.itemView.getLeft() +
                            (1.0f - mSelectionOffset) * left);
                    right = (int) (mSelectionOffset * nextTitle.itemView.getRight() +
                            (1.0f - mSelectionOffset) * right);
                }
            }
            smoothScrollBy((right + left - getWidth()) / 2, 0);
        }
//        ViewHolder selectedChild = this.findViewHolderForAdapterPosition(tabIndex);
//        ViewHolder nextView = this.findViewHolderForAdapterPosition(tabIndex + 1);
//        if(selectedChild!=null)
//        {
//            int width = getMeasuredWidth();
//            float scroll1 = width / 2.f - selectedChild.itemView.getMeasuredWidth() / 2.f;
//            int scrollOffset = 0;
//            if (nextView != null) {
//                float scroll2 = width / 2.f - nextView.itemView.getMeasuredWidth() / 2.f;
//
//                float scroll = scroll1 + (selectedChild.itemView.getMeasuredWidth() - scroll2);
//                float dx = scroll * positionOffset;
//                scrollOffset = (int) (scroll1 - dx);
//
//            } else {
//                scrollOffset = (int) scroll1;
//            }
//
//            layoutManager.scrollToPositionWithOffset(tabIndex, scrollOffset);
//        }
    }

    private void changeSeleteItem(int pos) {
        mViewPager.setCurrentItem(pos);
    }

    void onViewPagerPageChanged(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        invalidate();
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = SlidingTabLayout2.this.getAdapter().getItemCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }
            onViewPagerPageChanged(position, positionOffset);
            View selectedTitle = getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                scrollToTab(position, 0);
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }

    private class SimpleTabAdapter extends RecyclerView.Adapter {
        private ArrayList<String> mvalue;

        public SimpleTabAdapter(ArrayList<String> mvalue) {
            this.mvalue = mvalue;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TabTextHolder tabTextHolder = new TabTextHolder(new TextView(parent.getContext()));
            return tabTextHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TabTextHolder) holder).textView.setText(mvalue.get(position));
        }

        @Override
        public int getItemCount() {
            return mvalue.size();
        }
    }

    private class TabTextHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TabTextHolder(TextView itemView) {
            super(itemView);
            textView = itemView;
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    80));
           textView.setGravity(Gravity.CENTER);
            textView.setPadding(10,10,10,10);
            this.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = TabTextHolder.this.getAdapterPosition();
                    if (pos != NO_POSITION) {
                        changeSeleteItem(pos);
                        Toast.makeText(SlidingTabLayout2.this.getContext(), "position:" + pos, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
