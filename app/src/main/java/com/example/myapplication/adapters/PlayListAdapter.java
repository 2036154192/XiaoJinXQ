package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {

    private List<Track> mData = new ArrayList<>();
    private int playingIndex = 0;
    private SobPopWindow.PlayListItemClickListener mItemClick = null;

    @NonNull
    @NotNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);
        return new InnerHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PlayListAdapter.InnerHolder holder, int position) {
        Track track = mData.get(position);
        TextView trackTitleTv = holder.itemView.findViewById(R.id.track_title_tv);
        trackTitleTv.setText(track.getTrackTitle());
        View playIconView = holder.itemView.findViewById(R.id.play_icon_view);
        playIconView.setVisibility(playingIndex == position ? View.VISIBLE : View.GONE);
        trackTitleTv.setTextColor(BaseApplication.getmContext().getResources()
                .getColor(playingIndex == position ?R.color.second_color:R.color.play_list_text_color));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClick != null) {
                    mItemClick.onItemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPostion(int postion) {
        playingIndex = postion;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(SobPopWindow.PlayListItemClickListener listItemClickListener) {
        this.mItemClick = listItemClickListener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
