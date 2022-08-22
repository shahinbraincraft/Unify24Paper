package com.meicam.effectsdkdemo.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meicam.effectsdkdemo.R;

import java.util.ArrayList;


/**
 * @author ms
 */
public class BeautyShapeAdapter extends RecyclerView.Adapter<BeautyShapeAdapter.ViewHolder> {

    private ArrayList<BeautyShapeDataItem> mDataList;
    private int mSelectedPos = -1;
    private Context mContext;
    private OnItemClickListener mClickListener;

    public BeautyShapeAdapter(Context context, ArrayList<BeautyShapeDataItem> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext( )).inflate(R.layout.beauty_shape_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BeautyShapeDataItem item = mDataList.get(position);
        holder.shape_icon.setImageResource(item.resId);
        holder.shape_name.setText(item.name);
        if (mSelectedPos == position) {
            holder.shape_name.setTextColor(Color.parseColor("#CC4A90E2"));
            holder.shape_icon_layout.setAlpha(1.0f);
            holder.shape_name.setAlpha(1.0f);
            GradientDrawable background = (GradientDrawable) holder.shape_icon_layout.getBackground( );
            background.setColor(mContext.getResources( ).getColor(R.color.black25PercentColor));
        } else {
            GradientDrawable background = (GradientDrawable) holder.shape_icon_layout.getBackground( );
            background.setColor(mContext.getResources( ).getColor(R.color.white));
            holder.shape_name.setTextColor(Color.WHITE);
            holder.shape_icon_layout.setAlpha(1.0f);
            holder.shape_name.setAlpha(1.0f);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                notifyItemChanged(mSelectedPos);
                mSelectedPos = position;
                notifyItemChanged(mSelectedPos);
                if (mClickListener != null) {
                    mClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size( );
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout shape_icon_layout;
        private ImageView shape_icon;
        private TextView shape_name;


        public ViewHolder(View view) {
            super(view);
            shape_icon_layout = (RelativeLayout) view.findViewById(R.id.shape_icon_layout);
            shape_icon = (ImageView) view.findViewById(R.id.shape_icon);
            shape_name = (TextView) view.findViewById(R.id.shape_txt);
        }
    }

}
