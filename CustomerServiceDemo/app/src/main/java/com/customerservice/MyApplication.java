package com.customerservice;

import android.app.Application;

import com.ioyouyun.customerservice.receiver.CsBroadCastCenter;
import com.ioyouyun.customerservice.utils.CsAppUtils;

/**
 * Created by Bill on 2016/12/8.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initCS();
    }

    /**
     * 初始化客服
     */
    // TODO 客服初始化 跟 wchatsdk 初始化封到一块
    private void initCS(){
        CsAppUtils.init(getApplicationContext());
        CsBroadCastCenter.getInstance().init(getApplicationContext());
    }

}
