package com.wonderful.mobilelibrary;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class MovieListActivity extends BaseActivity {

    private static final String TAG = "MovieListActivity";

    private SwipeRefreshLayout swipeRefresh;
    private MovieListAdapter movieAdapter;
    private int curPage = 0;
    private int queryLimit = 10;
    private List<UploadVideo> queryVideoList = new ArrayList<>();
    private List<UploadVideo> VideoList = new ArrayList<>();

    private Start[] starts = {
            new Start("star1",R.drawable.star1),new Start("star2",R.drawable.star2),
            new Start("star3",R.drawable.star3),new Start("star4",R.drawable.star4),
            new Start("star5",R.drawable.star5),new Start("star6",R.drawable.star6),
            new Start("star7",R.drawable.star7),new Start("star8",R.drawable.star8),
            new Start("star9",R.drawable.star9),new Start("star10",R.drawable.star10)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movie_list_recycler_view);
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

        queryMoreVideos();

    }

   private void refreshVideoList(){
       queryMoreVideos();
       swipeRefresh.setRefreshing(false);
    }

    private void queryMoreVideos(){
        BmobQuery<UploadVideo> videos = new BmobQuery<>();
        videos.count(UploadVideo.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null){
                    if(integer>queryVideoList.size()){
                        queryVideos(curPage);
                        curPage++;
                    }else {
                        showToast("没有更多数据了");
                    }
                }else {
                    MyLog.e(TAG,"查询失败 " + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    private void queryVideos(final int page){
        BmobQuery<UploadVideo> videos = new BmobQuery<>();
        videos.setLimit(queryLimit);
        videos.setSkip(page*queryLimit);
        videos.findObjects(new FindListener<UploadVideo>() {
            @Override
            public void done(List<UploadVideo> list, BmobException e) {
                if(e == null){
                    if(list != null && list.size()>0){
                        if(page == 0){
                            queryVideoList.clear();
                        }
                        queryVideoList.addAll(list);
                        movieAdapter.notifyDataSetChanged();
                    }
                }else {
                    MyLog.e(TAG,"查询失败 " + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }
}
