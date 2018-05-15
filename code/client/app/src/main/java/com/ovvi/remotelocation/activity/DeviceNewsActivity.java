package com.ovvi.remotelocation.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.ovvi.remotelocation.R;

public class DeviceNewsActivity extends TitleActivity {

    private ListView mListView;
    private String newsTitle;
    private String newsTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setContentView(R.layout.device_news);
    }

    private void initView() {
        hideForwardView();
        hideDropView();
        showBackwardView(R.drawable.top_bar_back);
        setTitle(R.string.device_text);

        mListView = (ListView) findViewById(R.id.list_news);
    }
}
