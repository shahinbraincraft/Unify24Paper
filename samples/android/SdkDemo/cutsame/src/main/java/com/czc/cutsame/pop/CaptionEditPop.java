package com.czc.cutsame.pop;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.czc.cutsame.R;
import com.meishe.base.utils.KeyboardUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.BasePopupView;
import com.meishe.third.pop.util.XPopupUtils;

/**
 * All rights reserved,Designed by www.meishesdk.com
 *
 * @Author : LiHangZhou
 * @CreateDate :2021/3/10 18:43
 * @Description :根据软键盘高度而变动的字幕编辑弹窗
 * According to the height of the soft keyboard and change the subtitle editing pop-up window
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
public class CaptionEditPop extends BasePopupView {
    /*
     *注意：本类是继承于BasePopupView，自定义了一些处理。除了一些比较特殊的使用,平常的使用没必要参照。
     */
    private FrameLayout mFlRootContainer;
    private LinearLayout mLlRoot;
    private EditText mEtCaption;
    private TextView mTvCaption;
    private String mCaptionText;
    private Button mBtConfirm;
    private EventListener mListener;

    public CaptionEditPop(@NonNull Context context) {
        super(context);
        mFlRootContainer = findViewById(R.id.fl_container);
    }

    public static CaptionEditPop create(Context context) {
        return (CaptionEditPop) new XPopup
                .Builder(context)
                .dismissOnTouchOutside(true)
                .hasShadowBg(true)
                .asCustom(new CaptionEditPop(context));
    }

    @Override
    protected int getPopupLayoutId() {
        return R.layout.pop_caption_edit_container;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.cut_layout_edit_bottom_view;
    }

    @Override
    protected void initPopupContent() {
        //自行实现相关逻辑
        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), mFlRootContainer, false);
        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
        params.gravity = Gravity.TOP;
        mFlRootContainer.addView(contentView, params);
        getPopupContentView().setTranslationX(popupInfo.offsetX);
        getPopupContentView().setTranslationY(popupInfo.offsetY);
        XPopupUtils.applyPopupSize((ViewGroup) getPopupContentView(), getMaxWidth(), getMaxHeight());
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mLlRoot = findViewById(R.id.ll_root_view);
        mEtCaption = findViewById(R.id.et_caption);
        mTvCaption = findViewById(R.id.tv_caption);
        mBtConfirm = findViewById(R.id.bt_confirm);

        initListener();
    }

    private void initListener() {
        mBtConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                KeyboardUtils.hideSoftInput(mEtCaption);
                if (mListener != null) {
                    CharSequence text = mTvCaption.getText();
                    mListener.onConfirm(text == null ? "" : text.toString());
                }
            }
        });
        mEtCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    mTvCaption.setText(s.toString());
                }
            }
        });
    }

    @Override
    protected int getMaxWidth() {
        return ScreenUtils.getScreenWidth();
    }

    @Override
    protected int getPopupHeight() {
        return ScreenUtils.getScreenHeight();
    }

    @Override
    public BasePopupView show() {
        //重写，不使用父类方法，下面的处理借鉴于BasePopupView
        if (getParent() != null) return this;
        final Activity activity = (Activity) getContext();
        popupInfo.decorView = (ViewGroup) activity.getWindow().getDecorView();
        /*
         * 1. add PopupView to its decorView after measured.
         * 在测量后添加PopupView到它的decorView。
         * */
        popupInfo.decorView.post(new Runnable() {
            @Override
            public void run() {
                if (getParent() != null) {
                    ((ViewGroup) getParent()).removeView(CaptionEditPop.this);
                }
                popupInfo.decorView.addView(CaptionEditPop.this, new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));

                /*
                 * 2. do init，game start.
                 * 做初始化,游戏开始
                 * */
                init();
            }
        });
        return this;
    }

    /**
     * 设置字幕文字
     * Set caption text
     */
    public void setCaptionText(final String text) {
        if (mEtCaption != null) {
            mEtCaption.setText(text);
            mTvCaption.setText(text);
            mTvCaption.post(new Runnable() {
                @Override
                public void run() {
                    mEtCaption.setSelection(text.length());
                }
            });
        } else {
            mCaptionText = text;
        }
    }

    /**
     * 设置事件监听
     *
     * @param listener the listener
     */
    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    @Override
    protected void doAfterShow() {
        //重写，不使用父类方法.
        KeyboardUtils.showSoftInput(mEtCaption);
        if (!TextUtils.isEmpty(mCaptionText) && TextUtils.isEmpty(mTvCaption.getText())) {
            setCaptionText(mCaptionText);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        KeyboardUtils.registerSoftInputChangedListener((Activity) getContext(), mSoftInputChangedListener);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KeyboardUtils.unregisterSoftInputChangedListener(((Activity) getContext()).getWindow());
    }

    KeyboardUtils.OnSoftInputChangedListener mSoftInputChangedListener = new KeyboardUtils.OnSoftInputChangedListener() {
        @Override
        public void onSoftInputChanged(int height) {
            //监听软键盘
            if (height == 0) {
                //键盘消失则弹窗消失
                if (isShow()) {
                    dismiss();
                }
            } else {
                //重新设置布局高度，防止被软键盘遮盖
                ViewGroup.LayoutParams rootParams = mLlRoot.getLayoutParams();
                rootParams.height = getPopupHeight() - height;
                //mLlRoot.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mLlRoot.setLayoutParams(rootParams);
                ViewGroup.LayoutParams rootContainerParams = getPopupContentView().getLayoutParams();
                rootContainerParams.height = rootParams.height;
                getPopupContentView().setLayoutParams(rootContainerParams);
            }
        }
    };

    public interface EventListener {
        /**
         * 确定
         * Confirm
         */
        void onConfirm(String text);
    }
}
