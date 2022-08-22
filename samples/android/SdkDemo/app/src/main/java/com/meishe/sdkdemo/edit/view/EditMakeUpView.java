package com.meishe.sdkdemo.edit.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsColor;
import com.meishe.base.utils.ZipUtils;
import com.meishe.http.AssetType;
import com.meishe.http.bean.BaseBean;
import com.meishe.http.bean.BaseDataBean;
import com.meishe.http.bean.CategoryInfo;
import com.meishe.modulemakeupcompose.MakeupManager;
import com.meishe.modulemakeupcompose.makeup.BeautyData;
import com.meishe.modulemakeupcompose.makeup.ColorData;
import com.meishe.modulemakeupcompose.makeup.Makeup;
import com.meishe.modulemakeupcompose.makeup.MakeupArgs;
import com.meishe.modulemakeupcompose.makeup.MakeupCategoryInfo;
import com.meishe.modulemakeupcompose.makeup.MakeupCustomModel;
import com.meishe.modulemakeupcompose.makeup.MakeupData;
import com.meishe.modulemakeupcompose.makeup.MakeupEffectContent;
import com.meishe.modulemakeupcompose.makeup.NoneItem;
import com.meishe.net.custom.BaseResponse;
import com.meishe.net.custom.RequestCallback;
import com.meishe.net.custom.SimpleDownListener;
import com.meishe.net.model.Progress;
import com.meishe.sdkdemo.BuildConfig;
import com.meishe.sdkdemo.MSApplication;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.base.http.HttpManager;
import com.meishe.sdkdemo.capture.EditMakeupAdapter;
import com.meishe.sdkdemo.capture.MakeupAdapter;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.asset.NvHttpRequest;
import com.meishe.sdkdemo.view.ButtonRoundColorView;
import com.meishe.sdkdemo.view.ColorSeekBar;
import com.meishe.sdkdemo.view.MagicProgress;
import com.meishe.utils.ColorUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 *
 * @Author: lpf
 * @CreateDate: 2021/11/08.
 * @Description: 美妆View
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class EditMakeUpView extends RelativeLayout implements NvHttpRequest.NvHttpRequestListener {
    public static final float DEFAULT_MAKEUP_INTENSITY = 0.6f;
    private static final String TAG = "MakeUpView";
    private static final int TYPE_COMPOSE = 0x100;
    private static final int TYPE_CUSTOM = 0x200;
    private static final int TYPE_MAKEUP = 0x300;
    private boolean isFaceModel106 = (BuildConfig.FACE_MODEL == 106);
    private Context mContext;
    private RecyclerView mMakeupRecyclerView;
    //    private ImageView mMakeupChangeBtn;
    private TextView mTvColor, mTvAlpha;
    //    private TextView mMakeupChangeBtnText, mTvColor, mTvAlpha;
    private View mMakeupColorHinLayout, mMakeupTopLayout;
    //    private EditColorPickerView mColorPickerView;
    private MagicProgress mMakeupSeekBar;
    private TabLayout mMakeUpTab;
//    private boolean mIsMakeupMainMenu = true;

    private EditMakeupAdapter mMakeupAdapter;
    private MakeUpEventListener mMakeUpEventListener;
    /**
     * 整妆
     */
    private ArrayList<BeautyData> mMakeupComposeData = new ArrayList<>();
    /**
     * 单妆分类
     */
    private ArrayList<CategoryInfo> mCategoryInfos = new ArrayList<>();
    /**
     * 单妆，包括妆容
     */
    private ArrayList<MakeupCustomModel> mMakeupCustomData = new ArrayList<>();

    private String mCurrentEffectId = null;

    /**
     * as customCategory--> eyeshadow
     */

    private boolean isClearMakeup = false;
    private ColorSeekBar mColorSeekView;
    private ButtonRoundColorView mCustomButtonView;
    private Makeup mCurrentItem;

    public EditMakeUpView(Context context) {
        this(context, null);
    }

    public EditMakeUpView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public EditMakeUpView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
        initListener();
        initData();
    }

    public void setMakeupComposeData(ArrayList<BeautyData> makeupDataList) {
        mMakeupComposeData = makeupDataList;
        if (mMakeupAdapter != null) {
            changeToMakeupMainMenu();
        }
    }

    public void setMakeupCustomData(ArrayList<MakeupCustomModel> mMakeupCustomData) {
        this.mMakeupCustomData = mMakeupCustomData;
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_edit_make_up, this);
        mMakeupRecyclerView = rootView.findViewById(R.id.beauty_makeup_item_list);
        mMakeupTopLayout = rootView.findViewById(R.id.makeup_top_layout);
//        mMakeupChangeBtn = rootView.findViewById(R.id.change_btn);
//        mMakeupChangeBtnText = rootView.findViewById(R.id.change_btn_text);
        mTvColor = rootView.findViewById(R.id.tv_color);
        mTvAlpha = rootView.findViewById(R.id.tv_alpha);
        mMakeupColorHinLayout = rootView.findViewById(R.id.makeup_color_hint_layout);
