package com.ovvi.remotelocation.adapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.Members;

public class MemberAdapter extends BaseAdapter {

    List<Members> list = new ArrayList<Members>();
    private LayoutInflater inflater = null;
    private Handler handler;
    private Bitmap bitmap;
    private Context mContext;

    public MemberAdapter(Context context, List<Members> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
        handler = new Handler();
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        Members mInfo = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listmember_item, null);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.member_icon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.member_name);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.member_phone);
            viewHolder.add_state = (TextView) convertView.findViewById(R.id.state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ViewHolder finalViewHolder = viewHolder;
        if (mInfo.getPortrait() != null && !"".equals(mInfo.getPortrait())) {

            new Task().execute(mInfo.getPortrait());
            handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == 0x123) {
                        if (bitmap != null) {
                            finalViewHolder.imageView.setImageBitmap(bitmap);
                        } else {
                            finalViewHolder.imageView
                                    .setImageResource(R.drawable.people_icon);
                        }
                    }
                };
            };
        } else {
            viewHolder.imageView.setImageResource(R.drawable.people_icon);
        }
        viewHolder.tvTitle.setText(mInfo.getLabel());
        viewHolder.tvContent.setText(mInfo.getUserName());
        viewHolder.add_state.setText(Common.member_state[mInfo.getState()]);

        return convertView;
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

    private class ViewHolder {
        private ImageView imageView;
        private TextView tvTitle;
        private TextView tvContent;
        private TextView add_state;
    }
}