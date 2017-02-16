package com.example.eli.bledebug.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.eli.bledebug.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Eli on 2017/2/15.
 */

public class ExpandableListViewaAdapter extends BaseExpandableListAdapter {
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    Context mContext;
    //定义两个List用来控制Group和Child中的String;

//    private List<String> groupArray;//组列表
//    private List<List<String>> childArray;//子列表

    ArrayList<HashMap<String, String>> groupArray =
            new ArrayList<HashMap<String, String>>();//组列表
    ArrayList<ArrayList<HashMap<String, String>>> childArray
            = new ArrayList<ArrayList<HashMap<String, String>>>();//子列表

    public ExpandableListViewaAdapter(Context a, ArrayList<HashMap<String, String>> group, ArrayList<ArrayList<HashMap<String, String>>> child) {
        this.mContext = a;
        this.groupArray = group;
        this.childArray = child;
    }

    @Override
    public int getGroupCount() {
        return groupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childArray.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getGroup(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childArray.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = (View) ((Activity) mContext).getLayoutInflater().from(mContext).inflate(
                    R.layout.expandablelistview_groups, null);
            groupHolder = new GroupHolder();
            groupHolder.txt = (TextView) convertView.findViewById(R.id.textGroup);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.txt.setText(groupArray.get(groupPosition).get(LIST_NAME));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ItemHolder itemHolder = null;
        if (convertView == null) {
            convertView = (View) ((Activity) mContext).getLayoutInflater().from(mContext).inflate(
                    R.layout.expandablelistview_child, null);
            itemHolder = new ItemHolder();
            itemHolder.txt = (TextView) convertView.findViewById(R.id.textChild);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }
        itemHolder.txt.setText(Html.fromHtml(childArray.get(groupPosition).get(childPosition).get(LIST_UUID)));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder {
        public TextView txt;
    }

    class ItemHolder {
        public TextView txt;
    }
}
