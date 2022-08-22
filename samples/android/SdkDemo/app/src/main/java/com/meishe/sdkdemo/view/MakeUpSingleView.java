package com.meishe.sdkdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.meishe.sdkdemo.capture.MakeupAdapter;
import com.meishe.sdkdemo.edit.view.VerticalSeekBar;
import com.meishe.sdkdemo.utils.FileUtils;
import com.meishe.sdkdemo.utils.PathUtils;
import com.meishe.sdkdemo.utils.Util;
import com.meishe.sdkdemo.utils.asset.NvAsset;
import com.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meishe.sdkdemo.utils.asset.NvHttpRequest;
import com.meishe.utils.ColorUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * All rights Reserved, Designed By www.meishesdk.com
 * 移植于MakeUpView
 *
 * @Author: Guijun
 * @CreateDate: 2021/07/06 11:29
 * @Description: 美妆View 单美妆view
 * @Copyright: www.meishesdk.com Inc. All rights reserved.
 */
public class MakeUpSingleView extends RelativeLayout implements NvHttpRequest.NvHttpRequestListener {
    public static final float DEFAULT_MAKEUP_INTENSITY = 0.6f;
    private static final String TAG = "MakeUpView";
    private static final int TYPE_CUSTOM = 0x200;
    private static final int TYPE_MAKEUP = 0x300;
    private boolean isFaceModel106 = (BuildConfig.FACE_MODEL == 106);
    private Context mContext;
    private RecyclerView mMakeupRecyclerView;
    private TextView mTvColor, mTvAlpha;
    private View mMakeupColorHinLayout, mMakeupTopLayout;
    private ColorPickerView mColorPickerView;
    private VerticalIndicatorSeekBar mMakeupSeekBar;
    private TabLayout mMakeUpTab;
    private MakeupAdapter mMakeupAdapter;
    private MakeUpEventListener mMakeUpEventListener;
    /**
     * 单妆分类
     */
    private ArrayList<CategoryInfo> mCategoryInfos = new ArrayList<>();
    /**
     * 单妆，包括妆容
     */
    private ArrayList<MakeupCustomModel> mMakeupCustomData = new ArrayList<>();

    private String mCurrentEffectId = null;

    private int pageNum = 1;
    private int pageSize = 10;


    /**
     * as customCategory--> eyeshadow
     */

    private boolean isClearMakeup = false;
    private String mMakeupUUID;

    public MakeUpSingleView(Context context) {
        this(context, null);
    }

