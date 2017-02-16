package com.example.eli.bledebug.adapter;

/**
 * Created by Eli on 2016/7/22.
 * <p/>
 * .* 网络列表适配器
 * .* @author ELI
 * <p/>
 * .
 */

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.eli.bledebug.R;

import java.util.List;

public class BlueListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    List<BluetoothDevice> mList;

    public BlueListAdapter(Context context, List<BluetoothDevice> list) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
    }

    //新加的一个函数，用来更新数据
    public void setData(List<BluetoothDevice> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public BluetoothDevice getDevice(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_wifi_list, null);
            //加载布局模板控件
            viewHolder.textName = ((TextView) convertView.findViewById(R.id.txt_name_wifi));
            viewHolder.textLevel = ((TextView) convertView.findViewById(R.id.txt_level_wifi));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textName.setText(mList.get(position).getName()); //显示热点名称
        return convertView;
    }

    public final class ViewHolder {
        public TextView textLevel;
        public TextView textName;
    }
}