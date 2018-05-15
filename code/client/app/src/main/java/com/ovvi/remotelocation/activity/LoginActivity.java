package com.ovvi.remotelocation.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.TokenResult;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.gson.LoginGson;
import com.ovvi.remotelocation.model.logic.RemoteLocationLogicManager;
import com.ovvi.remotelocation.model.logic.RemoteLocationLogicPolicy;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.PreferenceHelper;
import com.ovvi.remotelocation.utils.TelNumMatch;

/**
 * 登录主界面
 * 
 * @author chensong
 * 
 */
public class LoginActivity extends Activity implements OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText phoneEditText;
    private EditText pwdEditText;
    private Button loginButton;
    private TextView registerButton;
    private TextView fgtButton;

    String username;
    String password;
    private Context mContext;
    private HttpUtil client;
    private Handler handler = new Handler();
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        mContext = getApplication();
        client = new HttpUtil(mContext);
        initView();

    }

    private void initView() {

        phoneEditText = (EditText) findViewById(R.id.login_phone);
        pwdEditText = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_btn);
        registerButton = (TextView) findViewById(R.id.register_login_btn);
        fgtButton = (TextView) findViewById(R.id.forgot_btn);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        fgtButton.setOnClickListener(this);
        if (CommonUtil.isOldMode()) {
            fgtButton.setVisibility(View.GONE);
            pwdEditText.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
        } else {
            fgtButton.setVisibility(View.VISIBLE);
            pwdEditText.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
        case R.id.login_btn:
            username = phoneEditText.getText().toString();
            password = pwdEditText.getText().toString();
            if (CommonUtil.isOldMode()) {
                registerAccount();
            } else {
                onLoginItemClicked();
            }
            break;

        case R.id.register_login_btn:
            onRegisterItemClicked();
            break;

        case R.id.forgot_btn:
            onForgetItemClicked();
            break;

        default:
            break;
        }
    }

    private void showTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(getResources().getString(R.string.network_error));
        builder.setMessage(getResources().getString(R.string.network_setting));
        builder.setPositiveButton(getResources().getString(R.string.dialog_positive),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 如果没有网络连接，则进入网络设置界面
                        dialog.cancel();
                    }
                });
        builder.create();
        builder.show();
    }

    private void registerAccount() {

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(mContext, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (CommonUtil.checkNetworkState(mContext)) {
            HashMap<String, String> map = new HashMap<String, String>();
            String brand = CommonUtil.getPhoneBrand();
            String product = CommonUtil.getPhoneModel();
            String imei = CommonUtil.getImei(mContext);

            LogUtils.d(TAG, "username=" + username + " brand=" + brand + " product="
                    + product + " imei=" + imei + " type=" + String.valueOf(Common.type));
            if (!TelNumMatch.isValidPhoneNumber(username)) {
                Toast.makeText(mContext,
                        getResources().getString(R.string.username_format_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            map.put("userName", username);
            map.put("nickname", username);
            map.put("password", "");
            map.put("brand", brand);
            map.put("product", product);
            map.put("imei", imei);
            map.put("type", String.valueOf(Common.type));

            LogUtils.d(TAG, "map.length=" + map.size());
            client.postRequest(Common.task.register, Common.api.register, map);
            client.setResponse(new onResponseCode() {

                @Override
                public void setPostResponse(final String jsonData) {
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            dealRegisterResponseData(jsonData);
                        }
                    };
                    handler.post(runnable);

                }

            });
        } else {
            showTips();
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    private void dealRegisterResponseData(String jsonData) {
        LogUtils.d(TAG, "dealRegisterResponseData jsonData=" + jsonData);
        CommonGson response = gson.fromJson(jsonData, CommonGson.class);
        int code = response.getCode();
        String msg = response.getMsg();
        if (code == Common.code.SUCCESS || code == Common.code.RECORD_ALREADY_EXIST) {
            onLoginItemClicked();
        } else {
            switch (code) {
            case Common.code.ILLEGAL_FORMAT:
                msg = getResources().getString(R.string.username_format_error);
                break;
            case Common.code.VT_FAILED:
                msg = getResources().getString(R.string.current_time_error);
                break;
            default:
                break;
            }
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }

    }

    private void onLoginItemClicked() {
        if (CommonUtil.checkNetworkState(getApplication())) {
            HttpUtil client = new HttpUtil(getApplicationContext());
            HashMap<String, String> map = new HashMap<String, String>();

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(mContext,
                        getResources().getString(R.string.username_empty),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            map.put("userName", username);
            if (CommonUtil.isOldMode()) {
                map.put("password", "");
            } else {
                map.put("password", password);
            }
            map.put("type", String.valueOf(Common.type));

            client.postRequest(Common.task.login, Common.api.login, map);
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

        } else {
            showTips();
        }
    }

    private void dealResponseData(String jsonData) {
        LogUtils.d(TAG, "dealresponseData jsonData=" + jsonData);
        if (!CommonUtil.isJsonDataResultEmpty(jsonData)) {
            LoginGson response = gson.fromJson(jsonData, LoginGson.class);
            TokenResult tk = response.getResult();
            int code = response.getCode();
            String msg = response.getMsg();
            if (code == Common.code.SUCCESS) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        updateUserDataBases();
                    }
                });

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, LocationBrowserActivity.class);
                PreferenceHelper.putString(mContext, "token", tk.getTk());
                startActivity(intent);
                finish();
            }
        } else {
            CommonGson response = gson.fromJson(jsonData, CommonGson.class);
            int code = response.getCode();
            String msg = "";
            switch (code) {
            case Common.code.LOGIN_FAILED:
                msg = getResources().getString(R.string.login_verify_toast);
                break;
            case Common.code.VT_FAILED:
                msg = getResources().getString(R.string.current_time_error);
                break;
            case Common.code.PARAM_EMPTY:
                msg = getResources().getString(R.string.login_password_empty);
                break;

            default:
                break;
            }
            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserDataBases() {
        if (!username.equals(PreferenceHelper.getString(mContext, "userName"))) {
            RemoteLocationLogicPolicy policy = RemoteLocationLogicManager.getInstance(
                    mContext).getRemoteLocationLogicPolicy();
            policy.clearAllNotice();
            PreferenceHelper.putString(mContext, "userName", username);
        }

    }

    private void onRegisterItemClicked() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void onForgetItemClicked() {
        Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
        startActivity(intent);
    }

}
