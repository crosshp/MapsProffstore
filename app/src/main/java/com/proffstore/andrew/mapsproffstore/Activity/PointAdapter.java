package com.proffstore.andrew.mapsproffstore.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.proffstore.andrew.mapsproffstore.Entity.PointItem;
import com.proffstore.andrew.mapsproffstore.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 12.05.2016.
 */
public class PointAdapter extends BaseAdapter {
    public List<PointItem> list;
    private Context mContext;
    private LayoutInflater mInflator;


    private ArrayList<Integer> pointsIndex = new ArrayList<>();

    public PointAdapter(Context context, List<PointItem> list) {
        this.list = list;
        this.mContext = context;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public ArrayList<Integer> getPointsIndex() {
        return pointsIndex;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public PointItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflator.inflate(R.layout.points_item, null);
            holder.text = (TextView) convertView.findViewById(R.id.pointName);
         //   holder.checkbox = (android.support.v7.widget.AppCompatCheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final int pos = position;
        holder.text.setText(list.get(position).getName());
        /*holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list.get(pos).setChecked(isChecked);
                pointsIndex.add(pos);
            }
        });*/

        return convertView;
    }

    static class ViewHolder {
        TextView text;
        android.support.v7.widget.AppCompatCheckBox checkbox;
    }
}

