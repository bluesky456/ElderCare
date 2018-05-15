package com.ovvi.remotelocation.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.model.AddressListInfo;

public class AddressListAdapter extends BaseAdapter {

    private List<AddressListInfo> list = new ArrayList<AddressListInfo>();
    AddressListInfo pInfo;
    LayoutInflater inflater;

    public AddressListAdapter(Context mContext, List<AddressListInfo> list) {
        this.list = list;
        inflater = LayoutInflater.from(mContext);
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
        pInfo = list.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listaddress_item, null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.address_desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvName.setText(pInfo.getAddressDesc());
        return convertView;
    }

    private class ViewHolder {
        private TextView tvName;
    }
}
