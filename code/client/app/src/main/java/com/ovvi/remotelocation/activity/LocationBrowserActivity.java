package com.ovvi.remotelocation.activity;

import java.util.HashMap;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.TokenResult;
import com.ovvi.remotelocation.gson.LoginGson;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.PreferenceHelper;
import com.ovvi.remotelocation.utils.TelNumMatch;
import com.ovvi.remotelocation.widget.TopBarLayout;
import com.ovvi.remotelocation.widget.TopBarLayout.setOnBackAndForwardClickListener;

/**
 * 应用主界面
 * 
 * @author chensong
 * 
 */
public class LocationBrowserActivity extends Activity implements OnClickListener {

    private static final String TAG = "LocationBrowserActivity";
    private TextView tabHelp;
    private TextView tabSetting;

    private TopBarLayout topBarLayout;
    private SettingsFragment settingsFragment;
    LocationMainFragment locationMainFragment;
    FragmentTransaction transaction;

    private Context mContext;

    private String token = null;
    private String brand = null;
    private String product = null;
    private String imei = null;
    private String phone = null;
    private HttpUtil client;
    private Handler handler = new Handler();
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();

    private String[] permissions = new String[] { Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS };

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_tab_menu);
        mContext = getApplication();

        client = new HttpUtil(mContext);
        initView();
        if (CommonUtil.checkNetworkState(mContext)) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                LogUtils.d(TAG, " checkPermissionAllGranted");
                requestPermissions(new String[] { Manifest.permission.READ_PHONE_STATE },
                        MY_PERMISSION_REQUEST_CODE);
            } else {
                LogUtils.d(TAG, "no checkPermissionAllGranted");
                checkCurrentAccount();
            }
        } else {
            showTips();
            return;
        }

    }

    private void showTips() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(getResources().getString(R.string.network_error));
        builder.setMessage(getResources().getString(R.string.network_setting));
        builder.setPositiveButton(getResources().getString(R.string.dialog_positive),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(LocationBrowserActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.create();
        builder.show();
    }

    // 检测账户
    private void checkCurrentAccount() {

        HashMap<String, String> map = new HashMap<String, String>();
        brand = CommonUtil.getPhoneBrand();
        product = CommonUtil.getPhoneModel();
        imei = CommonUtil.getImei(mContext);
        token = PreferenceHelper.getString(mContext, "token");
        phone = CommonUtil.getNativePhoneNumber(mContext);

        LogUtils.d(TAG, "checkCurrentAccount phone=" + phone + " tokent=" + token);
        if (TextUtils.isEmpty(token)) {
            // token 不存在
            LogUtils.d(TAG, " isOldMode=" + CommonUtil.isOldMode());
            if (CommonUtil.isOldMode()) {
                if (phone != null && !TextUtils.isEmpty(phone)) {
                    if (TelNumMatch.isValidPhoneNumber(phone)) {
                        onLoginPost();
                    }
                } else {
                    // 手机号为null，跳转到登录界面手动输入手机号
                    Intent intent = new Intent();
                    intent.setClass(LocationBrowserActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent();
                intent.setClass(LocationBrowserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            addFragment();
        }

    }

    private void onLoginPost() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userName", phone);
        map.put("password", "");
        map.put("type", String.valueOf(Common.type));
        client.postRequest(Common.task.login, Common.api.login, map);

        client.setResponse(new onResponseCode() {

            @Override
            public void setPostResponse(final String responseData) {
                // TODO Auto-generated method stub
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        dealLoginResponseData(responseData);
                    }
                };
                handler.post(runnable);
            }
        });
    }

    private void dealLoginResponseData(String jsonData) {
        LogUtils.d(TAG, "dealLoginResponseData jsonData=" + jsonData);
        if (!CommonUtil.isJsonDataResultEmpty(jsonData)) {
            LoginGson response = gson.fromJson(jsonData, LoginGson.class);
            TokenResult result = gson.fromJson(jsonData, LoginGson.class).getResult();
            int code = response.getCode();
            String msg = response.getMsg();
            if (code == Common.code.SUCCESS) {
                token = result.getTk();
                locationMainFragment.refreshUser();
                PreferenceHelper.putString(mContext, "token", token);
            } else {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        int i = 0;
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                i++;
            }
        }
        if (i == 0) {
            return true;
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        switch (requestCode) {
        case MY_PERMISSION_REQUEST_CODE:
            LogUtils.d(TAG, "grantResults=" + grantResults[0] + "  permissions="
                    + permissions[0]);
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(permission.READ_PHONE_STATE)) {
                    Toast.makeText(mContext, R.string.toast_permission_deny,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else {
                addFragment();
                checkCurrentAccount();
            }
            break;

        default:
            break;
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        // 将这一行注释掉，阻止activity保存fragment的状态,避免Fragment加载时出现界面重叠
        // super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("deprecation")
    private void addFragment() {

        transaction = getFragmentManager().beginTransaction();
        tabHelp.setSelected(true);
        tabHelp.setTextColor(getResources()
                .getColor(R.color.bottom_bar_font_select_color));
        tabSetting.setTextColor(getResources().getColor(
                R.color.bottom_bar_font_noselect_color));
        if (locationMainFragment == null) {
            locationMainFragment = new LocationMainFragment();
        }
        if (!locationMainFragment.isAdded()) {
            transaction.add(R.id.fragment_container, locationMainFragment);
        }
        transaction.commit();
    }

    private void initView() {
        tabHelp = (TextView) findViewById(R.id.txt_help);
        tabSetting = (TextView) findViewById(R.id.txt_setting);
        topBarLayout = (TopBarLayout) findViewById(R.id.topbar);

        topBarLayout.showTitle(false);

        tabHelp.setOnClickListener(this);
        tabSetting.setOnClickListener(this);

        topBarLayout.showBackButtonVisibility(false);
        if (CommonUtil.isOldMode()) {
            topBarLayout.showForwardButton(false);
        } else {
            topBarLayout.showForwardButton(true);
            topBarLayout.setForwardView(R.drawable.member_list_icon);
            topBarLayout
                    .setOnBackAndForwardClickListener(new setOnBackAndForwardClickListener() {

                        @Override
                        public void onRightButtonClick() {
                            locationMainFragment.showPopupWindow(topBarLayout
                                    .getForwardView());
                        }

                        @Override
                        public void onLeftButtonClick() {

                        }
                    });
        }
    }

    // 重置所有文本的选中状态
    public void selected() {
        tabHelp.setSelected(false);
        tabSetting.setSelected(false);
    }

    // 隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction) {
        if (settingsFragment != null) {
            transaction.hide(settingsFragment);
        }
        if (locationMainFragment != null) {
            transaction.hide(locationMainFragment);
        }
    }

    @Override
    public void onClick(View v) {
        transaction = getFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (v.getId()) {
        case R.id.txt_setting:
            selected();
            tabSetting.setSelected(true);
            tabHelp.setTextColor(getResources().getColor(
                    R.color.bottom_bar_font_noselect_color));
            tabSetting.setTextColor(getResources().getColor(
                    R.color.bottom_bar_font_select_color));

            topBarLayout.showBackButtonVisibility(false);
            topBarLayout.showForwardButton(false);
            topBarLayout.showTitle(true);
            topBarLayout.setTitle(getResources().getString(R.string.settings_text));

            if (settingsFragment == null) {
                settingsFragment = new SettingsFragment(LocationBrowserActivity.this);
            }
            settingsFragment.refresh();
            if (!settingsFragment.isAdded()) {
                transaction.add(R.id.fragment_container, settingsFragment);
            } else {
                if (locationMainFragment != null) {
                    transaction.hide(locationMainFragment);
                }
                transaction.show(settingsFragment);
            }
            break;
        case R.id.txt_help:
            selected();

            tabHelp.setSelected(true);
            tabHelp.setTextColor(getResources().getColor(
                    R.color.bottom_bar_font_select_color));
            tabSetting.setTextColor(getResources().getColor(
                    R.color.bottom_bar_font_noselect_color));

            topBarLayout.showBackButtonVisibility(false);
            if (!CommonUtil.isOldMode()) {
                topBarLayout.showForwardButton(true);
            }
            topBarLayout.setForwardView(R.drawable.member_list_icon);
            topBarLayout.showTitle(false);

            if (locationMainFragment == null) {

                locationMainFragment = new LocationMainFragment();
            }

            locationMainFragment.refreshUser();
            if (!locationMainFragment.isAdded()) {

                transaction.add(R.id.fragment_container, locationMainFragment);
            } else {
                if (settingsFragment != null) {
                    transaction.hide(settingsFragment);
                }
                transaction.show(locationMainFragment);

            }
            break;

        }
        transaction.commit();

    }
}
