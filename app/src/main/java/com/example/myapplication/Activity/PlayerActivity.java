package com.example.myapplication.Activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.adapters.PlayerTrackPagerAdapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.interfaces.IPlayerCallback;
import com.example.myapplication.ppresenters.AbumDetailPresenter;
import com.example.myapplication.ppresenters.PlayerPresenter;
import com.example.myapplication.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBth;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat minFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mCurrentPosition;
    private SeekBar mTrackSeekBar;
    private TextView mTrackDuraction;
    private int mCurrentProgess = 0;
    private boolean mIsUserTouchProgressBar = false;
    private ImageView mPlayPre;
    private ImageView mPlayerNext;
    private TextView mTrackTitle;
    private String mTrackTitleText;
    private ViewPager mTrackPagerView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserSlidePager = false;
    private ImageView mPlayerModeSwitchBtn;
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeMap = new HashMap<>();
    private XmPlayListControl.PlayMode mPlayMode = PLAY_MODEL_LIST;
    private static int BG_ANIMATION_DURATION = 400;

    static {
        sPlayModeMap.put(PLAY_MODEL_LIST,PLAY_MODEL_LIST_LOOP);
        sPlayModeMap.put(PLAY_MODEL_LIST_LOOP,PLAY_MODEL_RANDOM);
        sPlayModeMap.put(PLAY_MODEL_RANDOM,PLAY_MODEL_SINGLE_LOOP);
        sPlayModeMap.put(PLAY_MODEL_SINGLE_LOOP,PLAY_MODEL_LIST_LOOP);
    }

    private ImageView mPlayerList;
    private SobPopWindow mSobPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.reqisterViewCallBack(this);
        initEvient();
        initBgAnimation();
    }

    private void initEvient() {

        mControlBth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter.isPlay()){
                    mPlayerPresenter.pause();
                }else {
                    mPlayerPresenter.play();
                }
            }
        });

        mTrackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mCurrentProgess = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = false;
                mPlayerPresenter.seekTo(mCurrentProgess);
            }
        });

        mPlayPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });
        mPlayerNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });

        mTrackPagerView.addOnPageChangeListener(this);
        mTrackPagerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action =  event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                    break;
                }
                return false;
            }
        });

        mPlayerModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PLAY_MODEL_SINGLE 单曲播放
//                PLAY_MODEL_SINGLE_LOOP 单曲循环播放
//                PLAY_MODEL_LIST 列表播放
//                PLAY_MODEL_LIST_LOOP 列表循环
//                PLAY_MODEL_RANDOM 随机播放
                //根据当前的mode获取下一个mode
                switchPlayMode();
            }
        });

        mPlayerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示播放列表
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                //修改背景透明度设置动画
                mEnterBgAnimator.start();
            }
        });

        //pop窗体消失执行
        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mOutBgAnimation.start();
            }
        });
    }

    private void switchPlayMode() {
        XmPlayListControl.PlayMode playMode = sPlayModeMap.get(mPlayMode);
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    private void initBgAnimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f,0.7f);
        mEnterBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                //改变背景透明度
                updateByAlpha(animatedValue);
            }
        });
        mOutBgAnimation = ValueAnimator.ofFloat(0.7f,1.0f);
        mOutBgAnimation.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                //改变背景透明度
                updateByAlpha(animatedValue);
            }
        });

        mSobPopWindow.setPlayListItemClickListener(new SobPopWindow.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //播放列表被点击
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });

        mSobPopWindow.setplayListPlayModeClickListener(new SobPopWindow.playListPlayModeClickListener() {
            @Override
            public void onPlayModeClick() {
                //点击却换模式
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                //点击却换顺序
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.revesePlayList();
                }
            }
        });
    }

    public void updateByAlpha(float alpha){
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    private void updatePlayModeBtnImg() {
        //根据状态更换图标
        int resId = R.drawable.selector_player_mode_list_order;
        switch (mPlayMode){
            case PLAY_MODEL_LIST:
                resId =  R.drawable.selector_player_mode_list_order;
                break;
            case PLAY_MODEL_RANDOM:
                resId =  R.drawable.selector_player_mode_random;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId =  R.drawable.selector_player_mode_list_order_looper;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId =  R.drawable.selector_player_mode_single_loop;
                break;
        }
        mPlayerModeSwitchBtn.setImageResource(resId);
    }

    private void initView() {
        mControlBth = this.findViewById(R.id.play_or_pause_btn);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mTrackSeekBar = this.findViewById(R.id.track_seek_bar);
        mTrackDuraction = this.findViewById(R.id.track_duraction);
        mPlayPre = this.findViewById(R.id.play_pre);
        mPlayerNext = this.findViewById(R.id.player_next);
        mTrackTitle = this.findViewById(R.id.track_title);
        mPlayerModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);
        mPlayerList = this.findViewById(R.id.player_list);
        if(!TextUtils.isEmpty(mTrackTitleText)){
            mTrackTitle.setText(mTrackTitleText);
        }

        mTrackPagerView = this.findViewById(R.id.track_pager_view);
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        mTrackPagerView.setAdapter(mTrackPagerAdapter);

        mSobPopWindow = new SobPopWindow();

    }

    @Override
    public void onPlayStart() {
        //开始播放，修改UI
        if (mControlBth != null) {
            mControlBth.setImageResource(R.mipmap.stop_normal);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBth != null) {
            mControlBth.setImageResource(R.mipmap.play_normal);
        }
    }

    @Override
    public void onPlayStop() {

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
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
        if (mSobPopWindow != null) {
            mSobPopWindow.setListData(list);
        }
    }

    @Override
    public void onPlayModeChage(XmPlayListControl.PlayMode mode) {
        mPlayMode = mode;
        mSobPopWindow.updatePlayMode(mPlayMode);
        updatePlayModeBtnImg();
    }

    @Override
    public void onProgressChage(int currentProgress, int total) {
        //更新进度条
        mTrackSeekBar.setMax(total);
        String totaiTime;
        String currentPostion;
        if (total > 1000*60*60) {
            totaiTime = mHourFormat.format(total);
            currentPostion = mHourFormat.format(currentProgress);
        }else {
            totaiTime = minFormat.format(total);
            currentPostion = minFormat.format(currentProgress);
        }
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPostion);
        }
        if (mTrackDuraction != null) {
            mTrackDuraction.setText(totaiTime);
        }
        //跟新进度条
        if (!mIsUserTouchProgressBar){
            mTrackSeekBar.setProgress(currentProgress);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIdex) {
        if (track == null) {
            Log.d(TAG, "onTrackUpdate: track==null");
            return;
        }
        //更新标题
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitle != null) {
            mTrackTitle.setText(mTrackTitleText);
        }
        //当前的节目改变以后，要修改页面的图片
        if (mTrackPagerView != null) {
            mTrackPagerView.setCurrentItem(playIdex,true);
        }
        //修改播放列表位置
        if (mSobPopWindow != null) {
            mSobPopWindow.setCurrentPlayPostion(playIdex);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mSobPopWindow.updateOrderIcon(isReverse);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallBack(this);
            mPlayerPresenter = null;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //当页面选中的时候，就去切换播放内容
        if (mPlayerPresenter != null && mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
