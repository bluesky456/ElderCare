package com.ovvi.remotelocation.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.adapter.PopupListAdapter;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.HomeElderlResult;
import com.ovvi.remotelocation.bean.HomeResult;
import com.ovvi.remotelocation.bean.Members;
import com.ovvi.remotelocation.bean.Notice;
import com.ovvi.remotelocation.bean.UserInfo;
import com.ovvi.remotelocation.constants.Constant;
import com.ovvi.remotelocation.db.DBManager;
import com.ovvi.remotelocation.dialog.DialogUtils;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.gson.HomeElderlGson;
import com.ovvi.remotelocation.gson.HomeGson;
import com.ovvi.remotelocation.model.PopupListInfo;
import com.ovvi.remotelocation.model.logic.RemoteLocationLogicManager;
import com.ovvi.remotelocation.model.logic.RemoteLocationLogicPolicy;
import com.ovvi.remotelocation.model.provider.DataBaseConstants.ContentUri;
import com.ovvi.remotelocation.service.GsonDataSaver;
import com.ovvi.remotelocation.service.MessageService;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.PreferenceHelper;

public class LocationMainFragment extends Fragment implements OnClickListener {

    private static final String TAG = "LocationMainFragment";

    private ScrollView menuView;
    private ImageView image;
    private TextView tvNewNotice;
    private ImageView imageline;
    private ImageView home_line;
    private TextView nameTv;
    private TextView phoneTv;
    private TextView remoteView;

    /** 没有联系人时提示 */
    private TextView toastTv;
    private LinearLayout top_layout;

    /** 用户头像 */
    private String portraitString;
    /** 用户昵称 */
    private String nicknameString;
    /** 用户名 */
    private String usernameString;

    private int add_member_state;

    private RelativeLayout remoteLayout;
    private RelativeLayout trackLayout;
    private RelativeLayout fenceLayout;
    private RelativeLayout houseLayout;
    private Context mContext;
    private PopupListAdapter pAdapter;

    private List<PopupListInfo> pListInfos = new ArrayList<PopupListInfo>();
    private PopupListInfo pInfo;

    private ListView popListView;
    private LinearLayout layout;
    private PopupWindow popupWindow;

    private DBManager dbManager;
    private HttpUtil client;
    private Handler handler = new Handler();
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();
    private DialogUtils dialogUtils;

    private String[] permissions = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private static final int MY_PERMISSION_REQUEST_CODE = 10010;

    private Bitmap bitmap;
    private int current_user = 0;
    /** 家成员信息 */
    private List<Members> members = new ArrayList<Members>();
    private int currentUser_Id;

    public LocationMainFragment() {
    }

    private ContentObserver observer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        dbManager = DBManager.getInstance(mContext);
        client = new HttpUtil(mContext);
        refreshUser();

