package com.wonderful.mobilelibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SuccessActivity extends BaseActivity implements View.OnClickListener {

    private Button backUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        backUp = (Button)findViewById(R.id.success_backup);

        backUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.success_backup:
                intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
