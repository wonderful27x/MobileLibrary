package com.wonderful.mobilelibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder>{

    private List<UploadVideo> videosList ;
    private Context mContext;
    private MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    private Bitmap bitmap;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView videoImage;
        TextView videoName;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            videoImage = (ImageView)view.findViewById(R.id.movie_item_image);
            videoName = (TextView)view.findViewById(R.id.movie_item_title);
        }
    }

    public MovieListAdapter(List<UploadVideo> videosList){
        this.videosList = videosList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                UploadVideo video = videosList.get(position);
                Intent intent = new Intent(mContext,MoviePlayActivity.class);
                intent.putExtra("videoUrl",video.getVideo().getFileUrl());
                //intent.putExtra("videoUrl",video.getVideoUrl());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        UploadVideo video = videosList.get(position);
        holder.videoName.setText(video.getVideoName());
        String videoUrl = video.getVideo().getFileUrl();
        //String videoUrl = video.getVideoUrl();

        if(videoUrl != null){
            retriever.setDataSource(videoUrl,new HashMap());
            bitmap = retriever.getFrameAtTime();
            Glide.with(mContext).load(bitmap).into(holder.videoImage);
        }
    }

    @Override
    public int getItemCount(){
        return videosList.size();
    }
}
