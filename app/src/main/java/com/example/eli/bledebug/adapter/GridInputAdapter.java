package com.example.eli.bledebug.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.eli.bledebug.R;

import java.util.List;

/**
 * Created by Eli on 2017/2/15.
 */

public class GridInputAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> listName;
    private LayoutInflater mInflater;

    public GridInputAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.listName = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listName.size();
    }

    @Override
    public Object getItem(int position) {
        return listName.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //定义一个ImageView,显示在GridView里

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_grid_input, null);
            //加载布局模板控件
            viewHolder.textName = ((TextView) convertView.findViewById(R.id.txt_input));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textName.setText(listName.get(position)); //显示
        return convertView;
    }

    public final class ViewHolder {
        public TextView textName;
    }
}
