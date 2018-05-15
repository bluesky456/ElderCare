package com.ovvi.remotelocation.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.Manifest;
import android.Manifest.permission;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.UserPhoto;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.gson.PhotoGson;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.PreferenceHelper;

public class UserInfoActivity extends TitleActivity implements OnClickListener {

    private final static String TAG = "UserInfoActivity";
    private final int REQUEST_GALLERY = 0;
    private final int REQUEST_CAMERA = 1;
    private final int PHOTO_PICKED_WITH_DATA = 2;

    private ImageView imageView;
    private EditText nameEditText;
    private EditText phoneEditText;
    private Button btnButton;

    private Context context;
    private Intent intent;
    private HttpUtil client;
    Handler handler = new Handler();
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();
    private Bitmap portriteBitmap;
    /** 头像的网络地址 */
    String picturePath = "http://192.168.9.164/res/portrait/1be23358e2c79932cb59e45d1860cac3.jpg";
    /** 头像存储地址 */
    String uriPicturePath = null;

    /** 拍照图像存储路径 **/
    private static final File PHOTO_DIR = new File("/storage/emulated/0/"
            + "/DCIM/Camera");

    File mCurrentPhotoFile;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_info_layout);
        intent = getIntent();
        initView();
        showBackwardView(R.drawable.top_bar_back);
        hideForwardView();
        setTitle(R.string.people_info);
        phoneEditText.setEnabled(false);
        context = getApplication();
        client = new HttpUtil(context);

        uriPicturePath = intent.getStringExtra("image");
        LogUtils.d(TAG, "onCreate uriPicturePath="+uriPicturePath);
        if (!TextUtils.isEmpty(uriPicturePath)) {
            new Task().execute(uriPicturePath);
            handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == 0x123) {
                        if (portriteBitmap != null) {
                            imageView.setImageBitmap(portriteBitmap);
                        } else {
                            imageView.setImageResource(R.drawable.main_icon);
                        }
                    }
                };
            };
        } else {
            uriPicturePath = picturePath;
            imageView.setBackgroundResource(R.drawable.main_icon);
        }
        nameEditText.setText(intent.getStringExtra("name"));
        phoneEditText.setText(intent.getStringExtra("phone"));
        initPhotoError();
    }

    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.image);
        nameEditText = (EditText) findViewById(R.id.name_edit);
        phoneEditText = (EditText) findViewById(R.id.phone_edit);
        btnButton = (Button) findViewById(R.id.submit);
        btnButton.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            portriteBitmap = GetImageInputStream((String) params[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Message message = new Message();
            message.what = 0x123;
            handler.sendMessage(message);
        }

    }

    /**
     * 获取网络图片
     * 
     * @param imageurl
     *            图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); // 超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); // 设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        case R.id.image:
            showTypeDialog();
            break;
        case R.id.submit:
            onUpdateInfoClicked();
            break;

        default:
            break;
        }
    }

    private void onUpdateInfoClicked() {
        String nickname = nameEditText.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(context, getResources().getString(R.string.label_empty),
                    Toast.LENGTH_SHORT).show();
        } else if (nickname.getBytes().length > 12) {
            Toast.makeText(context,
                    getResources().getString(R.string.member_name_too_length),
                    Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("nickname", nickname);
            if (TextUtils.isEmpty(uriPicturePath)) {
                map.put("portrait", picturePath);
            } else {
                map.put("portrait", uriPicturePath);
            }
            LogUtils.d(TAG, "onUpdateInfoClicked uriPicturePath=" + uriPicturePath);
            map.put("token", PreferenceHelper.getString(context, "token"));

            client.postRequest(Common.task.userinfo, Common.api.userinfo, map);
            client.setResponse(new onResponseCode() {

                @Override
                public void setPostResponse(final String responseData) {
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            updateUserInfo(responseData);
                        }
                    };
                    handler.post(runnable);
                }
            });

        }

    }

    private void updateUserInfo(String jsonData) {
        CommonGson response = gson.fromJson(jsonData, CommonGson.class);
        int code = response.getCode();
        String msg = response.getMsg();
        if (code == Common.code.SUCCESS) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new OnClickListener() {// 在相册中选取
                    @Override
                    public void onClick(View v) {
                        createCroppedPhotoFile();
                        getPhotoPick(REQUEST_GALLERY, mCroppedPhotoPath);
                        dialog.dismiss();
                    }
                });
        tv_select_camera.setOnClickListener(new OnClickListener() {// 调用照相机
                    @Override
                    public void onClick(View v) {
                        openCamera();
                        dialog.dismiss();
                    }
                });
        dialog.setView(view);
        dialog.show();
    }

    public final void getPhotoPick(int reqCode, String... outFile) {

        String outFileStr = null;
        if (outFile != null && outFile.length > 0) {
            outFileStr = outFile[0];
        }

        LogUtils.d(TAG, "getPhotoPick outFileStr=" + outFileStr);
        uriPicturePath = outFileStr;
        Intent intent = createCropPhotoIntent(null, outFileStr);// 标准方式

        if (outFileStr == null) {
            intent.putExtra("return-data", true);
        }

        try {
            startActivityForResult(intent, reqCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param photoFile
     *            拍照生成的图片,如果直接拉起图库则传null
     * @param outputX
     * @param outputY
     * @param outFile
     * @return
     */
    public static Intent createCropPhotoIntent(File photoFile, String outFile) {

        Intent intent = new Intent();
        if (photoFile != null) {// 传入拍照生成的图片给图库
            intent.setAction("com.android.camera.action.CROP");
            intent.setDataAndType(Uri.fromFile(photoFile), "image/*");
        } else {// 直接拉起图库
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.setPackage("com.android.gallery3d");
        }

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        if (outFile != null) {
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);
            Uri croppedPhotoUri = Uri.fromFile(new File(outFile));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, croppedPhotoUri);// 标准方式
        }
        return intent;
    }

    private void openCamera() {
        File photoDir;// 照片存放目录
        photoDir = PHOTO_DIR;
        LogUtils.d(TAG, "photoDir.exists()=" + photoDir.exists());
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
        mCurrentPhotoFile = new File(photoDir, getPhotoFileName());

        LogUtils.d(TAG, "mCurrentPhotoFile ==" + mCurrentPhotoFile);
        try {
            if (!ishasPermission()) {
                requestPermissions(new String[] { Manifest.permission.CAMERA },
                        MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));

                startActivityForResult(intent, REQUEST_CAMERA);
            }
        } catch (Exception e) {
            LogUtils.d(TAG, "take photo failed.");
        }

    }

    private boolean ishasPermission() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            if (checkSelfPermission(permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private String mCroppedPhotoPath;
    private static final String CROPPED_PHOTO_PATH_KEY = "cropped_photo_path";

    private void createCroppedPhotoFile() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        String photoFile = "Location-" + dateFormat.format(date) + ".jpg";

        File dir = new File(getApplicationContext().getExternalCacheDir() + "/tmp");
        dir.mkdirs();
        File file = new File(dir, photoFile);
        mCroppedPhotoPath = file.getAbsolutePath();
        LogUtils.d(TAG, "createCroppedPhotoFile mCroppedPhotoPath=" + mCroppedPhotoPath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 用户选择了权限不再提示，提醒用户去自己去设置
        if (grantResults.length > 0 && requestCode == MY_PERMISSIONS_REQUEST_CAMERA
                && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(permission.CAMERA)) {
                Toast.makeText(context, R.string.toast_permission_deny,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        LogUtils.d(TAG, "requestCode=" + requestCode + " resultCode=" + resultCode);

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case REQUEST_GALLERY: {
                if (data != null) {
                    handleImagePicked(data);
                }
                break;
            }
            case REQUEST_CAMERA: {
                LogUtils.d(TAG, "REQUEST_CAMERA mCurrentPhotoFile==" + mCurrentPhotoFile
                        + "  mCroppedPhotoPath=" + mCroppedPhotoPath);
                try {
                    createCroppedPhotoFile();

                    doCropPhoto(mCurrentPhotoFile, PHOTO_PICKED_WITH_DATA,
                            mCroppedPhotoPath);
                } catch (Exception e) {
                    LogUtils.d(TAG, "onActivityResult REQUEST_CAMERA e==" + e.toString());
                    e.printStackTrace();
                }
                break;
            }
            case PHOTO_PICKED_WITH_DATA: {
                LogUtils.d(TAG, "PHOTO_PICKED_WITH_DATA mCurrentPhotoFile=="
                        + mCurrentPhotoFile);
                if (mCurrentPhotoFile != null && mCurrentPhotoFile.exists()) {
                    mCurrentPhotoFile.delete();
                    // 刷新图库数据库，否则图库仍然会显示该图片（但不能显示图片内容）
                    MediaScannerConnection.scanFile(UserInfoActivity.this,
                            new String[] { mCurrentPhotoFile.getAbsolutePath() },
                            new String[] { null }, null);
                }

                if (data != null) {
                    // handlePickedPhoto(data);
                    handleImagePicked(data);
                }
                break;
            }
            default:
                break;
            }
        }
    }

    public final void doCropPhoto(File photoFile, int reqCode, String... outFile) {
        LogUtils.d(TAG, "doCropPhoto photoFile=" + photoFile);
        if (photoFile == null || !photoFile.exists()) {
            return;
        }

        MediaScannerConnection
                .scanFile(getApplication(), new String[] { photoFile.getAbsolutePath() },
                        new String[] { null }, null);

        // Launch gallery to crop the photo
        String outFileStr = null;
        if (outFile != null && outFile.length > 0) {
            outFileStr = outFile[0];
        }
        uriPicturePath = outFileStr;
        Intent intent = createCropPhotoIntent(photoFile, outFileStr);// 标准方式

        if (outFileStr == null) {
            intent.putExtra("return-data", true);
            intent.putExtra("snscontact", true);
        }

        startActivityForResult(intent, reqCode);
    }

    /**
     * 处理头像的函数
     * 
     * @param data
     *            Intent
     */
    private void handleImagePicked(Intent data) {

        // mIsInputed = true;
        LogUtils.d(TAG, "handleImagePicked data=" + data + "  mCroppedPhotoPath="+mCroppedPhotoPath);

        if (data == null) {
            return;
        }
        Bitmap bitmap = null;
        File photoFile = new File(mCroppedPhotoPath);
        updatePortrait(photoFile);
        try {
            FileInputStream inputStream = new FileInputStream(photoFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            // photoFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap squareBm = null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width + (int) (0.1 * height) < height) {
            squareBm = Bitmap.createBitmap(bitmap, 0, (int) (height * 0.1), width, width);
        }
        Bitmap bmScaled;
        if (squareBm != null) {
            bmScaled = Bitmap.createScaledBitmap(squareBm, 100, 100, true);
            squareBm.recycle();
        } else {
            bmScaled = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
        }

        LogUtils.d(TAG,
                "handleImagePicked data=" + data.getStringExtra(MediaStore.EXTRA_OUTPUT));
        portriteBitmap = bmScaled;
        // if (bmScaled != null) {
        // imageView.setImageBitmap(bmScaled);
        // } else {
        // imageView.setImageResource(R.drawable.main_icon);
        // }
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 上传头像
     * 
     * @param bitmap
     */
    private void updatePortrait(File portraitFile) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("token", PreferenceHelper.getString(context, "token"));

        map.put("portrait", portraitFile);
        LogUtils.d(TAG, "updatePortrait portraitFile=" + portraitFile);
        client.upLoadFile(Common.api.portrait, map);
        client.setResponse(new onResponseCode() {

            @Override
            public void setPostResponse(final String responseData) {
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        dealPortrait(responseData);
                    }
                };
                handler.post(runnable);
            }
        });

    }

    private void dealPortrait(String json) {

        LogUtils.d(TAG, "dealPortrait json=" + json);
        PhotoGson response = gson.fromJson(json, PhotoGson.class);
        UserPhoto portrait = response.getResult();
        int code = response.getCode();
        String msg = response.getMsg();
        if (code == Common.code.SUCCESS) {
            uriPicturePath = portrait.getPortrait();
            LogUtils.d(TAG, "dealPortrait uriPicturePath=" + uriPicturePath
                    + " portriteBitmap=" + portriteBitmap);
            File file = new File(mCroppedPhotoPath);
            file.delete();
            imageView.setImageBitmap(portriteBitmap);
        }
    }

}
