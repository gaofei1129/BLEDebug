package com.example.eli.bledebug.adapter;

/**
 * Created by Eli on 2017/2/15.
 * <p/>
 * .* 列表适配器
 * .* @author ELI
 * <p/>
 * .
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eli.bledebug.R;

import java.util.List;

public class InputHistoryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    List<String> mList;
    private Callback mCallback;

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     *
     * @author Ivan Xu
     *         2014-11-26
     */
    public interface Callback {
        public void click(View v, int position);
    }

    public InputHistoryAdapter(Context context, List<String> list,Callback callback) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
        this.mCallback = callback;
    }

    //新加的一个函数，用来更新数据
    public void setData(List<String> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public String getDevice(int position) {
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
            convertView = mInflater.inflate(R.layout.item_history_list, null);
            //加载布局模板控件
            viewHolder.textName = ((TextView) convertView.findViewById(R.id.txt_input));
            viewHolder.imgDel = ((ImageView) convertView.findViewById(R.id.img_del));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textName.setText(mList.get(position)); //显示
        viewHolder.imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.click(v, position);
            }
        });
        return convertView;
    }

    public final class ViewHolder {
        public TextView textName;
        public ImageView imgDel;
    }

}