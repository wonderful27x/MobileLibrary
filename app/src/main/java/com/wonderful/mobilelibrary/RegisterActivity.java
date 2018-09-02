package com.wonderful.mobilelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private Button register;
    private EditText account;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (Button)findViewById(R.id.register_btn_register);
        account = (EditText)findViewById(R.id.register_account);
        password = (EditText)findViewById(R.id.register_password);

        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.register_btn_register:
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
                user.signUp(new SaveListener<MobileLibraryUser>() {
                    @Override
                    public void done(MobileLibraryUser user, BmobException e) {
                        if(e==null){
                            Intent intent = new Intent(RegisterActivity.this,SuccessActivity.class);
                            startActivity(intent);
                        }else{
                            showToast("失败，账号可能已被注册");
                            MyLog.e(TAG,"注册失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
                break;
        }
    }
}
