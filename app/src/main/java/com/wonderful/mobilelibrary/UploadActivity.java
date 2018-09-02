package com.wonderful.mobilelibrary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class UploadActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UploadActivity";

    private Button choose;
    private Button startUpload;
    private RadioGroup categoryGroup;
    private RadioGroup privacyGroup;
    private RadioButton survive;
    private RadioButton fitness;
    private RadioButton cook;
    private RadioButton amuse;
    private RadioButton personal;
    private RadioButton expose;
    private String category = null;
    private String privacy = null;
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
        categoryGroup = (RadioGroup)findViewById(R.id.upload_category_group);
        privacyGroup = (RadioGroup)findViewById(R.id.upload_privacy_group);
        survive = (RadioButton)findViewById(R.id.upload_survive);
        fitness = (RadioButton)findViewById(R.id.upload_fitness);
        cook = (RadioButton)findViewById(R.id.upload_cook);
        amuse = (RadioButton)findViewById(R.id.upload_amuse);
        personal = (RadioButton)findViewById(R.id.upload_personal);
        expose = (RadioButton)findViewById(R.id.upload_public);
        progressBar = (ProgressBar)findViewById(R.id.upload_progress_bar);

        choose.setOnClickListener(this);
        startUpload.setOnClickListener(this);

        progressBar.setVisibility(View.GONE);

        categoryGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(survive.getId() == checkedId){
                    category = "SURVIVE";
                }
                if(fitness.getId() == checkedId){
                    category = "FITNESS";
                }
                if(cook.getId() == checkedId){
                    category = "COOK";
                }
                if(amuse.getId() == checkedId){
                    category = "AMUSE";
                }
            }
        });

        privacyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(personal.getId() == checkedId){
                    privacy = "PERSONAL";
                }
                if(expose.getId() == checkedId){
                    privacy = "EXPOSE";
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(LOAD){
            choose.setText("ok");
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
            case R.id.upload_upload:
                if(LOAD){
                    if(privacy == null||category == null){
                        showToast("请选择相关属性");
                        return;
                    }
                    uploadBatch();
                }
        }
    }

    private void uploadBatch(){
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences.Editor editor = getSharedPreferences("MobileLibrary",MODE_PRIVATE).edit();
        editor.putBoolean("uploadFinish",false);
        editor.apply();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, //禁止交互
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap bitmap;
        retriever.setDataSource(uploadUrl);
        bitmap = retriever.getFrameAtTime();
        File imageFile = getFile(bitmap);
        final String[] filePaths = new String[2];
        filePaths[0] = imageFile.getPath();
        filePaths[1] = uploadUrl;

        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                if(urls.size()==filePaths.length){
                    SharedPreferences.Editor editor = getSharedPreferences("MobileLibrary",MODE_PRIVATE).edit();
                    editor.putBoolean("uploadFinish",true);
                    editor.apply();
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    insertObject(files);
                }
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                MyLog.e(TAG,"上传失败：" + statuscode + errormsg);
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total,int totalPercent) {
                //1、curIndex--表示当前第几个文件正在上传
                //2、curPercent--表示当前上传文件的进度值（百分比）
                //3、total--表示总的上传文件数
                //4、totalPercent--表示总的上传进度（百分比）
                progressBar.setProgress(curPercent);
                SharedPreferences.Editor editor = getSharedPreferences("MobileLibrary",MODE_PRIVATE).edit();
                editor.putInt("progress",curPercent);
                editor.apply();
            }
        });
    }

    private File getFile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        File file = new File(Environment.getExternalStorageDirectory() + "/videoImage.jpg");
        try {
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
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

    private void insertObject(List<BmobFile> files){
        UploadVideo videos = new UploadVideo();
        MobileLibraryUser author = BmobUser.getCurrentUser(MobileLibraryUser.class);
        BmobACL acl = new BmobACL();
        if(privacy.equals("EXPOSE")){
            acl.setPublicReadAccess(true);
        }else if(privacy.equals("PERSONAL")){
            acl.setReadAccess(BmobUser.getCurrentUser(),true);
        }
        acl.setWriteAccess(BmobUser.getCurrentUser(), true);   // 设置当前用户可写的权限
        videos.setVideoImage(files.get(0));
        videos.setVideo(files.get(1));
        videos.setVideoName(uploadName);
        videos.setCategory(category);
        videos.setPrivacy(privacy);
        videos.setAuthor(author);
        videos.setACL(acl);
        videos.save(new SaveListener<String>() {
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

}
