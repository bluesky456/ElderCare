package com.ovvi.remotelocation.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.model.ItemMenu;
import com.ovvi.remotelocation.utils.CommonUtil;

public class SettingsItemAdapter extends BaseAdapter {

    List<ItemMenu> list = new ArrayList<ItemMenu>();
    private LayoutInflater inflater = null;

    public SettingsItemAdapter(List<ItemMenu> list, Context context, Integer[] icon,
            Integer[] name) {
        this.list = list;
        inflater = LayoutInflater.from(context);
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
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        ItemMenu itemMenu = list.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listmenu_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.menu_icon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.menu_name);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.menu_select);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (CommonUtil.isOldMode()) {
            LayoutParams params = viewHolder.imageView.getLayoutParams();
            params.height = 120;
            params.width = 120;
            viewHolder.imageView.setLayoutParams(params);
        }
        viewHolder.imageView.setImageResource(itemMenu.getMenuView());
        viewHolder.tvTitle.setText(itemMenu.getMenuTitle());
        if (TextUtils.isEmpty(itemMenu.getMenuContent())) {
            viewHolder.tvContent.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.tvContent.setText(itemMenu.getMenuContent());
        }

        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView tvTitle;
        private TextView tvContent;
    }
}
