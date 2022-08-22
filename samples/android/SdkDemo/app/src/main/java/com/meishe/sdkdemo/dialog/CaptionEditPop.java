package com.meishe.sdkdemo.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsStreamingContext;
import com.meishe.base.utils.BarUtils;
import com.meishe.base.utils.KeyboardUtils;
import com.meishe.base.utils.ScreenUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.background.MultiColorInfo;
import com.meishe.sdkdemo.edit.background.view.MultiColorView;
import com.meishe.sdkdemo.edit.compoundcaption.FontInfo;
import com.meishe.sdkdemo.edit.data.AssetItem;
import com.meishe.sdkdemo.edit.data.ParseJsonFile;
import com.meishe.sdkdemo.utils.AppManager;
import com.meishe.sdkdemo.utils.KeyBoardUtil;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.third.pop.XPopup;
import com.meishe.third.pop.core.BasePopupView;
import com.meishe.third.pop.util.XPopupUtils;
import com.meishe.utils.ColorUtil;

import java.util.ArrayList;
import java.util.List;

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
    private ImageView mBtConfirm;
    private EventListener mListener;
    private View tabInput;
    private View tabStyle;
    private TextView tvInput;
    private TextView tvStyle;
    private View signStyle;
    private View signInput;
    private View captionStyleView;
    private int keyboardHeight = 0;
    private RecyclerView captionFont;
    private MultiColorView captionColor;
    private String captionColorInfo;
    private List<AssetItem> fonts = new ArrayList<>();
    private FontAdapter fontAdapter;
    private NvAsset fontAsset;
    private View viewBreak;
    private boolean showNavigation;
    private int narHeight;

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
        return R.layout.dialog_caption_edit_container;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_caption_edit_head;
    }

    @Override
    protected void initPopupContent() {
        //自行实现相关逻辑
        View contentView = LayoutInflater.from(getContext()).inflate(getImplLayoutId(), mFlRootContainer, false);
        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
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
        mBtConfirm = findViewById(R.id.commit);
        tabInput = findViewById(R.id.tab_input);
        tabStyle = findViewById(R.id.tab_style);
        tvInput = findViewById(R.id.tv_input);
        tvStyle = findViewById(R.id.tv_style);
        signInput = findViewById(R.id.sign_input);
        signStyle = findViewById(R.id.sign_style);
        captionStyleView = findViewById(R.id.caption_style);
        captionFont = findViewById(R.id.caption_font);
        captionColor = findViewById(R.id.caption_color);
        viewBreak = findViewById(R.id.view_break);

        initListener();
        initData();
    }

    private void initData() {
        initCaptionFonts();
    }

    private void initCaptionFonts() {
        NvsStreamingContext streamingContext = NvsStreamingContext.getInstance();
        if (streamingContext == null){
            return;
        }
        String fontJsonPath = "font/info.json";
        String fontJsonText = ParseJsonFile.readAssetJsonFile(getContext(), fontJsonPath);
        if (TextUtils.isEmpty(fontJsonText)) {
            return;
        }
        List<FontInfo> infoList = ParseJsonFile.fromJson(fontJsonText, new TypeToken<List<FontInfo>>() {
        }.getType());
        if (infoList == null) {
            return;
        }
        int fontCount = infoList.size();
        for (int idx = 0; idx < fontCount; idx++) {
            FontInfo fontInfo = infoList.get(idx);
            if (fontInfo == null) {
                continue;
            }
            String fontAssetPath = "assets:/font/" + fontInfo.getFontFileName();
            String fontName = streamingContext.registerFontByFilePath(fontAssetPath);
            AssetItem assetItem = new AssetItem();
            NvAsset asset = new NvAsset();
            String fontCoverPath = "file:///android_asset/font/" + fontInfo.getImageName();
            asset.coverUrl = fontCoverPath;
            asset.isReserved = true;
            asset.bundledLocalDirPath = fontAssetPath;
            asset.name = fontName;
            assetItem.setAsset(asset);
            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
            fonts.add(assetItem);
        }
        AssetItem assetItem = new AssetItem();
        NvAsset asset = new NvAsset();
        assetItem.setImageRes(R.mipmap.comp_caption_default);
        assetItem.setAssetMode(AssetItem.ASSET_NONE);
        assetItem.setAsset(asset);
        fonts.add(0, assetItem);
        fontAdapter = new FontAdapter(getContext(), fonts);
        captionFont.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        captionFont.setAdapter(fontAdapter);
    }

    private void initListener() {
        mBtConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                KeyboardUtils.hideSoftInput(mEtCaption);
                if (mListener != null) {
                    CharSequence text = mTvCaption.getText();
                    mListener.onConfirm(text == null ? "" : text.toString(), captionColorInfo, fontAsset);
                }
            }
        });
        mEtCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBtConfirm.setAlpha(TextUtils.isEmpty(s)?0.5f:1.0f);
                mBtConfirm.setClickable(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    mTvCaption.setText(s.toString());
                }
            }
        });
        tabInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                tvInput.setTextColor(Color.parseColor("#ffffff"));
                signInput.setVisibility(VISIBLE);
                tvStyle.setTextColor(Color.parseColor("#cfffffff"));
                signStyle.setVisibility(INVISIBLE);
                captionStyleView.setVisibility(GONE);
                // 弹起键盘
                setRootHeight(keyboardHeight);
                KeyboardUtils.showSoftInput(AppManager.getInstance().currentActivity());
            }
        });
        tabStyle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                tvInput.setTextColor(Color.parseColor("#cfffffff"));
                signInput.setVisibility(INVISIBLE);
                tvStyle.setTextColor(Color.parseColor("#ffffff"));
                signStyle.setVisibility(VISIBLE);
                captionStyleView.setVisibility(VISIBLE);
                setRootHeight(0);
                // 收起键盘
                KeyBoardUtil.hideSoftKeyBroad(AppManager.getInstance().currentActivity());
            }
        });
        captionColor.setOnMultiColorItemClickListener(new MultiColorView.OnMultiColorItemClickListener() {
            @Override
            public void onItemClick(View view, MultiColorInfo colorInfo) {
                captionColorInfo = colorInfo.getColorValue();
                mTvCaption.setTextColor(Color.parseColor(colorInfo.getColorValue()));
            }
        });
    }

    @Override
    protected int getMaxWidth() {
        return ScreenUtils.getScreenWidth();
    }

    @Override
    protected int getPopupHeight() {
        return ScreenUtils.getScreenHeight() - ScreenUtils.getStatusBarHeight();
    }

    @Override
    public BasePopupView show() {
        captionColorInfo = null;
        fontAsset = null;
        if (captionStyleView != null) {
            captionStyleView.setVisibility(GONE);
        }

        //重写，不使用父类方法，下面的处理借鉴于BasePopupView
        if (getParent() != null){
            return this;
        }
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

    public void setCaptionTextColor(NvsColor color) {
        String colorString = ColorUtil.nvsColorToHexString(color);
        if (mTvCaption != null) {
            mTvCaption.setTextColor(Color.parseColor(colorString));
        }
        if (captionColor != null) {
            captionColor.setSelectPosition(-1);
            List<MultiColorInfo> colorList = captionColor.getColorList();
            if (colorList != null) {
                for (int i = 0; i < colorList.size(); i++) {
                    MultiColorInfo multiColorInfo = colorList.get(i);
                    if (TextUtils.equals(colorString, multiColorInfo.getColorValue())) {
                        captionColor.setSelectPosition(i);
                        break;
                    }
                }
            }
        }
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
     * 设置默认字体
     *
     * @param fontName
     */
    public void setCaptionFont(String fontName) {
        if (fontAdapter != null) {
            fontAdapter.setSelectPosition(-1);
        }
        if (!TextUtils.isEmpty(fontName) && fonts != null && fonts.size() > 0) {
            if (mTvCaption != null) {
                applyTvFont(fonts.get(0).getAsset());
            }
            for (int i = 0; i < fonts.size(); i++) {
                if (TextUtils.equals(fonts.get(i).getAsset().name, fontName)) {
                    if (fontAdapter != null) {
                        fontAdapter.setSelectPosition(i);
                        applyTvFont(fonts.get(i).getAsset());
                    }
                    break;
                }
            }
        }

    }

    private void applyTvFont(NvAsset asset) {
        String assetLocalDirPath = asset.bundledLocalDirPath;
        Typeface newTypeface = null;
        if (!TextUtils.isEmpty(assetLocalDirPath)) {
            int index = assetLocalDirPath.indexOf('/');
            String fontPath = assetLocalDirPath.substring(index + 1);
            newTypeface = Typeface.createFromAsset(getContext().getAssets(), fontPath);
        }
        mTvCaption.setTypeface(newTypeface);
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
                    //dismiss();
                }
            } else {
                keyboardHeight = height;
                //重新设置布局高度，防止被软键盘遮盖
                setRootHeight(height);
//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) captionStyleView.getLayoutParams();
//                if (layoutParams != null) {
//                    layoutParams.height = height;
//                }
//                captionStyleView.setLayoutParams(layoutParams);
                mEtCaption.requestFocus();
            }
        }
    };

    private void setRootHeight(final int height) {
        ViewGroup.LayoutParams rootParams = viewBreak.getLayoutParams();
//        rootParams.height = getPopupHeight() - height - BarUtils.getNavBarHeight(getContext());
        int narBar = (!showNavigation && BarUtils.isAllScreenDevice(getContext())) ?
                0 : BarUtils.getNavBarHeight(getContext());
//        rootParams.height = getPopupHeight() - height - 20 + narBar;//20随便写的 目前有点遮挡布局
        rootParams.height = height + narBar;//20随便写的 目前有点遮挡布局
        //mLlRoot.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        viewBreak.setLayoutParams(rootParams);
//        ViewGroup.LayoutParams rootContainerParams = getPopupContentView().getLayoutParams();
//        rootContainerParams.height = rootParams.height;
//        getPopupContentView().setLayoutParams(rootContainerParams);
    }

    public void resetSign() {
        if (tvInput != null) {
            tvInput.setTextColor(Color.parseColor("#ffffff"));
            signInput.setVisibility(VISIBLE);
            tvStyle.setTextColor(Color.parseColor("#cfffffff"));
            signStyle.setVisibility(INVISIBLE);
        }
    }

    public void setNarBar(boolean showNavigation, int narHeight) {
        this.showNavigation = showNavigation;
        this.narHeight =narHeight;
    }

    private class FontAdapter extends RecyclerView.Adapter<FontViewHolder> {
        public FontAdapter(Context context, List<AssetItem> data) {
            this.context = context;
            this.data = data;
            mOptions = new RequestOptions();
            mOptions.fitCenter();
            mOptions.skipMemoryCache(false);
            mOptions.placeholder(R.mipmap.defalut_font);
        }

        private int selectPosition = -1;
        private Context context;
        private List<AssetItem> data;
        RequestOptions mOptions;

        public void setSelectPosition(int selectPosition) {
            this.selectPosition = selectPosition;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public FontViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_caption_font, null);
            return new FontViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CaptionEditPop.FontViewHolder viewHolder, int position) {
            final int clickPosition = position;
            AssetItem assetItem = data.get(position);
//            viewHolder.tvText.setText(assetItem.getAsset().name);
            viewHolder.ivSelect.setVisibility(selectPosition == position ? VISIBLE : GONE);
            if (assetItem.getAssetMode() == AssetItem.ASSET_NONE) {
                viewHolder.ivBg.setImageResource(assetItem.getImageRes());
            } else {
                NvAsset asset = assetItem.getAsset();
                String imageUrl = asset.coverUrl;
                if (!TextUtils.isEmpty(imageUrl)) {
                    Glide.with(context)
                            .asBitmap()
                            .load(imageUrl)
                            .apply(mOptions)
                            .into(viewHolder.ivBg);
                }
            }
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectPosition != clickPosition) {
                        selectPosition = clickPosition;
                        NvAsset asset = data.get(clickPosition).getAsset();
                        fontAsset = asset;
                        notifyDataSetChanged();
                        if (mTvCaption != null && asset != null) {
                            applyTvFont(asset);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }
    }

    private class FontViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivSelect;
        private ImageView ivBg;
        private TextView tvText;

        public FontViewHolder(View itemView) {
            super(itemView);
            ivBg = itemView.findViewById(R.id.iv_bg);
            tvText = itemView.findViewById(R.id.nameAsset);
            ivSelect = itemView.findViewById(R.id.iv_select_bg);
        }
    }

    public interface EventListener {
        /**
         * 确定
         * Confirm
         */
        void onConfirm(String text, String textColor, NvAsset fontAsset);
    }
}
