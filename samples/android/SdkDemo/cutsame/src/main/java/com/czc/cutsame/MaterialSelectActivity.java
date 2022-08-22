package com.czc.cutsame;

import static com.meicam.sdk.NvsAssetPackageManager.TEIMPLATE_FOOTAGE_TYPE_FREEZE_FRAME;
import static com.meicam.sdk.NvsAssetPackageManager.TEIMPLATE_FOOTAGE_TYPE_IMAGE;
import static com.meicam.sdk.NvsAssetPackageManager.TEIMPLATE_FOOTAGE_TYPE_VIDEO;
import static com.meicam.sdk.NvsAssetPackageManager.TEIMPLATE_FOOTAGE_TYPE_VIDEO_IMAGE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.czc.cutsame.adapter.MediaSelectedAdapter;
import com.czc.cutsame.bean.RatioInfo;
import com.czc.cutsame.bean.Template;
import com.czc.cutsame.bean.TemplateClip;
import com.czc.cutsame.fragment.MediaFragment;
import com.czc.cutsame.util.CustomConstants;
import com.czc.cutsame.util.RatioUtil;
import com.czc.cutsame.view.SelectRatioDialog;
import com.czc.cutsame.view.VideoReverseDialog;
import com.meicam.sdk.NvsAVFileInfo;
import com.meishe.base.adapter.CommonFragmentAdapter;
import com.meishe.base.bean.MediaData;
import com.meishe.base.bean.MediaTag;
import com.meishe.base.model.BaseActivity;
import com.meishe.base.utils.CommonUtils;
import com.meishe.base.utils.LogUtils;
import com.meishe.base.utils.ToastUtils;
import com.meishe.base.view.decoration.ItemDecoration;
import com.meishe.engine.editor.EditorController;
import com.meishe.third.adpater.BaseQuickAdapter;
import com.meishe.third.pop.XPopup;
import com.meishe.third.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;



/**
 * author : lhz
 * date   : 2020/10/20
 * desc   :素材选择页面 Material selection page
 */
public class MaterialSelectActivity extends BaseActivity {
    public final static int TYPE_DEFAULT = 0;
    public final static int TYPE_ONE_FINISH = 1;//选中一个一个就跑 Pick one and run
    private ImageView mIvBack;
    private TextView mTvNext;
    private RecyclerView mRvMediaList;

    private MediaSelectedAdapter mSelectedAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>(3);
    private List<String> mTabTitleList = new ArrayList<>(3);
    private String mTemplateId;
    private Template mTemplate;
    private int mType;
    private List<TemplateClip> mClipList = null;
    private VideoReverseDialog videoReverseDialog = null;
    //倒放成功数量 reverse success num
    private int reverseSuccessNum = 0;
    /**
     * 模板类别
     */
    private String mTemplateType=Template.TYPE_TEMPLATE_STANDER;

    private boolean mIsReplace =false;
    private int mReplaceIndex = -1;

