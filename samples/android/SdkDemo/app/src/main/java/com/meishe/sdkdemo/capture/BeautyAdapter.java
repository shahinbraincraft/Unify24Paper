package com.meishe.sdkdemo.capture;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.meishe.base.utils.CommonUtils;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 美颜
 */
public class BeautyAdapter extends RecyclerView.Adapter<BeautyAdapter.ViewHolder> {

    /**
     * 真实的数据实体类，控制UI显示
     */
    private List<BeautyShapeDataItem> mDataList;
    private int mSelectedPos = Integer.MAX_VALUE;
    private int mStrengthSelectPos = 2;
    private Context mContext;
    private OnItemClickListener mClickListener;
    private boolean mIsEnable = true;
    //判断是否是美型
    private boolean isBeautyShape = false;
    private boolean needFirstBack = false;

    /**
     * 两种类型
     * 1.美型的类型，点击返回选择类型的列表
     * 2.美型的item操作，点击设置值
     */
    public static final int TYPE_KIND_BACK = 1;
    public static final int TYPE_KIND_ITEM = 2;

    /**
     * 点类型
     */
    public static final int TYPE_POINT = 3;
    //选中的是哪个类型
    private BeautyShapeDataKindItem shapeDataKindItem;

    private boolean isHandClick = false;
    /*是否处于扩展状态*/
    private boolean isExtendState = false;

    /**
     * 只是存放磨皮的数据
     */
    private List<BeautyShapeDataItem> mTmpBeautyData;
    private Switch mSwitchView;

    public BeautyAdapter(Context context, List<BeautyShapeDataItem> skinDataList,List<BeautyShapeDataItem> dataList) {
        mContext = context;
        mDataList = dataList;
        mTmpBeautyData=skinDataList;
    }

    public void setIsBeautyShape(boolean isBeautyShape) {
        this.isBeautyShape = isBeautyShape;
    }

    public void setDataList(List<BeautyShapeDataItem> data) {

        this.mDataList = data;
    }

    public void setEnable(boolean enable) {
        mIsEnable = enable;
        if (!mIsEnable) {
            hideStrength();
        }
        mSelectedPos = Integer.MAX_VALUE;
        notifyDataSetChanged();
    }


    public void setSwitch(Switch smallShapeSwitch){
        mSwitchView = smallShapeSwitch;
    }

    public void setSelectPos(int pos) {
        mSelectedPos = pos;
        notifyDataSetChanged();
    }

    public int getSelectPos() {
        return mSelectedPos;
    }

    public BeautyShapeDataItem getSelectItem() {
        if (mDataList != null && mSelectedPos >= 0 && mSelectedPos < mDataList.size()) {
            return mDataList.get(mSelectedPos);
        }
        return null;
    }

