package com.example.myapplication.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.RecommendListAdapter;
import com.example.myapplication.adapters.SearcRecommendAdapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.interfaces.IAlbumDetialPresenter;
import com.example.myapplication.interfaces.ISearchCallback;
import com.example.myapplication.ppresenters.AbumDetailPresenter;
import com.example.myapplication.ppresenters.SearchPresenter;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.UIUtil;
import com.example.myapplication.views.FlowTextLayout;
import com.example.myapplication.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.pay.AlbumPriceTypeDetail;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback {

    private ImageView mBackBtn;
    private EditText mInputBox;
    private View mSearchBtn;
    private FrameLayout mResultContainer;
    private static final String TAG = "SearchActivity";
    private SearchPresenter mSearchPresenter;
    private UILoader mMContent;
    private RecyclerView mResultList;
    private RecommendListAdapter mAlbumListAdapter;
    private FlowTextLayout mRecommendHotWordView;
    private InputMethodManager mSystemService;
    private View mInputDelete;
    private RecyclerView mSearchRecommendList;
    private SearcRecommendAdapter mSearcRecommendAdapter;
    private TwinklingRefreshLayout mRefreshLayout;
    private boolean mNeedSuggenstWords = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallBack(this);
            mSearchPresenter = null;
        }
    }

    private void initPresenter() {

        mSystemService = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        mSearchPresenter = SearchPresenter.getInstance();
        mSearchPresenter.reqisterViewCallBack(this);
        mSearchPresenter.getHotWord();
    }

    private void initEvent() {
        mMContent.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mMContent.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //搜索
                String s = mInputBox.getText().toString().trim();
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(SearchActivity.this, "搜索不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(s);
                    mMContent.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mSearchPresenter.getHotWord();
                    mInputDelete.setVisibility(View.GONE);
                }else {
                    mInputDelete.setVisibility(View.VISIBLE);
                    if (mNeedSuggenstWords) {
                        getSuggestWord(s.toString());
                    }else {
                        mNeedSuggenstWords = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mRecommendHotWordView.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                //不需要相关的联想词
                mNeedSuggenstWords =false;

                switch2Search(text);
            }
        });
        mInputDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputBox.setText("");
            }
        });
        if (mSearcRecommendAdapter != null) {
            mSearcRecommendAdapter.setItemClickListener(new SearcRecommendAdapter.ItemClickListener() {
                @Override
                public void onItemClick(String keyword) {
                    //不需要相关的联想词
                    mNeedSuggenstWords =false;
                    //推荐热词点击
                    switch2Search(keyword);
                }
            });
        }
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                String s = mInputBox.getText().toString().trim();
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(s);
                    mMContent.updateStatus(UILoader.UIStatus.LOADING);
                }
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //加载更多
                    if (mSearchPresenter != null) {
                        mSearchPresenter.loadMore();
                }
            }
        });
        mAlbumListAdapter.setonRecomendItemClickLister(new RecommendListAdapter.onRecomendItemClickLister() {
            @Override
            public void onItemClick(int postion, Album album) {
                AbumDetailPresenter.getInstance().setTargetAlbum(album);
                //列表点击事件
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void switch2Search(String text) {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "搜索不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mInputBox != null) {
            mInputBox.setText(text);
            mInputBox.setSelection(text.length());
            if (mSearchPresenter != null) {
                mSearchPresenter.doSearch(text);
            }
        }
        if (mMContent != null) {
            mMContent.updateStatus(UILoader.UIStatus.LOADING);
        }
    }

    private void getSuggestWord(String keyword) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(keyword);
        }
    }

    private void initView() {
        mBackBtn = findViewById(R.id.search_back);
        mInputBox = findViewById(R.id.search_input);
        mInputBox.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputBox.requestFocus();
                mSystemService.showSoftInput(mInputBox,InputMethodManager.SHOW_IMPLICIT);
            }
        },300);
        mSearchBtn = findViewById(R.id.search_bth);
        mResultContainer = findViewById(R.id.search_container);
        //mFlowTextLayoutayout = findViewById(R.id.flow_text_layout);
        if (mMContent == null) {
            mMContent = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            if (mMContent.getParent() instanceof ViewGroup) {
                ((ViewGroup) mMContent.getParent()).removeView(mMContent);
            }
            mResultContainer.addView(mMContent);
        }
        mInputDelete = findViewById(R.id.search_input_delete);
        mInputDelete.setVisibility(View.GONE);
    }

    private View createSuccessView(ViewGroup container) {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);

        mResultList = resultView.findViewById(R.id.result_list_view);
        mRecommendHotWordView = resultView.findViewById(R.id.recommend_hot_word_view);
        mRefreshLayout = resultView.findViewById(R.id.search_result_refresh_layout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mResultList.setLayoutManager(linearLayoutManager);
        mAlbumListAdapter = new RecommendListAdapter();
        mResultList.setAdapter(mAlbumListAdapter);
        mResultList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
                outRect.top = UIUtil.dip2px(view.getContext(),5);
            }
        });

        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(linearLayoutManager1);
        mSearcRecommendAdapter = new SearcRecommendAdapter();
        mSearchRecommendList.setAdapter(mSearcRecommendAdapter);
        mSearchRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
                outRect.top = UIUtil.dip2px(view.getContext(),5);
            }
        });

        return resultView;
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {

        handleSearchResult(result);

        //隐藏键盘
        if (mSystemService.isActive(mInputBox)) {
            mSystemService.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void handleSearchResult(List<Album> result) {
        mRefreshLayout.finishRefreshing();
        hideSuccessView();
        mRefreshLayout.setVisibility(View.VISIBLE);

        if (result != null) {
            if (result.size() == 0) {
                if (mMContent != null) {
                    mMContent.updateStatus(UILoader.UIStatus.EMPTY);
                }
            }else {
                mAlbumListAdapter.setData(result);
                mMContent.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {

        hideSuccessView();
        mRecommendHotWordView.setVisibility(View.VISIBLE);

        if (mMContent != null) {
            mMContent.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        Log.d(TAG, "onHotWordLoaded: "+hotWordList.size());
        List<String> hotwords = new ArrayList<>();
        hotwords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchword = hotWord.getSearchword();
            hotwords.add(searchword);
        }
        //更新UI
        mRecommendHotWordView.setTextContents(hotwords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
        if (isOkay) {
            handleSearchResult(result);
        }else {
            Toast.makeText(this, "没有更多内容", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {
        //联想关键字
        if (mSearcRecommendAdapter != null) {
            mSearcRecommendAdapter.setData(keyWordList);
        }
        //控制UI显示隐藏
        if (mMContent != null) {
            mMContent.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        hideSuccessView();
        mSearchRecommendList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(int i, String s) {
        if (mMContent != null) {
            mMContent.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    private void hideSuccessView(){
        mSearchRecommendList.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.GONE);
        mRecommendHotWordView.setVisibility(View.GONE);

    }
}