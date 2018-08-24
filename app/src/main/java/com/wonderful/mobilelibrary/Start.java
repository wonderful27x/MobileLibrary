package com.wonderful.mobilelibrary;

public class Start {

    private String name;
    private int imageId;

    public Start(String name,int imageId){
        this.name = name;
        this.imageId = imageId;
    }

    public String getName(){
        return name;
    }

    public int getImageId(){
        return imageId;
    }
}
