/**
 * Copyright 2016 andy
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.refactor.ultraindicator.lib;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Create by andy (https://github.com/andyxialm)
 * Create time: 16/8/25 23:56
 * Description : indicator view
 */
public class UltraIndicatorView extends View implements ViewPager.OnPageChangeListener {

    private static final String KEY_INSTANCE_STATE = "InstanceState";
    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFAULT_HEIGHT = 50;

    private static final int DEFAULT_ANIM_DURATION = 300;
    private static final int DEFAULT_CHECKED_COLOR = Color.WHITE;
    private static final int DEFAULT_UNCHECKED_COLOR = Color.parseColor("#555555");

    private Paint mPaint;
    private int mCheckedColor;
    private int mUnCheckedColor;

    private int mAnimDuration;
    private int mCheckedPosition;
    private float mRadius, mCheckedRunningRadius;

    private float mGap;
    private ValueAnimator mValueAnimator;

    public UltraIndicatorView(Context context) {
        this(context, null);
    }

    public UltraIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UltraIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UltraIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.UltraIndicatorView);
        mCheckedColor = ta.getColor(R.styleable.UltraIndicatorView_checkedColor, DEFAULT_CHECKED_COLOR);
        mUnCheckedColor = ta.getColor(R.styleable.UltraIndicatorView_uncheckedColor, DEFAULT_UNCHECKED_COLOR);
        mAnimDuration = ta.getInt(R.styleable.UltraIndicatorView_duration, DEFAULT_ANIM_DURATION);
        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int horizontalAvailableWidth = getMeasuredWidth() - paddingLeft - paddingRight;
        int verticalAvailableHeight = getMeasuredHeight() - paddingTop - paddingBottom;
        mRadius = Math.min(horizontalAvailableWidth, verticalAvailableHeight) / 4 / 2;
        mCheckedRunningRadius = mRadius;
        mGap = horizontalAvailableWidth < verticalAvailableHeight
                ? (horizontalAvailableWidth - 3 * 2 * mRadius) / 4
                : (verticalAvailableHeight - 3 * 2 * mRadius) / 4;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidthSize(widthMeasureSpec), measureHeightSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int position = 0;
        for (int rowChildCount = 3; rowChildCount > 0; rowChildCount--) {
            for (int i = 0; i < rowChildCount; i++) {
                mPaint.setColor(position == mCheckedPosition ? mCheckedColor : mUnCheckedColor);
                float cx = getPaddingLeft() + (i + 1) * mGap + ((i * 2 + 1) * mRadius);  // 2n + 1 (n = 0,1,2)
                float cy = getPaddingTop() + ((3 - rowChildCount) + 1) * mGap + (((3 - rowChildCount) * 2 + 1) * mRadius);
                canvas.drawCircle(cx, cy, position == mCheckedPosition ? mCheckedRunningRadius : mRadius, mPaint);
                position++;
            }
        }
    }

    /**
     * measure width
     * @param measureSpec spec
     * @return width
     */
    private int measureWidthSize(int measureSpec) {
        int defSize = dp2px(DEFAULT_WIDTH);
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);

        int result = 0;
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = Math.min(defSize, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    /**
     * measure height
     * @param measureSpec spec
     * @return height
     */
    private int measureHeightSize(int measureSpec) {
        int defSize = dp2px(DEFAULT_HEIGHT);
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);

        int result = 0;
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = Math.min(defSize, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.end();
        }
    }

    /**
     * start checked animation
     */
    private void startCheckedAnim() {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            return;
        }
        mValueAnimator = ValueAnimator.ofFloat(1.0f, 1.2f, 1.0f);
        mValueAnimator.setDuration(mAnimDuration);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.setRepeatCount(0);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                mCheckedRunningRadius = animatedValue * mRadius;
                invalidate();
            }
        });
        mValueAnimator.start();
    }

    /**
     * bind viewpager
     * @param viewPager vp
     */
    @SuppressWarnings("unused")
    public void setupWithViewPager(ViewPager viewPager) {
        if (viewPager == null) {
            return;
        }
        viewPager.removeOnPageChangeListener(this);
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * set checked position
     * @param checkedPosition position
     */
    @SuppressWarnings("unused")
    public void setCheckedPosition(int checkedPosition) {
        mCheckedPosition = checkedPosition;
        invalidate();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCheckedPosition = position;
        invalidate();
        startCheckedAnim();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * dp to px
     * @param dpValue dp
     * @return px
     */
    private int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * calculate gradient color
     * @param startColor start color
     * @param endColor   end color
     * @param percent    percent
     * @return gradient color
     */
    @SuppressWarnings("unused")
    private static int getGradientColor(int startColor, int endColor, float percent) {
        int startA = Color.alpha(startColor);
        int startR = Color.red(startColor);
        int startG = Color.green(startColor);
        int startB = Color.blue(startColor);

        int endA = Color.alpha(endColor);
        int endR = Color.red(endColor);
        int endG = Color.green(endColor);
        int endB = Color.blue(endColor);

        int currentA = (int) (startA * (1 - percent) + endA * percent);
        int currentR = (int) (startR * (1 - percent) + endR * percent);
        int currentG = (int) (startG * (1 - percent) + endG * percent);
        int currentB = (int) (startB * (1 - percent) + endB * percent);
        return Color.argb(currentA, currentR, currentG, currentB);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(KEY_INSTANCE_STATE, mCheckedPosition);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCheckedPosition = bundle.getInt(KEY_INSTANCE_STATE);
            setCheckedPosition(mCheckedPosition);
            super.onRestoreInstanceState(bundle.getParcelable(KEY_INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

}
