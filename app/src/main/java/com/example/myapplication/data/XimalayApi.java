package com.example.myapplication.data;

import com.example.myapplication.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class XimalayApi {

    private XimalayApi(){}

    private static XimalayApi sXimalayApi;

    public static XimalayApi getXimalayApi(){
        if (sXimalayApi == null) {
            synchronized (XimalayApi.class){
                sXimalayApi = new XimalayApi();
            }
        }
        return sXimalayApi;
    }

    public void getRecommendList(IDataCallBack<SearchAlbumList> callBack, int YESHU){
        String q = "戏曲";
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, q);
        map.put(DTransferConstants.CATEGORY_ID,"0");
        map.put(DTransferConstants.PAGE, YESHU+"");
        map.put(DTransferConstants.CALC_DIMENSION,2+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.RECOMMAND_COUNT+"");
        CommonRequest.getSearchedAlbums(map,callBack);
    }

    public void getAlbumDetail(IDataCallBack<TrackList> callBack,long id,int page){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, id+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.RECOMMAND_COUNT+"");
        CommonRequest.getTracks(map,callBack);
    }

    //根据关键字搜索
    public void searchByKeyword(String currentKeyword,int page,IDataCallBack<SearchAlbumList> callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, currentKeyword);
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE,Constants.RECOMMAND_COUNT+"");
        CommonRequest.getSearchedAlbums(map,callBack);
    }

    //获取推荐的热词
    public void getHotWords(IDataCallBack<HotWordList> callBack){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, Constants.COUNT_HOT_WORD+"");
        CommonRequest.getHotWords(map,callBack);
    }

    //根据关键字获取联想词
    public void getSuggestWord(String keyword, IDataCallBack<SuggestWords> callBack){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map, callBack);
    }

}
