package com.wonderful.mobilelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ChangePasswordActivity extends BaseActivity{

    private EditText oldPasswordEdit;
    private EditText newPasswordEdit;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPasswordEdit = (EditText)findViewById(R.id.chane_old_password);
        newPasswordEdit = (EditText)findViewById(R.id.chane_new_password);
        save = (Button)findViewById(R.id.change_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEdit.getText().toString();
                String newPassword = newPasswordEdit.getText().toString();
                BmobUser.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            showToast("密码修改成功");
                            BmobUser.logOut();
                            Intent intent = new Intent(ChangePasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            showToast("密码修改失败");
                        }
                    }
                });
            }
        });
    }
}
