package com.czc.cutsame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.czc.cutsame.R;

import java.util.List;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2021/3/10 16:28
 * @Description :字幕框 Caption box
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptionBox extends View {
    private Paint mRectPaint = new Paint();
    private List<PointF> mListPointF;

    public CaptionBox(Context context) {
        this(context, null);
    }

    public CaptionBox(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptionBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRectPaint();
    }

    private void initRectPaint() {
        // 设置颜色
        mRectPaint.setColor(getResources().getColor(R.color.color_ffff365e));
        // 设置抗锯齿
        mRectPaint.setAntiAlias(true);
        // 设置线宽
        mRectPaint.setStrokeWidth(3);
        // 设置非填充
        mRectPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(getRectPath(), mRectPaint);
    }

    /**
     * 设置边框的四个点
     * Sets the four points of the border
     */
    public void setPointFList(List<PointF> listPointF) {
        mListPointF = listPointF;
        invalidate();
    }

    /**
     * 获取边框路径
     * Gets the path of border
     */
    private Path getRectPath() {
        Path path = new Path();
        if (mListPointF != null && mListPointF.size() >= 4) {
            path.moveTo(mListPointF.get(0).x, mListPointF.get(0).y);
            path.lineTo(mListPointF.get(1).x, mListPointF.get(1).y);
            path.lineTo(mListPointF.get(2).x, mListPointF.get(2).y);
            path.lineTo(mListPointF.get(3).x, mListPointF.get(3).y);
            path.close();
        }
        return path;
    }
}
