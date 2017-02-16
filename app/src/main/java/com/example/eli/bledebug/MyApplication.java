package com.example.eli.bledebug;

import android.app.Application;
import android.util.Log;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

/**
 * Created by Eli on 2017/2/16.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PushAgent mPushAgent = PushAgent.getInstance(this);
//注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.e("PushAgent-onSuccess---", deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("PushAgent-onFailure---", s1);
            }
        });
    }
}
