package com.wonderful.mobilelibrary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class MovieListActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "MovieListActivity";

    private SwipeRefreshLayout swipeRefresh;
    private MovieListAdapter movieAdapter;
    private int curPage = 0;
    private int queryLimit = 10;
    private List<UploadVideo> queryVideoList = new ArrayList<>();
    private Button backUp;
    private Button search;
    private EditText searchFor;
    private boolean isSearch = false;
    private int videoAmount;
    private int controlValue = 0;
    private String searchCategory;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Intent intent = getIntent();
        searchCategory = intent.getStringExtra("category");

        recyclerView = (RecyclerView) findViewById(R.id.movie_list_recycler_view);
        searchFor = (EditText)findViewById(R.id.movie_list_search_for);
        backUp = (Button)findViewById(R.id.movie_list_backup);
        search = (Button)findViewById(R.id.movie_list_search);
        fab = (FloatingActionButton)findViewById(R.id.movie_list_fab);

        backUp.setOnClickListener(this);
        search.setOnClickListener(this);
        fab.setOnClickListener(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        movieAdapter = new MovieListAdapter(queryVideoList);
        recyclerView.setAdapter(movieAdapter);


        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.movie_list_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshVideoList();
            }
        });

        getVideosAmount(searchCategory);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.movie_list_backup:
                finish();
                break;
            case R.id.movie_list_search:
                if(!TextUtils.isEmpty(searchFor.getText().toString())) {
                    isSearch = true;
                    curPage = 0;
                    queryVideoList.clear();
                    queryVideos(searchCategory);
                }
                break;
            case R.id.movie_list_fab:
                recyclerView.smoothScrollToPosition(queryVideoList.size());
                break;
            default:
                break;
        }
    }

    private void searchForWant(){
        List<UploadVideo> VideoList = new ArrayList<>();
        String searchContent = searchFor.getText().toString();
        String matchName;
        boolean ok;

        VideoList.clear();
        searchContent = ".*"+searchContent+".*";

        for (UploadVideo v:queryVideoList){
            matchName = v.getVideoName();
            ok = Pattern.matches(searchContent,matchName);
            if(ok){
                VideoList.add(v);
            }
        }
        queryVideoList.clear();
        queryVideoList.addAll(VideoList);
        movieAdapter.notifyDataSetChanged();
        curPage = 0;
    }

   private void refreshVideoList(){
        queryVideos(searchCategory);
    }

    private void queryVideos(String category){
        if(videoAmount ==0 ){
            controlValue = 1;
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
            return;
        }
        BmobQuery<UploadVideo> videos = new BmobQuery<>();
        if (category.equals("FITNESS")){
            videos.addWhereEqualTo("category", "FITNESS");
        }else if(category.equals("COOK")){
            videos.addWhereEqualTo("category", "COOK");
        }else if(category.equals("SURVIVE")){
            videos.addWhereEqualTo("category", "SURVIVE");
        }else if(category.equals("AMUSE")){
            videos.addWhereEqualTo("category", "AMUSE");
        }
        videos.setLimit(queryLimit);
        videos.setSkip(curPage*queryLimit);
        videos.findObjects(new FindListener<UploadVideo>() {
            @Override
            public void done(List<UploadVideo> list, BmobException e) {
                if(e == null){
                    if(list != null && list.size()>0){
                        if(curPage == 0){
                            queryVideoList.clear();
                        }
                        queryVideoList.addAll(list);
                        if(videoAmount<=queryVideoList.size()){
                            Message message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        }else {
                            curPage++;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }else {
                    MyLog.e(TAG,"查询失败 " + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    swipeRefresh.setRefreshing(false);
                    if(controlValue == 0) {
                        movieAdapter.notifyDataSetChanged();
                        controlValue = 1;
                    }else {
                        showToast("没有更多数据了");
                    }
                    if(isSearch) {
                        searchForWant();
                        controlValue = 0;
                        isSearch = false;
                    }
                    break;
                case 1:
                    if(isSearch) {
                        queryVideos(searchCategory);
                    }else {
                        swipeRefresh.setRefreshing(false);
                        movieAdapter.notifyDataSetChanged();
                    }
                    break;
                case 2:
                    queryVideos(searchCategory);
                    break;
                default:
                    break;
            }
        }
    };
    private void getVideosAmount(String category){
        BmobQuery<UploadVideo> videos = new BmobQuery<>();
        if (category.equals("FITNESS")){
            videos.addWhereEqualTo("category", "FITNESS");
        }else if(category.equals("COOK")){
            videos.addWhereEqualTo("category", "COOK");
        }else if(category.equals("SURVIVE")){
            videos.addWhereEqualTo("category", "SURVIVE");
        }else if(category.equals("AMUSE")){
            videos.addWhereEqualTo("category", "AMUSE");
        }
        videos.count(UploadVideo.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null){
                    videoAmount = integer;
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                }else {
                    MyLog.e(TAG,"查询失败 " + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }
}
