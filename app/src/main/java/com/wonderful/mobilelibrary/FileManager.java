package com.wonderful.mobilelibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import rx.Subscription;

public class FileManager extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "FileManager";

    private ListView listView;
    private List<UploadVideo> fileList = new ArrayList<>();
    private List<UploadVideo> fileModifyList = new ArrayList<>();
    private TextView fileAmount;
    private LinearLayout linearLayout;
    private FileManagerAdapter fileManagerAdapter;
    private static final int NOSELECT_STATE = -1;
    private Boolean isMultiSelect = false;
    private Button setPrivate;
    private Button setPublic;
    private Button cancel;
    private Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        listView = (ListView)findViewById(R.id.file_list);
        fileAmount = (TextView)findViewById(R.id.file_amount);
        linearLayout = (LinearLayout)findViewById(R.id.file_layout_buttons);
        setPrivate = (Button)findViewById(R.id.file_setPrivate);
        setPublic = (Button)findViewById(R.id.file_setPublic);
        cancel = (Button)findViewById(R.id.file_cancel);
        delete = (Button)findViewById(R.id.file_delete);

        setPrivate.setOnClickListener(this);
        setPublic.setOnClickListener(this);
        cancel.setOnClickListener(this);
        delete.setOnClickListener(this);

        fileManagerAdapter = new FileManagerAdapter(FileManager.this,fileList,NOSELECT_STATE);
        listView.setAdapter(fileManagerAdapter);

        searchAll();

    }

    @Override
    public void onClick(View view){
        List<BmobObject> modifyList = new ArrayList<>();
        BmobACL acl = new BmobACL();
        switch (view.getId()){
            case R.id.file_setPrivate:
                acl.setWriteAccess(BmobUser.getCurrentUser(), true);
                acl.setReadAccess(BmobUser.getCurrentUser(),true);
                for (BmobObject video:fileModifyList){
                    video.setACL(acl);
                }
                modifyList.clear();
                modifyList.addAll(fileModifyList);
                new BmobBatch().updateBatch(modifyList).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> o, BmobException e) {
                        if (e == null) {
                            Message message = new Message();
                            message.what = 3;
                            handler.sendMessage(message);
                        } else {
                            showToast("更新失败");
                            MyLog.e(TAG, "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
                break;
            case R.id.file_setPublic:
                acl.setPublicReadAccess(true);
                acl.setWriteAccess(BmobUser.getCurrentUser(), true);
                for (BmobObject video:fileModifyList){
                    video.setACL(acl);
                }
                modifyList.clear();
                modifyList.addAll(fileModifyList);
                new BmobBatch().updateBatch(modifyList).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> o, BmobException e) {
                        if (e == null) {
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                        } else {
                            showToast("更新失败");
                            MyLog.e(TAG, "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
                break;
            case R.id.file_delete:
                delete();
                break;
            case R.id.file_cancel:
                isMultiSelect = false;
                fileModifyList.clear();
                linearLayout.setVisibility(View.GONE);
                fileManagerAdapter = new FileManagerAdapter(FileManager.this,fileList,NOSELECT_STATE);
                listView.setAdapter(fileManagerAdapter);
                break;
            default:
                break;
        }
    }

    private void delete(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(FileManager.this);
        dialog.setTitle("删除文件");
        dialog.setMessage("删除后文件将不可恢复");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<BmobObject> modifyList = new ArrayList<>();
                modifyList.clear();
                modifyList.addAll(fileModifyList);
                new BmobBatch().deleteBatch(modifyList).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> o, BmobException e) {
                        if (e == null) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        } else {
                            showToast("删除失败");
                            MyLog.e(TAG, "删除失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    private void searchAll(){
        MobileLibraryUser user = BmobUser.getCurrentUser(MobileLibraryUser.class);
        BmobQuery<UploadVideo> query = new BmobQuery<UploadVideo>();
        query.addWhereEqualTo("author", user);
        query.order("-updatedAt");
        query.findObjects(new FindListener<UploadVideo>() {
            @Override
            public void done(List<UploadVideo> object,BmobException e) {
                if(e==null){
                    fileList.addAll(object);
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                }else{
                    MyLog.e(TAG,"查询失败："+e.getMessage()+e.getErrorCode());
                }
            }

        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    fileAmount.setText("" + fileList.size());
                    fileManagerAdapter = new FileManagerAdapter(FileManager.this,fileList,NOSELECT_STATE);
                    listView.setAdapter(fileManagerAdapter);
                    break;
                case 1:
                    OperationSucceed("删除成功");
                    break;
                case 2:
                    OperationSucceed("更新成功");
                    break;
                case 3:
                    OperationSucceed("更新成功");
                    break;
                default:
                    break;
            }
        }
    };

    private void OperationSucceed(String message){
        showToast(message);
        fileModifyList.clear();
        fileList.clear();
        linearLayout.setVisibility(View.GONE);
        isMultiSelect = false;
        searchAll();
    }

    class FileManagerAdapter extends BaseAdapter {
        private List<UploadVideo> fileList;
        private LayoutInflater inflater;

        private HashMap<Integer, Integer> isCheckBoxVisible;
        private HashMap<Integer, Boolean> isChecked;

        public FileManagerAdapter(Context context, List<UploadVideo> fileList, int position) {
            inflater = LayoutInflater.from(context);
            this.fileList = fileList;
            isCheckBoxVisible = new HashMap<>();
            isChecked = new HashMap<>();
            if (isMultiSelect) {
                for (int i = 0; i < fileList.size(); i++) {
                    isCheckBoxVisible.put(i, CheckBox.VISIBLE);
                    isChecked.put(i, false);
                }
            } else {
                for (int i = 0; i < fileList.size(); i++) {
                    isCheckBoxVisible.put(i, CheckBox.INVISIBLE);
                    isChecked.put(i, false);
                }
            }
            if (isMultiSelect && position >= 0) {
                isChecked.put(position, true);
            }
        }

        class ViewHolder {
            public TextView videoName;
            public CheckBox checkBox;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.file_list_item, null);
                viewHolder.videoName = (TextView) view.findViewById(R.id.file_name);
                viewHolder.checkBox = (CheckBox) view.findViewById(R.id.file_check);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.videoName.setText(fileList.get(position).getVideoName());
            viewHolder.checkBox.setChecked(isChecked.get(position));
            viewHolder.checkBox.setVisibility(isCheckBoxVisible.get(position));

            view.setOnLongClickListener(new myLongClick(position, fileList));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isMultiSelect) {
                        if (viewHolder.checkBox.isChecked()) {
                            viewHolder.checkBox.setChecked(false);
                            isChecked.put(position,false);
                            fileModifyList.remove(fileList.get(position));
                        } else {
                            viewHolder.checkBox.setChecked(true);
                            isChecked.put(position,true);
                            fileModifyList.add(fileList.get(position));
                        }
                    }else {
                        Intent intent = new Intent(FileManager.this,MoviePlayActivity.class);
                        intent.putExtra("videoUrl",fileList.get(position).getVideo().getFileUrl());
                        startActivity(intent);
                    }
                }
            });
            return view;
        }

        class myLongClick implements View.OnLongClickListener {
            private int position;
            private List<UploadVideo> fileList;

            public myLongClick(int position, List<UploadVideo> fileList) {
                this.position = position;
                this.fileList = fileList;

            }

            @Override
            public boolean onLongClick(View view) {
                if (isMultiSelect == true){
                    return false;
                }
                isMultiSelect = true;
                fileModifyList.clear();
                fileModifyList.add(fileList.get(position));
                isChecked.put(position,true);
                linearLayout.setVisibility(View.VISIBLE);
                for(int i=0; i<fileList.size(); i++){
                    isCheckBoxVisible.put(i,CheckBox.VISIBLE);
                }
                fileManagerAdapter = new FileManagerAdapter(FileManager.this,fileList,position);
                listView.setAdapter(fileManagerAdapter);
                return true;
            }
        }

        @Override
        public int getCount() {
            return fileList.size();
        }

        @Override
        public Object getItem(int position) {
            return fileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
