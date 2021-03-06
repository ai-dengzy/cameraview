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

package com.yuchang.camera.ui.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apeng.permissions.EsayPermissions;
import com.apeng.permissions.OnPermission;
import com.apeng.permissions.Permission;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.yuchang.camera.CaptureListener;
import com.yuchang.camera.CommonUtil;
import com.yuchang.camera.DemoApplication;
import com.yuchang.camera.R;
import com.yuchang.camera.util.ImageUtil;
import com.yuchang.camera.util.LocationService;
import com.yuchang.camera.util.urlhttp.CallBackUtil;
import com.yuchang.camera.util.urlhttp.UrlHttpUtil;
//import com.umeng.message.PushAgent;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;
import com.yuchang.camera.view.PrivacyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MMActiviry extends AppCompatActivity implements Toolbar.OnMenuItemClickListener, CaptureListener,CameraKitEventListener {
    private static final double RAIO_WIDTH = 3;
    private static final double RAIO_HEIGHT = 4;
    private static int LIGHT_FLAG = 0;//0：自动；1：关闭；2：打开
    private CameraView cameraView;
    private Toolbar toolbar;
    private LocationService locationService;
    private TextView facingText;
    private TextView flashText;
    private TextView previewSizeText;
    private TextView photoSizeText;

    private Button flashOnButton;
    private Button flashOffButton;

    private Button photoButton;

    private Button facingFrontButton;
    private Button facingBackButton;

//    private Button permissionsButton;

    private ImageView imageView;
    private ImageView mIm_light;
    private static final String HTTP_PRE = "http://wthrcdn.etouch.cn/weather_mini?city=";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    //变量参数
    private boolean isAgreePrivacy; // 是否同意隐私政策，第一次同意后，不再弹出对话框
    private boolean b_watermark_switch;
    private boolean b_weather_switch;
    private boolean b_longitude_switch;
    private boolean b_add_switch;
    private boolean b_projectname_switch;
    private boolean b_place_switch;
    private boolean b_time_switch;
    private boolean b_custom_switch;
    private boolean b_voice_switch;
    private boolean b_abtain_switch;
    private boolean b_titileShow_switch;
    private boolean b_content;
    private int background_color_depth_flag = 1;
    private int background_color = 0;
    //    private int front_color_flag = -1;
    private int front_color = -1;
    private int front_size_flag = 0;
    private int front_size = 1;

    private String str_weather = "";
    private String str_longitude = "";
    private String str_latitude = "";
    private String str_add = "";
    private String str_projectname = "(待填)";
    private String str_place = "(待填)";
    private String str_time = "";
    private String str_abtain = "";
    private String str_titileShow = "";
    private String str_location = "";
    private String str_content = "作业内容";
    private String str_longitude_latitude = "";
    LinearLayout ll_titile_background;
    LinearLayout ll_add;
    LinearLayout ll_project_name;
    LinearLayout ll_place;
    LinearLayout ll_weather;
    LinearLayout ll_abtain;
    LinearLayout ll_logitude;
    LinearLayout ll_time;
    LinearLayout ll_content;
    TextView project_weather;
    TextView project_logitude_latitude;
    TextView tv_fixed_add;
    TextView jtv_weather;
    TextView jtv_logitude;
    TextView jtv_time;
    TextView tv_content;
    TextView jtv_content;
    TextView jtv_projectName;
    TextView jtv_abtain;
    TextView jtv_place;
    TextView tv_custom;
    TextView tv_titile;
    TextView tv_abtain;
    private SoundPool sp;//声明一个SoundPool
    private int music;//定义一个整型用load（）；来设置suondID
    private boolean isSaved = false;
    float paint_size;
    TextView tv_projectName;
    TextView jtv_title_pro;
    TextView tv_projectAdd;//地址信息内容
    TextView project_place;//施工单位内容
    TextView project_time;
    public static final int LOCATION_CODE = 301;
    public static final int PERSSION_CODE = 319;
    private String mLocality = "";
    private Toast mToast;
    private List<String> list_keyword;
    ImageView iv_light;
    private SharedPreferences mSharedPreferences;
    private RelativeLayout mLl_takened;
    private TextView mTv_test;
    ImageUtil imageUtil;
    private Bitmap mToLeftBottom1;
    double screenWidth;
    double screenHeight;
    double cameraHeight;
    double temp_offbottom;
    private int mHeight;//状态栏高度
    String sj_xinghao;//手机型号
    String sj_changshang;//手机型号
    boolean is_horizontion;
    private LinearLayout mLinearLayout;

    public static boolean isOppo() {
        String manufacturer = Build.MANUFACTURER;
        //这个字符串可以自己定义,例如判断华为就填写huawei,魅族就填写meizu
        if ("oppo".equalsIgnoreCase(manufacturer)) {
            return true;
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmactiviry);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.black),0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        cameraView = findViewById(R.id.camera);
        cameraView.setFacing(CameraKit.Constants.FACING_BACK);
        cameraView.addCameraKitListener(this);

        /**
         * 获取手机型号
         */
        sj_xinghao = android.os.Build.MODEL;

        /**
         * 获取手机厂商
         */
         sj_changshang = android.os.Build.BRAND;
         TextView textView = findViewById(R.id.textview);

//        ViewGroup.LayoutParams lp1;
//        lp1= cameraView.getLayoutParams();
//        lp1.height= (int) getResources().getDimension(R.dimen.px_900);
//        cameraView.setLayoutParams(lp1);

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(this);
        photoButton = findViewById(R.id.photoButton);
        photoButton.setOnClickListener(photoOnClickListener);
        mLinearLayout = findViewById(R.id.ll_title);
//        flashOnButton = findViewById(R.id.flashOnButton);
//        flashOffButton = findViewById(R.id.flashOffButton);
//        facingFrontButton = findViewById(R.id.facingFrontButton);
//        facingBackButton = findViewById(R.id.facingBackButton);
//        permissionsButton = findViewById(R.id.permissionsButton1);
        project_place = findViewById(R.id.project_place);
        project_time = findViewById(R.id.project_time);
        ll_titile_background = findViewById(R.id.ll_titile_background);
        ll_add = findViewById(R.id.ll_add);
        ll_project_name = findViewById(R.id.ll_project_name);
        ll_place = findViewById(R.id.ll_place);
        ll_weather = findViewById(R.id.ll_weather);
        ll_abtain = findViewById(R.id.ll_abtain);
        ll_logitude = findViewById(R.id.ll_logitude);
        ll_content = findViewById(R.id.ll_content);
        ll_time = findViewById(R.id.ll_time);
        //ll_titile_background.setVisibility(View.GONE);
        project_weather = findViewById(R.id.project_weather);
        project_logitude_latitude = findViewById(R.id.project_logitude_latitude);
        tv_titile = findViewById(R.id.tv_titile);
        tv_fixed_add = findViewById(R.id.tv_fixed_add);
        tv_projectAdd = findViewById(R.id.project_add);
        jtv_title_pro = findViewById(R.id.jtv_title);
        jtv_weather = findViewById(R.id.jtv_weather);
        jtv_logitude = findViewById(R.id.jtv_logitude);
        jtv_time = findViewById(R.id.jtv_time);
        jtv_content = findViewById(R.id.jtv_content);
        jtv_projectName = findViewById(R.id.jtv_projectName);
        jtv_abtain = findViewById(R.id.jtv_abtain);
        jtv_place = findViewById(R.id.jtv_place);
        tv_abtain = findViewById(R.id.tv_abtain);
        tv_custom = findViewById(R.id.tv_custom);
        tv_projectName = findViewById(R.id.project_name);
        tv_content = findViewById(R.id.tv_content);
        tv_projectName.setText(str_projectname);
        project_place = findViewById(R.id.project_place);
        project_place.setText(str_place);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = sdf.format(new Date());
        str_time = "" + time;
        project_time.setText(str_time); //更新时间
        imageUtil = new ImageUtil();
        sp = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(this, R.raw.takend, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
        list_keyword = new ArrayList<String>();
        mSharedPreferences = getSharedPreferences("camera", MODE_PRIVATE);
        isAgreePrivacy = mSharedPreferences.getBoolean("isAgreePrivacy", false);
        b_voice_switch = mSharedPreferences.getBoolean("sh_voice_switch", true);
        b_watermark_switch = mSharedPreferences.getBoolean("sh_watermark_switch", true);
        b_abtain_switch = mSharedPreferences.getBoolean("sh_abtain_switch", true);
        b_place_switch = mSharedPreferences.getBoolean("sh_watermark_projectadd", true);
        b_titileShow_switch = mSharedPreferences.getBoolean("sh_titileShow_switch", true);
        b_projectname_switch = mSharedPreferences.getBoolean("sh_watermark_projectname", true);
        b_add_switch = mSharedPreferences.getBoolean("sh_watermark_add", true);
        b_content = mSharedPreferences.getBoolean("sh_content", true);
        b_time_switch = mSharedPreferences.getBoolean("sh_watermark_projecttime", true);
        b_longitude_switch = mSharedPreferences.getBoolean("sh_watermark_longitude", true);
        b_weather_switch = mSharedPreferences.getBoolean("sh_watermark_weather", true);
        background_color_depth_flag = mSharedPreferences.getInt("background_color_depth_flag", 1);
        background_color = mSharedPreferences.getInt("background_color", -1);
        //front_color_flag = mSharedPreferences.getInt("front_color_flag",-1);
        front_color = mSharedPreferences.getInt("front_color", -1);
        front_size_flag = mSharedPreferences.getInt("front_size_flag", -1);
        str_abtain = mSharedPreferences.getString("et_abtainCompany", "");
        str_projectname = mSharedPreferences.getString("et_projectName", "");
        //str_location = mSharedPreferences.getString("et_location","str_位置信息");
        str_content = mSharedPreferences.getString("et_content", "");
        str_titileShow = mSharedPreferences.getString("et_titileShow", "");
        str_place = mSharedPreferences.getString("et_projectAdd", "");
        imageView = findViewById(R.id.imageView1);
        iniData();
        //友盟推送
//        PushAgent.getInstance(this).onAppStart();
        //打开相册
        findViewById(R.id.main_menu_gallery1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivity(intent);
                finish();
            }
        });

        //相机翻转
        findViewById(R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.toggleFacing();
            }
        });
        //设置
        findViewById(R.id.iv_setup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setUpActivity = new Intent(MMActiviry.this, SetUpActivity.class);
                setUpActivity.putExtra("str_projectname", str_projectname);
                setUpActivity.putExtra("str_place", str_place);
                setUpActivity.putExtra("str_weather", str_weather);
                setUpActivity.putExtra("sh_voice_switch", b_voice_switch);
                setUpActivity.putExtra("str_titileShow", str_titileShow);
                setUpActivity.putExtra("str_abtainCompany", str_abtain);
                setUpActivity.putExtra("str_add", str_add);
                setUpActivity.putExtra("str_location", str_location);
                setUpActivity.putExtra("str_content", str_content);
                setUpActivity.putExtra("str_time", str_time);
                setUpActivity.putExtra("str_longitude_latitude", str_longitude_latitude);
                startActivityForResult(setUpActivity, 0);
            }
        });
        //闪光灯
        mIm_light = findViewById(R.id.switch_flash);
        mIm_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraView != null) {
                    switch (LIGHT_FLAG) {
                        case 0://当闪关灯自动状态
                            //设置为关闭
                            LIGHT_FLAG = 1;
                            cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                            mIm_light.setBackground(MMActiviry.this.getResources().getDrawable(
                                    R.drawable.icon_light_close));
                            break;
                        case 1://当闪光灯关闭状态
                            //设置为打开
                            LIGHT_FLAG = 2;
                            cameraView.setFlash(CameraKit.Constants.FLASH_ON);
                            mIm_light.setBackground(MMActiviry.this.getResources().getDrawable(
                                    R.drawable.icon_light_open));
                            break;
                        case 2://当闪光灯打开状态
                            //设置为打开
                            LIGHT_FLAG = 0;
                            cameraView.setFlash(CameraKit.Constants.FLASH_AUTO);
                            mIm_light.setBackground(MMActiviry.this.getResources().getDrawable(
                                    R.drawable.icon_light));
                            break;
                    }
                }
            }
        });

        setCaptureLisenter(this);
        //友盟推送