    public BeautyShapeDataItem getItem(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return null;
        }
        if (mDataList == null) {
            return null;
        }
        for (int i = 0; i < mDataList.size(); i++) {
            BeautyShapeDataItem beautyShapeDataItem = mDataList.get(i);
            if (beautyShapeDataItem == null) {
                continue;
            }
            String name = beautyShapeDataItem.name;
            if (tag.equals(name)) {
                return beautyShapeDataItem;
            }
        }
        return null;
    }

    public List<BeautyShapeDataItem> getItems() {
        return mDataList;
    }

    public void setWittenName(String newName) {
        if (mDataList != null) {
            for (int i = 0; i < mDataList.size(); i++) {
                BeautyShapeDataItem beautyShapeDataItem = mDataList.get(i);
                if (beautyShapeDataItem == null) {
                    continue;
                }
                String name = beautyShapeDataItem.name;
                if (!TextUtils.isEmpty(name) && name.startsWith(mContext.getResources().getString(R.string.whitening))) {
                    beautyShapeDataItem.name = newName;
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    /**
     * 设置选中的美型类型
     *
     * @param shapeDataKindItem
     */
    public void setSelectedKind(BeautyShapeDataKindItem shapeDataKindItem) {
        this.shapeDataKindItem = shapeDataKindItem;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && null != shapeDataKindItem) {
            return TYPE_KIND_BACK;
        } else {
            BeautyShapeDataItem beautyShapeDataItem = mDataList.get(position);
            if (beautyShapeDataItem != null) {
                boolean isPoint = beautyShapeDataItem.isPoint;
                if (isPoint) {
                    return TYPE_POINT;
                }
            }
            return TYPE_KIND_ITEM;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void updateDataList(ArrayList dataList) {
        mDataList.clear();
        mDataList.addAll(dataList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        View view;
        switch (viewType) {
            case TYPE_POINT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beauty_shape_point_item, parent, false);
                holder = new ViewHolder(view);
                break;
            case TYPE_KIND_BACK:
            case TYPE_KIND_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beauty_shape_item, parent, false);
                holder = new ViewHolder(view);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int itemType = getItemViewType(position);
        if (itemType == TYPE_KIND_BACK) {
            //返回键，
            if (null != shapeDataKindItem) {
                int padding = ScreenUtils.dip2px(mContext, 10);
//                holder.shape_icon.setPadding(padding, padding, padding, padding);
                holder.shape_icon.setImageResource(R.mipmap.beauty_back);
                holder.shape_name.setText(shapeDataKindItem.getName());
//                holder.shape_name.setTextColor(Color.BLACK);
//                holder.shape_icon_layout.setBackgroundResource(R.drawable.shape_beauty_menu_select);
                //设置点击事件
                holder.shape_icon_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mClickListener) {
                            mClickListener.onItemClick(v, position, shapeDataKindItem.getName());
                        }
                    }
                });
            }
        } else if (itemType == TYPE_POINT) {
            if (mIsEnable) {
                holder.itemView.setAlpha(1f);
            } else {
                holder.itemView.setAlpha(0.5f);
            }
        } else {
            final BeautyShapeDataItem item = mDataList.get(position);
            if (item.strength!=0){
                holder.blue_point.setVisibility(View.VISIBLE);
            }else{
                holder.blue_point.setVisibility(View.GONE);
            }
            if (position == 0) {
                if (isHandClick) {
                    isHandClick = false;
                    if (item.name.equals(mContext.getResources().getString(R.string.strength))) {
                        holder.shape_icon.setImageResource(R.mipmap.beauty_back);
                        item.name = mContext.getString(R.string.back);
                    } else {
                        holder.shape_icon.setImageResource(R.mipmap.ic_strength);
                        item.name = mContext.getString(R.string.strength);
                    }
                    holder.shape_name.setText(item.name);
                } else {
                    if (!isExtendState) {
                        holder.shape_icon.setImageResource(R.mipmap.ic_strength);
                        item.name = mContext.getString(R.string.strength);
                        holder.shape_name.setText(item.name);
                        boolean hasSkin=false;
                        for (int i = 0; i < mTmpBeautyData.size(); i++) {
                            BeautyShapeDataItem beautyShapeDataItem = mTmpBeautyData.get(i);
                            if (beautyShapeDataItem==null){
                                continue;
                            }
                            if (beautyShapeDataItem.strength!=0){
                                hasSkin=true;
                                break;
                            }
                        }
                        if (hasSkin){
                            holder.blue_point.setVisibility(View.VISIBLE);
                        }else{
                            holder.blue_point.setVisibility(View.GONE);
                        }
                    } else {
                        holder.shape_icon.setImageResource(R.mipmap.beauty_back);
                        item.name = mContext.getString(R.string.back);
                    }

                }

                if (mIsEnable) {
                    holder.shape_icon_layout.setAlpha(1f);
                    holder.shape_name.setAlpha(1f);
                } else {
                    holder.shape_icon_layout.setAlpha(0.5f);
                    holder.shape_name.setAlpha(0.5f);
                }
                holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.ms_disable_color));
            } else {
                holder.shape_icon.setImageResource(item.resId);
                holder.shape_name.setText(item.name);
                if (mIsEnable) {
                    holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.black_alfph));
                } else {
                    holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.ms_disable_color));
                }


                if (mIsEnable && (mSelectedPos == position)) {
                    holder.shape_icon.setSelected(true);
                    holder.shape_name.setTextColor(Color.parseColor("#CC4A90E2"));
                    holder.shape_icon_layout.setAlpha(1.0f);
                    holder.shape_name.setAlpha(1.0f);
                } else {
                    holder.shape_icon.setSelected(false);
                    if (mIsEnable && mSelectedPos != position) {
                        if (isBeautyShape) {
                            holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.black_alfph));
                        } else {
                            holder.shape_name.setTextColor(Color.BLACK);
                        }
                        holder.shape_icon_layout.setAlpha(1.0f);
                        holder.shape_name.setAlpha(0.8f);

                    } else if (!mIsEnable) {
                        holder.shape_name.setTextColor(Color.BLACK);
                        holder.shape_icon_layout.setAlpha(0.5f);
                        holder.shape_name.setAlpha(0.5f);
                    }
                }

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mSwitchView.isChecked()){
                        return;
                    }
                    if (!mIsEnable) {
                        return;
                    }
                    if (item.isPoint) {
                        return;
                    }
                    int tmpPosition = position;
                    if (position == 0) {
                        isHandClick = true;
                        if (item.name.equals(mContext.getResources().getString(R.string.strength))) {
                            showStrength();
                        } else {
                            hideStrength();
                        }
                        notifyDataSetChanged();
                        return;
                    } else {
                        //position 不是0
                        if (isExtendState) {//展开状态
                            //最后一个磨皮的位置
                            int skinLastIndex=4;
                            if (position > skinLastIndex) { //收起磨皮
                                hideStrength();
                                tmpPosition -= skinLastIndex;
                                mSelectedPos = tmpPosition;
                                mClickListener.onItemClick(v, mSelectedPos, mDataList.get(mSelectedPos).name);
                                notifyDataSetChanged();
                                return;
                            } else {
                                mStrengthSelectPos = position;
                            }
                        }
                    }

                    if (mClickListener != null) {
                        notifyItemChanged(mSelectedPos);
                        mSelectedPos = tmpPosition;
                        notifyItemChanged(mSelectedPos);
                        mClickListener.onItemClick(v, tmpPosition, item.name);
                    }
                }
            });
        }
    }

    //展开磨皮
    private void showStrength() {
        if (mTmpBeautyData != null && mTmpBeautyData.size() > 0) {
            for (int i = mTmpBeautyData.size() - 1; i >= 0; i--) {
                BeautyShapeDataItem beautyShapeDataItem = mTmpBeautyData.get(i);
                if (beautyShapeDataItem == null) {
                    continue;
                }
                mDataList.add(1, beautyShapeDataItem);
            }
        }
        isExtendState = true;
        mSelectedPos = 0;
        mClickListener.onItemClick(null, mSelectedPos, mDataList.get(mSelectedPos).name);
        mSelectedPos = mStrengthSelectPos;
        mClickListener.onItemClick(null, mSelectedPos, mDataList.get(mSelectedPos).name);
        notifyDataSetChanged();
    }

    //隐藏磨皮
    private void hideStrength() {
        for (int i = 0; i < mDataList.size(); i++) {
            BeautyShapeDataItem beautyShapeDataItem = mDataList.get(1);
            if (beautyShapeDataItem.name.equals(mContext.getResources().getString(R.string.strength_1))
                    || beautyShapeDataItem.name.equals(mContext.getResources().getString(R.string.advanced_strength_1))
                    || beautyShapeDataItem.name.equals(mContext.getResources().getString(R.string.advanced_strength_2))
                    || beautyShapeDataItem.name.equals(mContext.getResources().getString(R.string.blackPoint))) {
                BeautyShapeDataItem remove = mDataList.remove(1);
                i--;
            }
        }
        mSelectedPos = 0;
        if (mClickListener != null) {
            mClickListener.onItemClick(null, mSelectedPos, mDataList.get(mSelectedPos).name);
        }
        isExtendState = false;
    }

    public BeautyShapeDataItem getSelectedBeautyTempData() {
        if (CommonUtils.isIndexAvailable(mStrengthSelectPos - 1, mTmpBeautyData)) {
            return mTmpBeautyData.get(mStrengthSelectPos - 1);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String name);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout shape_icon_layout;
        private ImageView shape_icon;
        private TextView shape_name;
        private View blue_point;


        public ViewHolder(View view) {
            super(view);
            shape_icon_layout = (RelativeLayout) view.findViewById(R.id.shape_icon_layout);
            shape_icon = (ImageView) view.findViewById(R.id.shape_icon);
            shape_name = (TextView) view.findViewById(R.id.shape_txt);
            blue_point = (View) view.findViewById(R.id.blue_point);
        }
    }


    public int getStrengthSelectPos() {
        return mStrengthSelectPos;
    }
}
