package com.ovvi.remotelocation.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ovvi.remotelocation.R;

/**
 * 
 * @author chensong 自定义标题栏
 * 
 */
public class TitleActivity extends Activity implements OnClickListener {

    // private RelativeLayout mLayoutTitleBar;
    private TextView mTitleTextView;
    private TextView mNetworkTips;
    private Button mBackwardbButton;
    private Button mForwardButton;
    private FrameLayout mContentLayout;
    private ImageView dropView;
    private BroadcastReceiver mConnectionChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
				ConnectivityManager manager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				boolean enabled = false;

				NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
					if (null != activeNetwork && activeNetwork.isConnected() && activeNetwork.isAvailable()) {
						if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
								|| activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
							// 当前移动网络连接可用
							enabled = true;
						}
					}
				
				onNetworkChanged(enabled);
			}
		}
	};;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setupViews(); // 加载 activity_title 布局 ，并获取标题及两侧按钮

		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		this.registerReceiver(mConnectionChangeReceiver, filter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mConnectionChangeReceiver) {
			this.unregisterReceiver(mConnectionChangeReceiver);
			mConnectionChangeReceiver = null;
		}
	}

	/**
	 * 网络状态变化回调
	 * @param isConnected 网络是否可用
	 */
	private void onNetworkChanged(boolean isConnected) {
		if (null != mNetworkTips) {
			mNetworkTips.setVisibility(isConnected ? View.GONE : View.VISIBLE);
		}
	}

    private void setupViews() {
        super.setContentView(R.layout.activity_title);
        mTitleTextView = (TextView) findViewById(R.id.text_title);
        mNetworkTips = (TextView) findViewById(R.id.network_tips_tv);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
        mBackwardbButton = (Button) findViewById(R.id.button_backward);
        dropView = (ImageView) findViewById(R.id.drop);
        mForwardButton = (Button) findViewById(R.id.button_forward);

        mTitleTextView.setOnClickListener(this);
        mBackwardbButton.setOnClickListener(this);
        mForwardButton.setOnClickListener(this);
        dropView.setOnClickListener(this);
    }

    /**
     * 显示返回按钮
     * 
     * @param backwardResid
     *            文字
     * @param show
     *            true则显示
     */
    protected void showBackwardView(int backwardResid) {
        if (mBackwardbButton != null) {
            mBackwardbButton.setBackgroundResource(backwardResid);
            mBackwardbButton.setVisibility(View.VISIBLE);
        } // else ignored
    }

    /**
     * 隐藏返回按钮
     * 
     * @param backwardResid
     * @param hide
     */
    protected void hideBackwardView() {
        if (mBackwardbButton != null) {
            mBackwardbButton.setVisibility(View.INVISIBLE);
        } // else ignored
    }

    /**
     * 显示提交按钮
     * 
     * @param forwardResId
     *            文字
     * @param show
     *            true则显示
     */
    protected void showForwardView(int forwardResId) {
        if (mForwardButton != null) {
            mForwardButton.setVisibility(View.VISIBLE);
            mForwardButton.setBackgroundResource(forwardResId);
        }
    }

    protected void showDropView() {
        if (dropView != null) {
            dropView.setVisibility(View.VISIBLE);
        }
    }

    protected void hideDropView() {
        if (dropView != null) {
            dropView.setVisibility(View.GONE);
        }
    }

    protected void setDropView(int dropViewId) {
        if (dropView != null) {
            dropView.setImageResource(dropViewId);
        }
    }

    /**
     * 隐藏提交按钮
     * 
     * @param forwardResId
     * @param hide
     */
    protected void hideForwardView() {
        if (mForwardButton != null) {
            mForwardButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 返回按钮点击后触发
     * 
     * @param backwardView
     */
    protected void onBackward(View backwardView) {
        super.onBackPressed();
    }

    /**
     * 提交按钮点击后触发
     * 
     * @param forwardView
     */
    protected void onForward(View forwardView) {

    }

    protected void onTitleClicked(View dropView) {
    }

    /** 设置标题内容 */
    @Override
    public void setTitle(int titleId) {
        mTitleTextView.setText(titleId);
    }

    /** 设置标题内容 */
    @Override
    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    /** 设置标题文字颜色 */
    @Override
    public void setTitleColor(int textColor) {
        mTitleTextView.setTextColor(textColor);
    }

    @Override
    public void setContentView(int layoutResID) {
        mContentLayout.removeAllViews();
        View.inflate(this, layoutResID, mContentLayout);
        onContentChanged();
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        onContentChanged();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view, params);
        onContentChanged();
    }

    /**
     * 按钮点击调用的方法
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.button_backward:
            onBackward(v);
            break;

        case R.id.button_forward:
            onForward(v);
            break;

        case R.id.drop:
            onTitleClicked(v);
        default:
            break;
        }
    }

}