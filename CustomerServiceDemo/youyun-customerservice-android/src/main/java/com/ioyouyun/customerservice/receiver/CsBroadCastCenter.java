package com.ioyouyun.customerservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill on 2016/12/13.
 *
 * 本地广播用来全局接收IM消息
 */

public class CsBroadCastCenter {

    private LocalBroadcastManager mLocalBroadcast;
    private static CsBroadCastCenter singleBroadcast = new CsBroadCastCenter();
    private List<BroadcastReceiver> broadcastReceiverList = new ArrayList<>();

    public static CsBroadCastCenter getInstance() {
        return singleBroadcast;
    }

    public boolean init(Context context) {
        if (null == context) {
            return false;
        }
        mLocalBroadcast = LocalBroadcastManager.getInstance(context);
        if (null == mLocalBroadcast) {
            return false;
        }
        return true;
    }

    public void broadcast(Intent intent){
        if(null == mLocalBroadcast){
            return;
        }
        mLocalBroadcast.sendBroadcast(intent);
    }

    public void registerReceiver(BroadcastReceiver br, String... actions){
        if(null == br || null == actions || null == mLocalBroadcast){
            return;
        }
        IntentFilter filter = new IntentFilter();
        for (String action : actions)
            filter.addAction(action);

        mLocalBroadcast.registerReceiver(br, filter);
        broadcastReceiverList.add(br);
    }

    public void unregisterReceiver(BroadcastReceiver br){
        if(null == br || null == mLocalBroadcast){
            return;
        }
        mLocalBroadcast.unregisterReceiver(br);
        broadcastReceiverList.remove(br);
    }

    public void unregisterAllReceiver(){
        if(null == mLocalBroadcast){
            return;
        }
        for (BroadcastReceiver broadcastReceiver : broadcastReceiverList){
            mLocalBroadcast.unregisterReceiver(broadcastReceiver);
        }
        broadcastReceiverList.clear();
    }


}
