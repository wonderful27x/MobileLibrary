package com.wonderful.mobilelibrary;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class UploadVideo extends BmobObject {

    private String VideoName;
    private BmobFile Video;
    private BmobFile videoImage;
    private String category;
    private String privacy;
    private MobileLibraryUser author;

    public UploadVideo(){

    }

    public UploadVideo(String VideoName,BmobFile Video,BmobFile videoImage){
        this.VideoName = VideoName;
        this.Video = Video;
        this.videoImage = videoImage;
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

    public void setAuthor(MobileLibraryUser author){
        this.author = author;
    }

    public MobileLibraryUser getAuthor(){
        return author;
    }

    public void setVideoImage(BmobFile videoImage){
        this.videoImage = videoImage;
    }

    public BmobFile getVideoImage(){
        return videoImage;
    }
}

/*public class UploadVideo extends BmobObject {

        private String VideoName;
        private BmobFile Video;
        private String category;
        private String privacy;
        private MobileLibraryUser author;

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

        public void setAuthor(MobileLibraryUser author){
            this.author = author;
        }

        public MobileLibraryUser getAuthor(){
            return author;
        }
}*/
