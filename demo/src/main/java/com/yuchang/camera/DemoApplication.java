/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yuchang.camera;

//import static com.umeng.message.UmengDownloadResourceService.TAG;

import android.app.Application;
import android.app.Service;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;

import com.yuchang.camera.R;
import com.yuchang.camera.util.LocationService;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
//import com.umeng.message.IUmengRegisterCallback;
//import com.umeng.message.PushAgent;

public class DemoApplication extends Application {
    public static final String APP_ID = "ab8cf50692";
    public LocationService locationService;
    public Vibrator mVibrator;
    @Override
    public void onCreate() {
        super.onCreate();
        Beta.autoInit = true;
        Beta.autoCheckUpgrade = true;
        Beta.upgradeCheckPeriod = 60 * 1000;
        Beta.initDelay = 1 * 1000;
        Beta.largeIconId = R.mipmap.ic_launcher;
        Beta.smallIconId = R.mipmap.ic_launcher;
        Beta.defaultBannerId = R.mipmap.ic_launcher;
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        Beta.showInterruptedStrategy = true;
        //Beta.autoDownloadOnWifi = true;
        //Beta.canShowUpgradeActs.add(MainActivity.class);
        //Beta.upgradeDialogLayoutId = R.layout.upgrade_layout;

        Bugly.init(this, APP_ID, true);

        // 友盟
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
//        PushAgent mPushAgent = PushAgent.getInstance(this);

        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        UMConfigure.setLogEnabled(false);
        UMConfigure.init(this, "5e0b2e08cb23d2114c000ed6","Umeng",  UMConfigure.DEVICE_TYPE_PHONE, null);
        /*UMConfigure.init(this, "5e0b2e08cb23d2114c000ed6", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "ac0943931db1ec8ea6a4e7df7415d3fd");
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG,"注册成功：deviceToken：-------->  " + deviceToken);
            }
            @Override
            public void onFailure(String s, String s1) {
                Log.e(TAG,"注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });*/
    }

}
