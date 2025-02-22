package com.meishe.third.pop.widget;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import androidx.annotation.NonNull;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.animator.ShadowBgAnimator;
import com.meishe.third.pop.enums.LayoutStatus;
import com.meishe.third.pop.enums.PopupPosition;
import com.meishe.third.pop.util.XPopupUtils;

/**
 * Description: 根据手势拖拽子View的layout，这种类型的弹窗比较特殊，不需要额外的动画器，因为
 * 动画是根据手势滑动而发生的
 * Drag and drop the layout of the child View with your hand gesture. This type of pop-up window is special and doesn't require an extra animator because
 * The animation occurs by swiping the gesture
 * Create by dance, at 2018/12/20
 */
public class PopupDrawerLayout extends FrameLayout {

    /**
     * The Status.
     * 现状
     */
    LayoutStatus status = null;
    /**
     * The Drag helper.
     * 拖动
     */
    ViewDragHelper dragHelper;
    /**
     * The Place holder.
     * 占位符
     */
    View placeHolder,
    mChild;
    /**
     * The Position.
     * 位置
     */
    public PopupPosition position = PopupPosition.Left;
    /**
     * The Bg animator.
     * Bg动画师
     */
    ShadowBgAnimator bgAnimator = new ShadowBgAnimator();
    /**
     * The Argb evaluator.
     * Argb评估者
     */
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    /**
     * The Default color.
     * 默认颜色
     */
    int defaultColor = Color.TRANSPARENT;
    /**
     * The Is draw status bar shadow.
     * 绘制状态栏阴影
     */
    public boolean isDrawStatusBarShadow = false;
    /**
     * The Fraction.
     * 分数
     */
    float fraction = 0f;
    /**
     * The Enable shadow.
     * 启用阴影
     */
    public boolean enableShadow = true;

    public PopupDrawerLayout(Context context) {
        this(context, null);
    }

    public PopupDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * Sets drawer position.
     * 抽屉的位置
     * @param position the position 位置
     */
    public void setDrawerPosition(PopupPosition position) {
        this.position = position;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        placeHolder = getChildAt(0);
        mChild = getChildAt(1);
    }

    /**
     * The Ty.
     * ty
     */
    float ty;
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ty = getTranslationY();
    }

    /**
     * The Has layout.
     * 有布局
     */
    boolean hasLayout = false;
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        placeHolder.layout(0,0, placeHolder.getMeasuredWidth(), placeHolder.getMeasuredHeight());
        if (!hasLayout) {
            if (position == PopupPosition.Left) {
                mChild.layout(-mChild.getMeasuredWidth(), 0, 0, getMeasuredHeight());
            } else {
                mChild.layout(getMeasuredWidth(), 0, getMeasuredWidth() + mChild.getMeasuredWidth(), getMeasuredHeight());
            }
            hasLayout = true;
        } else {
            mChild.layout(mChild.getLeft(), mChild.getTop(), mChild.getRight(), mChild.getBottom());
        }
    }

    /**
     * The Is intercept.
     * 拦截
     */
    boolean isIntercept = false;
    /**
     * The X.
     * x
     */
    float x, /**
     * The Y.
     * y
     */
    y;
    /**
     * The Is to left.
     * 从右到左
     */
    boolean isToLeft, /**
     * The Can child scroll left.
     * child可以向左滚动
     */
    canChildScrollLeft;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        isToLeft = ev.getX() < x;
        x = ev.getX();
        y = ev.getY();
