package com.wonderful.mobilelibrary;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import java.io.File;

import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static cn.bmob.v3.b.From.e;
import static java.lang.Thread.*;

public class UploadActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UploadActivity";

    private Button choose;
    private Button startUpload;
    private RadioButton survive;
    private RadioButton fitness;
    private RadioButton cook;
    private RadioButton amuse;
    private RadioButton personal;
    private RadioButton expose;
    private static String category = null;
    private static String privacy = null;
    private String uploadUrl;
    private String uploadName;
    private boolean LOAD = false;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        choose = (Button)findViewById(R.id.upload_choose);
        startUpload = (Button)findViewById(R.id.upload_upload);
        survive = (RadioButton)findViewById(R.id.upload_survive);
        fitness = (RadioButton)findViewById(R.id.upload_fitness);
        cook = (RadioButton)findViewById(R.id.upload_cook);
        amuse = (RadioButton)findViewById(R.id.upload_amuse);
        personal = (RadioButton)findViewById(R.id.upload_personal);
        expose = (RadioButton)findViewById(R.id.upload_public);
        progressBar = (ProgressBar)findViewById(R.id.upload_progress_bar);

        choose.setOnClickListener(this);
        startUpload.setOnClickListener(this);
        survive.setOnClickListener(this);
        fitness.setOnClickListener(this);
        cook.setOnClickListener(this);
        amuse.setOnClickListener(this);
        personal.setOnClickListener(this);
        expose.setOnClickListener(this);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume(){
        super.onResume();
        category = null;
        privacy = null;
        if(LOAD){
            choose.setBackgroundResource(R.drawable.apple_pic);
        }
        SharedPreferences pref = getSharedPreferences("MobileLibrary",MODE_PRIVATE);
        boolean uploadFinish = pref.getBoolean("uploadFinish",true);
        int progress = pref.getInt("progress",0);
        if(uploadFinish){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.GONE);
        }else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, //禁止交互
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);
        }
    }

    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.upload_choose:
                intent = new Intent(UploadActivity.this,WhatToUploadActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.upload_survive:
                category = "SURVIVE";
                break;
            case R.id.upload_fitness:
                category = "FITNESS";
                break;
            case R.id.upload_cook:
                category = "COOK";
                break;
            case R.id.upload_amuse:
                category = "AMUSE";
                break;
            case R.id.upload_personal:
                privacy = "PERSONAL";
                break;
            case R.id.upload_public:
                privacy = "EXPOSE";
                break;
            case R.id.upload_upload:
                if(LOAD){
                    if(privacy == null||category == null){
                        showToast("请选择相关属性");
                        return;
                    }
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.VISIBLE);

                    SharedPreferences.Editor editor = getSharedPreferences("MobileLibrary",MODE_PRIVATE).edit();
                    editor.putBoolean("uploadFinish",false);
                    editor.apply();
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, //禁止交互
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    File file = new File(uploadUrl);
                    final BmobFile bmobFile = new BmobFile(file);
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                SharedPreferences.Editor editor = getSharedPreferences("MobileLibrary",MODE_PRIVATE).edit();
                                editor.putBoolean("uploadFinish",true);
                                editor.apply();
                                insertObject(new UploadVideo(uploadName,bmobFile));
                                progressBar.setVisibility(View.GONE);
                                progressBar.setProgress(0);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }else {
                                MyLog.e(TAG,"上传失败" + e.getMessage() + e.getErrorCode());
                                MyLog.e(TAG,"错误地址=" + uploadUrl);
                            }
                        }
                        @Override
                        public void onProgress(Integer value) {
                            progressBar.setProgress(value);
                            SharedPreferences.Editor editor = getSharedPreferences("MobileLibrary",MODE_PRIVATE).edit();
                            editor.putInt("progress",value);
                            editor.apply();
                        }
                    });
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    uploadUrl = data.getStringExtra("uploadUrl");
                    uploadName = data.getStringExtra("uploadName");
                    LOAD = true;
                }
                break;
            default:
                break;
        }
    }

    private void insertObject(UploadVideo object){
        MobileLibraryUser author = BmobUser.getCurrentUser(MobileLibraryUser.class);
        BmobACL acl = new BmobACL();
        if(privacy.equals("EXPOSE")){
            acl.setPublicReadAccess(true);
        }else if(privacy.equals("PERSONAL")){
            acl.setReadAccess(BmobUser.getCurrentUser(),true);
        }
        acl.setWriteAccess(BmobUser.getCurrentUser(), true);   // 设置当前用户可写的权限
        object.setCategory(category);
        object.setPrivacy(privacy);
        object.setAuthor(author);
        object.setACL(acl);
        object.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    showToast("添加数据成功");
                }else{
                    MyLog.e(TAG,"添加数据失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    break;
                default:
                    break;
            }
        }
    };

}