        observer = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange) {
                refreshNewNotice();
            }
        };
        mContext.getContentResolver().registerContentObserver(
                ContentUri.REMOTE_LOCATION_NOTICES, true, observer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.main_layout_normal, null);
        initView(view);

        layout = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.pop_layout, null);
        // 找到布局的控件
        popListView = (ListView) layout.findViewById(R.id.pop_list);
        pAdapter = new PopupListAdapter(mContext, pListInfos);
        popListView.setAdapter(pAdapter);

        LogUtils.d(TAG, " onCreateView mList");
        return view;

    }

    private void refreshNewNotice() {
        if (null != tvNewNotice) {
            RemoteLocationLogicPolicy policy = RemoteLocationLogicManager.getInstance(
                    mContext).getRemoteLocationLogicPolicy();
            int unReadNoticeCount = policy.getUnReadNoticesCount();
            tvNewNotice.setText(String.valueOf(unReadNoticeCount));
            tvNewNotice.setVisibility((0 < unReadNoticeCount) ? View.VISIBLE
                    : View.INVISIBLE);
        }
    }

    private void initView(View view) {

        tvNewNotice = (TextView) view.findViewById(R.id.new_notice);
        refreshNewNotice();
        tvNewNotice.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Constant.ACTION_SHOW_NOTICE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }
        });

        image = (ImageView) view.findViewById(R.id.people_icon);
        imageline = (ImageView) view.findViewById(R.id.fence_line);
        home_line = (ImageView) view.findViewById(R.id.home_line);
        toastTv = (TextView) view.findViewById(R.id.add_toast);
        nameTv = (TextView) view.findViewById(R.id.main_desc);
        phoneTv = (TextView) view.findViewById(R.id.main_info);
        remoteView = (TextView) view.findViewById(R.id.remote_textView);

        menuView = (ScrollView) view.findViewById(R.id.menu_item);
        top_layout = (LinearLayout) view.findViewById(R.id.top_layout);

        remoteLayout = (RelativeLayout) view.findViewById(R.id.menu_remote);
        trackLayout = (RelativeLayout) view.findViewById(R.id.menu_track);
        fenceLayout = (RelativeLayout) view.findViewById(R.id.menu_fence);
        houseLayout = (RelativeLayout) view.findViewById(R.id.menu_home);

        remoteLayout.setOnClickListener(this);
        trackLayout.setOnClickListener(this);
        fenceLayout.setOnClickListener(this);
        houseLayout.setOnClickListener(this);
        if (CommonUtil.isOldMode()) {
            fenceLayout.setVisibility(View.GONE);
            imageline.setVisibility(View.GONE);
            houseLayout.setVisibility(View.VISIBLE);
            home_line.setVisibility(View.VISIBLE);
            remoteView.setText(getResources().getString(R.string.my_location));
            top_layout.setBackgroundResource(R.drawable.background_main);
            LayoutParams people = image.getLayoutParams();
            people.height = 200;
            people.width = 200;
            image.setLayoutParams(people);

            LayoutParams params = remoteLayout.getLayoutParams();
            params.height = 120;
            remoteLayout.setLayoutParams(params);

            LayoutParams track = trackLayout.getLayoutParams();
            track.height = 120;
            trackLayout.setLayoutParams(track);

            LayoutParams fence = fenceLayout.getLayoutParams();
            fence.height = 120;
            fenceLayout.setLayoutParams(fence);

            LayoutParams house = houseLayout.getLayoutParams();
            house.height = 120;
            houseLayout.setLayoutParams(house);

        } else {
            houseLayout.setVisibility(View.GONE);
            home_line.setVisibility(View.GONE);
            imageline.setVisibility(View.VISIBLE);
            fenceLayout.setVisibility(View.VISIBLE);
            remoteView.setText(getResources().getString(R.string.remote_text));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshUser();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void refreshUser() {
        String token = PreferenceHelper.getString(getActivity(), "token");
        HashMap<String, String> map = new HashMap<String, String>();

        if (token != null) {
            map.put("token", token);
        } else {
            Intent intent = new Intent();
            intent.setClass(mContext, LoginActivity.class);
            startActivity(intent);
        }
        client.postRequest(Common.task.home, Common.api.home, map);
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

    private void enableMessageService(Context context) {
        LogUtils.d(TAG, "enableMessageService from "
                + this.getActivity().getLocalClassName());
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(Constant.ACTION_ENABLE_MESSAGE_SERVICE);
        context.startService(intent);
    }

    private void dealResponseData(String jsonData) {
        LogUtils.d(TAG, "dealResponseData jsonData=" + jsonData);
        if (!CommonUtil.isJsonDataResultEmpty(jsonData)) {
            if (CommonUtil.isOldMode()) {

                LogUtils.d(TAG, "dealResponseData isOldMode");
                HomeElderlGson respone = gson.fromJson(jsonData, HomeElderlGson.class);
                int code = respone.getCode();
                String msg = respone.getMsg();
                HomeElderlResult result = respone.getResult();
                LogUtils.d(TAG, "dealResponseData result=" + result);
                UserInfo info = result.getInfo();
                LogUtils.d(TAG, "dealResponseData info=" + info);
                if (code == Common.code.SUCCESS && info != null) {
                    portraitString = info.getPortrait();
                    nicknameString = info.getNickname();
                    usernameString = info.getUserName();
                    LogUtils.d(TAG, "dealResponseData portraitString=" + portraitString);
                    if (portraitString != null && !TextUtils.isEmpty(portraitString)) {
                        new Task().execute(portraitString);
                        handler = new Handler() {
                            public void handleMessage(android.os.Message msg) {
                                if (msg.what == 0x123) {
                                    if (bitmap != null) {
                                        image.setImageBitmap(bitmap);
                                    } else {
                                        image.setImageResource(R.drawable.main_icon);
                                    }

                                }
                            };
                        };
                    } else {
                        image.setBackgroundResource(R.drawable.main_icon);
                    }

                    phoneTv.setText(usernameString);
                    nameTv.setText(nicknameString);

                    // enable message service
                    enableMessageService(getActivity());
                    GsonDataSaver saver = new GsonDataSaver(getActivity(),
                            Constant.PREFERENCE_NOTICE_FILE);
                    List<Notice> list = saver
                            .getDataList(Constant.PREFERENCE_ADD_FAMILY_KEY);
                    if (!list.isEmpty()) {
                        // TODO show add family dialog
                    }
                }
            } else {
                HomeGson response = gson.fromJson(jsonData, HomeGson.class);
                HomeResult result = response.getResult();
                members = result.getMembers();
                LogUtils.d(TAG, "dealResponseData members=" + members);
                int code = response.getCode();
                if (code == Common.code.SUCCESS) {
                    if (members != null && members.size() > 0) {
                        showAddMemberToast(false);
                        refresh(members);
                    } else {
                        showAddMemberToast(true);
                    }
                }

                // enable message service
                enableMessageService(getActivity());
                GsonDataSaver saver = new GsonDataSaver(getActivity(),
                        Constant.PREFERENCE_NOTICE_FILE);
                List<Notice> list = saver.getDataList(Constant.PREFERENCE_ADD_FAMILY_KEY);
                if (!list.isEmpty()) {
                    // TODO show add family dialog
                }
            }
        } else {
            CommonGson response = gson.fromJson(jsonData, CommonGson.class);
            int code = response.getCode();
            String msg = response.getMsg();
            switch (code) {
            case Common.code.DATA_EMPTY:
                members = null;
                showAddMemberToast(true);
                break;
            case Common.code.USER_NOT_EXIST:
            case Common.code.TOKEN_FAILED:
                Intent intent = new Intent();
                intent.setClass(mContext, LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            default:
                break;
            }
        }

    }

    class Task extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            bitmap = GetImageInputStream((String) params[0]);
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

    private void refresh(List<Members> mList) {
        Members member = mList.get(current_user);
        add_member_state = member.getState();
        if (TextUtils.isEmpty(member.getPortrait())) {
            image.setImageResource(R.drawable.main_icon);
        } else {
            new Task().execute(member.getPortrait());
            handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == 0x123) {
                        if (bitmap != null) {
                            image.setImageBitmap(bitmap);
                        } else {
                            image.setImageResource(R.drawable.main_icon);
                        }
                    }
                };
            };
        }
        currentUser_Id = member.getId();
        nameTv.setText(member.getLabel());
        phoneTv.setText(member.getUserName());
    }

    private void popUpHints() {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils(mContext);
            dialogUtils.setTitle("添加家人");
            dialogUtils.setMessage("当前家人列表为空，是否开始添加家成员？");
            dialogUtils.setOnPositiveClickListener("是",
                    new DialogUtils.setOnPositiveClickListener() {

                        @Override
                        public void onPositiveClick() {
                            dialogUtils.dismiss();
                            Intent intent = new Intent(mContext,
                                    FamilyMemberActivity.class);
                            startActivity(intent);
                        }
                    });
            dialogUtils.setOnNegativeClickListener("否",
                    new DialogUtils.setOnNegativeClickListener() {

                        @Override
                        public void onNegativeClick() {
                            LogUtils.d(TAG, "onNegativeClick ");
                            dialogUtils.dismiss();
                        }
                    });
        }
        dialogUtils.show();

    }

    private void showAddMemberToast(boolean flag) {
        if (flag) {
            toastTv.setVisibility(View.VISIBLE);
            top_layout.setBackgroundResource(R.color.white);
            nameTv.setVisibility(View.GONE);
            phoneTv.setVisibility(View.GONE);
            menuView.setVisibility(View.GONE);
            image.setVisibility(View.INVISIBLE);

        } else {
            nameTv.setVisibility(View.VISIBLE);
            phoneTv.setVisibility(View.VISIBLE);
            menuView.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            toastTv.setVisibility(View.GONE);
            top_layout.setBackgroundResource(R.drawable.background_main);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != observer) {
            mContext.getContentResolver().unregisterContentObserver(observer);
            observer = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isOldMode()) {
            switch (v.getId()) {
            case R.id.menu_remote:
                onRemoteItemClicked(currentUser_Id);
                break;
            case R.id.menu_track:
                onTrackItemClicked(currentUser_Id);
                break;
            case R.id.menu_home:
                onHouseItemClicked();
                break;

            default:
                break;
            }
        } else {
            if (add_member_state == 0) {
                Toast.makeText(mContext,
                        getResources().getString(R.string.add_member_state_confirmed),
                        Toast.LENGTH_SHORT).show();
            } else if (add_member_state == 2) {
                Toast.makeText(mContext,
                        getResources().getString(R.string.add_member_state_no),
                        Toast.LENGTH_SHORT).show();
            } else {
                switch (v.getId()) {
                case R.id.menu_remote:
                    onRemoteItemClicked(currentUser_Id);
                    break;
                case R.id.menu_track:
                    onTrackItemClicked(currentUser_Id);
                    break;

                case R.id.menu_fence:
                    onFenceItemClicked(currentUser_Id);
                    break;

                default:
                    break;
                }
            }
        }

    }

    /**
     * 展示成员列表
     * 
     * @param parent
     */
    public void showPopupWindow(View parent) {

        // 设置适配器
        if (members == null) {
            return;
        }

        if (members.size() > 0) {
            pListInfos.clear();
            for (Members member : members) {

                pInfo = new PopupListInfo(member.getPortrait(), member.getLabel());
                pListInfos.add(pInfo);
            }
        }
        pAdapter.notifyDataSetChanged();

        // 实例化popupWindow
        popupWindow = new PopupWindow(layout, 500, 600);
        // 控制键盘是否可以获得焦点
        popupWindow.setFocusable(true);
        // 设置popupWindow弹出窗体的背景
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        WindowManager manager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.4f;
        getActivity().getWindow().setAttributes(lp);
        popupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });

        // 获取xoff
        int xpos = manager.getDefaultDisplay().getWidth() - popupWindow.getWidth() - 10;
        // xoff,yoff基于anchor的左下角进行偏移。
        if (Build.VERSION.SDK_INT < 24) {
            popupWindow.showAsDropDown(parent, xpos, 60);
        } else {
            int[] a = new int[2];
            parent.getLocationInWindow(a);
            popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, xpos,
                    parent.getHeight() + a[1] + 18);
            popupWindow.update();
        }
        // 监听
        popListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {

                pAdapter.setSelectItem(index);

                current_user = index;
                refresh(members);
                // 关闭popupWindow
                popupWindow.dismiss();
                popupWindow = null;
            }
        });
    }

    /**
     * 远程定位
     * 
     * @param userId
     */
    private void onRemoteItemClicked(int userId) {
        if (CommonUtil.isOldMode()) {
            Intent intent = new Intent(mContext, RemotePositionActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } else {
            askLocation(userId);
        }
    }

    /**
     * 远程定位请求
     */
    private void askLocation(int userId) {

        HashMap<String, String> map = new HashMap<String, String>();

        map.put("token", PreferenceHelper.getString(mContext, "token"));
        map.put("toId", String.valueOf(userId));
        client.postRequest(Common.task.remote_ask, Common.api.remote_ask, map);
        client.setResponse(new onResponseCode() {

            @Override
            public void setPostResponse(final String responseData) {
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        dealRemoteLocationAsk(responseData);
                    }
                };
                handler.post(runnable);
            }
        });

    }

    /**
     * 处理远程定位请求响应数据
     * 
     * @param json
     */
    private void dealRemoteLocationAsk(String json) {
        LogUtils.d(TAG, "dealRemoteLocationAsk json=" + json);
        CommonGson response = gson.fromJson(json, CommonGson.class);
        int code = response.getCode();
        String msg = response.getMsg();
        if (code == Common.code.SUCCESS || code == Common.code.RECORD_ALREADY_EXIST) {
            Intent intent = new Intent(mContext, RemotePositionActivity.class);
            intent.putExtra("userId", currentUser_Id);
            startActivity(intent);
        } else {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 历史轨迹
     * 
     * @param userId
     */
    private void onTrackItemClicked(int userId) {
        Intent intent = new Intent(mContext, HistoryTrackActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    /**
     * 地理围栏
     * 
     * @param userId
     */
    private void onFenceItemClicked(int userId) {
        Intent intent = new Intent(mContext, GeoFenceActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    /**
     * 
     * 回家指路
     */
    private void onHouseItemClicked() {
        Intent intent = new Intent(mContext, HomeRouteActivity.class);
        startActivity(intent);

    }

}
