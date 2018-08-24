package com.wonderful.mobilelibrary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";

    private EditText account;
    private EditText password;
    private Button login;
    private CheckBox rememberPassword;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button)findViewById(R.id.login_btn_login);
        account = (EditText)findViewById(R.id.login_account);
        password = (EditText)findViewById(R.id.login_password);
        rememberPassword = (CheckBox)findViewById(R.id.login_remember_password);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        login.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isRemember = pref.getBoolean("remember_password",false);
        if(isRemember) {
            String mAccount = pref.getString("account", "");
            String mPassword = pref.getString("password", "");
            account.setText(mAccount);
            password.setText(mPassword);
            rememberPassword.setChecked(true);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_btn_login:
                BmobUser.logOut();
                String accountText = account.getText().toString();
                String passwordText = password.getText().toString();
                if(TextUtils.isEmpty(accountText)||TextUtils.isEmpty(passwordText)){
                    showToast("账号或密码不能为空");
                    return;
                }
                if(!NetworkUtil.networkCheck(this)){
                    showToast("网络无连接");
                    return;
                }
                MobileLibraryUser user = new MobileLibraryUser();
                user.setUsername(accountText);
                user.setPassword(passwordText);
                user.login(new SaveListener<MobileLibraryUser>() {
                    @Override
                    public void done(MobileLibraryUser user, BmobException e) {
                        if(e==null){
                            showToast("登录成功");
                            editor = pref.edit();
                            if(rememberPassword.isChecked()){
                                editor.putBoolean("remember_password",true);
                                editor.putString("account",account.getText().toString());
                                editor.putString("password",password.getText().toString());
                            }else {
                                editor.clear();
                            }
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }else{
                            showToast("失败，请检查账号密码是否正确");
                            MyLog.e(TAG,"登录失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
