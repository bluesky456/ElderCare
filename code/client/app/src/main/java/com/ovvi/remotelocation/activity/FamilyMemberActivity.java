package com.ovvi.remotelocation.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.adapter.MemberAdapter;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.Members;
import com.ovvi.remotelocation.dialog.DialogUtils;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.gson.MemberListGson;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.PreferenceHelper;

/**
 * 家成员列表主界面
 * 
 * @author chensong
 * 
 */
public class FamilyMemberActivity extends TitleActivity implements OnItemClickListener,
        OnClickListener {

    private static final String TAG = "FamilyMemberActivity";

    private Context mContext;
    private TextView noMember;
    private LinearLayout addButton;
    private Button forward;

    private ListView listView;
    private String nameString;
    private String phoneString;

    private List<Members> list = new ArrayList<Members>();
    List<Members> mList;
    private MemberAdapter mAdapter;

    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();
    private Members mInfo;
    private HttpUtil client;
    private Handler handler = new Handler();
    private DialogUtils dialogUtils;

    /** 操作用于id */
    private int select_id;
    /** 成员新的备注名 */
    private String newLabel;
    private boolean isContacts = false;

    private static final int MY_PERMISSION_REQUEST_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.famile_member_normal);
        initView();

        mContext = getApplication();
        client = new HttpUtil(mContext);
        mAdapter = new MemberAdapter(this, list);
        listView.setAdapter(mAdapter);

        hideForwardView();
        showBackwardView(R.drawable.top_bar_back);
        setTitle(R.string.add_family_member_title);
        listView.setOnItemClickListener(this);
        addButton.setOnClickListener(this);

        Log.d(TAG, "mAdapter.getCount()=" + mAdapter.getCount());
    }

    /**
     * 刷新成员列表
     */
    private void refreshMemberList() {
        String token = PreferenceHelper.getString(mContext, "token");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", token);

        if (client == null) {
            client = new HttpUtil(mContext);
        }
        client.postRequest(Common.task.family_list, Common.api.family_list, map);
        client.setResponse(new onResponseCode() {

            @Override
            public void setPostResponse(final String responseData) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        dealMemberList(responseData);
                    }
                };
                handler.post(runnable);

            }
        });

    }

    /**
     * 处理获取成员列表的响应数据
     * 
     * @param json
     */
    private void dealMemberList(String json) {
        LogUtils.d(TAG, "dealMemberList json==" + json);
        if (!CommonUtil.isJsonDataResultEmpty(json)) {
            MemberListGson response = gson.fromJson(json, MemberListGson.class);
            int code = response.getCode();
            mList = response.getResult();
            if (code == Common.code.SUCCESS) {
                list.clear();
                for (Members member : mList) {
                    list.add(member);
                }
                showMemberList();
                mAdapter.notifyDataSetChanged();
            }
        } else {
            CommonGson response = gson.fromJson(json, CommonGson.class);
            int code = response.getCode();
            if (code == Common.code.DATA_EMPTY) {
                list.clear();
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    private void initView() {
        noMember = (TextView) findViewById(R.id.no_member);
        addButton = (LinearLayout) findViewById(R.id.no_member_btn);
        listView = (ListView) findViewById(R.id.member_list);
        forward = (Button) findViewById(R.id.button_forward);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    LogUtils.d(TAG, "onStart isContacts=" + isContacts);
                    if (isContacts) {
                        Thread.sleep(500);
                    }
                    refreshMemberList();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isContacts = false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
        // TODO Auto-generated method stub
        select_id = list.get(index).getId();

        String title = getResources().getString(R.string.dialog_alert_title);
        String editLabel = getResources().getString(R.string.edit_member_label);
        String editDele = getResources().getString(R.string.del_member);
        showOperateDialog(title, editLabel, editDele);

    }

    /**
     * 修改成员备注名
     */
    private void dialogEditMember() {
        LogUtils.d(TAG, "dialogEditMember ");
        AlertDialog.Builder builder = new AlertDialog.Builder(FamilyMemberActivity.this);
        final AlertDialog dialog = builder.create();
        View contentview = View.inflate(FamilyMemberActivity.this,
                R.layout.dialog_rename_member, null);
        final EditText rename = (EditText) contentview.findViewById(R.id.edit_rename);
        Button cancel = (Button) contentview.findViewById(R.id.dialog_cancel);
        Button confirm = (Button) contentview.findViewById(R.id.dialog_confirm);
        LogUtils.d(TAG, "dialogEditMember newLabel..=" + newLabel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                newLabel = rename.getText().toString();
                if (TextUtils.isEmpty(newLabel)) {
                    Toast.makeText(FamilyMemberActivity.this,
                            getResources().getString(R.string.dialog_rename),
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (newLabel.getBytes().length > 12) {
                    Toast.makeText(FamilyMemberActivity.this,
                            getResources().getString(R.string.member_name_too_length),
                            Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    postMemberLabel();
                    dialog.dismiss();
                }
            }
        });
        dialog.setView(contentview);
        dialog.show();

    }

    /**
     * 删除确认
     */
    private void dialogDel() {
        final DialogUtils dialogUtils = new DialogUtils(FamilyMemberActivity.this);
        dialogUtils.setTitle(getResources().getString(R.string.del_member));
        dialogUtils.setMessage(getResources().getString(R.string.del_member_confirm));
        dialogUtils.setOnPositiveClickListener(
                getResources().getString(R.string.dialog_positive),
                new DialogUtils.setOnPositiveClickListener() {

                    @Override
                    public void onPositiveClick() {
                        deleteMember();
                        dialogUtils.dismiss();
                    }
                });
        dialogUtils.setOnNegativeClickListener(
                getResources().getString(R.string.dialog_negative),
                new DialogUtils.setOnNegativeClickListener() {

                    @Override
                    public void onNegativeClick() {
                        // TODO Auto-generated method stub
                        LogUtils.d(TAG, "onNegativeClick ");
                        dialogUtils.dismiss();
                    }
                });
        dialogUtils.show();
    }

    /**
     * 修改成员备注名请求
     */
    private void postMemberLabel() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", PreferenceHelper.getString(mContext, "token"));
        map.put("id", String.valueOf(select_id));
        map.put("label", newLabel);

        client.postRequest(Common.task.elabel, Common.api.elabel, map);
        client.setResponse(new onResponseCode() {

            @Override
            public void setPostResponse(final String responseData) {
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        dealResponse(responseData);
                    }
                };
                handler.post(runnable);
            }
        });
    }

    private void dealResponse(String json) {
        LogUtils.d(TAG, "dealResponse json=" + json);
        CommonGson response = gson.fromJson(json, CommonGson.class);
        int code = response.getCode();
        String msg = response.getMsg();
        if (code == Common.code.SUCCESS) {
            refreshMemberList();
        } else {
            switch (code) {
            case Common.code.NICKNAME_LENGTH:
                msg = getResources().getString(R.string.member_name_too_length);
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
            }
        }
    }

    /**
     * 删除家庭成员
     */
    private void deleteMember() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", PreferenceHelper.getString(mContext, "token"));
        map.put("uid", String.valueOf(select_id));
        LogUtils.d(TAG, "deleteMember uid=" + String.valueOf(select_id));
        LogUtils.d(TAG,
                "deleteMember token=" + PreferenceHelper.getString(mContext, "token"));
        client.postRequest(Common.task.family_del, Common.api.family_del, map);
        client.setResponse(new onResponseCode() {

            @Override
            public void setPostResponse(final String responseData) {
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        dealResponse(responseData);
                    }
                };
                handler.post(runnable);
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (addButton == v || forward == v) {
            String title = getResources().getString(R.string.add_member_title);
            String manualContent = getResources().getString(R.string.add_member_manual);
            String contactContent = getResources()
                    .getString(R.string.add_member_cantacts);

            showOperateDialog(title, manualContent, contactContent);
        }

    }

    /**
     * 操作对话框
     * 
     * @param title
     * @param content1
     * @param content2
     */
    private void showOperateDialog(String title, final String content1,
            final String content2) {
        final Intent intent = new Intent();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_operate_member, null);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_select_title);
        TextView tv_content1 = (TextView) view.findViewById(R.id.tv_select_write);
        TextView tv_content2 = (TextView) view.findViewById(R.id.tv_select_content);
        RelativeLayout menu_one = (RelativeLayout) view.findViewById(R.id.content_one);
        RelativeLayout menu_two = (RelativeLayout) view.findViewById(R.id.content_two);
        tv_title.setText(title);
        tv_content1.setText(content1);
        tv_content2.setText(content2);
        menu_one.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (content1.equals(getResources().getString(R.string.edit_member_label))) {
                    dialogEditMember();
                    dialog.dismiss();
                } else {
                    intent.setClass(FamilyMemberActivity.this, AddMemberActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }
        });

        menu_two.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (content2.equals(getResources().getString(R.string.del_member))) {
                    dialogDel();
                    dialog.dismiss();
                } else {
                    if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[] { Manifest.permission.READ_CONTACTS },
                                MY_PERMISSION_REQUEST_CODE);
                        dialog.dismiss();
                    } else {
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setData(ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, 1);
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == MY_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                    // 选择不再提示并拒绝后给出提示
                    Toast.makeText(mContext, R.string.toast_permission_deny,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mInfo = null;
        HashMap<String, String> map = new HashMap<String, String>();
        String token = PreferenceHelper.getString(mContext, "token");
        if (resultCode == Activity.RESULT_OK) {
            mInfo = getAddressBook(data);
            map.clear();
            map.put("phoneNum", mInfo.getUserName());
            map.put("label", mInfo.getLabel());
            map.put("token", token);

            LogUtils.d(TAG, "onActivityResult mInfo.getLabel()=" + mInfo.getLabel());
            LogUtils.d(TAG, "onActivityResult map.size()=" + map.size());
            if (map.size() == 3) {
                isContacts = true;
                LogUtils.d(TAG, "onActivityResult map.size()1111=" + map.size());
                client.postRequest(Common.task.family_add, Common.api.family_add, map);
                client.setResponse(new onResponseCode() {

                    @Override
                    public void setPostResponse(final String responseData) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                LogUtils.d(TAG,
                                        "onActivityResult dealAddMemberResponseData11="
                                                + responseData);
                                dealAddMemberResponseData(responseData);
                            }
                        };
                        handler.post(runnable);

                    }
                });
            }
        }

    }

    /**
     * 处理添加成员的响应数据
     * 
     * @param jsonData
     */
    private void dealAddMemberResponseData(String jsonData) {
        LogUtils.d(TAG, "dealAddMemberResponseData jsonData=" + jsonData);
        CommonGson response = gson.fromJson(jsonData, CommonGson.class);
        String msg = response.getMsg();
        int code = response.getCode();

        if (code == Common.code.SUCCESS) {
            showDialogHints();
        } else {
            switch (code) {
            case Common.code.ILLEGAL_FORMAT:
                msg = getResources().getString(R.string.username_format_error);
                break;
            case Common.code.ALREADY_MEMBER:
                msg = getResources().getString(R.string.already_family_member);
                break;

            default:
                break;
            }
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogHints() {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils(FamilyMemberActivity.this);
            dialogUtils.setTitle(getResources().getString(R.string.add_family_member_title));
            dialogUtils.setMessage(getResources().getString(R.string.add_family_send_sms));
            dialogUtils.setOnPositiveClickListener(getResources().getString(R.string.dialog_positive),
                    new DialogUtils.setOnPositiveClickListener() {

                        @Override
                        public void onPositiveClick() {
                            sendSMS();
                            Toast.makeText(FamilyMemberActivity.this,
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
    
    // 发送短信
    public void sendSMS() {
        SmsManager smsManager = SmsManager.getDefault();
        String text = "该用户请求添加为家成员，apk下载链接：  http://older.legalaxy.cn/res/old/ElderLocation.apk";
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
                LogUtils.d("FamilymemberActivity", " action==" + intent.getAction());
                switch (getResultCode()) {
                case Activity.RESULT_OK:
                    LogUtils.d("FamilymemberActivity", "send message success");
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

        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        }, new IntentFilter(DELIVERED_SMS_ACTION));
    }

    /**
     * 显示成员列表
     */
    private void showMemberList() {
        LogUtils.d(TAG, "showMemberList mAdapter.getCount()=" + mAdapter.getCount()
                + "  mList=" + mList);
        if (mAdapter.getCount() == 0 && mList == null) {
            listView.setVisibility(View.GONE);
            noMember.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
        } else {
            noMember.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            showForwardView(R.drawable.top_bar_add);
        }
    }

    /**
     * 获取通讯录联系人
     * 
     * @param data
     * @return
     */
    private Members getAddressBook(Intent data) {
        ContentResolver reContentResolverol = getContentResolver();
        Uri contactData = data.getData();
        Cursor cursor = managedQuery(contactData, null, null, null, null);
        cursor.moveToFirst();
        nameString = cursor.getString(cursor
                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        // 条件为联系人ID
        String contactId = cursor.getString(cursor
                .getColumnIndex(ContactsContract.Contacts._ID));
        // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
        Cursor phone = reContentResolverol.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                null, null);
        while (phone.moveToNext()) {
            phoneString = phone.getString(phone
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        }
        mInfo = new Members();
        mInfo.setLabel(nameString);
        mInfo.setUserName(phoneString);

        return mInfo;

    }

}