//        mColorPickerView = rootView.findViewById(R.id.color_picker_view);
        mColorSeekView = rootView.findViewById(R.id.color_seekBar);
        mCustomButtonView = rootView.findViewById(R.id.custom_btn);
        mMakeupSeekBar = rootView.findViewById(R.id.seek_bar);
        mMakeupSeekBar.setMax(100);
        mMakeupSeekBar.setPointEnable(false);
        mMakeupSeekBar.setBreakProgress(0);

        mMakeUpTab = rootView.findViewById(R.id.makeup_tab);

        mMakeupAdapter = new EditMakeupAdapter(mContext, mMakeupComposeData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mMakeupRecyclerView.setLayoutManager(layoutManager);
        mMakeupRecyclerView.setAdapter(mMakeupAdapter);
        mMakeupAdapter.setEnable(true);

//        mColorSeekView.setOnColorSeekBarStateChangeListener(new EditColorPickerView.OnColorSeekBarStateChangeListener() {
//            @Override
//            public void onColorSeekBarStateChanged(boolean show) {
////                mMakeupColorHinLayout.setVisibility(show ? View.VISIBLE : View.GONE);
//            }
//        });

        mColorSeekView.setOnColorChangedListener(new ColorSeekBar.OnColorChangedListener() {
            @Override
            public void onColorChanged(int co) {

            }

            @Override
            public void onColorChanged(int co, float progress) {
                Log.e("lpf","color-"+co+" progress"+progress);
                mCustomButtonView.setColor(co);

                ColorData colorData = new ColorData(mColorSeekView.rawX, -1, co);
                int color = colorData.color;
                mTvColor.setVisibility(View.VISIBLE);
                mTvColor.setText(String.format(getResources().getString(R.string.make_up_tone), ColorUtil.intColorToHexString(color).toUpperCase()));
                MakeupData makeupData = MakeupManager.getInstacne().getMakeupEffect(mCurrentEffectId);
                if (makeupData == null) {
                    return;
                }
                if (mCurrentItem!=null){
                    mCurrentItem.setColor(co);
                    mCurrentItem.setProgress(progress);
                }
                makeupData.setColorData(colorData);
                MakeupManager.getInstacne().addMakeupEffect(mCurrentEffectId, makeupData);
                float alphaF = (Color.alpha(color) * 1.0f / 255f);
                float red = (Color.red(color) * 1.0f / 255f);
                float green = (Color.green(color) * 1.0f / 255f);
                float blue = (Color.blue(color) * 1.0f / 255f);
                NvsColor nvsColor = new NvsColor(red, green, blue, alphaF);
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupColorChanged(mCurrentEffectId, nvsColor);
                }
            }
        });

        mCustomButtonView.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_no_color_selected, null));
        mCustomButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mColorSeekView.getVisibility() == View.VISIBLE) {
                    mColorSeekView.setVisibility(GONE);
                    mCustomButtonView.setSelected(false);
                    mCustomButtonView.setText("");
                    mCustomButtonView.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_no_color_selected, null));
                } else {
                    mColorSeekView.setVisibility(VISIBLE);
                    mCustomButtonView.setSelected(true);
                    int color=0;
                    if (mCurrentItem!=null){
                         color = mCurrentItem.getColor();
                        if (color==0){
                            color=Color.WHITE;
                        }
                    }else{
                        color=Color.WHITE;
                    }
                    mCustomButtonView.setColor(color);
                    mCustomButtonView.setBitmap(null);
                }
            }
        });
        mMakeupTopLayout.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mMakeupTopLayout.getId() == v.getId() && mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeUpViewDismiss();
                }
                return false;
            }
        });
    }

    private void initListener() {
        mMakeUpTab.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.blue_63));
                }
                tabSelect(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.gray_90));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
//        mColorPickerView.setOnColorChangedListener(new EditColorPickerView.OnColorChangedListener() {
//
//            @Override
//            public void onColorChanged(ColorData colorData) {
//                int color = colorData.color;
//                mTvColor.setVisibility(View.VISIBLE);
//                mTvColor.setText(String.format(getResources().getString(R.string.make_up_tone), ColorUtil.intColorToHexString(color).toUpperCase()));
//                MakeupData makeupData = MakeupManager.getInstacne().getMakeupEffect(mCurrentEffectId);
//                if (makeupData == null) {
//                    return;
//                }
//                makeupData.setColorData(colorData);
//                MakeupManager.getInstacne().addMakeupEffect(mCurrentEffectId, makeupData);
//                float alphaF = (Color.alpha(color) * 1.0f / 255f);
//                float red = (Color.red(color) * 1.0f / 255f);
//                float green = (Color.green(color) * 1.0f / 255f);
//                float blue = (Color.blue(color) * 1.0f / 255f);
//                NvsColor nvsColor = new NvsColor(red, green, blue, alphaF);
//                if (mMakeUpEventListener != null) {
//                    mMakeUpEventListener.onMakeupColorChanged(mCurrentEffectId, nvsColor);
//                }
//            }
//        });

        mMakeupSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {

            @Override
            public void onProgressChange(int progress, boolean fromUser) {
//                if (!fromUser) {
//                    return;
//                }
                if (TextUtils.isEmpty(mCurrentEffectId)) {
                    return;
                }
                MakeupData makeupData = MakeupManager.getInstacne().getMakeupEffect(mCurrentEffectId);
                if (makeupData == null) {
                    return;
                }
                makeupData.setIntensity(progress / 100f);
                String alpha = String.format(getResources().getString(R.string.make_up_transparency), progress) + "%";
                mTvAlpha.setText(alpha);
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupIntensityChanged(mCurrentEffectId, (progress / 100F));
                }
            }
        });