//        PushAgent.getInstance(this).onAppStart();
        mLl_takened = findViewById(R.id.rl_bottom);
        WindowManager wm = this.getWindowManager();
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        mHeight = getResources().getDimensionPixelSize(resourceId);
        //        Display display = wm.getDefaultDisplay() ;
//        int height = display.getHeight();
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        cameraHeight = (screenWidth / RAIO_WIDTH)*RAIO_HEIGHT;
        double raio = screenHeight / screenWidth;
        RelativeLayout.LayoutParams layoutParams;
        if (raio > 2.0) {
            // 移动相机
            layoutParams = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
            layoutParams.height = (int) cameraHeight;
            temp_offbottom = (int) (getResources().getDimension(R.dimen.px_160)) / 2;
            layoutParams.bottomMargin = (int) temp_offbottom;
            cameraView.setLayoutParams(layoutParams);
            // 移动底部拍照布局
            RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) mLl_takened.getLayoutParams();
            temp_offbottom += (int) ((getResources().getDimension(R.dimen.px_200)) / 2);
            layoutParams1.bottomMargin = (int) ((getResources().getDimension(R.dimen.px_200)) / 2);
//            mLl_takened.setLayoutParams(layoutParams1);
        } else if (raio > 1.9) {
            // 移动相机
            layoutParams = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
            layoutParams.height = (int) cameraHeight;
            temp_offbottom = (int) (getResources().getDimension(R.dimen.px_150)) / 2;
            layoutParams.bottomMargin = (int) temp_offbottom;
            cameraView.setLayoutParams(layoutParams);
            // 移动底部拍照布局
            RelativeLayout.LayoutParams layoutParams1 =
                    (RelativeLayout.LayoutParams) mLl_takened.getLayoutParams();
            temp_offbottom += (int) (getResources().getDimension(R.dimen.px_150)) / 2;
            layoutParams1.bottomMargin = (int) (getResources().getDimension(R.dimen.px_150)) / 2;
//            mLl_takened.setLayoutParams(layoutParams1);
        } else if (raio > 1.8) {
            // 移动相机
            layoutParams = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
            layoutParams.height = (int) cameraHeight;
            temp_offbottom = (int) (getResources().getDimension(R.dimen.px_100)) / 2;
            layoutParams.bottomMargin = (int) temp_offbottom;
            cameraView.setLayoutParams(layoutParams);
            // 移动底部拍照布局
            RelativeLayout.LayoutParams layoutParams1 =
                    (RelativeLayout.LayoutParams) mLl_takened.getLayoutParams();
            layoutParams1.bottomMargin = (int) (getResources().getDimension(R.dimen.px_100)) / 2;
            temp_offbottom += (int) (getResources().getDimension(R.dimen.px_100)) / 2;
//            mLl_takened.setLayoutParams(layoutParams1);
        }
        ViewGroup.LayoutParams lp = cameraView.getLayoutParams();
        lp.height= (int) cameraHeight;
        cameraView.setLayoutParams(lp);


        /*EsayPermissions.with(this)
                .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
//                .permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA,Permission.ACCESS_FINE_LOCATION,Permission.ACCESS_COARSE_LOCATION)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            Toast.makeText(MMActiviry.this, "已获取相机权限", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MMActiviry.this, "取权限成功，部分权限未正常授予", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            Toast.makeText(MMActiviry.this, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_LONG).show();
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            EsayPermissions.gotoPermissionSettings(MMActiviry.this);
                        } else {
                            Toast.makeText(MMActiviry.this, "获取权限失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });*/
        //不添加华为手机百度地图获取不到数据
      /*  if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED|| ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
        }*/

        //部分手机拍照关闭声音后，还有声音。使用系统静音处理
        // getDoNotDisturb();

        // 没有同意隐私政策
        if (!isAgreePrivacy){
            showPrivacy();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // -----------location config ------------
        locationService = ((DemoApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity
        // ，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
//        int type = getIntent().getIntExtra("from", 0);
//        if (type == 0) {
//            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
//        } else if (type == 1) {
        locationService.start();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAgreePrivacy){
            // 动态请求权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    &&ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    &&ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    &&ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraView.start();
            }  else {
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERSSION_CODE);
            }
        }
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {

        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.stop();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.main_menu_about) {
            AlertDialog dialog = new AlertDialog.Builder(MMActiviry.this)
                    .setTitle(R.string.about_dialog_title)
                    .setMessage(R.string.about_dialog_message)
                    .setNeutralButton("Dismiss", null)
                    .show();

            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#91B8CC"));
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setText(Html.fromHtml("<b>Dismiss</b>"));
            return true;
        }

        if (item.getItemId() == R.id.main_menu_gallery) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//            startActivity(intent);
            return true;
        }
        return false;
    }
    private void getNetWeather(String city) {
        UrlHttpUtil.get(HTTP_PRE + city, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(int code, String errorMessage) {

            }

            @Override
            public void onResponse(String response) {
                JSONObject dataJson = null;
                try {
                    dataJson = new JSONObject(response);
                    JSONObject response1 = dataJson.getJSONObject("data");
                    JSONArray data = response1.getJSONArray("forecast");
                    JSONObject info = data.getJSONObject(0);
                    String high = info.getString("high").substring(2);
                    String low = info.getString("low").substring(2);
                    String type = info.getString("type");
                    String fengxiang = info.getString("fengxiang");
                    str_weather = "" + type + "," + fengxiang + "," + low + " ~" + high;
                    project_weather.setText(str_weather);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                int tag = 1;
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                str_latitude = location.getLatitude() + "";
                sb.append(location.getLatitude());
                sb.append("\nlongtitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nProvince : ");// 获取省份
                sb.append(location.getProvince());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                mLocality = location.getCityCode();
                sb.append("\ncity : ");// 城市
                mLocality = location.getCity();
                getNetWeather(mLocality);
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nTown : ");// 获取镇信息
                sb.append(location.getTown());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
//                str_location = location.getAddrStr();
                if (!isSaved) {
                    String addstr = location.getAddrStr();
                    String addstr_t = location.getAddrStr().substring(0,2);
                    if (addstr_t.equals("中国")){
                        addstr = addstr.substring(2,addstr.length());
                        str_location = addstr;
                    }
                    tv_projectAdd.setText(addstr);
                    str_longitude = location.getLongitude() + "";
                    str_longitude_latitude = str_longitude + "/" + str_latitude;
                    project_logitude_latitude.setText(str_longitude_latitude);
                }
                sb.append(location.getAddrStr());
                sb.append("\nStreetNumber : ");// 获取街道号码
                sb.append(location.getStreetNumber());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append("poiName:");
                        sb.append(poi.getName() + ", ");
                        sb.append("poiTag:");
                        sb.append(poi.getTags() + "\n");
                    }
                }
                if (location.getPoiRegion() != null) {
                    sb.append(
                            "PoiRegion: ");// 返回定位位置相对poi的位置关系，仅在开发者设置需要POI
                    // 信息时才会返回，在网络不通或无法获取时有可能返回null
                    PoiRegion poiRegion = location.getPoiRegion();
                    sb.append("DerectionDesc:"); // 获取POIREGION的位置关系，ex:"内"
                    sb.append(poiRegion.getDerectionDesc() + "; ");
                    sb.append("Name:"); // 获取POIREGION的名字字符串
                    sb.append(poiRegion.getName() + "; ");
                    sb.append("Tags:"); // 获取POIREGION的类型
                    sb.append(poiRegion.getTags() + "; ");
                    sb.append("\nSDK版本: ");
                }
                sb.append(locationService.getSDKVersion()); // 获取SDK版本
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                    //str_weather = "离线状态获取失败";
                    //str_location = "离线状态获取失败";
                    //project_weather.setText("离线状态获取失败");
                    //tv_projectAdd.setText("离线状态获取失败");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                    //project_weather.setText("服务端网络定位失败");
                    //tv_projectAdd.setText("服务端网络定位失败");
                    //str_weather = "服务端网络定位失败";
                    //str_location = "服务端网络定位失败";
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                    //project_weather.setText("请检查网络是否通畅");
                    //tv_projectAdd.setText("请检查网络是否通畅");
                    //str_weather = "请检查网络是否通畅";
                    //str_location = "请检查网络是否通畅";
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    //project_weather.setText
                    // ("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    //tv_projectAdd.setText
                    // ("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    //str_weather = "无法获取有效定位依据导致定位失败";
                    //str_location = "无法获取有效定位依据导致定位失败";
                }
                //Log.d("AAAAAA",sb.toString());
                //mTv_test.setText(sb+"sss");
                //mTv_test.setMovementMethod(ScrollingMovementMethod.getInstance());
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            super.onConnectHotSpotMessage(s, i);
        }
    };
