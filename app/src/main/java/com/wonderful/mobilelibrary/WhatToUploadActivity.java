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
