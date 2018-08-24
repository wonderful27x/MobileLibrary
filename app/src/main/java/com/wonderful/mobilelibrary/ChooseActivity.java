package com.wonderful.mobilelibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseActivity extends BaseActivity implements View.OnClickListener {

    private Button chooseMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        chooseMovie = (Button) findViewById(R.id.choose_movie);

        chooseMovie.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.choose_movie:
                intent = new Intent(this, MovieListActivity.class);
                startActivity(intent);
                break;
        }
    }
}
