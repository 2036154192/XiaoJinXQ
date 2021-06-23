package com.example.myapplication.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {

    private static final String TAG = "DetailListAdapter";
    private List<Track> mData = new ArrayList<>();
    //格式化时间
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:dd");
    private OnClickList mOnClickList = null;
    private ItemLongClickListener mItemLongClickListener = null;

    @NonNull
    @NotNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, parent, false);
        return new InnerHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DetailListAdapter.InnerHolder holder, int position) {

        TextView ordetTv = holder.itemView.findViewById(R.id.order_text);
        TextView titleTv = holder.itemView.findViewById(R.id.detail_item_title);
        TextView playCountTv = holder.itemView.findViewById(R.id.detail_item_play_count);
        TextView durationTv = holder.itemView.findViewById(R.id.detail_item_duration);
        TextView updateDataTv = holder.itemView.findViewById(R.id.detail_item_update_time);

        Track track = mData.get(position);
        ordetTv.setText((position+1)+"");
        titleTv.setText(track.getTrackTitle()+"");
        playCountTv.setText(track.getPlayCount()+"");
        int durationMin  = track.getDuration() * 1000;
        durationTv.setText( mDateFormat.format(durationMin));
        String updateTime = mSimpleDateFormat.format(track.getUpdatedAt());
        updateDataTv.setText(updateTime);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickList != null) {

                    mOnClickList.onClickLists(mData,position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(track);
                }
                return true;
            }
        });
        }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+mData.size());
        return mData.size();
    }

    public void setData(List<Track> tracks) {
        mData.clear();
        mData.addAll(tracks);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    public void setOnclickList(OnClickList list){
        this.mOnClickList = list;
    }

    public interface OnClickList{
        void onClickLists(List<Track> data, int position);
    }

    public void setItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    public interface ItemLongClickListener{
        void onItemLongClick(Track track);
    }

}
