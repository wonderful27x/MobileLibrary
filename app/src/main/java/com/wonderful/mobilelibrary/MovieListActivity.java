package com.wonderful.mobilelibrary;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovieListActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefresh;
    private List<Start> startList = new ArrayList<>();
    private StartAdapter adapter;

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

        initStart();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movie_list_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StartAdapter(startList);
        recyclerView.setAdapter(adapter);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.movie_list_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStarts();
            }
        });
    }

    private void initStart(){
        startList.clear();
        for(int i = 0;i<50;i++){
            Random random = new Random();
            int index = random.nextInt(starts.length);
            startList.add(starts[index]);
        }
    }

   private void refreshStarts(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initStart();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
}
