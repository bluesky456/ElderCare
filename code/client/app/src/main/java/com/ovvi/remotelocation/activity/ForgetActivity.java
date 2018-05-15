package com.ovvi.remotelocation.activity;

import java.util.HashMap;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.SeekAccountResult;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.gson.SeekAccountGson;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;

/**
 * 忘记密码界面
 * 
 * @author chensong
 * 
 */
public class ForgetActivity extends TitleActivity implements OnClickListener {

    private final static String TAG = "ForgetActivity";

    private Context mContext;
    private EditText user;
    private EditText pwd;

    private Button submit;
    private TextView issue;
    private TextView answerBirth;
    private TextView answer;
    private String username;
    private LinearLayout layout;
    private Handler handler = new Handler();

    private HttpUtil client;
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();

    /** 密保答案 */
    private String answerString;
    /** 密保问题 */
    private String issueString;

    /** 新密码 */
    private String newPassword;

    private String selectDate;
    private String currentDate;

    private int issueCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_layout);

        initView();

        hideForwardView();
        setTitle(R.string.forgot_title);
        showBackwardView(R.drawable.top_bar_back);
        mContext = getApplication();
        client = new HttpUtil(mContext);

    }

    private void initView() {
        layout = (LinearLayout) findViewById(R.id.issue_verify);
        user = (EditText) findViewById(R.id.forget_phone);
        pwd = (EditText) findViewById(R.id.forget_password);
        issue = (TextView) findViewById(R.id.forget_issue);
        answer = (EditText) findViewById(R.id.issue_answer);
        answerBirth = (TextView) findViewById(R.id.birth_answer);
        submit = (Button) findViewById(R.id.submit);

        answerBirth.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupSelectDateWindow(v);
            }
        });

        submit.setOnClickListener(this);
    }

    /**
     * 生日选择
     * 
     * @param view
     */
    protected void popupSelectDateWindow(View view) {
        View contentview = LayoutInflater.from(ForgetActivity.this).inflate(
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
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == submit) {
            String issueBtn = getResources().getString(R.string.issue_submit);
            if (submit.getText().equals(issueBtn)) {
                onVerifyIssue();
            } else {
                updatePassword();
            }
        }
    }

    private void onVerifyIssue() {
        username = user.getText().toString();
        HashMap<String, String> map = new HashMap<String, String>();
        if (username == null || TextUtils.isEmpty(username)) {
            Toast.makeText(mContext, getResources().getString(R.string.username_account),
                    Toast.LENGTH_SHORT).show();
        } else {
            map.put("phoneNum", username);
            client.postRequest(Common.task.question, Common.api.question, map);
            client.setResponse(new onResponseCode() {

                @Override
                public void setPostResponse(final String responseData) {
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            dealAccountResponse(responseData);
                        }
                    };
                    handler.post(runnable);
                }
            });
        }
    }

    private void dealAccountResponse(String json) {

        LogUtils.d(TAG, " dealAccountResponse json=" + json);
        if (!CommonUtil.isJsonDataResultEmpty(json)) {
            SeekAccountGson response = gson.fromJson(json, SeekAccountGson.class);
            SeekAccountResult result = response.getResult();
            int code = response.getCode();
            String msg = null;

            if (code == Common.code.SUCCESS) {
                layout.setVisibility(View.VISIBLE);
                submit.setText(R.string.submit);
                issueCode = result.getQcode();
                issueString = Common.question_data[issueCode];
                issue.setText(issueString);
                user.setEnabled(false);
                if (result.getQcode() == 0) {
                    answer.setVisibility(View.GONE);
                    answerBirth.setVisibility(View.VISIBLE);
                } else {
                    answer.setVisibility(View.VISIBLE);
                    answerBirth.setVisibility(View.GONE);
                }
            }

        } else {
            CommonGson response = gson.fromJson(json, CommonGson.class);
            String msg = response.getMsg();
            int code = response.getCode();

            switch (code) {
            case Common.code.ILLEGAL_FORMAT:
                msg = getResources().getString(R.string.username_format_error);
                break;
            case Common.code.DATA_EMPTY:
                msg = getResources().getString(R.string.user_no_register);
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

    private void updatePassword() {
        if (issueCode == 0) {
            answerString = answerBirth.getText().toString();
        } else {
            answerString = answer.getText().toString();
        }
        newPassword = pwd.getText().toString();

        if (TextUtils.isEmpty(answerString) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(mContext, getResources().getString(R.string.toast_text),
                    Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("phoneNum", username);
            map.put("password", newPassword);
            // 密保答案
            map.put("answer", answerString);

            client.postRequest(Common.task.seek_pwd, Common.api.seek_pwd, map);
            client.setResponse(new onResponseCode() {

                @Override
                public void setPostResponse(final String responseData) {
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            dealUpdatePassword(responseData);
                        }
                    };
                    handler.post(runnable);
                }
            });
        }

    }

    /**
     * 更新用户密码
     * 
     * @param json
     */
    private void dealUpdatePassword(String json) {

        CommonGson response = gson.fromJson(json, CommonGson.class);
        int code = response.getCode();
        String msg = response.getMsg();
        if (code == Common.code.SUCCESS) {

            Intent intent = new Intent();
            intent.setClass(ForgetActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            switch (code) {
            case Common.code.PASSWORD_ERROR:
                msg = getResources().getString(R.string.password_format_error);
                break;
            case Common.code.ANSWER_UNMATCH:
                msg = getResources().getString(R.string.secrecy_answer_error);
                break;

            default:
                break;
            }
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
