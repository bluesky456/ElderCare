package com.ovvi.remotelocation.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.adapter.SettingsItemAdapter;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.SettingsResponse;
import com.ovvi.remotelocation.bean.UserInfo;
import com.ovvi.remotelocation.db.DBManager;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.model.ItemMenu;
import com.ovvi.remotelocation.model.User;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.PreferenceHelper;

/**
 * 设置的主界面
 * 
 * @author chensong
 * 
 */
public class SettingsFragment extends Fragment implements OnItemClickListener,
        OnClickListener {

    private final static String TAG = "SettingsFragment";

    private final Integer[] menu_icons = { R.drawable.settings_menu_member,
            R.drawable.settings_menu_locaton, /*
                                               * R.drawable.settings_menu_alert,
                                               * R
                                               * .drawable.settings_menu_address
                                               * ,R.drawable.settings_menu_news,
                                               */
            R.drawable.settings_menu_logout };

    private final Integer[] menu_text = { R.string.family_text, R.string.location_text,
    /* R.string.alarm_text, R.string.address_text,R.string.device_text, */
    R.string.logout_text };

    private final Integer[] menu_icons_elderly = { R.drawable.settings_menu_member,
            R.drawable.settings_menu_locaton, /* R.drawable.settings_menu_alert, */
            R.drawable.settings_menu_address, /* R.drawable.settings_menu_news, */
            R.drawable.settings_menu_logout };

    private final Integer[] menu_text_elderly = { R.string.family_text,
            R.string.location_text,/* R.string.alarm_text, */
            R.string.address_text, /* R.string.device_text, */
            R.string.logout_text };

    private final int FAST_MODE = 0;
    private final int NORMAL_MODE = 1;
    private final int POWER_MODE = 2;
    private final int SHOCE_BELL = 0;
    private final int BELL = 1;
    private final int MUTE = 2;
    private final int SHOCE = 3;
    /** 默认定位模式 */
    private int location_mode = -1;
    /** 默认报警提醒 */
    private int alarmStyle = SHOCE_BELL;
    private TextView nickname;
    private TextView userphone;
    private ImageView imView;
    private RelativeLayout relativeLayout;
    private ListView listView;
    List<ItemMenu> list = new ArrayList<ItemMenu>();
    List<User> users = new ArrayList<User>();

    private Context mContext;

    private DBManager dbManager;
    private HttpUtil client;
    private Handler handler = new Handler();
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();
    private UserInfo userinfo;
    private Bitmap bitmap;

    public SettingsFragment(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        dbManager = DBManager.getInstance(mContext);
        if (mContext == null) {
            mContext = getActivity();
        }
        client = new HttpUtil(mContext);
        if(PreferenceHelper.getInt(mContext, "locationMode") != -1) {
            location_mode = PreferenceHelper.getInt(mContext, "locationMode");
        } else {
            location_mode = NORMAL_MODE;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.settings_layout, null);
        initView(view);

        listView.setAdapter(new SettingsItemAdapter(list, mContext, menu_icons, menu_text));
        listView.setOnItemClickListener(this);
        relativeLayout.setOnClickListener(this);
        return view;
    }

    public void initView(View view) {
        if (CommonUtil.isOldMode()) {
            for (int i = 0; i < menu_icons_elderly.length; i++) {
                list.add(new ItemMenu(menu_icons_elderly[i],
                        getString(menu_text_elderly[i]), ""));
            }
        } else {
            for (int i = 0; i < menu_icons.length; i++) {
                list.add(new ItemMenu(menu_icons[i], getString(menu_text[i]), ""));
            }
        }
        listView = (ListView) view.findViewById(R.id.listview);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.user_info);
        imView = (ImageView) view.findViewById(R.id.user_icon);
        nickname = (TextView) view.findViewById(R.id.user_name);
        userphone = (TextView) view.findViewById(R.id.user_phone);
    }

    public void refresh() {
        if (mContext == null) {
            mContext = getActivity();
        }
        String token = PreferenceHelper.getString(mContext, "token");
        HashMap<String, String> map = new HashMap<String, String>();

        LogUtils.d(TAG, "refresh token=" + token);
        if (token != null) {
            map.put("token", token);
        }
        if (client == null) {
            client = new HttpUtil(mContext);
        }
        client.postRequest(Common.task.settings, Common.api.settings, map);
        client.setResponse(new onResponseCode() {

            @Override
            public void setPostResponse(final String responseData) {
                // TODO Auto-generated method stub
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        dealResponseData(responseData);
                    }
                };
                handler.post(runnable);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void dealResponseData(String jsonData) {

        LogUtils.d(TAG, "dealResponseData jsonData=" + jsonData);
        if (!CommonUtil.isJsonDataResultEmpty(jsonData)) {
            SettingsResponse response = gson.fromJson(jsonData, SettingsResponse.class);
            int code = response.getCode();
            String msg = response.getMsg();
            userinfo = response.getResult();

            if (code == Common.code.SUCCESS) {
                if (userinfo.getPortrait() != null
                        && !TextUtils.isEmpty(userinfo.getPortrait())) {
                    LogUtils.d(TAG,
                            "dealResponseData getPortrait=" + userinfo.getPortrait());

                    new Task().execute(userinfo.getPortrait());
                    handler = new Handler() {
                        public void handleMessage(android.os.Message msg) {
                            if (msg.what == 0x123) {
                                LogUtils.d(TAG, "dealResponseData bitmap=" + bitmap);
                                if (bitmap != null) {
                                    imView.setImageBitmap(bitmap);
                                } else {
                                    imView.setImageResource(R.drawable.people_icon);
                                }
                            }
                        };
                    };
                } else {
                    LogUtils.d(TAG, "dealResponseData setImageResource");
                    imView.setImageResource(R.drawable.people_icon);
                }
                nickname.setText(userinfo.getNickname());
                userphone.setText(userinfo.getUserName());
            }
        } else {
            CommonGson response = gson.fromJson(jsonData, CommonGson.class);
            String msg = response.getMsg();
            LogUtils.d(TAG, "dealResponseData result=="+response.getResult());
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            
        }

    }

    class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            bitmap = GetImageInputStream((String) params[0]);
            LogUtils.d(TAG, "Task bitmap=" + bitmap);
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
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
//        refresh();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long arg3) {
        // TODO Auto-generated method stub

        Log.d("aaaa", "SettingsActivity onItemClick  index=" + index);
        switch (index) {
        case 0:
            Intent intent = new Intent(mContext, FamilyMemberActivity.class);
            startActivity(intent);
            break;

        case 1:
            onLocationItemClicked();
            break;
        case 2:

            if (CommonUtil.isOldMode()) {
                onSetAddressItemClicked();
            } else {
                onFinishItemClicked();
                // onAlarmItemClicked();
            }
            break;
        case 3:
            if (CommonUtil.isOldMode()) {
                onFinishItemClicked();
            } else {
                // onDeviceNewsItemClicked();
            }
            break;
        // case 4:
        // onFinishItemClicked();
        // break;

        default:
            break;
        }
    }

    private void onDeviceNewsItemClicked() {
        Intent intent = new Intent(mContext, DeviceNewsActivity.class);
        startActivity(intent);

    }

    private void onSetAddressItemClicked() {
        Intent intent = new Intent(mContext, FamilyAddressActivity.class);
        startActivity(intent);
    }

    private void onAlarmItemClicked() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

        dialog.setTitle(getResources().getString(R.string.alarm_text));
        dialog.setSingleChoiceItems(
                new String[] { getResources().getString(R.string.shock_bell_text),
                        getResources().getString(R.string.bell_text),
                        getResources().getString(R.string.shock_text),
                        getResources().getString(R.string.mute_text) }, alarmStyle,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        alarmStyle = which;
                        dialog.dismiss();
                    }
                });
        dialog.show();

    }

    private void onLocationItemClicked() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

        dialog.setTitle(getResources().getString(R.string.location_text));
        dialog.setSingleChoiceItems(
                new String[] { getResources().getString(R.string.fast_mode),
                        getResources().getString(R.string.normal_mode),
                        getResources().getString(R.string.power_mode) }, location_mode,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        location_mode = which;
                        PreferenceHelper.putInt(mContext, "locationMode", which);
                        dialog.dismiss();
                    }
                });
        dialog.show();

    }

    private void onFinishItemClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getActivity(), R.layout.dialog_exit, null);
        TextView exitAccount = (TextView) view.findViewById(R.id.tv_exit_account);
        TextView exitApplication = (TextView) view.findViewById(R.id.tv_exit_application);
        exitAccount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(getContext(), LocationBrowserActivity.class);
                PreferenceHelper.removeData(mContext, "token");
                startActivity(intent);
                finish();
            }
        });
        exitApplication.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    private void finish() {
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == relativeLayout) {
            Intent intent = new Intent();
            //用户信息为null时不跳转
            if (userinfo != null) {
                if (userinfo.getPortrait() != null
                        && !TextUtils.isEmpty(userinfo.getPortrait())) {
                    intent.putExtra("image", userinfo.getPortrait());
                } else {
                    intent.putExtra("image", "");
                }

                intent.setClass(mContext, UserInfoActivity.class);
                intent.putExtra("name", userinfo.getNickname());
                intent.putExtra("phone", userinfo.getUserName());
                startActivityForResult(intent, 3);

            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d(TAG, "onActivityResult requestCode=" + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 3) {
                refresh();
            }
        }
    }

}
