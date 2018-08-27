package com.wonderful.mobilelibrary;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class UploadVideo extends BmobObject {

        private String VideoName;
        private BmobFile Video;
        private String category;
        private String privacy;

        public UploadVideo(){

        }

        public UploadVideo(String VideoName,BmobFile Video){
            this.VideoName = VideoName;
            this.Video = Video;
        }

        public void setVideoName(String VideoName){
            this.VideoName = VideoName;
        }

        public String getVideoName(){
            return VideoName;
        }

        public void setVideo(BmobFile Video){
            this.Video = Video;
        }

        public BmobFile getVideo(){
            return Video;
        }

        public void setCategory(String category){
            this.category = category;
        }

        public String getCategory(){
            return category;
        }

        public void setPrivacy(String privacy){
            this.privacy = privacy;
        }

        public String getPrivacy(){
            return privacy;
        }
}


/*public class UploadVideo extends BmobObject {

    private String VideoName;
    private String VideoUrl;
    private String category;
    private String privacy;

    public UploadVideo(){

    }

    public UploadVideo(String VideoName,String VideoUrl){
        this.VideoName = VideoName;
        this.VideoUrl = VideoUrl;
    }

    public void setVideoName(String VideoName){
        this.VideoName = VideoName;
    }

    public String getVideoName(){
        return VideoName;
    }

    public void setVideoUrl(String VideoUrl){
        this.VideoUrl = VideoUrl;
    }

    public String getVideoUrl(){
        return VideoUrl;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getCategory(){
        return category;
    }

    public void setPrivacy(String privacy){
        this.privacy = privacy;
    }

    public String getPrivacy(){
        return privacy;
    }
}*/