//        boolean canChildScrollRight = canScroll(this, ev.getX(), ev.getY(), -1);
        canChildScrollLeft = canScroll(this, ev.getX(), ev.getY(), 1);
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            x = 0;
            y = 0;
        }
        isIntercept = dragHelper.shouldInterceptTouchEvent(ev);
        if (isToLeft && !canChildScrollLeft) {
            return isIntercept;
        }

        boolean canChildScrollHorizontal = canScroll(this, ev.getX(), ev.getY());
        if(!canChildScrollHorizontal)return isIntercept;

        return super.onInterceptTouchEvent(ev);
    }

    private boolean canScroll(ViewGroup group, float x, float y, int direction) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            int[] location = new int[2];
            child.getLocationInWindow(location);
            Rect rect = new Rect(location[0], location[1], location[0] + child.getWidth(),
                    location[1] + child.getHeight());
            boolean inRect = XPopupUtils.isInRect(x, y, rect);
            if (inRect && child instanceof ViewGroup) {
                if (child instanceof ViewPager) {
                    ViewPager pager = (ViewPager) child;
                    if(direction==0){
                        return pager.canScrollHorizontally(-1) || pager.canScrollHorizontally(1);
                    }
                    return pager.canScrollHorizontally(direction);
                } else if (child instanceof HorizontalScrollView) {
                    HorizontalScrollView hsv = (HorizontalScrollView) child;
                    if(direction==0){
                        return hsv.canScrollHorizontally(-1) || hsv.canScrollHorizontally(1);
                    }
                    return hsv.canScrollHorizontally(direction);
                } else {
                    return canScroll((ViewGroup) child, x, y, direction);
                }
            }
        }
        return false;
    }
    private boolean canScroll(ViewGroup group, float x, float y) {
        return canScroll(group, x, y, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dragHelper.continueSettling(true)) return true;
        dragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * The Callback.
     * 回调
     */
    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return !dragHelper.continueSettling(true);
        }
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return 1;
        }
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if(child==placeHolder)return left;
            return fixLeft(left);
        }
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if(changedView==placeHolder){
                placeHolder.layout(0,0, placeHolder.getMeasuredWidth(), placeHolder.getMeasuredHeight());
                int newLeft = fixLeft(mChild.getLeft() + dx);
                mChild.layout(newLeft, mChild.getTop(), newLeft + mChild.getMeasuredWidth(), mChild.getBottom());
                calcFraction(newLeft);
            }else {
                calcFraction(left);
            }
        }

        private void calcFraction(int left){
            // fraction = (now - start) * 1f / (end - start)
            if (position == PopupPosition.Left) {
                fraction = (left + mChild.getMeasuredWidth()) * 1f / mChild.getMeasuredWidth();
                if (left == -mChild.getMeasuredWidth() && listener != null && status != LayoutStatus.Close) {
                    status = LayoutStatus.Close;
                    listener.onClose();
                }
            } else if (position == PopupPosition.Right) {
                fraction = (getMeasuredWidth() - left) * 1f / mChild.getMeasuredWidth();
                if (left == getMeasuredWidth() && listener != null && status != LayoutStatus.Close) {
                    status = LayoutStatus.Close;
                    listener.onClose();
                }
            }
            if(enableShadow) setBackgroundColor(bgAnimator.calculateBgColor(fraction));
            if (listener != null) {
                listener.onDismissing(fraction);
                if (fraction == 1f && status != LayoutStatus.Open) {
                    status = LayoutStatus.Open;
                    listener.onOpen();
                }
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(releasedChild==placeHolder && xvel==0){
                close();
                return;
            }
            if(releasedChild==mChild && isToLeft && !canChildScrollLeft && xvel<-500){
                close();
                return;
            }

            int centerLeft = 0;
            int finalLeft = 0;
            if (position == PopupPosition.Left) {
                if (xvel < -1000) {
                    finalLeft = -mChild.getMeasuredWidth();
                } else {
                    centerLeft = -mChild.getMeasuredWidth() / 2;
                    finalLeft = mChild.getLeft() < centerLeft ? -mChild.getMeasuredWidth() : 0;
                }
            } else {
                if (xvel > 1000) {
                    finalLeft = getMeasuredWidth();
                } else {
                    centerLeft = getMeasuredWidth() - mChild.getMeasuredWidth() / 2;
                    finalLeft = releasedChild.getLeft() < centerLeft ? getMeasuredWidth() - mChild.getMeasuredWidth() : getMeasuredWidth();
                }
            }
            dragHelper.smoothSlideViewTo(mChild, finalLeft, releasedChild.getTop());
            ViewCompat.postInvalidateOnAnimation(PopupDrawerLayout.this);
        }
    };

    private int fixLeft(int left) {
        if (position == PopupPosition.Left) {
            if (left < -mChild.getMeasuredWidth()) left = -mChild.getMeasuredWidth();
            if (left > 0) left = 0;
        } else if (position == PopupPosition.Right) {
            if (left < (getMeasuredWidth() - mChild.getMeasuredWidth()))
                left = (getMeasuredWidth() - mChild.getMeasuredWidth());
            if (left > getMeasuredWidth()) left = getMeasuredWidth();
        }
        return left;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(false)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    Paint paint;
    /**
     * The Shadow rect.
     * 阴影矩形
     */
    Rect shadowRect;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isDrawStatusBarShadow) {
            if (paint == null) {
                paint = new Paint();
                shadowRect = new Rect(0, 0, getMeasuredHeight(), XPopupUtils.getStatusBarHeight());
            }
            paint.setColor((Integer) argbEvaluator.evaluate(fraction, defaultColor, XPopup.statusBarShadowColor));
            canvas.drawRect(shadowRect, paint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        status = null;
        fraction = 0f;
        setTranslationY(ty);
    }

    /**
     * 打开Drawer
     * Open the Drawer
     */
    public void open() {
        post(new Runnable() {
            @Override
            public void run() {
                dragHelper.smoothSlideViewTo(mChild, position == PopupPosition.Left ? 0 : (mChild.getLeft() - mChild.getMeasuredWidth()), 0);
                ViewCompat.postInvalidateOnAnimation(PopupDrawerLayout.this);
            }
        });
    }

    /**
     * The Is can close.
     * 可以关闭
     */
    public boolean isCanClose = true;

    /**
     * 关闭Drawer
     * Close the Drawer
     */
    public void close() {
        if (dragHelper.continueSettling(true)) return;
        if(!isCanClose)return;
        post(new Runnable() {
            @Override
            public void run() {
                dragHelper.smoothSlideViewTo(mChild, position == PopupPosition.Left ? -mChild.getMeasuredWidth() : getMeasuredWidth(), 0);
                ViewCompat.postInvalidateOnAnimation(PopupDrawerLayout.this);
            }
        });
    }

    private OnCloseListener listener;

    /**
     * Sets on close listener.
     * 设置为密切监听
     * @param listener the listener
     */
    public void setOnCloseListener(OnCloseListener listener) {
        this.listener = listener;
    }

    /**
     * The interface On close listener.
     * 关闭监听器的接口
     */
    public interface OnCloseListener {
        /**
         * On close.
         * 关闭
         */
        void onClose();

        /**
         * On open.
         * 打开
         */
        void onOpen();

        /**
         * 关闭过程中执行
         * Execute during the close process
         * @param fraction 关闭的百分比
         */
        void onDismissing(float fraction);
    }
}
