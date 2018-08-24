package com.wonderful.mobilelibrary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class MainActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headView;
    private ImageView headImage;
    private Button drawer;
    private Button register;
    private Button login;
    private Button fitness;
    private TextView ismember;

    private BmobUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmob.initialize(this,"344a2f524ac27a17eb28899ec0020f04");

        viewsInit();
        listenerInit();


    }

    @Override
    protected void onResume(){
        super.onResume();
        user = BmobUser.getCurrentUser();
        if(user != null){
            login.setText("上传");
            ismember.setText(user.getUsername());
        }
    }

    private void viewsInit(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawer = (Button)findViewById(R.id.main_btn_drawer);
        register = (Button)findViewById(R.id.main_btn_register);
        login = (Button)findViewById(R.id.main_btn_login);
        fitness = (Button)findViewById(R.id.main_btn_fitness);
        ismember = (TextView) findViewById(R.id.main_txv_member);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headView = navigationView.getHeaderView(0);
        headImage = (ImageView) headView.findViewById(R.id.head_image);
        navigationView.setItemIconTintList(null);
    }

    private void listenerInit(){
        drawer.setOnClickListener(this);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        fitness.setOnClickListener(this);
        headImage.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case R.id.main_btn_drawer:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.main_btn_login:
                if(user == null) {
                    intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    intent = new Intent(this,UploadActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.main_btn_register:
                intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.main_btn_fitness:
                intent = new Intent(this,ChooseActivity.class);
                startActivity(intent);
                break;
            case R.id.head_image:
                showToast("你点击了头像");
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_backUp:
                drawerLayout.closeDrawers();
                return true;
            case R.id.menu_upload:
                intent = new Intent(this,UploadActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_login:
                drawerLayout.closeDrawers();
                intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

}