//        mMakeupChangeBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mCategoryInfos.isEmpty()) {
//                    return;
//                }
////                    HashMap<String,String> fxSet = MakeupManager.getInstacne().getMapFxMap();
////                    Set<String> strings = fxSet.keySet();
////                    for (String fxName : strings) {
////                        if (mMakeUpEventListener != null) {
////                            mMakeUpEventListener.removeVideoFxByName(fxName);
////                        }
////                    }
////                    fxSet.clear();
//                mCurrentEffectId = null;
//                // display custom makeup category
//                mMakeUpTab.setVisibility(View.VISIBLE);
//                if (mMakeUpTab.getTabCount() == 0) {
//                    for (int i = 0; i < mCategoryInfos.size(); i++) {
//                        CategoryInfo item = mCategoryInfos.get(i);
//                        item.setDisplayName(Util.upperCaseName(item.getDisplayName()));
//                        TextView textView = new TextView(mContext);
//                        textView.setText(item.getDisplayNameZhCn());
//                        textView.setTextSize(12);
//                        textView.setGravity(Gravity.CENTER);
//                        textView.setTextColor(getResources().getColor(R.color.gray_90));
//                        mMakeUpTab.addTab(mMakeUpTab.newTab().setCustomView(textView));
//                    }
//                }
//                TabLayout.Tab tab = mMakeUpTab.getTabAt(0);
//                if (tab == null) {
//                    return;
//                }
//                if (!tab.isSelected()) {
//                    tab.select();
//                } else {
//                    tabSelect(tab);
//                }
//            } else{
//                // display compose makeup category
//                mMakeUpTab.setVisibility(View.INVISIBLE);
//                changeToMakeupMainMenu();
//            }
//        });
    }

    private void initData() {
        if (mMakeupCustomData.isEmpty()) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            }, 100);
        } else {
            getMaterialListAll(TYPE_MAKEUP, AssetType.MAKEUP_TYPE_ALL, null, mMakeupCustomData.get(0));
        }
