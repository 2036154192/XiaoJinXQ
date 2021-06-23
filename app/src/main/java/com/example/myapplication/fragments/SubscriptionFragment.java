package com.example.myapplication.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activity.DetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapters.RecommendListAdapter;
import com.example.myapplication.base.BaseFragment;
import com.example.myapplication.interfaces.ISubscriptionCallback;
import com.example.myapplication.interfaces.ISubscriptionPreesenter;
import com.example.myapplication.ppresenters.AbumDetailPresenter;
import com.example.myapplication.ppresenters.SubscriptionPresenter;
import com.example.myapplication.utils.UIUtil;
import com.example.myapplication.views.ConfrimDialog;
import com.example.myapplication.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, RecommendListAdapter.onRecomendItemClickLister, RecommendListAdapter.onAlbumItemLongClickLister, ConfrimDialog.onDialogActionClickListener {

    private ISubscriptionPreesenter mSubscriptionPreesenter;
    private RecyclerView mDyList;
    private RecommendListAdapter mRecommendListAdapter;
    private static final String TAG = "SubscriptionFragment";
    private Album mCurrentClickAlbum = null;
    private ConfrimDialog mConfrimDialog;
    private TextView mTextdfsdf;
    private TextView mClearBt;
    private View mListview;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_subscription2, container, false);
        mDyList = rootView.findViewById(R.id.dy_list);
        TwinklingRefreshLayout refreshLayout = rootView.findViewById(R.id.dy_twrel);
        mClearBt = rootView.findViewById(R.id.clear_bt);
        mTextdfsdf = rootView.findViewById(R.id.textdfsdf);
        mListview = rootView.findViewById(R.id.sdfsafsdfdf);
        mClearBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("确定清空订阅");
                dialog.setCancelable(true);    //设置是否可以通过点击对话框外区域或者返回按键关闭对话框
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mSubscriptionPreesenter != null) {
                            mSubscriptionPreesenter.clearSubscription();
                        }
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.show();
            }
        });
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        mDyList.setLayoutManager(linearLayoutManager);
        mDyList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        mRecommendListAdapter = new RecommendListAdapter();
        mDyList.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setonRecomendItemClickLister(this);
        mRecommendListAdapter.setonAlbumItemLongClickLister(this);
        mSubscriptionPreesenter = SubscriptionPresenter.getInstance();
        mSubscriptionPreesenter.reqisterViewCallBack(this);
        mSubscriptionPreesenter.getSubscription();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscriptionPreesenter != null) {
            mSubscriptionPreesenter.unRegisterViewCallBack(this);
        }
        mRecommendListAdapter.setonRecomendItemClickLister(null);
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        Toast.makeText(getContext(), isSuccess?R.string.cancel_sub_success:R.string.cancel_sub_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscritpionLoadeds(List<Album> albums) {
        if (albums.size()==0) {
            mListview.setVisibility(View.GONE);
            mTextdfsdf.setVisibility(View.VISIBLE);
        }else {
            mListview.setVisibility(View.VISIBLE);
            mTextdfsdf.setVisibility(View.GONE);
        }
        if (mRecommendListAdapter != null) {
            mRecommendListAdapter.setData(albums);
        }
    }

    @Override
    public void onSubTooMany() {
        Toast.makeText(getContext(), "订阅太多了，无法订阅", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClearSub() {
        Toast.makeText(getContext(), "清除成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int postion, Album album) {
        AbumDetailPresenter.getInstance().setTargetAlbum(album);
        //列表点击事件
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Album album) {
        this.mCurrentClickAlbum = album;
        //长时间点击删除
        mConfrimDialog = new ConfrimDialog(getActivity());
        mConfrimDialog.setOnDialogActionClickListener(this);
        mConfrimDialog.show();
    }

    @Override
    public void onCancelSubClick() {
        //确认
        if (mCurrentClickAlbum != null && mSubscriptionPreesenter!=null) {
            mSubscriptionPreesenter.deleteSubscription(mCurrentClickAlbum);
            mConfrimDialog.dismiss();
        }
    }

    @Override
    public void onGiveUpClick() {
        mConfrimDialog.dismiss();
    }
}