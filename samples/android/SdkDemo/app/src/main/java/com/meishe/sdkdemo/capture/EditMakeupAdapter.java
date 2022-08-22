package com.meishe.sdkdemo.capture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.meishe.modulemakeupcompose.makeup.BeautyData;
import com.meishe.sdkdemo.R;
import com.meishe.sdkdemo.edit.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;


public class EditMakeupAdapter extends RecyclerView.Adapter<EditMakeupAdapter.ViewHolder> {

    /*单装*/
    public static final int MAKE_UP_RANDOM_BG_TYPE = 101;
    public static final int MAKE_UP_WHITE_BG_TYPE = 102;
    /*妆容或者单装*/
    public static final int MAKE_UP_ROUND_ICON_TYPE = 103;
    private List<BeautyData> mDataList;
    private int mSelectedPos = Integer.MAX_VALUE;
    private Context mContext;
    private OnItemClickListener mClickListener;
    private boolean mIsEnable = true;
    private boolean mIsFirstLoad = true;
    private int mViewType = MAKE_UP_RANDOM_BG_TYPE;

    public EditMakeupAdapter(Context context, ArrayList<BeautyData> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    public void setDataList(List<BeautyData> data, int viewType) {
        this.mDataList = data;
        this.mViewType = viewType;
    }

    public List<BeautyData> getDataList() {
        return mDataList;
    }

    public void setEnable(boolean enable) {
        mIsEnable = enable;
        notifyDataSetChanged();
    }

    public void setSelectPos(int pos) {
        mSelectedPos = pos;
        notifyDataSetChanged();
    }

    public int getSelectPos() {
        return mSelectedPos;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public BeautyData getSelectItem() {
        if (mDataList != null && mSelectedPos >= 0 && mSelectedPos < mDataList.size()) {
            return mDataList.get(mSelectedPos);
        }
        return null;
    }

    public void updateDataList(ArrayList dataList) {
        mDataList.clear();
        mDataList.addAll(dataList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_make_up, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        final int position = pos;
        BeautyData item = mDataList.get(position);
        String path = (String) item.getImageResource();
        if (!path.startsWith("/storage/") && !path.startsWith("http")) {
            path = "file:///android_asset/" + path;
        }
        RequestBuilder requestBuilder = Glide.with(mContext.getApplicationContext()).load(path);
        if (holder.getItemViewType() == MAKE_UP_ROUND_ICON_TYPE) {
//            requestBuilder.apply(mOptions);
        }
        requestBuilder.into(holder.makeup_imageAsset);
        holder.makeup_text.setText(item.getName(mContext));
        if (mIsEnable) {
            if (mSelectedPos == position) {
                holder.makeup_imageMask.setVisibility(View.VISIBLE);
                if (!mIsFirstLoad && mViewType == MAKE_UP_RANDOM_BG_TYPE) {
                    holder.makeup_item_layout.setSelected(true);
                }
                if (mIsFirstLoad) {
                    mIsFirstLoad = false;
                }
            } else {
                if (holder.makeup_item_layout.isSelected()) {
                    holder.makeup_item_layout.setSelected(false);
                }
                holder.makeup_imageMask.setVisibility(View.GONE);
            }
        } else {
            holder.makeup_imageMask.setVisibility(View.GONE);
        }

        holder.makeup_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsEnable) {
                    return;
                }
                if (mClickListener != null) {
                    mSelectedPos = position;
                    notifyDataSetChanged();
                    mClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private View makeup_item_layout;
        private RoundImageView makeup_imageAsset;
        private ImageView makeup_imageMask;
        private TextView makeup_text;

        public ViewHolder(View view) {
            super(view);
            makeup_item_layout = view.findViewById(R.id.makeup_item_layout);
            makeup_imageAsset = (RoundImageView) view.findViewById(R.id.makeup_imageAsset);
            makeup_imageMask = (ImageView) view.findViewById(R.id.makeup_icon_mask);
            makeup_text = (TextView) view.findViewById(R.id.makeup_text);
        }
    }

}
