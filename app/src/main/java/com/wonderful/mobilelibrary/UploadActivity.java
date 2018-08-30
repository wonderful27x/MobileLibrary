package com.wonderful.mobilelibrary;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class UploadActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UploadActivity";

    private Button choose;
    private Button startUpload;
    private RadioButton survive;
    private RadioButton fitness;
    private RadioButton cook;
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
        personal = (RadioButton)findViewById(R.id.upload_personal);
        expose = (RadioButton)findViewById(R.id.upload_public);
        progressBar = (ProgressBar)findViewById(R.id.upload_progress_bar);

        choose.setOnClickListener(this);
        startUpload.setOnClickListener(this);
        survive.setOnClickListener(this);
        fitness.setOnClickListener(this);
        cook.setOnClickListener(this);
        personal.setOnClickListener(this);
        expose.setOnClickListener(this);

        progressBar.setVisibility(View.GONE);
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
                    File file = new File(uploadUrl);
                    final BmobFile bmobFile = new BmobFile(file);
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                Message message =new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                                insertObject(new UploadVideo(uploadName,bmobFile));
                                //insertObject(new UploadVideo(uploadName,bmobFile.getFileUrl()));
                            }else {
                                MyLog.e(TAG,"上传失败" + e.getMessage() + e.getErrorCode());
                                MyLog.e(TAG,"错误地址=" + uploadUrl);
                            }
                        }
                        @Override
                        public void onProgress(Integer value) {
                            // 返回的上传进度（百分比）
                            progressBar.setProgress(value);
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

    @Override
    protected void onResume(){
        super.onResume();
        if(LOAD){
            choose.setBackgroundResource(R.drawable.apple_pic);

        }
    }

    private void insertObject(UploadVideo object){
        MobileLibraryUser author = BmobUser.getCurrentUser(MobileLibraryUser.class);
        BmobACL acl = new BmobACL();    //创建一个ACL对象
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

    /*private void insertObject(UploadVideo object){
        BmobACL acl = new BmobACL();    //创建一个ACL对象
        if(privacy.equals("EXPOSE")){
            acl.setPublicReadAccess(true);
        }else if(privacy.equals("PERSONAL")){
            acl.setReadAccess(BmobUser.getCurrentUser(),true);
        }
        acl.setWriteAccess(BmobUser.getCurrentUser(), true);   // 设置当前用户可写的权限
        object.setCategory(category);
        object.setPrivacy(privacy);
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
    }*/

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    progressBar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

}
