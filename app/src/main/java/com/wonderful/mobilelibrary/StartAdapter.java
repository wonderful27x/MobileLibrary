package com.wonderful.mobilelibrary;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class StartAdapter extends RecyclerView.Adapter<StartAdapter.ViewHolder>{

    private List<Start> mStartList ;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView startImage;
        TextView startName;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            startImage = (ImageView)view.findViewById(R.id.movie_item_image);
            startName = (TextView)view.findViewById(R.id.movie_item_title);
        }
    }

    public StartAdapter(List<Start> startList){
        mStartList = startList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Start start = mStartList.get(position);
        holder.startName.setText(start.getName());
        Glide.with(mContext).load(start.getImageId()).into(holder.startImage);
    }

    @Override
    public int getItemCount(){
        return mStartList.size();
    }
}
