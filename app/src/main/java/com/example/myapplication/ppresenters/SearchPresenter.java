package com.example.myapplication.ppresenters;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.myapplication.data.XimalayApi;
import com.example.myapplication.interfaces.ISearchCallback;
import com.example.myapplication.interfaces.ISearchPresenter;
import com.example.myapplication.utils.Constants;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    private List<ISearchCallback> mSearchCallbacks = new ArrayList<>();
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage=DEFAULT_PAGE;
    //当前的搜索关键字
    private String mCurrentKeyword = null;
    private final XimalayApi mXimalayApi;

    private List<Album> searchResult = new ArrayList<>();

    private SearchPresenter(){
        mXimalayApi = XimalayApi.getXimalayApi();
    }
    private static SearchPresenter sSearchPresenter = null;
    public static SearchPresenter getInstance(){
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class){
                sSearchPresenter = new SearchPresenter();
            }
        }
        return sSearchPresenter;
    }

    @Override
    public void doSearch(String keyword) {
        mCurrentPage = 1;
        searchResult.clear();
        this.mCurrentKeyword = keyword;
        search();
    }

    private void search() {
        mXimalayApi.searchByKeyword(mCurrentKeyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable @org.jetbrains.annotations.Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                searchResult.addAll(albums);
                if (albums != null) {
                    Log.d(TAG, "search-onSuccess: "+albums.size());
                    if (mIsLoadMore) {
                        for (ISearchCallback searchCallback : mSearchCallbacks) {
                            searchCallback.onLoadMoreResult(searchResult,albums.size()==0);
                        }
                        mIsLoadMore = false;
                    }else {
                        for (ISearchCallback searchCallback : mSearchCallbacks) {
                            searchCallback.onSearchResultLoaded(searchResult);
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "search-onError: "+i+"--------"+s);
                for (ISearchCallback searchCallback : mSearchCallbacks) {
                    if (mIsLoadMore) {
                        searchCallback.onLoadMoreResult(searchResult,false);
                        mIsLoadMore=false;
                        mCurrentPage--;
                    }else {
                        searchCallback.onError(i,s);
                    }
                }
            }
        });
    }

    @Override
    public void reSearch() {
        search();
    }

    private boolean mIsLoadMore = false;

    @Override
    public void loadMore() {
        //判断有没有必要加载更多
        if (searchResult.size()< Constants.RECOMMAND_COUNT) {
            for (ISearchCallback searchCallback : mSearchCallbacks) {
                searchCallback.onLoadMoreResult(searchResult,false);
            }
        }else {
            mIsLoadMore = true;
            mCurrentPage++;
            doSearch(mCurrentKeyword);
        }
    }

    @Override
    public void getHotWord() {
        //做一个热式缓存
        mXimalayApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(@Nullable @org.jetbrains.annotations.Nullable HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    if (hotWords != null) {
                        Log.d(TAG, "getHotWord-onSuccess: "+hotWords.size());
                        for (ISearchCallback searchCallback : mSearchCallbacks) {
                            searchCallback.onHotWordLoaded(hotWords);
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "getHotWord-onError: "+i+"--------"+s);
            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
        mXimalayApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable @org.jetbrains.annotations.Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    if (keyWordList != null) {
                        Log.d(TAG, "getRecommendWord-onSuccess: "+keyWordList.size());
                        for (ISearchCallback searchCallback : mSearchCallbacks) {
                            searchCallback.onRecommendWordLoaded(keyWordList);
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "getRecommendWord-onError: "+i+"--------"+s);
                for (ISearchCallback searchCallback : mSearchCallbacks) {
                    searchCallback.onError(i,s);
                }
            }
        });
    }

    @Override
    public void reqisterViewCallBack(ISearchCallback callback) {
        if (!mSearchCallbacks.contains(callback)) {
            mSearchCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallBack(ISearchCallback callback) {
        mSearchCallbacks.remove(callback);
    }
}
