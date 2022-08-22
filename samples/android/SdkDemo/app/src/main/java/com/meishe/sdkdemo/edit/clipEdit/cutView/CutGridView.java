package com.meishe.sdkdemo.edit.clipEdit.cutView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/3/24.
 * @Description :调整(裁剪)网格控件
 * @Description :adjust(Crop)grid view
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CutGridView extends View {
    private Context mContext;
    private final static String TAG = "CutGridView";
    private float ratio;//真实显示宽高比
    private int ratioType;//外部ratioType
    private Paint mBgPaint;
    private PorterDuffXfermode xFermode;
    private int mPaintBgColor = Color.parseColor("#55000000");//整体背景
    private int mPaintColor = Color.WHITE;
    private int mPaintLineColor = Color.parseColor("#00FFFF");
    private Paint mPaint;
    private Path mPath = new Path();
    private RectF mDrawRect = new RectF();
    private Paint mCornerPaint;
    private final int BASE_STROKE_WIDTH = 2;
    private final int ANGEL_LENGTH = 30;//角边框绘制长度
    private ScaleGestureDetector mScaleDetector;
    private boolean canMove = false;//检测手指是否按下 用来决定是否处理事件
    private boolean twoFingerFlag;
    private int startId;
    private Point downPoint = new Point();
    private Point twoPoint = new Point();
    private boolean changeRectView = false;//是否进行CutGridView本身进行缩放平移
    private float twoFingerStartLength;
    private float twoFingerStartDegree;
    private CutCallbackListener callbackListener;
    private float lastTouchX;
    private float lastTouchY;
    private float twoFingerStartXLength;
    private float twoFingerStartYLength;
    private int mWidth;
    private int mHeight;
    private float tempWidth;
    private float tempRatio;
    private float tempHeight;

    public void setCallbackListener(CutCallbackListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
        mDrawRect.left = 0;
        mDrawRect.right = getWidth();
        mDrawRect.top = (int) ((getHeight() - getWidth() / ratio) / 2);
        mDrawRect.bottom = (int) ((getHeight() + getWidth() / ratio) / 2);
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    public CutGridView(Context context) {
        super(context);
    }

    public CutGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initView(context);
    }

    private void initPaint() {
        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.FILL);
        xFermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(BASE_STROKE_WIDTH);

        mCornerPaint = new Paint();
        mCornerPaint.setColor(mPaintColor);
        mCornerPaint.setStyle(Paint.Style.STROKE);
        mCornerPaint.setStrokeWidth(BASE_STROKE_WIDTH * 3);
    }

    private void initView(Context context) {
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBgAndRectFrame(canvas);
        drawMidLine(canvas);
        drawAngleBold(canvas);
    }

    /**
     * 绘制背景以及边框
     *
     * @param canvas
     */
    private void drawBgAndRectFrame(Canvas canvas) {
        //绘制背景
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        //绘制外围背景
        mBgPaint.setColor(mPaintBgColor);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBgPaint);
        mBgPaint.setXfermode(xFermode);
        mBgPaint.setColor(Color.RED);
        mBgPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mDrawRect, mBgPaint);
        mBgPaint.setXfermode(null);
        canvas.restoreToCount(layerId);

        //绘制矩形边框
        mPath.reset();
        //绘制边框
        mPaint.setColor(mPaintLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPath.moveTo(mDrawRect.left, mDrawRect.top);
        mPath.lineTo(mDrawRect.right, mDrawRect.top);
        mPath.lineTo(mDrawRect.right, mDrawRect.bottom);
        mPath.lineTo(mDrawRect.left, mDrawRect.bottom);
        mPath.lineTo(mDrawRect.left, mDrawRect.top);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 绘制内部实线
     *
     * @param canvas
     */
    private void drawMidLine(Canvas canvas) {
        mPaint.setColor(mPaintLineColor);
        //绘制中线
        int width = (int) (mDrawRect.right - mDrawRect.left);
        int height = (int) (mDrawRect.bottom - mDrawRect.top);
        //竖线
        mPath.moveTo(mDrawRect.left + (width) * 1.0F / 3, mDrawRect.top);
        mPath.lineTo(mDrawRect.left + (width) * 1.0F / 3, mDrawRect.bottom);
        canvas.drawPath(mPath, mPaint);

        mPath.moveTo(mDrawRect.left + (width) * 1.0F / 3 * 2, mDrawRect.top);
        mPath.lineTo(mDrawRect.left + (width) * 1.0F / 3 * 2, mDrawRect.bottom);
        canvas.drawPath(mPath, mPaint);

        //横线
        mPath.moveTo(mDrawRect.left, mDrawRect.top + (height) * 1.0F / 3 * 2);
        mPath.lineTo(mDrawRect.right, mDrawRect.top + (height) * 1.0F / 3 * 2);
        canvas.drawPath(mPath, mPaint);

        mPath.moveTo(mDrawRect.left, mDrawRect.top + (height) * 1.0F / 3);
        mPath.lineTo(mDrawRect.right, mDrawRect.top + (height) * 1.0F / 3);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 绘制加粗角
     *
     * @param canvas
     */
    private void drawAngleBold(Canvas canvas) {
        mCornerPaint.setColor(mPaintColor);
        int angleLength = ANGEL_LENGTH;
        int width = (int) (mDrawRect.right - mDrawRect.left);
        int height = (int) (mDrawRect.bottom - mDrawRect.top);
        if (angleLength > width) {
            angleLength = width;
        }
        if (angleLength > height) {
            angleLength = height;
        }
        //绘制左上角
        mPath.reset();
        mPath.moveTo(mDrawRect.left + angleLength + BASE_STROKE_WIDTH, mDrawRect.top + BASE_STROKE_WIDTH);
        mPath.lineTo(mDrawRect.left + BASE_STROKE_WIDTH, mDrawRect.top + BASE_STROKE_WIDTH);
        mPath.lineTo(mDrawRect.left + BASE_STROKE_WIDTH, mDrawRect.top + angleLength + +BASE_STROKE_WIDTH);
        canvas.drawPath(mPath, mCornerPaint);

        //绘制右上角
        mPath.moveTo(mDrawRect.right - angleLength - BASE_STROKE_WIDTH, mDrawRect.top + BASE_STROKE_WIDTH);
        mPath.lineTo(mDrawRect.right - BASE_STROKE_WIDTH, mDrawRect.top + BASE_STROKE_WIDTH);
        mPath.lineTo(mDrawRect.right - BASE_STROKE_WIDTH, mDrawRect.top + angleLength + BASE_STROKE_WIDTH);
        canvas.drawPath(mPath, mCornerPaint);

        //绘制右下角
        mPath.moveTo(mDrawRect.right - BASE_STROKE_WIDTH, mDrawRect.bottom - BASE_STROKE_WIDTH - angleLength);
        mPath.lineTo(mDrawRect.right - BASE_STROKE_WIDTH, mDrawRect.bottom - BASE_STROKE_WIDTH);
        mPath.lineTo(mDrawRect.right - BASE_STROKE_WIDTH - angleLength, mDrawRect.bottom - BASE_STROKE_WIDTH);
        canvas.drawPath(mPath, mCornerPaint);

        //绘制左下角
        mPath.moveTo(mDrawRect.left + BASE_STROKE_WIDTH, mDrawRect.bottom - BASE_STROKE_WIDTH - angleLength);
        mPath.lineTo(mDrawRect.left + BASE_STROKE_WIDTH, mDrawRect.bottom - BASE_STROKE_WIDTH);
        mPath.lineTo(mDrawRect.left + BASE_STROKE_WIDTH + angleLength, mDrawRect.bottom - BASE_STROKE_WIDTH);
        canvas.drawPath(mPath, mCornerPaint);
    }


    /**
     * 是否触摸点为绘制view的外部
     *
     * @param event
     * @return
     */
    private boolean outOfRect(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            int pointerCount = event.getPointerCount();
            if (pointerCount > 1) {
                Point two = new Point((int) event.getX(1), (int) event.getY(1));
                return !isInRect(two, mDrawRect);
            } else {
                Point one = new Point((int) event.getX(), (int) event.getY());
                return !isInRect(one, mDrawRect);
            }
        }
        return false;
    }

    /**
     * 计算是否进行上层缩放还是进行边框view的大小改变
     *
     * @param onePoint 点击1
     * @param twoPoint 点击2
     * @return false 进行上层缩放，true 进行本view边框大小改变
     */
    private boolean consumeChangeRect(Point onePoint, Point twoPoint) {
        if (onePoint != null && twoPoint != null && mDrawRect != null) {
            float width = mDrawRect.right - mDrawRect.left;
            float height = mDrawRect.bottom - mDrawRect.top;
//            RectF leftTop = new RectF(mDrawRect.left, mDrawRect.top, mDrawRect.left + width / 6, mDrawRect.top + height / 6);
//            RectF rightTop = new RectF(mDrawRect.right - width / 6, mDrawRect.top, mDrawRect.right, mDrawRect.top + height / 6);
//            RectF leftBottom = new RectF(mDrawRect.left, mDrawRect.bottom - height / 6, mDrawRect.left + width / 6, mDrawRect.bottom);
//            RectF rightBottom = new RectF(mDrawRect.right - width / 6, mDrawRect.bottom - height / 6, mDrawRect.right , mDrawRect.bottom);

            RectF tempRect = new RectF();
            //判断点是否在左上角
            tempRect.left = mDrawRect.left;
            tempRect.top = mDrawRect.top;
            tempRect.right = mDrawRect.left + width / 6f;
            tempRect.bottom = mDrawRect.top + height / 6f;
            //左上角与右下角对称
            if (isInRect(onePoint, tempRect)) {
                //右下角
                tempRect.left = mDrawRect.right - width / 6;
                tempRect.top = mDrawRect.bottom - height / 6;
                tempRect.right = mDrawRect.right;
                tempRect.bottom = mDrawRect.bottom;
                return isInRect(twoPoint, tempRect);
            }

            //判断点是否在右上角
            tempRect.left = mDrawRect.right - width / 6;
            tempRect.top = mDrawRect.top;
            tempRect.right = mDrawRect.right;
            tempRect.bottom = mDrawRect.top + height / 6;
            //右上角与左下角对称

            if (isInRect(onePoint, tempRect)) {
                //RectF leftBottom = new RectF(, , , );
                //左下角
                tempRect.left = mDrawRect.left;
                tempRect.top = mDrawRect.bottom - height / 6;
                tempRect.right = mDrawRect.left + width / 6;
                tempRect.bottom = mDrawRect.bottom;
                return isInRect(twoPoint, tempRect);
            }
            //！！！对称逻辑 先判断点1  后改为先判断点2
            //判断点是否在左上角
            tempRect.left = mDrawRect.left;
            tempRect.top = mDrawRect.top;
            tempRect.right = mDrawRect.left + width / 6f;
            tempRect.bottom = mDrawRect.top + height / 6f;
            //左上角与右下角对称
            if (isInRect(twoPoint, tempRect)) {
                //右下角
                tempRect.left = mDrawRect.right - width / 6;
                tempRect.top = mDrawRect.bottom - height / 6;
                tempRect.right = mDrawRect.right;
                tempRect.bottom = mDrawRect.bottom;
                return isInRect(onePoint, tempRect);
            }

            //判断点是否在右上角
            tempRect.left = mDrawRect.right - width / 6;
            tempRect.top = mDrawRect.top;
            tempRect.right = mDrawRect.right;
            tempRect.bottom = mDrawRect.top + height / 6;
            //右上角与左下角对称

            if (isInRect(twoPoint, tempRect)) {
                //RectF leftBottom = new RectF(, , , );
                //左下角
                tempRect.left = mDrawRect.left;
                tempRect.top = mDrawRect.bottom - height / 6;
                tempRect.right = mDrawRect.left + width / 6;
                tempRect.bottom = mDrawRect.bottom;
                return isInRect(onePoint, tempRect);
            }
        }
        return false;
    }

    // 触碰两点间距离
    private float getSpacing(MotionEvent event) {
        //通过三角函数得到两点间的距离
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取旋转角度
    private float getDegree(MotionEvent event) {
        //得到两个手指间的旋转角度
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 点是否在矩形内
     *
     * @param point
     * @param rectF
     * @return
     */
    public static boolean isInRect(Point point, RectF rectF) {
        if (point == null || rectF == null) return false;
        return point.x >= rectF.left && point.x <= rectF.right && point.y >= rectF.top && point.y <= rectF.bottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (outOfRect(event)) {
            Log.d(TAG, "onTouchEvent outOfRect true");
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent ACTION_DOWN x:" + event.getX() + " y:" + event.getY());
                //检测到一只手指按下
                canMove = true;
                twoFingerFlag = false;
                downPoint.x = (int) event.getX();
                downPoint.y = (int) event.getY();
                startId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                int pointerCount = event.getPointerCount();
                Log.d(TAG, "onTouchEvent ACTION_POINTER_DOWN pointerCount:" + pointerCount + "x:" + event.getX(1) + " y:" + event.getY(1));
                //检测到另外手指按下
                twoFingerFlag = true;
                if (pointerCount > 2) {
                    //只检测双指，3指不处理
                    break;
                }
                twoPoint.x = (int) event.getX(1);
                twoPoint.y = (int) event.getY(1);
                twoFingerStartXLength = event.getX(0) - event.getX(1);
                twoFingerStartYLength = event.getY(0) - event.getY(1);
                twoFingerStartLength = getSpacing(event);
                twoFingerStartDegree = getDegree(event);
                changeRectView = consumeChangeRect(downPoint, twoPoint);
                Log.d(TAG, "onTouchEvent ACTION_POINTER_DOWN changeRectView:" + changeRectView);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "onTouchEvent ACTION_POINTER_UP pointCount" + event.getPointerCount());
                //todo 只要有主要手指放开是否放弃操作

                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "onTouchEvent ACTION_MOVE");

                //主要处理事件
                if (!canMove) break;
                if (twoFingerFlag && event.getPointerCount() > 1) {
                    //todo 双指操作
                    if (changeRectView) {
                        //todo 本地view缩放 变形

                        float twoFingerXLength = event.getX(0) - event.getX(1);
                        float twoFingerYLength = event.getY(0) - event.getY(1);
                        float scaleX = twoFingerXLength / twoFingerStartXLength;
                        float scaleY = twoFingerYLength / twoFingerStartYLength;
                        if (tempWidth == 0) {
                            tempWidth = mDrawRect.right - mDrawRect.left;
                            tempHeight = mDrawRect.bottom - mDrawRect.top;
                            tempRatio = ratio * (scaleX / scaleY);
                        }
                        Log.d(TAG, "onTouchEvent twoFingerFlag scaleX:" + scaleX + " scaleY:" + scaleY + " changeRectView:" + changeRectView);

//                        float height = tempHeight * scaleY;
//                        if (height > mHeight) {
//                            height = mHeight;
//                        }
                        mDrawRect.top = mDrawRect.bottom - tempHeight * scaleY;
                        mDrawRect.right = mDrawRect.right - tempHeight * scaleY;
                        postInvalidate();
                    } else {
                        float dSpace = getSpacing(event);
                        float dDegree = getDegree(event);
                        float scale = dSpace / twoFingerStartLength;
                        float degree = dDegree - twoFingerStartDegree;
                        Log.d(TAG, "onTouchEvent twoFingerFlag scale:" + scale + " degree:" + degree + " changeRectView:" + changeRectView);
                        if (callbackListener != null) {
                            callbackListener.onScaleAndDegree(scale, degree);
                        }
                        //todo 回调
                    }

                } else {
                    //todo 单指拖动
                    float touchX = event.getX();
                    float touchY = event.getY();
                    Log.d(TAG, "onTouchEvent singleFingerFlag transX:" + (lastTouchX - touchX) + " transY:" + (lastTouchY - touchY));
                    if (callbackListener != null) {
                        if (lastTouchX != 0) {
                            callbackListener.onTrans(lastTouchX - touchX, lastTouchY - touchY);
                        }
                    }
                    lastTouchX = touchX;
                    lastTouchY = touchY;
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent ACTION_UP changeRectView:" + changeRectView);
                if (changeRectView) {

                }
                lastTouchX = 0;
                lastTouchY = 0;
                canMove = false;
                break;
        }
        return true;
    }

    public interface CutCallbackListener {
        //双指操作结束回调
        void onTwoFingerEnd();

        //平移回调
        void onTrans(float transX, float transY);

        //平移回调结束
        void onTransEnd();

        //缩放回调
        void onScaleAndDegree(float scale, float degree);
    }
}
