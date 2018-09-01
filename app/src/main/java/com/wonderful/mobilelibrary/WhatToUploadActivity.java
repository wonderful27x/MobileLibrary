package com.wonderful.mobilelibrary;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WhatToUploadActivity extends BaseActivity implements ListView.OnItemClickListener {

    List<String> uploadList = new ArrayList<>();
    private ListView listView;
    private File file;
    private int nameLocation;
    private String namePrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_to_upload);

        listView = (ListView)findViewById(R.id.what_to_upload_list);

        file = new File(Environment.getExternalStorageDirectory(),"uploadThings");
        namePrefix = file.getPath()+"/";
        nameLocation = file.getPath().length()+1;
        ListFiles(file);
        playable(uploadList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                WhatToUploadActivity.this,android.R.layout.simple_list_item_1,uploadList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
    }

    private void ListFiles(File dir){
        if(!dir.exists()||!dir.isDirectory())return;
        String[] files = dir.list();
        for(int i=0; i<files.length; i++){
            File file = new File(dir,files[i]);
            if(file.isFile()){
                uploadList.add(file.getPath().substring(nameLocation));
            }else {
                ListFiles(file);
            }
        }
    }

    private void playable(List<String> uploadList){
        List<String> uploadListChange = new ArrayList<>();
        uploadListChange.clear();
        String matchName = ".*\\.(mp4|flv|mkv|rmvb|3gp|avi)$";
        boolean ok;
        for (String s:uploadList){
            ok = Pattern.matches(matchName,s);
            if(ok){
                uploadListChange.add(s);
            }
        }
        uploadList.clear();
        uploadList.addAll(uploadListChange);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = uploadList.get(position);
        Intent intent = new Intent(this,UploadActivity.class);
        intent.putExtra("uploadUrl",namePrefix + url);
        intent.putExtra("uploadName",url);
        setResult(RESULT_OK,intent);
        finish();
    }
}