//    float canvasTextSize1 = 42;
//    float canvasTextSize2 = 44;
//    float canvasTextSize3 = 46;
//    float canvasTextSize4 = 48;
    float canvasTextSize1 = 42;
    float canvasTextSize2 = 50;
    float canvasTextSize3 = 58;
    float canvasTextSize4 = 66;
    private CaptureListener captureLisenter;        //按钮回调接口
    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //回调拍照接口
            captureLisenter.takePictures();
        }
    };



    private void saveImageToGallery_test(Bitmap bitmap) {
        //生成路径
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirName = "电企通相机";
        File appDir = new File(root, dirName);
        if (!appDir.exists()) {
            boolean mkdirs = appDir.mkdirs();
        }
        //文件名为时间
        long timeStamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        String sd = sdf.format(new Date(timeStamp));
        String fileName = sd + ".jpg";
        //获取文件
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            final FileOutputStream finalFos = fos;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, finalFos);
            if (!is_horizontion)
                Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();
            fos.flush();
            //通知系统相册刷新
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File(file.getPath()))));
        } catch (FileNotFoundException e) {
            saveImage(bitmap);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImage(Bitmap bmp) {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
        File appDir = new File(Environment.getExternalStorageDirectory(), "电企通相机");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (!is_horizontion)
                Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            saveImageToGallery(bmp);
        }
        // 发送广播，通知刷新图库的显示
        sendBroadcast(
                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        //通知系统相册刷新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(file.getPath()))));
    }

    public void saveImageToGallery(Bitmap bmp) {
        String[] PERMISSIONS = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"};
        //检测是否有写的权限
        int permission = ContextCompat.checkSelfPermission(this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        /*
         * 保存文件，文件名为当前日期
         */
        String fileName;
        File file;
        if (Build.BRAND.equals("Xiaomi")) { // 小米手机  ----> 电企通相机改为了“DCIM”
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/"
                    + format.format(new Date()) + ".JPEG";
        } else { // Meizu 、Oppo
            fileName =
                    Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + format.format(
                            new Date()) + ".JPEG";
        }
        file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
                // 插入图库
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(),
                        format.format(new Date()) + ".JPEG", null);
                if (!is_horizontion)
                    Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        sendBroadcast(
                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        //通知系统相册刷新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(file.getPath()))));
    }

    private static int baseBitmapWidth = 1080;//在拍出照片的bitmap为1080*1920的真机下测试
    private static int baseBitmapHeight = 1920;
    /**
     * 设置默认的百分比，所有拍照画到画布上的文字、底色都必须走此方法
     */
    public static float getPxRatio(float realBitmapWidth, float realBitmapHeight) {
        float ratioWidth = realBitmapWidth / baseBitmapWidth;
        float ratioHeight = realBitmapHeight / baseBitmapHeight;
        return Math.min(ratioWidth, ratioHeight);
    }

    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            background_color = data.getExtras().getInt("background_color");
            front_color = data.getExtras().getInt("front_color");
            str_projectname = "" + data.getExtras().getString("name");
            str_place = "" + data.getExtras().getString("add");
            str_time = "" + data.getExtras().getString("time");
            str_abtain = "" + data.getExtras().getString("et_abtainCompany");
            front_size = data.getExtras().getInt("front_size");
            b_watermark_switch = data.getExtras().getBoolean("b_watermark_switch");
            b_weather_switch = data.getExtras().getBoolean("b_weather_switch");
            b_longitude_switch = data.getExtras().getBoolean("b_longitude_switch");
            b_add_switch = data.getExtras().getBoolean("b_add_switch");
            b_projectname_switch = data.getExtras().getBoolean("b_projectname_switch");
            b_place_switch = data.getExtras().getBoolean("b_place_switch");
            b_time_switch = data.getExtras().getBoolean("b_time_switch");
            b_custom_switch = data.getExtras().getBoolean("b_custom_switch");
            background_color_depth_flag = data.getExtras().getInt("background_color_depth");
            b_voice_switch = data.getExtras().getBoolean("sh_voice_switch");
            b_abtain_switch = data.getExtras().getBoolean("b_abtain_switch");
            b_titileShow_switch = data.getExtras().getBoolean("b_titileShow_switch");
            str_titileShow = data.getExtras().getString("et_titileShow");
            str_content = data.getExtras().getString("et_content");
            str_weather = data.getExtras().getString("et_weather");
            str_location = data.getExtras().getString("et_location");
            b_content = data.getExtras().getBoolean("b_content");
            isSaved = data.getExtras().getBoolean("isSaved");
            if (!str_projectname.isEmpty()) {
                tv_projectName.setText(str_projectname);
            }
            if (!str_add.isEmpty()) {
                project_place.setText(str_place);
            }
            if (!str_time.isEmpty()) {
                project_time.setText(str_time);
            }
            if (background_color != -1) {
                setTitleBackgroundColor();
            }
            if (front_color != 0) {
                setTitleColor();
            }
            if (front_size != -1) {
                setFrontSize();
            }
            iniData();
        }
    }

    private void setFrontSize() {
        if (sj_xinghao.equals("PBAM00") ){//OPPOA5的是手机型号是PBAM00字体很小不知道是为啥，单独处理
            switch (front_size) {
                case 0:
                    float dimension = getResources().getDimension(R.dimen.px_text_10);
                    specificFrontSize(dimension);
                    break;
                case 1:
                    float dimension1 = getResources().getDimension(R.dimen.px_text_11);
                    specificFrontSize(dimension1);
                    break;
                case 2:
                    float dimension2 = getResources().getDimension(R.dimen.px_text_12);
                    specificFrontSize(dimension2);
                    break;
                case 3:
                    float dimension3 = getResources().getDimension(R.dimen.px_text_14);
                    specificFrontSize(dimension3);
                    break;
            }
        }else if (isOppo()){
            switch (front_size) {
                case 0:
                    float dimension = getResources().getDimension(R.dimen.px_text_4);
                    specificFrontSize(dimension);
                    break;
                case 1:
                    float dimension1 = getResources().getDimension(R.dimen.px_text_5);
                    specificFrontSize(dimension1);
                    break;
                case 2:
                    float dimension2 = getResources().getDimension(R.dimen.px_text_6);
                    specificFrontSize(dimension2);
                    break;
                case 3:
                    float dimension3 = getResources().getDimension(R.dimen.px_text_7);
                    specificFrontSize(dimension3);
                    break;
            }
        }else{
            switch (front_size) {
                case 0:
                    float dimension = getResources().getDimension(R.dimen.px_text_7);
                    specificFrontSize(dimension);
                    break;
                case 1:
                    float dimension1 = getResources().getDimension(R.dimen.px_text_8);
                    specificFrontSize(dimension1);
                    break;
                case 2:
                    float dimension2 = getResources().getDimension(R.dimen.px_text_9);
                    specificFrontSize(dimension2);
                    break;
                case 3:
                    float dimension3 = getResources().getDimension(R.dimen.px_text_10);
                    specificFrontSize(dimension3);
                    break;
            }
        }
    }

    private void specificFrontSize(float dimension) {
        project_weather.setTextSize(dimension);
        project_time.setTextSize(dimension);

        tv_fixed_add.setTextSize(dimension);
        jtv_weather.setTextSize(dimension);
        project_logitude_latitude.setTextSize(dimension);
        jtv_logitude.setTextSize(dimension);
        jtv_time.setTextSize(dimension);
        jtv_title_pro.setTextSize(dimension);
        tv_titile.setTextSize(dimension);
        tv_content.setTextSize(dimension);
        jtv_content.setTextSize(dimension);
        jtv_projectName.setTextSize(dimension);
        tv_abtain.setTextSize(dimension);
        jtv_abtain.setTextSize(dimension);
        jtv_place.setTextSize(dimension);
        tv_projectAdd.setTextSize(dimension);
        tv_projectName.setTextSize(dimension);
        project_place.setTextSize(dimension);
    }

    private void setTitleBackgroundColor() {
        switch (background_color_depth_flag) {
            case 0:
                ll_titile_background.setBackgroundColor(
                        getResources().getColor(R.color.titi_background_color_white_transparent));
                break;
            case 1:
                switch (background_color) {
                    case 0:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_white_1));
                        break;
                    case 1:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_blue_1));
                        break;
                    case 2:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_green_1));
                        break;
                    case 3:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_yellow_1));
                        break;
                    case 4:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_red_1));
                        break;
                    case 5:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_black_1));
                        break;
                    case 6:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_pruple_1));
                        break;
                }
                break;
            case 2:

                switch (background_color) {
                    case 0:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_white_2));
                        break;
                    case 1:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_blue_2));
                        break;
                    case 2:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_green_2));
                        break;
                    case 3:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_yellow_2));
                        break;
                    case 4:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_red_2));
                        break;
                    case 5:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_black_2));
                        break;
                    case 6:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_pruple_2));
                        break;
                }
                break;
            case 3:
                switch (background_color) {
                    case 0:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_white_3));
                        break;
                    case 1:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_blue_3));
                        break;
                    case 2:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_green_3));
                        break;
                    case 3:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_yellow_3));
                        break;
                    case 4:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_red_3));
                        break;
                    case 5:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_black_3));
                        break;
                    case 6:
                        ll_titile_background.setBackgroundColor(
                                getResources().getColor(R.color.titi_background_color_pruple_3));
                        break;
                }
                break;
        }
    }

    private void setTitleColor() {
        project_weather.setTextColor(front_color);
        tv_fixed_add.setTextColor(front_color);
        jtv_weather.setTextColor(front_color);
        project_logitude_latitude.setTextColor(front_color);
        jtv_logitude.setTextColor(front_color);
        jtv_time.setTextColor(front_color);
        jtv_title_pro.setTextSize(front_color);
        tv_titile.setTextSize(front_color);
        tv_content.setTextColor(front_color);
        jtv_content.setTextColor(front_color);
        jtv_projectName.setTextColor(front_color);
        tv_abtain.setTextColor(front_color);
        jtv_abtain.setTextColor(front_color);
        jtv_place.setTextColor(front_color);
        tv_projectAdd.setTextColor(front_color);
        tv_projectName.setTextColor(front_color);
        project_place.setTextColor(front_color);
        project_time.setTextColor(front_color);
    }

    private void iniData() {
//        ViewGroup.LayoutParams lp;
//        lp= cameraView.getLayoutParams();
//        lp.height= (int) getResources().getDimension(R.dimen.px_500);
//        cameraView.setLayoutParams(lp);


        //编辑内容
        tv_abtain.setText(str_abtain);
        jtv_title_pro.setText("作业内容：");
        tv_titile.setText(str_titileShow);
        project_place.setText(str_place);
        tv_projectName.setText(str_projectname);
        tv_projectAdd.setText(str_location);
        tv_content.setText(str_content);
        project_time.setText(str_time);
        project_logitude_latitude.setText(str_longitude_latitude);
        project_weather.setText(str_weather);
        //设置颜色深度 //设置背景色
        setTitleBackgroundColor();
        //设置字体颜色
        setTitleColor();
        //设置字体大小
        setFrontSize();
        //水印开关
        if (b_watermark_switch) {
            ll_titile_background.setVisibility(View.VISIBLE);
        } else {
            ll_titile_background.setVisibility(View.INVISIBLE);
        }
        //标题是否显示
        if (b_titileShow_switch) {
            mLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mLinearLayout.setVisibility(View.GONE);
        }
        //施工单位开关
        if (b_place_switch) {
            ll_place.setVisibility(View.VISIBLE);
        } else {
            ll_place.setVisibility(View.GONE);
        }
        //取证单位
        if (b_abtain_switch) {
            ll_abtain.setVisibility(View.VISIBLE);
        } else {
            ll_abtain.setVisibility(View.GONE);
        }
        //项目名称开关
        if (b_projectname_switch) {
            ll_project_name.setVisibility(View.VISIBLE);
        } else {
            ll_project_name.setVisibility(View.GONE);
        }
        //位置信息开关
        if (b_add_switch) {
            ll_add.setVisibility(View.VISIBLE);
        } else {
            ll_add.setVisibility(View.GONE);
        }
        //作业内容开关
        if (b_content) {
            ll_content.setVisibility(View.VISIBLE);
        } else {
            ll_content.setVisibility(View.GONE);
        }
        //当前日期开关
        if (b_time_switch) {
            ll_time.setVisibility(View.VISIBLE);
        } else {
            ll_time.setVisibility(View.GONE);
        }
        //经纬度数开关
        if (b_longitude_switch) {
            ll_logitude.setVisibility(View.VISIBLE);
        } else {
            ll_logitude.setVisibility(View.GONE);
        }
        //天气状况开关
        if (b_weather_switch) {
            ll_weather.setVisibility(View.VISIBLE);
        } else {
            ll_weather.setVisibility(View.GONE);
        }
        if (b_custom_switch) {
            tv_custom.setVisibility(View.VISIBLE);
        } else {
            tv_custom.setVisibility(View.GONE);
        }
    }

    public void setCaptureLisenter(CaptureListener captureLisenter) {
        this.captureLisenter = captureLisenter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (mToLeftBottom1!=null)
                    saveImageToGallery_test(mToLeftBottom1);
                break;
        }
    }

    @Override
    public void takePictures() {
        cameraView.captureImage();
    }

  /*  @Override
    public void recordShort(long time) {

    }

    @Override
    public void recordStart() {

    }

    @Override
    public void recordEnd(long time) {

    }

    @Override
    public void recordZoom(float zoom) {

    }

    @Override
    public void recordError() {

    }*/

    @Override
    public void onEvent(CameraKitEvent cameraKitEvent) {

    }

    @Override
    public void onError(CameraKitError cameraKitError) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onImage(CameraKitImage cameraKitImage) {
        // 播放拍照声音
        if (cameraView != null) {
            if (b_voice_switch) {
                sp.play(music, 1, 1, 0, 0, 1);
            }
        }

        //部分手机拍照关闭声音后，还有声音。使用系统静音处理
        // silentSwitchOn();

        Bitmap bitmap;
        if (cameraView.isFacingFront()) {
            bitmap = CommonUtil.loadBitmap(cameraKitImage.getJpeg());
        } else {
            bitmap = cameraKitImage.getBitmap();
        }

        //拍照原图为全屏的照片，对照片进行4:3的比例裁剪
        double temp_bottom = ((screenHeight - cameraHeight) + temp_offbottom) / 2;
        int initCropHeight = (int) ((screenHeight - cameraHeight) - temp_bottom);
        if (bitmap.getWidth()>bitmap.getHeight()){
            // 横屏
            is_horizontion = true;
            Toast.makeText(this, "此相机库不建议横屏，照片保存中...", Toast.LENGTH_SHORT).show();
//            bitmap = cropBitmapHorizontal(bitmap,initCropHeight+mHeight*2);
        }else{
            // 竖屏
            is_horizontion = false;
             bitmap = cropBitmap(bitmap,initCropHeight+mHeight*2);
        }
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//转换为支持的横竖屏


//        imageView.setImageBitmap(bitmap);

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(front_color);
        float bitmapWidth = bitmap.getWidth();
        float bitmapHeight = bitmap.getHeight();
        float ratio = getPxRatio(bitmapWidth, bitmapHeight);
        // 根据设置的字体大小，自动动态设置保存照片画布的宽
        int canvas_width = 5;
        switch (front_size) {
            case 0:
                paint_size = canvasTextSize1 * ratio;
                canvas_width = 4;
                break;
            case 1:
                paint_size = canvasTextSize2 * ratio;
                canvas_width = 5;
                break;
            case 2:
                paint_size = canvasTextSize3 * ratio;
                canvas_width = 5;
                break;
            case 3:
                paint_size = canvasTextSize4 * ratio;
                canvas_width = 6;
                break;
        }
        paint.setTextSize(paint_size);
        list_keyword.clear();
        if (b_place_switch) {
            list_keyword.add("施工单位：" + str_place);
        }
        if (b_abtain_switch) {
            list_keyword.add("取证单位：" + str_abtain);
        }
        if (b_projectname_switch) {
            list_keyword.add("项目名称：" + str_projectname);
        }
        if (b_add_switch) {
            list_keyword.add("^_^" + tv_projectAdd.getText());
        }
        if (b_content) {
            list_keyword.add("备注内容：" + str_content);
        }
        if (b_time_switch) {
            list_keyword.add("当前日期：" + str_time);
        }
        if (b_longitude_switch) {
            list_keyword.add("经纬度数：" + str_longitude_latitude);
        }
        if (b_weather_switch) {
            list_keyword.add("天气状况：" + str_weather);
        }
        if (!b_watermark_switch) {
            list_keyword.clear();
        }
//        if (bitmapWidth>bitmapHeight) {//只竖屏拍照
//            bitmap = imageUtil.createDegree(bitmap, 90);
//        }
        float paddingBottom = 150 * getPxRatio(bitmap.getWidth(), bitmap.getHeight());
        mToLeftBottom1 = imageUtil.drawTextToLeftBottom(MMActiviry.this, bitmap,
                list_keyword, b_titileShow_switch, "作业内容："+str_titileShow,
                paint, 40 * getPxRatio(bitmap.getWidth(), bitmap.getHeight()), paddingBottom,
                background_color_depth_flag, background_color,canvas_width);
//                                imageView.setImageBitmap(toLeftBottom1);
        saveImageToGallery_test(mToLeftBottom1);
        if (!is_horizontion)
            Toast.makeText(MMActiviry.this, "已保存", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVideo(CameraKitVideo cameraKitVideo) {

    }



    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    private Bitmap cropBitmap(Bitmap bitmap,int initCropHeight) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int cropHeight = (int) ((bitmapWidth/RAIO_WIDTH)*RAIO_HEIGHT);
        if ((cropHeight+initCropHeight)>bitmapHeight){
            cropHeight = bitmapHeight;
            initCropHeight = 0;
        }
        return Bitmap.createBitmap(bitmap,0, initCropHeight, bitmapWidth,
                cropHeight, null, false);
    }
    private Bitmap cropBitmapHorizontal(Bitmap bitmap,int initCropHeight) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int cropHeight = (int) ((bitmapWidth/RAIO_WIDTH)*RAIO_HEIGHT);
        if (cropHeight>bitmapHeight)
            cropHeight = bitmapHeight;
        return Bitmap.createBitmap(bitmap,0, initCropHeight, bitmapWidth,
                cropHeight, null, false);
    }


    private void silentSwitchOn() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if(audioManager != null){
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.getStreamVolume(AudioManager.STREAM_RING);
            Log.d("Silent:", "RINGING 已被静音");
        }
    }

    private void silentSwitchOff() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if(audioManager != null){
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.getStreamVolume(AudioManager.STREAM_RING);
            Log.d("SilentListenerService", "RINGING 取消静音");
        }
    }
    //获取Do not disturb权限,才可进行音量操作
    private void getDoNotDisturb(){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
    }

    /**
     * 显示用户协议和隐私政策
     */
    private void showPrivacy() {
        final PrivacyDialog dialog = new PrivacyDialog(MMActiviry.this);
        TextView tv_privacy_tips = dialog.findViewById(R.id.tv_privacy_tips);
        TextView btn_exit = dialog.findViewById(R.id.btn_exit);
        TextView btn_enter = dialog.findViewById(R.id.btn_enter);
        dialog.show();
        String string = getResources().getString(R.string.privacy_tips);
        String key1 = getResources().getString(R.string.privacy_tips_key1);
        int index1 = string.indexOf(key1);
        //需要显示的字串
        SpannableString spannedString = new SpannableString(string);
        //设置点击字体颜色
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.colorBlue));
        spannedString.setSpan(colorSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击字体大小
        TextView tvTip = dialog.findViewById(R.id.tv_privacy_tips);
//        AbsoluteSizeSpan sizeSpan1 = new AbsoluteSizeSpan(, true);
        spannedString.setSpan(null, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击事件
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                //打开隐私政策网址
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://ycdlfw.com/userAgreement/")));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        spannedString.setSpan(clickableSpan1, index1, index1 + key1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置点击后的颜色为透明，否则会一直出现高亮
        tv_privacy_tips.setHighlightColor(Color.TRANSPARENT);
        //开始响应点击事件
        tv_privacy_tips.setMovementMethod(LinkMovementMethod.getInstance());
        tv_privacy_tips.setText(spannedString);
        //设置弹框宽度占屏幕的80%
        WindowManager m = getWindowManager();
        Display defaultDisplay = m.getDefaultDisplay();
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (defaultDisplay.getWidth() * 0.80);
        dialog.getWindow().setAttributes(params);

        final boolean[] isExit = {false};
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExit[0]){
                    finish();
                    dialog.dismiss();
                }else{
                    Toast.makeText(MMActiviry.this,"退出后将无法使用此应用,确定吗?",Toast.LENGTH_LONG).show();
                    isExit[0] = true;
                }
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                SPUtil.put(MMActiviry.this, SP_VERSION_CODE, currentVersionCode);
//                SPUtil.put(MMActiviry.this, SP_PRIVACY, true);
                // 用户首次进入已同意隐私政策，设置变量值
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean("isAgreePrivacy",true);
                editor.commit();
                Toast.makeText(MMActiviry.this, getString(R.string.confirmed), Toast.LENGTH_SHORT).show();
                isAgreePrivacy = true;
                onResume();
            }
        });

    }



}

