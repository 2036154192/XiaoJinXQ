package com.example.myapplication.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.DeviceInfoProviderDefault;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDeviceInfoProvider;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.util.SharedPreferencesUtil;


public class BaseApplication extends Application {

    private static Handler sHandler = null;
    private static final String KEY_LAST_OAID = "last_oaid";
    private String oaid;
    private static Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        oaid = SharedPreferencesUtil.getInstance(getApplicationContext()).getString(KEY_LAST_OAID);
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        if (DTransferConstants.isRelease) {
            String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
            mXimalaya.setPackid("com.app.test.android");
            mXimalaya.init(this, mAppSecret,getDeviceInfoProvider(this));
        } else {
            String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
            mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
            mXimalaya.setPackid("com.ximalaya.qunfeng");
            mXimalaya.init(this, mAppSecret,getDeviceInfoProvider(this));
        }

        //初始化播放器
        XmPlayerManager.getInstance(this).init();

        sHandler = new Handler();

        mContext = getBaseContext();

    }
    public IDeviceInfoProvider getDeviceInfoProvider(Context context) {
        return new DeviceInfoProviderDefault(context) {
            @Override
            public String oaid() {
                return oaid;
            }
        };
    }
    public static Handler getHandler() {
        return sHandler;
    }
    public static Context getmContext(){
        return mContext;
    }
}
