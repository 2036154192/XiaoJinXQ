package com.example.myapplication.views;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.PlayListAdapter;
import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.utils.UIUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;


public class SobPopWindow extends PopupWindow {

    private final View mPopView;
    private TextView mCloseBth;
    private RecyclerView mTracksList;
    private PlayListAdapter mPlayListAdapter;
    private TextView mPlayModeTV;
    private ImageView mPlayModeIv;
    private View mPlayModeContainer;
    private playListPlayModeClickListener mplayListPlayModeClickListener = null;
    private View mOrderContainer;
    private ImageView mOrderIv;
    private TextView mOrderTv;

    public SobPopWindow(){
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //点击范围外关闭,如果不行先设置 setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);

        //载view
        mPopView = LayoutInflater.from(BaseApplication.getmContext()).inflate(R.layout.pop_paly_list, null);
        setContentView(mPopView);

        //设置窗口进入退出的动画
        setAnimationStyle(R.style.pop_animation);

        initView();
        initEvent();
    }

    private void initEvent() {
        //点击关闭按钮窗口消失
        mCloseBth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //却换播放模式
                if (mplayListPlayModeClickListener != null) {
                    mplayListPlayModeClickListener.onPlayModeClick();
                }
            }
        });
        mOrderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mplayListPlayModeClickListener != null) {
                    mplayListPlayModeClickListener.onOrderClick();
                }
            }
        });
    }

    private void initView() {
        mCloseBth = mPopView.findViewById(R.id.play_list_close_btn);
        mTracksList = mPopView.findViewById(R.id.play_list_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getmContext());
        mTracksList.setLayoutManager(linearLayoutManager);
        mPlayListAdapter = new PlayListAdapter();
        mTracksList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        mTracksList.setAdapter(mPlayListAdapter);
        //播放模式
        mPlayModeTV = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);
        //播放顺序
        mOrderIv = mPopView.findViewById(R.id.paly_list_order_iv);
        mOrderTv = mPopView.findViewById(R.id.paly_list_order_tv);
        mOrderContainer = mPopView.findViewById(R.id.play_list_order_container);
    }

    public void setListData(List<Track> data){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(data);
        }
    }

    public void setCurrentPlayPostion(int postion){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlayPostion(postion);
            mTracksList.scrollToPosition(postion);
        }
    }

    public void setPlayListItemClickListener(PlayListItemClickListener listItemClickListener){
        mPlayListAdapter.setOnItemClickListener(listItemClickListener);
    }

    public void updatePlayMode(XmPlayListControl.PlayMode playMode) {
        updatePlayModeBtnImg(playMode);
    }

    public void updateOrderIcon(boolean isOrder){
        mOrderIv.setImageResource(
                isOrder?R.drawable.selector_player_mode_list_order:R.drawable.selector_player_mode_list_reverse);
        mOrderTv.setText(isOrder?R.string.order_text:R.string.revers_text);
    }

    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode playMode) {
        //根据状态更换图标
        int resId = R.drawable.selector_player_mode_list_order;
        int textId = R.string.play_mode_order_text;
        switch (playMode){
            case PLAY_MODEL_LIST:
                resId =  R.drawable.selector_player_mode_list_order;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_RANDOM:
                resId =  R.drawable.selector_player_mode_random;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId =  R.drawable.selector_player_mode_list_order_looper;
                textId = R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId =  R.drawable.selector_player_mode_single_loop;
                textId = R.string.play_mode_single_play_text;
                break;
        }
            mPlayModeIv.setImageResource(resId);
            mPlayModeTV.setText(textId);
    }

    public interface PlayListItemClickListener{
        void onItemClick(int position);
    }

    public void setplayListPlayModeClickListener(playListPlayModeClickListener listener){
        this.mplayListPlayModeClickListener = listener;
    }

    public interface playListPlayModeClickListener{
        //播放模式
        void onPlayModeClick();
        //播放顺序
        void onOrderClick();

    }

}
