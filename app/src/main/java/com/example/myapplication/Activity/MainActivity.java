package com.example.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.fragments.HistoryFragment;
import com.example.myapplication.fragments.RecommendFragment;
import com.example.myapplication.fragments.SubscriptionFragment;
import com.example.myapplication.interfaces.IPlayerCallback;
import com.example.myapplication.ppresenters.PlayerPresenter;
import com.example.myapplication.ppresenters.RecommendPresenter;
import com.example.myapplication.views.RoundRectImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IPlayerCallback {

    private static final String TAG = "MainActivity";
    private ViewPager2 mViewPager2;
    private TabLayout mTableLayout;
    private RoundRectImageView mRoundRectImageView;
    private TextView mHeaderTitle;
    private TextView mSubTitle;
    private ImageView mPlayContorl;
    private PlayerPresenter mPlayerPresenter;
    private LinearLayout mPlayControItem;
    private LinearLayout mImageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        viewPager2Table();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.reqisterViewCallBack(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallBack(this);
        }
    }

    private void initEvent() {
        mPlayContorl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没有设置过播放就设置默认第一个专辑
                        playFirstRecommend();
                    }else {
                        if (mPlayerPresenter.isPlay()) {
                            mPlayerPresenter.pause();
                        }else {
                            mPlayerPresenter.play();
                        }
                    }
                }
            }
        });
        mPlayControItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到播放器节目
                boolean hasPlayList = mPlayerPresenter.hasPlayList();
                if (!hasPlayList) {
                    playFirstRecommend();
                }
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
            }
        });
        mImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });
    }

    //播放第一个专辑的第一首
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend != null && currentRecommend.size() > 0) {
            Album album = currentRecommend.get(0);
            long id = album.getId();
            mPlayerPresenter.playByAlbumId(id);
        }
    }

    private void initView() {
        mViewPager2 = findViewById(R.id.viewPager2);
        mTableLayout = findViewById(R.id.tabLayout);
        mRoundRectImageView = findViewById(R.id.main_track_cover);
        mHeaderTitle = findViewById(R.id.main_title);
        mHeaderTitle.setSelected(true);
        mSubTitle = findViewById(R.id.main_sub_title);
        mPlayContorl = findViewById(R.id.main_play_cover);
        mPlayControItem = findViewById(R.id.linearLayout3);
        mImageView2 = findViewById(R.id.imageView2);
    }

    private void viewPager2Table() {
        mViewPager2.setAdapter(new FragmentStateAdapter(this) {
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return new RecommendFragment();
                case 1:
                    return new SubscriptionFragment();
                default:
                    return new HistoryFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    });
        new TabLayoutMediator(mTableLayout, mViewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("推荐");
                    break;
                case 1:
                    tab.setText("订阅");
                    break;
                default:
                    tab.setText("历史");
            }
        }).attach();
    }

    @Override
    public void onPlayStart() {
        updatePlayControl(true);
    }

    private void updatePlayControl(boolean isPlaying){
        if (mPlayContorl != null) {
            mPlayContorl.setImageResource(isPlaying ?
                    R.drawable.selector_player_pause:R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPlayPre(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChage(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void onProgressChage(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIdex) {
        if (track != null) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            if (mHeaderTitle != null) {
                mHeaderTitle.setText(trackTitle);
            }
            if (mSubTitle != null) {
                mSubTitle.setText(nickname);
            }
            if (mRoundRectImageView != null) {
                Glide.with(this).load(coverUrlMiddle).into(mRoundRectImageView);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
