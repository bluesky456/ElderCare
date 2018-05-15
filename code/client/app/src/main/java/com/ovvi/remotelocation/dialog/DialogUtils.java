package com.ovvi.remotelocation.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ovvi.remotelocation.R;

/**
 * 自定义Dialog
 * 
 * @author chensong
 * 
 */
public class DialogUtils extends Dialog {

    private TextView titleTv;
    private TextView messageTv;
    private TextView leftBtn;
    private TextView rightBtn;

    private String titleStr;
    private String messageStr;
    private String negString;
    private String posString;

    private setOnNegativeClickListener negListener;// 监听点击事件
    private setOnPositiveClickListener posListener;

    // 设置取消按钮的显示内容和监听
    public void setOnNegativeClickListener(String str,
            setOnNegativeClickListener negListener) {
        if (str != null) {
            negString = str;
        }
        this.negListener = negListener;
    }

    // 设置监听器
    public void setOnPositiveClickListener(String str,
            setOnPositiveClickListener posListener) {
        if (str != null) {
            posString = str;
        }
        this.posListener = posListener;
    }

    public DialogUtils(Context context) {
        // TODO Auto-generated constructor stub
        super(context, R.style.MyDialog);
    }

    protected DialogUtils(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);

        setCanceledOnTouchOutside(false);
        initView();
        initData();
        initEvent();

    }

    private void initEvent() {
        // 设置确定按钮被点击后，向外界提供监听
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posListener != null) {
                    posListener.onPositiveClick();
                }
            }
        });

        // 设置取消按钮被点击后，向外界提供监听
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negListener != null) {
                    negListener.onNegativeClick();
                }
            }
        });
    }

    private void initData() {
        // 如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }
        // 如果设置按钮的文字
        if (posString != null) {
            rightBtn.setText(posString);
        }
        if (negString != null) {
            leftBtn.setText(negString);
        }
    }

    private void initView() {
        titleTv = (TextView) findViewById(R.id.dialog_title);
        messageTv = (TextView) findViewById(R.id.dialog_message);
        leftBtn = (TextView) findViewById(R.id.dialog_left);
        rightBtn = (TextView) findViewById(R.id.dialog_right);
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setMessage(String message) {
        messageStr = message;
    }

    /** 设置取消按钮被点击的接口 */
    public interface setOnNegativeClickListener {
        public void onNegativeClick();

    }

    /** 设置确定按钮被点击的接口 */
    public interface setOnPositiveClickListener {

        public void onPositiveClick();
    }
    
    @Override
    public void dismiss() {
        super.dismiss();
    }
}