    public MakeUpSingleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public MakeUpSingleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
        initData();
        initListener();
    }

    public void setMakeupCustomData(ArrayList<MakeupCustomModel> mMakeupCustomData) {
        this.mMakeupCustomData = mMakeupCustomData;
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_make_up, this);
        mMakeupRecyclerView = rootView.findViewById(R.id.beauty_makeup_item_list);
        mMakeupTopLayout = rootView.findViewById(R.id.makeup_top_layout);
        mTvColor = rootView.findViewById(R.id.tv_color);
        mTvAlpha = rootView.findViewById(R.id.tv_alpha);
        mMakeupColorHinLayout = rootView.findViewById(R.id.makeup_color_hint_layout);
        mColorPickerView = rootView.findViewById(R.id.color_picker_view);
        mMakeupSeekBar = rootView.findViewById(R.id.seek_bar);
        mMakeUpTab = rootView.findViewById(R.id.makeup_tab);

        mMakeupAdapter = new MakeupAdapter(mContext, new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mMakeupRecyclerView.setLayoutManager(layoutManager);
        mMakeupRecyclerView.setAdapter(mMakeupAdapter);
        mMakeupAdapter.setEnable(true);
        findViewById(R.id.change_layout).setVisibility(GONE);
        findViewById(R.id.line).setVisibility(GONE);
        mColorPickerView.setOnColorSeekBarStateChangeListener(new ColorPickerView.OnColorSeekBarStateChangeListener() {
            @Override
            public void onColorSeekBarStateChanged(boolean show) {
                mMakeupColorHinLayout.setVisibility(show ? View.VISIBLE : View.GONE);
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

    /**
     * 调用位置挪了一下
     */
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
        mColorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {

            @Override
            public void onColorChanged(ColorData colorData) {
                int color = colorData.color;
                mTvColor.setVisibility(View.VISIBLE);
                mTvColor.setText(String.format(getResources().getString(R.string.make_up_tone), ColorUtil.intColorToHexString(color).toUpperCase()));
                MakeupData makeupData = MakeupManager.getInstacne().getMakeupPackageEffect(mCurrentEffectId, mMakeupUUID);
                if (makeupData == null) {
                    return;
                }
                makeupData.setColorData(colorData);
//                MakeupManager.getInstacne().addMakeupPackageEffect(mCurrentEffectId, makeupData);
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
        mMakeupSeekBar.setOnSeekBarChangedListener(new VerticalIndicatorSeekBar.OnSeekBarChangedListener() {

            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                if (TextUtils.isEmpty(mCurrentEffectId)) {
                    return;
                }
                MakeupData makeupData = MakeupManager.getInstacne().getMakeupPackageEffect(mCurrentEffectId, mMakeupUUID);
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

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {

            }
        });
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
                getMaterialListAll(TYPE_CUSTOM, AssetType.MAKEUP_TYPE_LIST_ALL
                        , String.valueOf(customModel.getId()), customModel);
            }
        }
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
        setColorPickerVisibility(View.INVISIBLE, null);
        if (mMakeUpTab.getSelectedTabPosition() > 0) {
            MakeupData makeupData = MakeupManager.getInstacne().getMakeupPackageEffect(mCurrentEffectId, mMakeupUUID);
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
        mMakeupAdapter.setOnItemClickListener(new MakeupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BeautyData selectItem = mMakeupAdapter.getSelectItem();
                if (selectItem instanceof Makeup) {
                    Makeup item = (Makeup) selectItem;
                    if (!item.isIsCompose()) {
                        if (mMakeUpTab.getSelectedTabPosition() == 0) {
                            MakeupManager.getInstacne().setMakeupIndex(position);
                        } else {
//                            MakeupManager.getInstacne().removeMakeupEffect(mCurrentEffectId);
                            setColorPickerVisibility(View.INVISIBLE, item);
                            if (position != 0) {
                                onSubViewSelect(position, selectItem);
                            }
                        }
                    }
                    if (((Makeup) selectItem).isCustom()) {
                        installCustomAsset(selectItem.getFolderPath());
                    }
                }
                if (mMakeUpEventListener != null) {
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
        if (((Makeup) selectItem).getEffectContent() != null) {
            List<MakeupArgs> makeupArgs = ((Makeup) selectItem).getEffectContent().getMakeupArgs();
            if (makeupArgs != null && !makeupArgs.isEmpty()) {
                mMakeupUUID = makeupArgs.get(0).getUuid();
            }
        }

        MakeupData makeupEffect = MakeupManager.getInstacne().getMakeupPackageEffect(mCurrentEffectId, mMakeupUUID);
        if (makeupEffect == null) {
            makeupEffect = new MakeupData(position, DEFAULT_MAKEUP_INTENSITY, new ColorData());
            Makeup makeup = (Makeup) selectItem;
            MakeupEffectContent content = makeup.getEffectContent();
            if (content != null) {
                List<MakeupArgs> makeupArgs = content.getMakeupArgs();
                if (!makeupArgs.isEmpty()) {
                    makeupEffect.setUuid(mMakeupUUID);
                }
            }
            MakeupManager.getInstacne().addMakeupPackageEffect(mCurrentEffectId, makeupEffect);
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
            mColorPickerView.setDefaultColor(colors, colorData);
            setColorPickerVisibility(View.VISIBLE, item);
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

    public void setColorPickerVisibility(int visibility, Makeup makeup) {
        boolean colorFlag = false;
        if (makeup != null && makeup.getMakeupRecommendColors() != null && !makeup.getMakeupRecommendColors().isEmpty()) {
            colorFlag = true;
        }
        mColorPickerView.setVisibility(colorFlag ? visibility : INVISIBLE);
        mMakeupColorHinLayout.setVisibility(colorFlag ? visibility : INVISIBLE);
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

    private List<BeautyData> makeupComposeData;

    public void setMakeupComposeData(List<BeautyData> makeupComposeData) {
        this.makeupComposeData = makeupComposeData;
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
                        BaseBean<BaseDataBean<Makeup>> dataBean = response.getData();
                        if ((response.getCode() != 1) || (dataBean == null)) {
                            //ToastUtils.showShort("get makeup data error");
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
                                mMakeup.setIsCompose(false);
                                mMakeup.setFolderPath(PathUtils.getAssetDownloadPath(NvAsset.ASSET_MAKEUP));
                                switch (type) {
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
                                        mMakeup.setMakeupRecommendColors(customMakeupInfo.getMakeupRecommendColors());
                                        //下载素材并安装
                                        downloadAndinstallMakeupPkg(mMakeup.getFolderPath(), args.getMakeupUrl(), data.getPackageUrl());
                                        break;
                                    case TYPE_MAKEUP:
                                        Makeup makeupInfo = (Makeup) data.getInfoJson();
                                        if (makeupInfo == null) {
                                            return;
                                        }
                                        mMakeup.setUuid(data.getId());
                                        mMakeup.setEffectContent(makeupInfo.getEffectContent());
                                        //todo 改为主动下载(检查本地)
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
                        if (model != null) {
                            model.setRequest(true);
                            model.getModelContent().clear();
                            model.getModelContent().addAll(MakeupManager.getInstacne().getMakeupData(mContext, true));
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
                , MakeUpSingleView.this, NvAsset.ASSET_MAKEUP, localPath);
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