//        //获取整妆数据
//        getMaterialListAll(TYPE_COMPOSE, AssetType.MAKEUP_TYPE_ALL, null, null);
        mMakeUpTab.removeAllTabs();
        //获取内置单妆分类
        mCategoryInfos = MakeupManager.getInstacne().getMakeupTab(mContext);
        if (mMakeUpTab.getTabCount() == 0) {
            for (int i = 0; i < mCategoryInfos.size(); i++) {
                CategoryInfo item = mCategoryInfos.get(i);
                item.setDisplayName(Util.upperCaseName(item.getDisplayName()));
                TextView textView = new TextView(mContext);
                textView.setText(item.getDisplayNameZhCn());
                textView.setTextSize(12);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.gray_90));
                mMakeUpTab.addTab(mMakeUpTab.newTab().setCustomView(textView));
            }
        }
        getCustomTab();
    }

    private void tabSelect(TabLayout.Tab tab) {
        if (mMakeupCustomData.isEmpty()) {
            return;
        }
        mCurrentEffectId = mCategoryInfos.get(tab.getPosition()).getDisplayName();
        MakeupCustomModel customModel = mMakeupCustomData.get(tab.getPosition());
        if (customModel == null) {
            return;
        }
        if (customModel.isRequest()) {
            List<BeautyData> makeupData = customModel.getModelContent();
            parseSubCustom(makeupData);
        } else {
            if (tab.getPosition() == 0) {
                getMaterialListAll(TYPE_MAKEUP, AssetType.MAKEUP_TYPE_ALL
                        , null, customModel);
            } else {
                getMaterialListAll(TYPE_CUSTOM,  AssetType.MAKEUP_TYPE_LIST_ALL
                        , String.valueOf(customModel.getId()), customModel);
            }
        }
    }

    /**
     * 切换到主菜单
     */
    private void changeToMakeupMainMenu() {
        MakeupManager.getInstacne().clearMapFxData();
        mMakeupAdapter.setDataList(mMakeupComposeData, EditMakeupAdapter.MAKE_UP_RANDOM_BG_TYPE);
        mMakeupAdapter.notifyDataSetChanged();
        mMakeupAdapter.setEnable(true);
        final int index = MakeupManager.getInstacne().getComposeIndex();
        setColorPickerVisibility(View.INVISIBLE);
        mMakeupAdapter.setSelectPos(index);
        mMakeupAdapter.setOnItemClickListener(new EditMakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != 0) {
                    //这里清除选择的组合美妆
                    MakeupManager.getInstacne().clearCustomData();
                    MakeupManager.getInstacne().setMakeupIndex(0);
                }
                MakeupManager.getInstacne().setComposeIndex(position);
                if (mMakeUpEventListener != null) {
                    mMakeUpEventListener.onMakeupViewDataChanged(mMakeUpTab.getSelectedTabPosition(), position, isClearMakeup);
                }
            }
        });
        mMakeupRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mMakeupRecyclerView.scrollToPosition(Math.max(index, 0));
            }
        });
    }

    /**
     * 切换到子菜单
     *
     * @param data     数据
     * @param backText 返回显示文本
     */
    private void changeToMakeupSubMenu(final List<BeautyData> data, String backText) {
        mMakeupAdapter.setDataList(data, MakeupAdapter.MAKE_UP_ROUND_ICON_TYPE);
        mMakeupAdapter.notifyDataSetChanged();
        // custom makeup select position
        int index = 0;
        setColorPickerVisibility(View.INVISIBLE);
        if (mMakeUpTab.getSelectedTabPosition() != 0) {
            MakeupData makeupData = MakeupManager.getInstacne().getMakeupEffect(mCurrentEffectId);
            if (makeupData != null) {
                String uuid = makeupData.getUuid();
                if (!TextUtils.isEmpty(uuid)) {
                    for (int i = 0; i < data.size(); i++) {
                        Makeup makeup = (Makeup) data.get(i);
                        if (makeup == null) {
                            continue;
                        }
                        MakeupEffectContent content = makeup.getEffectContent();
                        if (content == null) {
                            continue;
                        }
                        List<MakeupArgs> args = content.getMakeupArgs();
                        if (args.isEmpty()) {
                            continue;
                        }
                        if (TextUtils.equals(uuid, args.get(0).getUuid())) {
                            onSubViewSelect(i, data.get(i));
                            index = makeupData.getIndex();
                            break;
                        }
                    }
                }
            }
        } else {
            index = MakeupManager.getInstacne().getMakeupIndex();
        }
        mMakeupAdapter.setSelectPos(index);
        mMakeupAdapter.setEnable(true);
        mMakeupAdapter.setOnItemClickListener(new EditMakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BeautyData selectItem = mMakeupAdapter.getSelectItem();
                if (selectItem instanceof Makeup) {
                    ((Makeup) selectItem).setType(mCurrentEffectId);
                     mCurrentItem = (Makeup) selectItem;
                    if (!mCurrentItem.isIsCompose()) {
                        if (mMakeUpTab.getSelectedTabPosition() == 0) {
                            MakeupManager.getInstacne().setMakeupIndex(position);
                        } else {
//                            MakeupManager.getInstacne().removeMakeupEffect(mCurrentEffectId);
                            if (position == 0) {
                                setColorPickerVisibility(View.INVISIBLE);
                            } else {
                                onSubViewSelect(position, selectItem);
                            }
                        }
                    }
                }
                if (mMakeUpEventListener != null) {
                    MakeupManager.getInstacne().setComposeIndex(0);
                    mMakeUpEventListener.onMakeupViewDataChanged(mMakeUpTab.getSelectedTabPosition(), position, false);
                }

            }
        });
        final int finalIndex = index;
        mMakeupRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mMakeupRecyclerView.scrollToPosition(finalIndex);
            }
        });
    }

    private void parseSubCustom(List<BeautyData> makeupData) {
        if (makeupData.isEmpty()) {
            return;
        }
        for (BeautyData beautyData : makeupData) {
            if (beautyData instanceof Makeup) {
                Makeup item = (Makeup) beautyData;
                if (item.isCustom()) {
                    installCustomAsset(beautyData.getFolderPath());
                }
                MakeupEffectContent content = item.getEffectContent();
                if (content == null) {
                    continue;
                }
                List<MakeupArgs> makeupArgs = content.getMakeupArgs();
                if (makeupArgs.isEmpty()) {
                    continue;
                }
                for (MakeupArgs args : makeupArgs) {
                    if (args == null) {
                        continue;
                    }
                    if (!TextUtils.isEmpty(args.getMakeupUrl()) && beautyData.isBuildIn()) {
                        String makeUpPath = item.getFolderPath() + File.separator + args.getMakeupUrl();
                        if (!makeUpPath.startsWith("/storage/")) {
                            makeUpPath = "assets:/" + makeUpPath;
                        }
                        installMakeupPkg(makeUpPath);
                    }
                }
            }
        }
        changeToMakeupSubMenu(makeupData, mContext.getString(R.string.makeup));
    }

    /**
     * 安装自定义美妆内的文件
     *
     * @param folderPath
     */
    private void installCustomAsset(String folderPath) {
        if (folderPath.startsWith("/storage/")) {
            File file = new File(folderPath);
            if (!file.exists() || !file.isDirectory()) {
                return;
            }
            for (File listFile : file.listFiles()) {
                installNewMakeup(listFile.getAbsolutePath());
            }
        } else {
            try {
                for (String filePath : mContext.getAssets().list(folderPath)) {
                    installNewMakeup("assets:/" + folderPath + File.separator + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

    }

    /**
     * select one type of makeup effect from a custom makeup category
     */
    private void onSubViewSelect(int position, BeautyData selectItem) {
        if (!(selectItem instanceof Makeup)) {
            return;
        }
        if (TextUtils.isEmpty(mCurrentEffectId)) {
            return;
        }
        MakeupData makeupEffect = MakeupManager.getInstacne().getMakeupEffect(mCurrentEffectId);
        if (makeupEffect == null) {
            makeupEffect = new MakeupData(position, DEFAULT_MAKEUP_INTENSITY, new ColorData());
            Makeup makeup = (Makeup) selectItem;
            MakeupEffectContent content = makeup.getEffectContent();
            if (content != null) {
                List<MakeupArgs> makeupArgs = content.getMakeupArgs();
                if (!makeupArgs.isEmpty()) {
                    makeupEffect.setUuid(makeupArgs.get(0).getUuid());
                }
            }
            MakeupManager.getInstacne().addMakeupEffect(mCurrentEffectId, makeupEffect);
        }
        makeupEffect.setIndex(position);
        //set Intensity
        float intensity = makeupEffect.getIntensity();
        String progress = String.format(getResources().getString(R.string.make_up_transparency), (int) (intensity * 100)) + "%";
        mTvAlpha.setText(progress);
        //set current use color
        ColorData colorData = makeupEffect.getColorData();
        if ((colorData != null)) {
            mTvColor.setVisibility((colorData.color == -1) ? INVISIBLE : VISIBLE);
            mTvColor.setText(String.format(getResources().getString(R.string.make_up_tone), ColorUtil.intColorToHexString(colorData.color).toUpperCase()));
        }
        mMakeupSeekBar.setProgress((int) (intensity * 100));

        //set RecommendColor
        Makeup item = (Makeup) selectItem;
        MakeupEffectContent makeupEffectContent = item.getEffectContent();
        if (makeupEffectContent == null) {
            return;
        }
        MakeupArgs makeupArgs = makeupEffectContent.getMakeupArgs().get(0);
        if (makeupArgs == null) {
            return;
        }
        List<MakeupArgs.RecommendColor> recommendColors = makeupArgs.getMakeupRecommendColors();
        if (recommendColors != null && !recommendColors.isEmpty()) {
            int[] colors = new int[recommendColors.size()];
            for (int index = 0; index < recommendColors.size(); index++) {
                colors[index] = splitColor(recommendColors.get(index).getMakeupColor());
            }
//            mColorPickerView.setDefaultColor(colors, colorData);
            mCustomButtonView.setVisibility(VISIBLE);
            mMakeupColorHinLayout.setVisibility(VISIBLE);
            mMakeupSeekBar.setVisibility(VISIBLE);

            if (mCurrentItem!=null){
                int color = mCurrentItem.getColor();
                mCustomButtonView.setColor(color);
                mColorSeekView.setColors(mCurrentItem.getProgress());

                MakeupData makeupData = MakeupManager.getInstacne().getMakeupEffect(mCurrentEffectId);
                if (makeupData == null) {
                    return;
                }
                if (mCurrentItem!=null){
                    mCurrentItem.setColor(color);
                }
                colorData.color=color;
                makeupData.setColorData(colorData);
                MakeupManager.getInstacne().addMakeupEffect(mCurrentEffectId, makeupData);
                float alphaF = (Color.alpha(color) * 1.0f / 255f);
                float red = (Color.red(color) * 1.0f / 255f);
                float green = (Color.green(color) * 1.0f / 255f);
                float blue = (Color.blue(color) * 1.0f / 255f);
                NvsColor nvsColor = new NvsColor(red, green, blue, alphaF);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mMakeUpEventListener != null) {
                            mMakeUpEventListener.onMakeupColorChanged(mCurrentEffectId, nvsColor);
                        }
                    }
                },0);
            }

            mColorSeekView.setVisibility(GONE);
            mCustomButtonView.setSelected(false);
            mCustomButtonView.setText("");
            mCustomButtonView.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_no_color_selected, null));




        }else{
            setColorPickerVisibility(View.INVISIBLE);
        }
    }

    public BeautyData getSelectItem() {
        if (mMakeupAdapter != null) {
            return mMakeupAdapter.getSelectItem();
        }
        return null;
    }

    /**
     * 获取当前应用的单妆
     *
     * @return
     */
    public String getSelectMakeupId() {
        int position = mMakeUpTab.getSelectedTabPosition();
        if ((position == 0) || mCategoryInfos.isEmpty()) {
            return "";
        }
        return mCurrentEffectId;
    }

    public ArrayList<CategoryInfo> getAllMakeupId() {
        return mCategoryInfos;
    }

    public void setColorPickerVisibility(int visibility) {
//        mColorPickerView.setVisibility(visibility);
        mColorSeekView.setVisibility(visibility);
        mCustomButtonView.setVisibility(visibility);

        mMakeupColorHinLayout.setVisibility(visibility);
        mMakeupSeekBar.setVisibility(visibility);
    }

    public void setOnMakeUpEventListener(MakeUpEventListener makeUpEventListener) {
        this.mMakeUpEventListener = makeUpEventListener;
    }

    @Override
    public void onGetAssetListSuccess(List responseArrayList, int assetType, boolean hasNext, String searchKey) {

    }

    @Override
    public void onGetAssetListFailed(Throwable e, int assetType) {
    }

    @Override
    public void onDonwloadAssetProgress(int progress, int assetType, String downloadId) {
    }

    @Override
    public void onDonwloadAssetSuccess(boolean success, String downloadPath, int assetType, String downloadId) {
        if (success) {
            installMakeupPkg(downloadPath);
            MakeupManager.getInstacne().installNewMakeUp(downloadPath);
        }
    }

    @Override
    public void onDonwloadAssetFailed(Throwable e, int assetType, String downloadId) {
    }

    public interface MakeUpEventListener {
        /**
         * 美妆应用
         *
         * @param tabPosition   tab位置
         * @param position      某一类中的position
         * @param isClearMakeup
         */
        void onMakeupViewDataChanged(int tabPosition, int position, boolean isClearMakeup);

        /**
         * 美妆颜色修改
         *
         * @param makeupId 美妆id
         * @param color    颜色
         */
        void onMakeupColorChanged(String makeupId, NvsColor color);

        /**
         * 美妆强度修改
         *
         * @param makeupId  美妆id
         * @param intensity 强度
         */
        void onMakeupIntensityChanged(String makeupId, float intensity);

        /**
         * 移除美妆中自带的特效
         *
         * @param name 特效名称
         */
        void removeVideoFxByName(String name);

        /**
         * 关闭美妆Dialog
         */
        void onMakeUpViewDismiss();
    }

    public int splitColor(String color) {
        String[] split = color.split(",");
        if (split.length == 4) {
            int red = (int) Math.floor(Float.parseFloat(split[0]) * 255 + 0.5D);
            int green = (int) Math.floor(Float.parseFloat(split[1]) * 255 + 0.5D);
            int blue = (int) Math.floor(Float.parseFloat(split[2]) * 255 + 0.5D);
            int alpha = (int) Math.floor(Float.parseFloat(split[3]) * 255 + 0.5D);
            return Color.argb(alpha, red, green, blue);
        }
        return 0;
    }

    /**
     * 获取单妆分类 Tab
     */
    private void getCustomTab() {
        HttpManager.getMaterialTypeAndCategory(null
                , AssetType.MAKEUP_TYPE_LIST_ALL
                , MSApplication.getSdkVersion()
                , new RequestCallback<List<MakeupCategoryInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<MakeupCategoryInfo>> response) {
                        if ((response.getCode() != 1) || (response.getData() == null)) {
                            //ToastUtils.showShort("get makeup tab error");
                            return;
                        }
                        List<MakeupCategoryInfo> data = response.getData();
                        if (data.isEmpty()) {
                            return;
                        }
                        List<CategoryInfo> mTabCategorys = data.get(0).getCategories().get(0).getKinds();
                        if (mTabCategorys.isEmpty()) {
                            return;
                        }
                        if (mCategoryInfos.isEmpty()) {
                            mCategoryInfos.addAll(mTabCategorys);
                        } else {
                            for (CategoryInfo categoryInfo : mTabCategorys) {
                                if (categoryInfo == null) {
                                    return;
                                }
                                boolean isExist = false;
                                for (CategoryInfo info : mCategoryInfos) {
                                    if (categoryInfo.getId() == info.getId()) {
                                        isExist = true;
                                        break;
                                    }
                                }
                                if (!isExist) {
                                    mCategoryInfos.add(categoryInfo);
                                    MakeupCustomModel baseModel = new MakeupCustomModel();
                                    baseModel.setId(categoryInfo.getId());
                                    baseModel.setMakeupId(categoryInfo.getDisplayName());
                                    ArrayList<BeautyData> none = new ArrayList<>();
                                    NoneItem noneItem = new NoneItem();
                                    noneItem.setIsCompose(false);
                                    none.add(noneItem);
                                    baseModel.setModelContent(none);
                                    mMakeupCustomData.add(baseModel);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(BaseResponse<List<MakeupCategoryInfo>> response) {
                        Log.e(TAG, "onError: get http makeup category data error!" + response.toString());
                        //ToastUtils.showShort("get makeup tab error");
                    }
                });
    }


    /**
     * 获取妆容，单妆素材数据
     */
    public void getMaterialListAll(final int type, AssetType assetType, String kind, final MakeupCustomModel model) {
        HttpManager.getMaterialList(null
                , assetType
                , kind
                , 1, NvAsset.AspectRatio_All, ""
                , MSApplication.getSdkVersion()
                , 1, 100
                , new RequestCallback<BaseBean<BaseDataBean<Makeup>>>() {
                    @Override
                    public void onSuccess(BaseResponse<BaseBean<BaseDataBean<Makeup>>> response) {
                        if ((response.getCode() != 1) || (response.getData() == null)) {
                            //ToastUtils.showShort("get makeup data error");
                            if (model != null) {
                                model.setRequest(true);
                                parseSubCustom(model.getModelContent());
                            }
                            return;
                        }
                        BaseBean<BaseDataBean<Makeup>> dataBean = response.getData();
                        if (dataBean == null) {
                            if (model != null) {
                                model.setRequest(true);
                                parseSubCustom(model.getModelContent());
                            }
                            return;
                        }
                        List<BaseDataBean<Makeup>> dataElements = dataBean.getElements();
                        List<BeautyData> localData = MakeupManager.getInstacne().getDownloadMakeupData(mContext);
                        ArrayList<BeautyData> beautyData = new ArrayList<>();
                        if (!dataElements.isEmpty()) {
                            for (BaseDataBean data : dataElements) {
                                Makeup mMakeup = new Makeup();
                                mMakeup.setCover(data.getCoverUrl());
                                mMakeup.setIsBuildIn(false);
                                mMakeup.setName(data.getDisplayNamezhCN());
                                mMakeup.setIsCompose(type == TYPE_COMPOSE);
                                mMakeup.setFolderPath(PathUtils.getAssetDownloadPath(NvAsset.ASSET_MAKEUP));
                                switch (type) {
                                    case TYPE_COMPOSE:
                                        mMakeup.setUuid(data.getId());
                                        String[] packageUrlStr = data.getPackageUrl().split("\\/");
                                        mMakeup.setUrl(packageUrlStr[packageUrlStr.length - 1]);
                                        //下载素材并安装
                                        downloadAndinstallMakeupPkg(mMakeup.getFolderPath(), mMakeup.getUrl(), data.getPackageUrl());
                                        break;
                                    case TYPE_CUSTOM:
                                        Makeup customMakeupInfo = (Makeup) data.getInfoJson();
                                        if (customMakeupInfo == null) {
                                            return;
                                        }
                                        MakeupEffectContent content = new MakeupEffectContent();
                                        List<MakeupArgs> makeupArgs = new ArrayList<>();
                                        MakeupArgs args = new MakeupArgs();
                                        args.setUuid(data.getId());
                                        args.setClassName(customMakeupInfo.getClassName());
                                        args.setType(customMakeupInfo.getMakeupId());
                                        String[] customStr = data.getPackageUrl().split("\\/");
                                        args.setMakeupUrl(mMakeup.getFolderPath() + File.separator + customStr[customStr.length - 1]);
                                        args.setMakeupRecommendColors(customMakeupInfo.getMakeupRecommendColors());
                                        makeupArgs.add(args);
                                        content.setMakeupArgs(makeupArgs);
                                        mMakeup.setEffectContent(content);
                                        //下载素材并安装
                                        downloadAndinstallMakeupPkg(mMakeup.getFolderPath(), args.getMakeupUrl(), data.getPackageUrl());
                                        break;
                                    case TYPE_MAKEUP:
                                        Makeup makeupInfo = (Makeup) data.getInfoJson();
                                        if (makeupInfo == null) {
                                            return;
                                        }
                                        MakeupEffectContent effectContent = makeupInfo.getEffectContent();
                                        if (effectContent == null) {
                                            return;
                                        }
                                        mMakeup.setUuid(data.getId());
                                        mMakeup.setEffectContent(effectContent);
                                        List<MakeupArgs> makeupArgsList = effectContent.getMakeupArgs();
                                        if ((makeupArgsList == null) || (makeupArgsList.isEmpty())) {
                                            return;
                                        }
//                                        for (MakeupArgs args1 : makeupArgsList) {
//                                            if (args1 == null) {
//                                                continue;
//                                            }
//                                            //下载素材并安装
//                                            String packageUrl = args1.getMakeupUrl();
//                                            String[] makeupStr = packageUrl.split("\\/");
//                                            args1.setMakeupUrl(mMakeup.getFolderPath() + File.separator + makeupStr[makeupStr.length - 1]);
//                                            downloadAndinstallMakeupPkg(mMakeup.getFolderPath(), args1.getMakeupUrl(), packageUrl);
//                                        }
                                        mMakeup.setEffectContent(makeupInfo.getEffectContent());

                                        if (localData != null) {
                                            for (BeautyData localDatum : localData) {
                                                if (localDatum != null && localDatum instanceof Makeup) {
                                                    if (TextUtils.equals(((Makeup) localDatum).getUuid(), mMakeup.getUuid())) {
                                                        mMakeup.setLocalFlag(true);
                                                    }
                                                }
                                            }
                                        }
                                        if (!mMakeup.isLocalFlag()) {
                                            downloadMakeupZip(data.getPackageUrl(), mMakeup.getFolderPath());
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                beautyData.add(mMakeup);
                            }
                        }

                        switch (type) {
                            case TYPE_COMPOSE:
                                if (!beautyData.isEmpty()) {
                                    //这里清除内置整妆，以服务器上美妆为准
                                    BeautyData none = mMakeupComposeData.get(0);
                                    mMakeupComposeData.clear();
                                    mMakeupComposeData.add(none);
                                    for (BeautyData bData : beautyData) {
                                        mMakeupComposeData.add(bData);
                                    }
                                    mMakeupAdapter.notifyDataSetChanged();
                                }
                                break;
                            case TYPE_CUSTOM:
                            case TYPE_MAKEUP:
                                if (model == null) {
                                    return;
                                }
                                model.setRequest(true);
                                if (!beautyData.isEmpty()) {
                                    //这里清除内置妆容或单妆，以服务器上美妆为准
                                    BeautyData none = model.getModelContent().get(0);
                                    model.getModelContent().clear();
                                    model.getModelContent().add(none);
                                    if (type == TYPE_MAKEUP) {
                                        model.getModelContent().addAll(MakeupManager.getInstacne().getMakeupData(mContext, true));
                                    } else {
                                        model.getModelContent().addAll(MakeupManager.getInstacne().getCustomMakeupDataListByPath(mContext, mCurrentEffectId));
                                    }
                                    List<BeautyData> modelContent = model.getModelContent();
                                    //todo 本地已下载与网络去重逻辑需指定
                                    for (BeautyData bData : beautyData) {
                                        boolean add = true;
                                        if (type == TYPE_MAKEUP) {
                                            for (BeautyData data : modelContent) {
                                                if (data instanceof Makeup && bData instanceof Makeup) {
                                                    if (TextUtils.equals(((Makeup) data).getUuid(), ((Makeup) bData).getUuid())) {
                                                        add = false;
                                                    }
                                                }
                                            }
                                            //todo 写好网络再删除
                                        }
                                        if (add) {
                                            model.getModelContent().add(bData);
                                        }
                                    }
                                }
                                parseSubCustom(model.getModelContent());
                                break;
                            default:
                                break;
                        }

                    }

                    @Override
                    public void onError(BaseResponse<BaseBean<BaseDataBean<Makeup>>> response) {
                        //ToastUtils.showShort("get makeup data error");
                        if (type == TYPE_COMPOSE) {
                            return;
                        }
                        if (model != null) {
                            model.setRequest(true);
                            parseSubCustom(model.getModelContent());
                        }
                    }
                });
    }

    /**
     * 下载美妆包
     *
     * @param packageUrl
     * @param folderPath
     */
    private void downloadMakeupZip(String packageUrl, String folderPath) {
        String[] split = packageUrl.split("/");
        String fileName = split[split.length - 1];
        File file = new File(folderPath + File.separator + fileName.split("\\.")[0]);
        if (file.exists()) {
            //todo 下载过 不严谨，同名未排除，要设定完整的去重机制
            return;
        }
        HttpManager.download(packageUrl, packageUrl, folderPath, fileName, new SimpleDownListener(packageUrl) {
            @Override
            public void onStart(Progress progress) {

            }

            @Override
            public void onProgress(Progress progress) {

            }

            @Override
            public void onFinish(File file, Progress progress) {
                // 解压 以及安装
                try {
                    List<File> files = ZipUtils.unzipFile(file, new File(folderPath + File.separator + fileName.split("\\.")[0]));
                    if (files != null && !files.isEmpty()) {
                        FileUtils.deleteFile(file);
                        for (File filePath : files) {
                            installNewMakeup(filePath.getAbsolutePath());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    private void installNewMakeup(String filePath) {
        if (filePath.endsWith(".makeup")) {
            NvAssetManager.sharedInstance().installAssetPackage(
                    filePath,
                    NvAsset.ASSET_MAKEUP, true);
        } else if (filePath.endsWith(".warp")) {
            NvAssetManager.sharedInstance().installAssetPackage(
                    filePath,
                    NvAsset.ASSET_MAKEUP_WARP, true);
        } else if (filePath.endsWith(".facemesh")) {
            NvAssetManager.sharedInstance().installAssetPackage(
                    filePath,
                    NvAsset.ASSET_MAKEUP_FACE, true);
        } else if (filePath.endsWith(".videofx")) {
            NvAssetManager.sharedInstance().installAssetPackage(
                    filePath,
                    NvAsset.ASSET_FILTER, true);
        }
        Log.d(TAG, "installNewMakeup :" + filePath);
    }

    /**
     * 下载
     *
     * @param packageUrl 文件路径
     */
    private void downloadAndinstallMakeupPkg(String folderPath, String localPath, String packageUrl) {
        if (TextUtils.isEmpty(folderPath) || TextUtils.isEmpty(localPath) || TextUtils.isEmpty(packageUrl)) {
            return;
        }
        File packageFile = new File(localPath);
        if (packageFile.exists()) {
            installMakeupPkg(packageFile.getAbsolutePath());
            return;
        }
        String assetDownloadDestPath = localPath + ".tmp";
        NvHttpRequest.sharedInstance().downloadAsset(packageUrl
                , folderPath
                , assetDownloadDestPath
                , ".makeup"
                , EditMakeUpView.this, NvAsset.ASSET_MAKEUP, localPath);
    }

    /**
     * 安装
     *
     * @param url 安装路径
     */
    private void installMakeupPkg(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        NvAssetManager.sharedInstance().installAssetPackage(
                url,
                NvAsset.ASSET_MAKEUP, true);
    }


}
