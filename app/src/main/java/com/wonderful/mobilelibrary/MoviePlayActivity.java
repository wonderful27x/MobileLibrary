package com.wonderful.mobilelibrary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.util.HashMap;

import cn.bmob.v3.b.I;

public class MoviePlayActivity extends BaseActivity implements View.OnClickListener {

    private VideoView videoView;
    private Button start;
    private Button pause;
    private Button replay;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_play);

        videoView = (VideoView)findViewById(R.id.movie_play_view);
        start = (Button)findViewById(R.id.movie_play_start);
        pause = (Button)findViewById(R.id.movie_play_pause);
        replay = (Button)findViewById(R.id.movie_play_replay);

        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        replay.setOnClickListener(this);

        if(ContextCompat.checkSelfPermission(MoviePlayActivity.this, Manifest.
            permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MoviePlayActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        Intent intent = getIntent();
        videoUrl = intent.getStringExtra("videoUrl");

        initVideoPath(videoUrl);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initVideoPath(videoUrl);
                }else {
                    showToast("拒绝权限将无法使用程序");
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void initVideoPath(String url){
        videoView.setVideoPath(url);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.movie_play_start:
                if(!videoView.isPlaying()){
                    videoView.start();
                }
                break;
            case R.id.movie_play_pause:
                if(videoView.isPlaying()){
                    videoView.pause();
                }
                break;
            case R.id.movie_play_replay:
                if(videoView.isPlaying()){
                    videoView.resume();
                }
                break;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(videoView != null){
            videoView.suspend();
        }
    }

}
