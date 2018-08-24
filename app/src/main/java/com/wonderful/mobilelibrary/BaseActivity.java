package com.wonderful.mobilelibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    Toast MyToast;

    public void showToast(String text){
        if(!TextUtils.isEmpty(text)){
            if(MyToast == null){
                MyToast = Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT);
            }else{
                MyToast.setText(text);
            }
            MyToast.show();
        }
    }
}
