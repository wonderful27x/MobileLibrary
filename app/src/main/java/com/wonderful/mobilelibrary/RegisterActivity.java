package com.wonderful.mobilelibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (Button)findViewById(R.id.register_btn_register);

        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.register_btn_register:
                intent = new Intent(this,SuccessActivity.class);
                startActivity(intent);
                break;
        }
    }
}
