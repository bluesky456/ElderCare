package com.ovvi.remotelocation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.utils.LogUtils;

public class TopBarLayout extends RelativeLayout implements OnClickListener {

    private TextView title;
    private Button back;
    private Button forward;
    private Context mContext;
    private AttributeSet mAttributeSet;
    private setOnBackAndForwardClickListener listener;// 监听点击事件

    // 设置监听器
    public void setOnBackAndForwardClickListener(setOnBackAndForwardClickListener listener) {
        LogUtils.d("TopbarLayout", "setOnBackAndForwardClickListener");
        this.listener = listener;
    }

    public TopBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_titlebar, this);
        mContext = context;
        mAttributeSet = attrs;
        back = (Button) findViewById(R.id.button_backward);
        forward = (Button) findViewById(R.id.button_forward);
        title = (TextView) findViewById(R.id.text_title);

        back.setOnClickListener(this);
        forward.setOnClickListener(this);

        // 获得自定义属性并赋值
        TypedArray typeArray = mContext.obtainStyledAttributes(mAttributeSet,
                R.styleable.TopBar);
        int leftBtnBackground = typeArray.getResourceId(
                R.styleable.TopBar_leftBackground, 0);
        int rightBtnBackground = typeArray.getResourceId(
                R.styleable.TopBar_rightBackground, 0);

    }

    public View getForwardView() {
        return forward;
    }
    
    public View getBackView() {
        return back;
    }
    
    @Override
    public void onClick(View v) {
        LogUtils.d("TopBarlayout ", "onClick v="+v);
        if (v == back) {
            listener.onLeftButtonClick();// 点击回调
            
        } else if (v == forward) {
            listener.onRightButtonClick();// 点击回调
        }
    }

    public void setBackView(int imageId) {
        back.setBackgroundResource(imageId);
    }
    
    public void setForwardView(int imageId) {
        forward.setBackgroundResource(imageId);
    }
    
    /** 设置返回按钮是否可见 */
    public void showBackButtonVisibility(boolean flag) {
        if (flag) {
            back.setVisibility(View.VISIBLE);
        } else {
            back.setVisibility(View.INVISIBLE);
        }
    }

    /** 设置标题内容是否可见 */
    public void showTitle(boolean flag) {
        if (flag) {
            title.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.INVISIBLE);
        }
    }

    /** 设置右边按钮是否可见 */
    public void showForwardButton(boolean flag) {
        if (flag) {
            forward.setVisibility(View.VISIBLE);
        } else {
            forward.setVisibility(View.INVISIBLE);
        }
    }

    public void setTitle(CharSequence title) {
        ((TextView) title).setText(title);
    }

    /** 设置标题内容 */
    public void setTitle(int titleId) {
        LogUtils.d("TopBarLayout", "setTitle ");
        title.setText(titleId);
    }

    /** 设置标题内容 */
    public void setTitle(String  str) {
        LogUtils.d("TopBarLayout", " String setTitle ");
        title.setText(str);
    }
    
    // 按钮点击接口
    public interface setOnBackAndForwardClickListener {
        public void onLeftButtonClick();

        public void onRightButtonClick();
    }

}
