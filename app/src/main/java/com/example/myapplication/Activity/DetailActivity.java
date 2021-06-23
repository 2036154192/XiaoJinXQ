package com.example.myapplication.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.DetailListAdapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.interfaces.IAlbumDetailBiewCallback;
import com.example.myapplication.interfaces.IPlayerCallback;
import com.example.myapplication.interfaces.ISubscriptionCallback;
import com.example.myapplication.ppresenters.AbumDetailPresenter;
import com.example.myapplication.ppresenters.PlayerPresenter;
import com.example.myapplication.ppresenters.SubscriptionPresenter;
import com.example.myapplication.utils.ImageBulr;
import com.example.myapplication.utils.UIUtil;
import com.example.myapplication.views.RoundRectImageView;
import com.example.myapplication.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailBiewCallback, UILoader.OnRetryClickListener, DetailListAdapter.OnClickList, IPlayerCallback, ISubscriptionCallback {

    private static final String TAG = "DetailActivity";
    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AbumDetailPresenter mAbumDetailPresenter;
    private int mCurrentPage = 1;
    private RecyclerView mDetailList;
    private DetailListAdapter mDetailListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId = -1;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTv;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTicak = null;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mTrackTitle = null;
    private TextView mSubBtn;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        initPresenter();
        //设置订阅按钮的状态
        updateSubState();
        updatePlaySate(mPlayerPresenter.isPlay());
        initListener();
    }

    private void updateSubState() {
        if (mSubscriptionPresenter != null) {
            boolean sub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(sub?R.string.cancel_sub_tips_text:R.string.sub_tips_text);
        }
    }

    private void initPresenter() {
        //专辑详情的presenter
        mAbumDetailPresenter = AbumDetailPresenter.getInstance();
        mAbumDetailPresenter.reqisterViewCallBack(this);
        //播放器的presenter
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.reqisterViewCallBack(this);
        updatePlaySate(mPlayerPresenter.isPlay());
        //订阅相关的
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.getSubscription();
        mSubscriptionPresenter.reqisterViewCallBack(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAbumDetailPresenter != null) {
            mAbumDetailPresenter.unRegisterViewCallBack(this);
        }
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallBack(this);
        }
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallBack(this);
        }
    }

    private void initListener() {
        if (mPlayControlBtn != null) {
            mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlayerPresenter != null) {
                        //判断是否有内容
                        boolean has = mPlayerPresenter.hasPlayList();
                        if (has){
                            if (mPlayerPresenter.isPlay()) {
                                //正在播放
                                mPlayerPresenter.pause();
                            }else {
                                mPlayerPresenter.play();
                            }
                        }else {
                            handlNoPlayList();
                        }
                    }
                }
            });
        }

        mSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                    boolean sub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    if (sub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                    }else {
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                    }
                }
            }
        });
    }

    //没有播放内容
    private void handlNoPlayList() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.setPlayList(mCurrentTicak,0);
        }
    }

    private void initView() {
        mDetailListContainer = findViewById(R.id.detail_list_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }

        mLargeCover = findViewById(R.id.iv_large_cover);
        mSmallCover = findViewById(R.id.iv_small_cover);
        mAlbumTitle = findViewById(R.id.tv_album_title);
        mAlbumAuthor = findViewById(R.id.tv_album_author);
        mPlayControlBtn = findViewById(R.id.detail_play_control);
        mPlayControlTv = findViewById(R.id.play_control_tv);
        mPlayControlTv.setSelected(true);

        mSubBtn = findViewById(R.id.detail_sub_btn);
    }

    private boolean isLoaderMore = false;

    private View createSuccessView(ViewGroup container) {
        View detaiListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list,container,false);
        mDetailList = detaiListView.findViewById(R.id.album_detail);
        mRefreshLayout = detaiListView.findViewById(R.id.refresh_layout);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mDetailList.setLayoutManager(mLinearLayoutManager);
        mDetailListAdapter = new DetailListAdapter();
        mDetailList.setAdapter(mDetailListAdapter);
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        mDetailListAdapter.setOnclickList(this);

        //刷新事件
//        BezierLayout bezierLayout = new BezierLayout(this);
//        mRefreshLayout.setHeaderView(bezierLayout);
//        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                //刷新
                if (mAbumDetailPresenter != null) {
                    mAbumDetailPresenter.pull2RefreshMore();
                    isLoaderMore = false;
                    if (mUiLoader != null) {
                        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
                    }
                    mRefreshLayout.finishRefreshing();
                    Toast.makeText(DetailActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //加载
                if (mAbumDetailPresenter != null) {
                    mAbumDetailPresenter.loadMore();
                    isLoaderMore = true;
                }
            }
        });

        return detaiListView;
    }

    @Override
    public void onDetailListloaded(List<Track> tracks) {

        if (isLoaderMore && mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            isLoaderMore =false;
        }

        this.mCurrentTicak = tracks;
        if (tracks == null || tracks.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        this.mCurrentAlbum = album;

        mCurrentId = (int) album.getId();

        if (mAbumDetailPresenter != null) {
            mAbumDetailPresenter.getAlbumDetale((int) album.getId(),mCurrentPage);
        }

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }

        if (mAlbumTitle != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }
        if (mLargeCover != null) {
            Picasso.get().load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();
                    if (drawable != null) {
                        ImageBulr.makeBlur(mLargeCover,DetailActivity.this);
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });

        }
        if (mSmallCover != null) {
            Picasso.get().load(album.getCoverUrlLarge()).into(mSmallCover);
        }
    }

    @Override
    public void onNetworkError(int i, String s) {
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    private int i = 1;
    @Override
    public void onLoaderMoreFinished(int size) {
        Log.d(TAG, "onLoaderMoreFinished: "+size);

        if (size>0){
            Toast.makeText(DetailActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void onRetryClick() {
        if (mAbumDetailPresenter != null) {
            mAbumDetailPresenter.getAlbumDetale((int) mCurrentId ,mCurrentPage);
        }
    }

    @Override
    public void onClickLists(List<Track> data, int position) {
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(data,position);
        Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlayStart() {
        updatePlaySate(true);
    }

    @Override
    public void onPlayPause() {
       updatePlaySate(false);
    }

    @Override
    public void onPlayStop() {
        updatePlaySate(false);
    }

    private void updatePlaySate(boolean play) {
        if (mPlayControlBtn != null && mPlayControlTv != null) {
            mPlayControlBtn.setImageResource(
                    play?R.drawable.selector_play_control_pause:R.drawable.selector_play_control_play);
            if (!play) {
                mPlayControlTv.setText(R.string.click_play_tips_text);
            }else {
                if (!TextUtils.isEmpty(mTrackTitle)) {
                    mPlayControlTv.setText(mTrackTitle);
                }
            }

        }
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
            mTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mTrackTitle) && mPlayControlTv != null) {
                mPlayControlTv.setText(mTrackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        if (isSuccess) {
            mSubBtn.setText(R.string.cancel_sub_tips_text);
        }
        String string = isSuccess ? "订阅成功" : "订阅失败";
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            mSubBtn.setText(R.string.sub_tips_text);
        }
        String string = getString(isSuccess ? R.string.cancel_sub_success : R.string.cancel_sub_failed);
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscritpionLoadeds(List<Album> albums) {
        //在这个界面不需要处理
        for (Album album : albums) {
            Log.d(TAG, "onSubscritpionLoadeds: "+album);
        }
    }

    @Override
    public void onSubTooMany() {
        Toast.makeText(this, "订阅太多了，无法订阅", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClearSub() {

    }
}