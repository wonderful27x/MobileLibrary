package com.wonderful.mobilelibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static com.wonderful.mobilelibrary.ActivityCollector.finishAll;

public class MainActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headView;
    private ImageView headImage;
    private Button drawer;
    private Button register;
    private Button login;
    private Button fitness;
    private Button cook;
    private Button survive;
    private Button amuse;
    private Button graphicMode;
    private Button videoMode;
    private TextView ismember;
    private TextView userName;
    private MobileLibraryUser user;
    private Uri imageUri;
    private String takePhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("MobileLibrary",MODE_PRIVATE);
        boolean agree = pref.getBoolean("firstStart",false);
        if(!agree){
            agreewith();
        }

        Bmob.initialize(this,"344a2f524ac27a17eb28899ec0020f04");

        viewsInit();
        listenerInit();

    }

    @Override
    protected void onResume(){
        super.onResume();
        user = BmobUser.getCurrentUser(MobileLibraryUser.class);
        if(user != null){
            login.setText("上传");
            ismember.setText(user.getUsername());
            userName.setText(user.getUsername());
            if(user.getHeadImage() == null){
                Glide.with(MainActivity.this).load(R.drawable.default_avatar).into(headImage);
            }else {
                Glide.with(MainActivity.this).load(user.getHeadImage().getFileUrl()).into(headImage);
            }
        }else {
            login.setText("登录");
            ismember.setText("您还不是会员");
            userName.setText("非会员");
            Glide.with(MainActivity.this).load(R.drawable.default_avatar).into(headImage);
        }
    }

    private void viewsInit(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawer = (Button)findViewById(R.id.main_btn_drawer);
        register = (Button)findViewById(R.id.main_btn_register);
        login = (Button)findViewById(R.id.main_btn_login);
        fitness = (Button)findViewById(R.id.main_btn_fitness);
        cook = (Button)findViewById(R.id.main_btn_cook);
        survive = (Button)findViewById(R.id.main_btn_survive);
        amuse = (Button)findViewById(R.id.main_btn_amuse);
        graphicMode = (Button)findViewById(R.id.main_graphic_mode);
        videoMode = (Button)findViewById(R.id.main_video_mode);
        ismember = (TextView) findViewById(R.id.main_txv_member);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headView = navigationView.getHeaderView(0);
        headImage = (ImageView) headView.findViewById(R.id.head_image);
        userName = (TextView) headView.findViewById(R.id.head_name);
        navigationView.setItemIconTintList(null);
    }

    private void listenerInit(){
        drawer.setOnClickListener(this);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        fitness.setOnClickListener(this);
        cook.setOnClickListener(this);
        survive.setOnClickListener(this);
        amuse.setOnClickListener(this);
        graphicMode.setOnClickListener(this);
        videoMode.setOnClickListener(this);
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
                user = BmobUser.getCurrentUser(MobileLibraryUser.class);
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
                intent = new Intent(this,MovieListActivity.class);
                intent.putExtra("category","FITNESS");
                startActivity(intent);
                break;
            case R.id.main_btn_cook:
                intent = new Intent(this,MovieListActivity.class);
                intent.putExtra("category","COOK");
                startActivity(intent);
                break;
            case R.id.main_btn_survive:
                intent = new Intent(this,MovieListActivity.class);
                intent.putExtra("category","SURVIVE");
                startActivity(intent);
                break;
            case R.id.main_btn_amuse:
                intent = new Intent(this,MovieListActivity.class);
                intent.putExtra("category","AMUSE");
                startActivity(intent);
                break;
            case R.id.head_image:
                uploadHeadImage();
                break;
            case R.id.main_graphic_mode:
                showToast("此功能暂未开放");
                break;
        }
    }

    private void uploadHeadImage(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("选择图片来源");
        dialog.setCancelable(true);
        dialog.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File outputImage = new File(getExternalCacheDir(),"outputImage.jpg");
                takePhotoPath = outputImage.getPath();
                try{
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(MainActivity.this,
                            "com.wonderful.mobilelibrary.fileprovider",outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,1);
            }
        });
        dialog.setNegativeButton("相册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                        WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    openAlbum();
                }
            }
        });
        dialog.show();
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    showToast("拒绝权限将无法使用此功能");
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    startUpload(takePhotoPath);
                }
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        String urlHere = handleImageOnkitKat(data);
                        startUpload(urlHere);
                    }else{
                        String urlHere = handleImageBeforeKitKat(data);
                        startUpload(urlHere);
                    }
                }
            default:
                break;
        }
    }

    private void startUpload(String headImageUrl){
        File file = new File(headImageUrl);
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    MobileLibraryUser newUser = BmobUser.getCurrentUser(MobileLibraryUser.class);
                    newUser.setHeadImage(bmobFile);
                    newUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Message message = new Message();
                                message.what = 0;
                                handler.sendMessage(message);
                            }else{
                                showToast("更新失败");
                                MyLog.e(TAG,"更新失败" + e.getMessage() + e.getErrorCode());
                            }
                        }
                    });
                }else {
                    MyLog.e(TAG,"上传失败" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    showToast("更新成功");
                    MobileLibraryUser userHere = BmobUser.getCurrentUser(MobileLibraryUser.class);
                    Glide.with(MainActivity.this).load(userHere.getHeadImage().getFileUrl()).into(headImage);
                default:
                    break;
            }
        }
    };

    @TargetApi(19)
    private String handleImageOnkitKat(Intent intent){
        String imagePath = null;
        Uri uri = intent.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        return imagePath;
    }

    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }
        return path;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_backup:
                drawerLayout.closeDrawers();
                return true;
            case R.id.menu_upload:
                user = BmobUser.getCurrentUser(MobileLibraryUser.class);
                if(user != null){
                    intent = new Intent(this,UploadActivity.class);
                    startActivity(intent);
                }else {
                    showToast("非会员无法上传，请先登录");
                }
                return true;
            case R.id.menu_switchAccount:
                drawerLayout.closeDrawers();
                intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_logout:
                BmobUser.logOut();
                drawerLayout.closeDrawers();
                login.setText("登录");
                ismember.setText("您还不是会员");
                userName.setText("非会员");
                return true;
            case R.id.menu_about_software:
                statement();
                return true;
            case R.id.menu_change_password:
                user = BmobUser.getCurrentUser(MobileLibraryUser.class);
                if(user != null) {
                    intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.menu_file_manager:
                user = BmobUser.getCurrentUser(MobileLibraryUser.class);
                if(user != null) {
                    intent = new Intent(MainActivity.this, FileManager.class);
                    startActivity(intent);
                }
                return true;
            case R.id.menu_register:
                intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    private void statement(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("声明：");
        dialog.setMessage("在使用软件的时候您必须遵守中华人民共和国相关法律条约，" +
                "包括不得将软件用于色情，赌博等。");
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    private void agreewith(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("声明：");
        dialog.setMessage("在使用软件的时候您必须遵守中华人民共和国相关法律条约，" +
                "包括不得将软件用于色情，赌博等。");
        dialog.setCancelable(false);
        dialog.setPositiveButton("我同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = getSharedPreferences("MobileLibrary",MODE_PRIVATE).edit();
                editor.putBoolean("firstStart",true);
                editor.apply();
            }
        });
        dialog.setNegativeButton("我不同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAll();
            }
        });
        dialog.show();
    }

}
