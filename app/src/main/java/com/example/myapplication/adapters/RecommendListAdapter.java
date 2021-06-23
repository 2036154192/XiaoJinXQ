package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {

    private List<Album> mData = new ArrayList<>();
    private onRecomendItemClickLister monRecomendItemClickLister = null;
    private onAlbumItemLongClickLister mLongClickLister = null;

    @NonNull
    @NotNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull InnerHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monRecomendItemClickLister != null) {
                    int clickPosition = (int) v.getTag();
                    monRecomendItemClickLister.onItemClick(clickPosition,mData.get(clickPosition));
                }
            }
        });
        holder.setData(mData.get(position));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //返回true表示消费掉该事件
                if (mLongClickLister != null) {
                    int clickPosition = (int) v.getTag();
                    mLongClickLister.onItemLongClick(mData.get(clickPosition));
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albums) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albums);
        }
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            ImageView imageView = itemView.findViewById(R.id.album_cover);
            TextView title = itemView.findViewById(R.id.album_title_tv);
            TextView desrcTv = itemView.findViewById(R.id.album_description_tv);
            TextView play = itemView.findViewById(R.id.album_play_cound);
            TextView size = itemView.findViewById(R.id.album_contant_size);

            Glide.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(imageView);
            title.setText(album.getAlbumTitle());
            desrcTv.setText(album.getAlbumIntro());
            play.setText(album.getPlayCount()+"");
            size.setText(album.getIncludeTrackCount()+"");
        }
    }

    public void setonRecomendItemClickLister(onRecomendItemClickLister lister){
        this.monRecomendItemClickLister = lister;
    }

    public interface onRecomendItemClickLister{
        void onItemClick(int postion, Album album);
    }

    public void setonAlbumItemLongClickLister(onAlbumItemLongClickLister longClickLister){
        this.mLongClickLister = longClickLister;
    }

    public interface onAlbumItemLongClickLister{
        void onItemLongClick(Album album);
    }

}