    @Override
    protected int bindLayout() {
        return R.layout.activity_template_material;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            mTemplate = intent.getParcelableExtra(CustomConstants.DATA_TEMPLATE);
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                //类型 type
                mType = bundle.getInt(CustomConstants.SELECTED_TYPE, TYPE_DEFAULT);
                mTemplateType = bundle.getString(CustomConstants.TEMPLATE_TYPE, Template.TYPE_TEMPLATE_STANDER);
                mIsReplace = bundle.getBoolean(CustomConstants.TEMPLATE_IS_REPLACE_TYPE);
                mReplaceIndex = bundle.getInt(CustomConstants.TEMPLATE_REPLACE_CLIP_INDEX);
                if (mType == TYPE_DEFAULT) {
                    //默认类型需要模板路径 The default type requires a template path
                    mClipList = intent.getParcelableArrayListExtra(CustomConstants.TEMPLATE_CLIP_LIST);
                    mTemplateId = intent.getStringExtra(CustomConstants.TEMPLATE_ID);
                } else if (mType == TYPE_ONE_FINISH) {
                    //单选素材，需要片段 Radio selection material, need snippets
                    TemplateClip templateClip = bundle.getParcelable(CustomConstants.TEMPLATE_CLIP);
                    mClipList = new ArrayList<>(1);
                    mClipList.add(templateClip);
                }
            }
        }
        mTabTitleList.clear();
        mTabTitleList.add(getResources().getString(R.string.material_all));
        mTabTitleList.add(getResources().getString(R.string.material_video));
        mTabTitleList.add(getResources().getString(R.string.material_photo));

        mFragmentList.clear();
        mFragmentList.add(MediaFragment.create(MediaData.TYPE_ALL, mMediaListener).setNeedHideTag(mTemplateType.equals(Template.TYPE_TEMPLATE_FREE)));
        mFragmentList.add(MediaFragment.create(MediaData.TYPE_VIDEO, mMediaListener).setNeedHideTag(mTemplateType.equals(Template.TYPE_TEMPLATE_FREE)));
        mFragmentList.add(MediaFragment.create(MediaData.TYPE_PHOTO, mMediaListener).setNeedHideTag(mTemplateType.equals(Template.TYPE_TEMPLATE_FREE)));
        mSelectedAdapter = new MediaSelectedAdapter();
    }

    @Override
    protected void initView() {
        mIvBack = findViewById(R.id.iv_back);
        SlidingTabLayout tabLayout = findViewById(R.id.tl_select_media);
        ViewPager viewPager = findViewById(R.id.vp_select_media);
        // RelativeLayout flSelectedParent = findViewById(R.id.rl_media_selected);
        TextView tvMediaNum = findViewById(R.id.tv_selected_num);
        ImageView imagePoint = findViewById(R.id.image_point);
        mTvNext = findViewById(R.id.tv_next);
        mRvMediaList = findViewById(R.id.rv_selected_list);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new CommonFragmentAdapter(getSupportFragmentManager(), mFragmentList));
        tabLayout.setViewPager(viewPager, mTabTitleList);

        if (mType == TYPE_DEFAULT || mType == TYPE_ONE_FINISH) {
            // 选则列表 Choose the list
            mRvMediaList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            mRvMediaList.addItemDecoration(new ItemDecoration(10, 10));
            mRvMediaList.setAdapter(mSelectedAdapter);
            mTvNext.setEnabled(false);
            if (mClipList == null) {
                LogUtils.e("mClipList == null");
                return;
            }
            if (mTemplateType.equals(Template.TYPE_TEMPLATE_STANDER)){
                mSelectedAdapter.setNewData(mClipList);
                tvMediaNum.setText(String.format(getString(R.string.selected_material_num_hint), mClipList.size()));
            }else if (mTemplateType.equals(Template.TYPE_TEMPLATE_FREE)){
                tvMediaNum.setText("请选择要使用的视频或者图片");
                dealNextDisplay(true);
            }


            if (mType != TYPE_ONE_FINISH) {
                for (TemplateClip templateClip : mClipList) {
                    if (templateClip.isHasGroup()) {
                        tvMediaNum.setText(getString(R.string.selected_same_assets));
                        imagePoint.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        }
        initListener();
    }

    private void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<TemplateClip> list = mSelectedAdapter.getData();
                if(CommonUtils.isEmpty(list)){
                    return;
                }
                final List<Integer> listPosition = new ArrayList<>();
                reverseSuccessNum = 0;
                for (int i = 0; i < list.size(); i++) {
                    TemplateClip clip = list.get(i);
                    if (clip.getMediaType() == MediaData.TYPE_VIDEO && TextUtils.isEmpty(clip.getReversePath()) && clip.getNeedReverse()) {
                        listPosition.add(i);
                    }
                }
                if (listPosition.size() == 0) {
                    if(mType == TYPE_ONE_FINISH){
                        if(list.size()>0){
                            dealFinishResult(list.get(0));
                        }
                    }else{
                        showSelectRatioView();
                    }
                    return;
                }
                //需要反转的视频
                final TemplateClip clip = list.get(listPosition.get(reverseSuccessNum));
                if (videoReverseDialog == null) {
                    videoReverseDialog = (VideoReverseDialog) new XPopup.Builder(MaterialSelectActivity.this).asCustom(new VideoReverseDialog(MaterialSelectActivity.this));
                    videoReverseDialog.setOnConvertListener(new VideoReverseDialog.OnConvertListener() {
                        @Override
                        public void onConvertFinish(String path, boolean convertSuccess) {
                            if (convertSuccess) {
                                reverseSuccessNum++;
                                clip.setReversePath(path);
                                if (reverseSuccessNum == listPosition.size()) {
                                    if(mType == TYPE_ONE_FINISH){
                                       dealFinishResult(clip);
                                    }else{
                                        showSelectRatioView();
                                    }
                                } else {
                                    TemplateClip clip = list.get(listPosition.get(reverseSuccessNum));
                                    videoReverseDialog.setPath(clip.getFilePath());
                                    videoReverseDialog.show();
                                }
                            } else {
                                ToastUtils.showShort(R.string.video_reverse_fail);
                            }
                        }
                    });
                }
                videoReverseDialog.setPath(clip.getFilePath());
                videoReverseDialog.show();
            }
        });
        mSelectedAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(
                    @NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getVisibility() != View.VISIBLE) {
                    return;
                }
                TemplateClip item = mSelectedAdapter.getItem(position);
                if (mTemplateType.equals(Template.TYPE_TEMPLATE_FREE)){
                    mSelectedAdapter.remove(item);
                    dealMediaUnselected(item);
                    return;
                }
                if (item != null) {
                    String filePath = item.getFilePath();
                    mSelectedAdapter.deleteClip(position);//注意此处，item中的关于媒体文件的属性被清空。 Notice here that the properties of the media file in Item are cleared.
                    if (!mSelectedAdapter.hasSameMedia(filePath)) {
                        dealMediaUnselected(item);
                    }
                    if (mSelectedAdapter.getSelectedPosition() >= 0) {
                        mRvMediaList.scrollToPosition(mSelectedAdapter.getSelectedPosition());
                        dealNextDisplay(false);
                    }
                }
            }
        });
    }

    public void showSelectRatioView() {
        int ration = EditorController.getInstance().getAssetPackageSupportedAspectRatio(mTemplateId, EditorController.ASSET_PACKAGE_TYPE_TEMPLATE);
        if (mTemplate != null) {
            int defaultAspectRatio = mTemplate.getDefaultAspectRatio();
            if (defaultAspectRatio == ration) {
                toCutSameEditor(ration);
                return;
            }
            List<RatioInfo> ratioInfos = RatioUtil.getSupportedAspectRatios(ration, mTemplate.getSupportedAspectRatio());
            SelectRatioDialog selectRatioDialog = (SelectRatioDialog) new XPopup.Builder(MaterialSelectActivity.this)
                    .asCustom(new SelectRatioDialog(MaterialSelectActivity.this, ration, ratioInfos, new SelectRatioDialog.OnSelectRatioListener() {
                        @Override
                        public void onSelectRatio(int tag) {
                            toCutSameEditor(tag);
                        }
                    }));
            selectRatioDialog.show();
        } else {
            toCutSameEditor(ration);
        }
    }

    private void toCutSameEditor(int tag) {
        EditorController.getInstance().changeTemplateAspectRatio(mTemplateId, tag);
        Intent it = new Intent(MaterialSelectActivity.this, CutSameEditorActivity.class);
        it.putParcelableArrayListExtra(CustomConstants.TEMPLATE_CLIP_LIST, (ArrayList<? extends Parcelable>) mSelectedAdapter.getData());
        it.putExtra(CustomConstants.TEMPLATE_ID, mTemplateId);
        it.putExtra(CustomConstants.TEMPLATE_TYPE, mTemplateType);
        it.putExtra(CustomConstants.TEMPLATE_RATIO, tag);
        startActivity(it);
        finish();
    }

    @Override
    protected void requestData() {

    }

    /**
     * 处理媒体未选中
     * Handle media unchecked
     */
    private void dealMediaUnselected(TemplateClip clip) {
        if (clip != null) {
            int[] tag = (int[]) clip.getTag();
            for (int i = 0; i < mFragmentList.size(); i++) {
                ((MediaFragment) mFragmentList.get(i)).dealSelectedState(tag[i], false);
                tag[i] = -1;
            }
            if (mTemplateType.equals(Template.TYPE_TEMPLATE_STANDER)){
                dealNextDisplay(false);
            }
        }

    }

    /**
     * 处理媒体选中
     * Handle media selection
     */
    private void dealMediaSelect(TemplateClip clip, MediaData mediaData) {
        if (clip.getTag() == null) {
            clip.setTag(new int[]{-1,-1,-1});
        }
        int[] index = (int[]) clip.getTag();
        MediaTag tag = (MediaTag) mediaData.getTag();//tag为空说明有问题。 A null tag indicates a problem.
        if (MediaData.TYPE_ALL == tag.getType()) {
            index[0] = tag.getIndex();
            index[1] = ((MediaFragment) mFragmentList.get(1)).dealSelected(mediaData.getThumbPath());
            index[2] = ((MediaFragment) mFragmentList.get(2)).dealSelected(mediaData.getThumbPath());
        } else if (MediaData.TYPE_PHOTO == tag.getType()) {
            index[2] = tag.getIndex();
            index[0] = ((MediaFragment) mFragmentList.get(0)).dealSelected(mediaData.getThumbPath());
        } else {
            index[1] = tag.getIndex();
            index[0] = ((MediaFragment) mFragmentList.get(0)).dealSelected(mediaData.getThumbPath());
        }
    }

    /**
     * 处理下一步的展示样式
     * Handle the next presentation style
     */
    private void dealNextDisplay(boolean enable) {
        if (mTvNext.isEnabled() == enable) {
            return;
        }
        if (enable) {
            mTvNext.setBackgroundResource(R.drawable.bg_rectangle_round_red365);
            mTvNext.setTextColor(getResources().getColor(R.color.white));
        } else {
            mTvNext.setBackgroundResource(R.drawable.bg_rectangle_round_gray4b4);
            mTvNext.setTextColor(getResources().getColor(R.color.gray_a4a));
        }
        mTvNext.setEnabled(enable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //判断如果同意的情况下就去把权限请求设置给当前fragment的 Set the request to the current fragment if it is approved
        for (int i = 0; i < mFragmentList.size(); i++) {
            mFragmentList.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (int i = 0; i < mFragmentList.size(); i++) {
            mFragmentList.get(i).onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 处理选择一个素材就finish的结果
     * Handle the result of selecting an ingredient on Finish
     */
    private void dealFinishResult(TemplateClip clip) {
        if (mTemplateType.equals(Template.TYPE_TEMPLATE_FREE)){
            if (mClipList!=null&&mClipList.size()>0){
                TemplateClip templateClip = mClipList.get(0);
                if (templateClip!=null){
                    clip.setInPoint(templateClip.getInPoint());
                }
            }
        }
        Intent it = new Intent();
        it.putExtra(CustomConstants.TEMPLATE_CLIP, clip);
        it.putExtra(CustomConstants.TEMPLATE_REPLACE_CLIP_INDEX, mReplaceIndex);
        setResult(RESULT_OK, it);
        finish();
    }

    private MediaFragment.MediaChangeListener mMediaListener = new MediaFragment.MediaChangeListener() {
        @Override
        public boolean onMediaChange(final MediaData mediaData) {
            final TemplateClip clip = mSelectedAdapter.getItem(mSelectedAdapter.getSelectedPosition());
            NvsAVFileInfo nvsAVFileInfo = EditorController.getInstance().getFileInfo(mediaData.getPath());
            if (nvsAVFileInfo == null) {
                return false;
            }
            long duration = nvsAVFileInfo.getDuration();

            if (mTemplateType.equals(Template.TYPE_TEMPLATE_FREE)){
                List<TemplateClip> list = mSelectedAdapter.getData();
                if (mIsReplace){
                    if (list!=null&&list.size()>0){
                        dealMediaSelect( list.get(0), mediaData);
                    }
                    list.clear();
                }
                int mediaType = mediaData.getType();
                TemplateClip templateClip=new TemplateClip();
                templateClip.setMediaType(mediaType);
                templateClip.setFilePath(mediaData.getPath());
                templateClip.setDuration(mediaType==MediaData.TYPE_PHOTO?4*1000*1000:duration);
                templateClip.setTemplateType(mTemplateType);
                templateClip.setTag(new int[]{-1,-1,-1});
                list.add(templateClip);

                mSelectedAdapter.setSelected(list.size()-1);
                mRvMediaList.scrollToPosition(list.size()-1);
                mSelectedAdapter.notifyDataSetChanged();
//                dealMediaSelect(templateClip, mediaData);
                return true;
            }

            if (clip != null){
                if ((clip.getType() == TEIMPLATE_FOOTAGE_TYPE_VIDEO) || (clip.getType() == TEIMPLATE_FOOTAGE_TYPE_FREEZE_FRAME)) {
                    if (mediaData.getType() == MediaData.TYPE_VIDEO && clip.getDuration() > duration) {
                        //类型匹配但是时长过短 The type matches but the length is too short
                        ToastUtils.showShort(R.string.video_too_short);
                        return false;
                    } else if (mediaData.getType() == MediaData.TYPE_PHOTO) {
                        //类型不匹配 type mismatch
                        ToastUtils.showShort(R.string.clip_need_video);
                        return false;
                    }
                } else if (clip.getType() == TEIMPLATE_FOOTAGE_TYPE_IMAGE) {
                    if (mediaData.getType() == MediaData.TYPE_VIDEO) {
                        //类型不匹配 type mismatch
                        ToastUtils.showShort(R.string.clip_need_photo);
                        return false;
                    }
                } else if (clip.getType() == TEIMPLATE_FOOTAGE_TYPE_VIDEO_IMAGE) {
                    if (mediaData.getType() == MediaData.TYPE_VIDEO && clip.getDuration() > duration) {
                        //类型匹配但是时长过短 The type matches but the length is too short
                        ToastUtils.showShort(R.string.video_too_short);
                        return false;
                    }
                } else {
                    return false;
                }
                // LogUtils.d("type=" + clip.getType());
            } else {
                if (mTemplateType.equals(Template.TYPE_TEMPLATE_STANDER)){
                    //类型错了，这里基本上0概率 Wrong type, basically zero probability here
                    return false;
                }
            }


            if (mType == TYPE_DEFAULT || mType == TYPE_ONE_FINISH) {
                List<TemplateClip> list = mSelectedAdapter.getData();
                if (mediaData.getType() != MediaData.TYPE_PHOTO) {
                    setMediaSelected(clip, mediaData, -1);
                    return true;
                }
                for (int i = 0; i < list.size(); i++) {
                    TemplateClip templateClip = list.get(i);
                    if (templateClip.getFootageId().equals(clip.getFootageId())) {
                        if (TextUtils.isEmpty(templateClip.getFilePath())) {
                            setMediaSelected(templateClip, mediaData, i);
                        }
                    }
                }

            }
            return true;
        }
    };

    public void setMediaSelected(TemplateClip templateClip, MediaData mediaData, int position) {
        templateClip.setMediaType(mediaData.getType());
        if (mTemplateType.equals(Template.TYPE_TEMPLATE_STANDER)){
            dealMediaSelect(templateClip, mediaData);
        }
        if (templateClip.isHasGroup() && position != -1) {
            mSelectedAdapter.setSelected(mediaData, position);
        } else {
            mSelectedAdapter.setSelected(mediaData);
        }
        if (mSelectedAdapter.getSelectedPosition() == -1) {
            dealNextDisplay(true);
            mRvMediaList.scrollToPosition(mSelectedAdapter.getData().size() - 1);
        } else {
            mRvMediaList.scrollToPosition(mSelectedAdapter.getSelectedPosition());
        }
    }
}
