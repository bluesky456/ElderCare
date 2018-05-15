package com.ovvi.remotelocation.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.dialog.DialogUtils;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.PreferenceHelper;
import com.ovvi.remotelocation.utils.TelNumMatch;

/**
 * 手动添加家成员界面
 * 
 * @author chensong
 * 
 */
public class AddMemberActivity extends TitleActivity implements OnClickListener {

    private static final String TAG = "AddMemberActivity";
    private TextView addName;
    private TextView addPhone;
    private Button confirmBtn;

    private String phoneString;

    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();
    private Handler handler = new Handler();

    private DialogUtils dialogUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_member_layout);

        initView();

        setTitle(R.string.add_family_member_title);
        hideForwardView();
        showBackwardView(R.drawable.top_bar_back);
    }

    private void initView() {
        addPhone = (TextView) findViewById(R.id.member_edit);
        addName = (TextView) findViewById(R.id.add_member_name);
        confirmBtn = (Button) findViewById(R.id.member_add);
        confirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (confirmBtn == v) {
            addMemberInfo();
        }
    }

    private void addMemberInfo() {
        String nameString = addName.getText().toString();
        phoneString = addPhone.getText().toString();
        if (!TelNumMatch.isValidPhoneNumber(phoneString)) {
            Toast.makeText(AddMemberActivity.this,
                    getResources().getString(R.string.member_phone_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(AddMemberActivity.this,
                    getResources().getString(R.string.member_label_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (nameString.getBytes().length > 12) {
            Toast.makeText(AddMemberActivity.this,
                    getResources().getString(R.string.member_name_too_length),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        HttpUtil client = new HttpUtil(getApplication());
        HashMap<String, String> map = new HashMap<String, String>();
        String token = PreferenceHelper.getString(AddMemberActivity.this, "token");
        map.put("phoneNum", phoneString);
        map.put("label", nameString);
        map.put("token", token);
        client.postRequest(Common.task.family_add, Common.api.family_add, map);
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

    private void dealResponseData(String jsonData) {
        CommonGson response = gson.fromJson(jsonData, CommonGson.class);
        String msg = response.getMsg();
        int code = response.getCode();

        if (code == Common.code.SUCCESS) {
            showDialogHints();
        } else {
            switch (code) {
            case Common.code.ALREADY_MEMBER:
                msg = getResources().getString(R.string.already_family_member);
                break;

            default:
                break;
            }
            Toast.makeText(AddMemberActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    // 发送短信
    public void sendSMS() {
        SmsManager smsManager = SmsManager.getDefault();
        String text = "该用户请求添加为家成员，apk下载链接： http://older.legalaxy.cn/res/old/ElderLocation.apk";
        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

        Intent intent1 = new Intent(SENT_SMS_ACTION);
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
        Intent intent2 = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
        smsManager.sendTextMessage(phoneString, null, text, sentIntent, deliveryIntent); // aidl服务,进程间的通信
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtils.d("AddmemberActivity", " action==" + intent.getAction());
                switch (getResultCode()) {
                case Activity.RESULT_OK:
                    LogUtils.d(TAG, "send message success");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    break;
                }
            }

        }, new IntentFilter(SENT_SMS_ACTION));

        // this.registerReceiver(new BroadcastReceiver() {
        // @Override
        // public void onReceive(Context context, Intent intent) {
        // Toast.makeText(AddMemberActivity.this, "收信人已经成功接收",
        // Toast.LENGTH_SHORT)
        // .show();
        // }
        // }, new IntentFilter(DELIVERED_SMS_ACTION));
    }

    private void showDialogHints() {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils(AddMemberActivity.this);
            dialogUtils.setTitle(getResources().getString(R.string.add_family_member_title));
            dialogUtils.setMessage(getResources().getString(R.string.add_family_send_sms));
            dialogUtils.setOnPositiveClickListener(getResources().getString(R.string.dialog_positive),
                    new DialogUtils.setOnPositiveClickListener() {

                        @Override
                        public void onPositiveClick() {
                            sendSMS();
                            Toast.makeText(AddMemberActivity.this,
                                    getResources().getString(R.string.add_member_state_confirmed),
                                    Toast.LENGTH_SHORT).show();
                            dialogUtils.dismiss();
                        }
                    });
            dialogUtils.setOnNegativeClickListener(getResources().getString(R.string.dialog_negative),
                    new DialogUtils.setOnNegativeClickListener() {

                        @Override
                        public void onNegativeClick() {
                            dialogUtils.dismiss();
                        }
                    });
        }
        dialogUtils.show();

    }
}
