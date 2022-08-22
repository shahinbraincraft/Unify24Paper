package com.meicam.effectsdkdemo.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.meicam.effectsdkdemo.Constants;
import com.meicam.effectsdkdemo.R;
import com.meicam.effectsdkdemo.data.ColorTypeItem;
import com.meicam.effectsdkdemo.view.CenterHorizontalView;

import java.util.ArrayList;
import java.util.List;

public class ColorListAdapter extends RecyclerView.Adapter<ColorListAdapter.ViewHolder> implements CenterHorizontalView.IHorizontalView {
    private Context mContext;
    private OnItemClickListener mClickListener;
    private List<ColorTypeItem> mColorTypeList = new ArrayList<>();


    private View mView;

    private int mSelectPos;
    private boolean fromScrollEvent;

    @Override
    public View getItemView() {
        return mView;
    }

    @Override
    public void onViewSelected(boolean isSelected, int pos, RecyclerView.ViewHolder holder, int itemWidth) {
        ColorTypeItem itemData = mColorTypeList.get(pos);
        if(itemData == null) {
            return;
        }
        if(isSelected) {
            mSelectPos = pos;

            if(itemData.getColorTypeName().equals(Constants.FX_COLOR_PROPERTY_BASIC)) {
                ((ViewHolder)holder).item_icon.setImageResource(itemData.isSelected()? R.mipmap.icon_adjust_ai_select :R.mipmap.icon_adjust_ai_unselect);
            } else {
                ((ViewHolder)holder).item_icon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_select_background));
            }

            if(mClickListener != null) {
                mClickListener.onItemClick(null, itemData);
            }
        } else {
            if(itemData.getColorTypeName().equals(Constants.FX_COLOR_PROPERTY_BASIC)) {
                itemData.setSelected(false);
            }
            ((ViewHolder)holder).item_icon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.fx_item_radius_shape_unselect));
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, ColorTypeItem colorTypeItem);
    }

    public ColorListAdapter(Context context, List<ColorTypeItem> colorTypeList) {
        mContext = context;
        this.mColorTypeList = colorTypeList;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView item_name;
        private ImageView item_icon;
        public ViewHolder(View view) {
            super(view);
            item_name = (TextView) view.findViewById(R.id.name);
            item_icon = (ImageView) view.findViewById(R.id.iv_adjust_icon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_type, parent,false);
        ViewHolder holder = new ViewHolder(mView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int pos) {

        holder.itemView.setTag(pos);
        final ColorTypeItem colorTypeItem = mColorTypeList.get(pos);
        if(colorTypeItem == null) {
            return;
        }
        holder.item_name.setText(colorTypeItem.getColorAtrubuteText());
        if(colorTypeItem.getColorTypeName().equals(Constants.FX_COLOR_PROPERTY_BASIC)) {
            holder.item_icon.setImageResource(colorTypeItem.isSelected()? R.mipmap.icon_adjust_ai_select :R.mipmap.icon_adjust_ai_unselect);
        } else {
            holder.item_icon.setImageResource(colorTypeItem.getmImageId());
        }

        if(mSelectPos == pos) {
            holder.item_icon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_select_background));
            if(fromScrollEvent && mClickListener != null) {
                fromScrollEvent = false;
                mClickListener.onItemClick(holder.itemView, colorTypeItem);
            }
        } else {
            holder.item_icon.setBackground(ContextCompat.getDrawable(mContext, R.drawable.fx_item_radius_shape_unselect));
        }

//        if(colorTypeItem.isSelected()) {
//            holder.item_name.setTextColor(ContextCompat.getColor(mContext, R.color.ms994a90e2));
//        } else {
//            holder.item_name.setTextColor(ContextCompat.getColor(mContext, R.color.ccffffff));
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getAdapterPosition() < 1) {
                    return;
                }
                int position = holder.getAdapterPosition() - 1;
                mSelectPos = position;

                ColorTypeItem itemData = mColorTypeList.get(position);

                if(itemData.getColorTypeName().equals(Constants.FX_COLOR_PROPERTY_BASIC)) {
                    boolean isSelect = !itemData.isSelected();
                    itemData.setSelected(isSelect);
                    holder.item_icon.setImageResource(isSelect? R.mipmap.icon_adjust_ai_select :R.mipmap.icon_adjust_ai_unselect);
                    holder.item_icon.setBackground(ContextCompat.getDrawable(mContext, isSelect ? R.drawable.shape_select_background : R.drawable.fx_item_radius_shape_unselect));
                }
                notifyDataSetChanged();

                if(mClickListener != null) {
                    mClickListener.onItemClick(view, itemData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mColorTypeList.size();
    }
}
