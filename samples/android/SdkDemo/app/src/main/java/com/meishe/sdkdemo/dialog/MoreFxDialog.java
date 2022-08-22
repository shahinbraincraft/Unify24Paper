package com.meishe.sdkdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishe.base.utils.Utils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.utils.ScreenUtils;

/**
 * * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : zcy
 * @CreateDate : 2021/6/29.
 * @Description :中文
 * @Description :English
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class MoreFxDialog extends Dialog {

    private LinearLayout llSticker;
    private LinearLayout mFuLayout;
    private MoreClickListener moreClickListener;
    private LinearLayout mComCaption;
    private int xLocation = 0;
    private int yLocation = 0;
    private View mBgSeg;
    private ImageView mIvProps;
    private TextView mTvProps;
    private boolean mNeedChangeMoreFxDialogPropsColor;

    public void setMoreClickListener(MoreClickListener moreClickListener) {
        this.moreClickListener = moreClickListener;
    }

    public MoreFxDialog(Context context, int xLocation, int yLocation) {
        super(context);
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }

    public MoreFxDialog(@NonNull Context context) {
        super(context);
    }

    public MoreFxDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public MoreFxDialog(@NonNull Context context, int themeResId, boolean needChangeMoreFxDialogPropsColor) {
        super(context, themeResId);
        mNeedChangeMoreFxDialogPropsColor = needChangeMoreFxDialogPropsColor;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fx_more);
        llSticker = findViewById(R.id.ll_sticker);
        mFuLayout = findViewById(R.id.ll_props);
        mComCaption = findViewById(R.id.ll_com_caption);
        mBgSeg = findViewById(R.id.ll_background_seg);
        mIvProps = findViewById(R.id.iv_props);
        mTvProps = findViewById(R.id.tv_props);
        if (mNeedChangeMoreFxDialogPropsColor) {
            changeProsBlack();
        }
        llSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moreClickListener != null) {
                    moreClickListener.onStickerClick();
                }
            }
        });
        mFuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moreClickListener != null) {
                    moreClickListener.onArSceneClick();
                }
            }
        });
        mComCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moreClickListener != null) {
                    moreClickListener.onComCaptionClick();
                }
            }
        });

        mBgSeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moreClickListener != null) {
                    moreClickListener.onBgSegClick();
                }
            }
        });


        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = ScreenUtils.dp2px(getContext(), 90);
        if (xLocation == 0) {
            p.x = ScreenUtils.getWindowWidth(getContext()) / 2 - ScreenUtils.dip2px(getContext(), (30 + 20));
        } else {
            p.x = xLocation;
        }
        if (yLocation == 0) {
            p.y = 200;
        } else {
            p.y = yLocation;
        }
        p.dimAmount = 0f;
        getWindow().setAttributes(p);

    }

    public void changeProsBlack() {
        if (mIvProps != null) {
            mIvProps.setImageResource(R.mipmap.capture_props_black);
        }
        if (mTvProps != null) {
            mTvProps.setTextColor(Utils.getApp().getResources().getColor(R.color.black));
        }
    }

    public interface MoreClickListener {
        //ComCaption
        void onComCaptionClick();

        //arScene
        void onArSceneClick();

        //sticker
        void onStickerClick();

        //sticker
        void onBgSegClick();
    }
}
