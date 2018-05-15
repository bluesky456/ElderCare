package com.ovvi.remotelocation.activity;

import java.util.HashMap;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.db.DBManager;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;

public class RegisterActivity extends TitleActivity implements OnClickListener {
    private final static String TAG = "RegisterActivity";

    /** 用户昵称 */
    private EditText nickname_edit;
    /** 注册的用户名 */
    private EditText newUser;
    /** 注册密码 */
    private EditText newPassword;
    /** 加密答案 */
    private EditText secrecy_answer;
    /** 确认密码 */
    private EditText confirmPassword;

    private TextView answerBirth;
    private Button regButton;

    private String nickname;
    private String username;
    private String password;
    /** 密保答案 */
    private String answer;
    /** 密保问题 */
    private String issue;

    private ProgressDialog progressDialog;
    private Context mContext;

    private DBManager dbManager;

    private Handler handler = new Handler();

    private Spinner spinner;
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();

    private ArrayAdapter<String> adapter;

    private String selectDate;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        showBackwardView(R.drawable.top_bar_back);
        setTitle(R.string.register_text);
        hideForwardView();
        initView();
        mContext = getApplication();
        dbManager = DBManager.getInstance(mContext);

    }

    private void initView() {

        nickname_edit = (EditText) findViewById(R.id.register_nicknamne);
        newUser = (EditText) findViewById(R.id.register_phone);
        newPassword = (EditText) findViewById(R.id.register_password);
        confirmPassword = (EditText) findViewById(R.id.conf_password);

        answerBirth = (TextView) findViewById(R.id.register_birth);
        secrecy_answer = (EditText) findViewById(R.id.register_secrecy_answer);
        regButton = (Button) findViewById(R.id.register_btn);

        regButton.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.spinner_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                Common.question_data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        answerBirth.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Common.question_Id == 0) {
                    popupSelectDateWindow(findViewById(R.id.register_secrecy_answer));
                }
            }
        });

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                    long id) {
                LogUtils.d(TAG, "onItemSelected position=" + position);
                Common.question_Id = position;
                issue = Common.question_data[position];
                secrecy_answer.setText("");
                answerBirth.setText("");
                if (Common.question_Id == 0) {
                    answerBirth.setVisibility(View.VISIBLE);
                    secrecy_answer.setVisibility(View.GONE);
                } else {
                    answerBirth.setVisibility(View.GONE);
                    secrecy_answer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

    }

    /**
     * 生日选择
     * 
     * @param view
     */
    protected void popupSelectDateWindow(View view) {
        View contentview = LayoutInflater.from(RegisterActivity.this).inflate(
                R.layout.date_register_answer_picker, null);
        final PopupWindow pWindow = new PopupWindow(contentview,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        pWindow.setFocusable(true);
        pWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));

        DatePicker datePicker = (DatePicker) contentview.findViewById(R.id.select_picker);
        TextView confirm = (TextView) contentview.findViewById(R.id.text_btn);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        currentDate = year + "-" + (month + 1) + "-" + day;

        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pWindow != null && pWindow.isShowing()) {
                    pWindow.dismiss();
                }
                if (selectDate == null) {
                    selectDate = currentDate;
                }
                answerBirth.setFocusable(false);
                answerBirth.setText(selectDate);
            }
        });
        datePicker.init(year, month, day, new OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {

                selectDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                LogUtils.d(TAG, "selectDate==" + selectDate);
            }
        });
        pWindow.showAtLocation(view, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 200);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {

        case R.id.register_btn:
            onRegisterItemClicked();
            break;
        case R.id.button_backward:
            onBackPressed();
            break;
        default:
            break;
        }
    }

    /** 注册 */
    private void onRegisterItemClicked() {
        nickname = nickname_edit.getText().toString();
        username = newUser.getText().toString();
        password = newPassword.getText().toString();
        if (Common.question_Id == 0) {
            answer = answerBirth.getText().toString();
        } else {
            answer = secrecy_answer.getText().toString();
        }

        String confirmPwd = confirmPassword.getText().toString();

        if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPwd)
                || TextUtils.isEmpty(answer)) {
            Toast.makeText(mContext, getResources().getString(R.string.toast_text),
                    Toast.LENGTH_SHORT).show();
        } else {
            if (!password.equals(confirmPwd)) {
                Toast.makeText(mContext,
                        getResources().getString(R.string.password_equal),
                        Toast.LENGTH_SHORT).show();
            } else {

                if (CommonUtil.checkNetworkState(mContext)) {
                    HttpUtil client = new HttpUtil(getApplicationContext());
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("userName", username);
                    map.put("nickname", nickname);
                    map.put("password", password);
                    map.put("brand", CommonUtil.getPhoneBrand());
                    map.put("product", CommonUtil.getPhoneModel());
                    map.put("imei", CommonUtil.getImei(getApplicationContext()));
                    map.put("type", String.valueOf(Common.type));
                    map.put("qcode", String.valueOf(Common.question_Id));
                    map.put("answer", answer);

                    LogUtils.d(TAG, "userName=" + username);
                    LogUtils.d(TAG, "nickname=" + nickname);
                    LogUtils.d(TAG, "imei=" + CommonUtil.getImei(getApplicationContext()));
                    LogUtils.d(TAG, "Brand=" + CommonUtil.getPhoneBrand());
                    LogUtils.d(TAG, "product=" + CommonUtil.getPhoneModel());
                    LogUtils.d(TAG, "answer=" + answer);
                    LogUtils.d(TAG, "question_Id=" + Common.question_Id);
                    LogUtils.d(TAG, "type=" + map.get("type"));

                    client.postRequest(Common.task.register, Common.api.register, map);
                    client.setResponse(new onResponseCode() {

                        @Override
                        public void setPostResponse(final String responseData) {

                            Runnable runnable = new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    dealResponseJsonData(responseData);
                                }
                            };
                            handler.post(runnable);
                        }

                    });

                } else {
                    showTips();
                }
            }
        }
    }

    private void dealResponseJsonData(String json) {
        LogUtils.d(TAG, "json=" + json);
        CommonGson response = gson.fromJson(json, CommonGson.class);
        int code = response.getCode();
        String msg = null;
        LogUtils.d(TAG, "code=" + code);
        if (code == Common.code.SUCCESS) {
            dbManager.dbInsertUser(nickname, username);

            Intent intent = new Intent();
            intent.setClass(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            switch (code) {
            case Common.code.RECORD_ALREADY_EXIST:
                msg = getResources().getString(R.string.register_already);
                break;
            case Common.code.VT_FAILED:
                msg = getResources().getString(R.string.current_time_error);
                break;
            default:
                break;
            }
            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                        // startActivity(new
                        // Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.dialog_negative),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        RegisterActivity.this.finish();
                    }
                });
        builder.create();
        builder.show();
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
