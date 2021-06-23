package com.example.myapplication.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activity.DetailActivity;
import com.example.myapplication.Activity.PlayerActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapters.DetailListAdapter;
import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.base.BaseFragment;
import com.example.myapplication.interfaces.IHistoryCallback;
import com.example.myapplication.ppresenters.HistoryPresent;
import com.example.myapplication.ppresenters.PlayerPresenter;
import com.example.myapplication.utils.UIUtil;
import com.example.myapplication.views.ConfrimCheckBoxDialog;
import com.example.myapplication.views.ConfrimDialog;
import com.example.myapplication.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HistoryFragment extends BaseFragment implements IHistoryCallback, DetailListAdapter.OnClickList, DetailListAdapter.ItemLongClickListener, ConfrimCheckBoxDialog.onDialogActionClickListener2 {

    private UILoader mUiLoader;
    private DetailListAdapter mDetailListAdapter;
    private HistoryPresent mHistoryPresent;
    private static final String TAG = "HistoryFragment";
    private TwinklingRefreshLayout mRefreshLayout;
    private RecyclerView mHistoryList;
    private ConfrimCheckBoxDialog mConfrimCheckBoxDialog;
    private Track mCurrentClickHistoryItem = null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history2, container, false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getmContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEmptyView(){
                    View inflate = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView viewById = inflate.findViewById(R.id.textView);
                    viewById.setText("没有历史记录");
                    return inflate;
                }
            };
        }else {
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        mHistoryPresent = HistoryPresent.getHistoryPresent();
        mHistoryPresent.reqisterViewCallBack(this);
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        mHistoryPresent.listHistories();
        rootView.addView(mUiLoader);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHistoryPresent != null) {
            mHistoryPresent.unRegisterViewCallBack(this);
        }
    }

    private View createSuccessView(ViewGroup container) {
        View inflate = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history, container, false);
        mRefreshLayout = inflate.findViewById(R.id.over_scroll_view);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadmore(false);
        mHistoryList = inflate.findViewById(R.id.history_list);
        mHistoryList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mDetailListAdapter = new DetailListAdapter();
        mHistoryList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        mDetailListAdapter.setOnclickList(this);
        mDetailListAdapter.setItemLongClickListener(this);
        mHistoryList.setAdapter(mDetailListAdapter);
        return inflate;
    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
        if (tracks.size() == 0 || tracks == null) {
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }else {
            mDetailListAdapter.setData(tracks);
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    @Override
    public void onClickLists(List<Track> data, int position) {
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(data,position);
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Track track) {
        this.mCurrentClickHistoryItem = track;
        mConfrimCheckBoxDialog = new ConfrimCheckBoxDialog(getActivity());
        mConfrimCheckBoxDialog.setOnDialogActionClickListener2(this);
        mConfrimCheckBoxDialog.show();
    }

    @Override
    public void onCancelSubClick2(boolean isCheck) {
        if (mHistoryPresent != null && mCurrentClickHistoryItem != null) {
            if (!isCheck) {
                mHistoryPresent.delHistory(mCurrentClickHistoryItem);
            }else {
                mHistoryPresent.clearHistory();
            }
            mConfrimCheckBoxDialog.dismiss();
        }
    }

    @Override
    public void onGiveUpClick2() {
        mConfrimCheckBoxDialog.dismiss();
    }

}