package com.wonderful.mobilelibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button)findViewById(R.id.login_btn_login);

        login.setOnClickListener(this);

        MyLog.d("LoginActivity","LoginActivity onCreated");
    }

    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.login_btn_login:
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        MyLog.d("LoginActivity","LoginActivity destroyed");
    }
}
