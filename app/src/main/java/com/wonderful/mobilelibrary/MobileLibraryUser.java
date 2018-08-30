package com.wonderful.mobilelibrary;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

public class MobileLibraryUser extends BmobUser {

    private BmobFile headImage;

    public void setHeadImage(BmobFile headImage){
        this.headImage = headImage;
    }

    public BmobFile getHeadImage(){
        return headImage;
    }

}
