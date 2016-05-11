package com.proffstore.andrew.mapsproffstore;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by Andrew on 12.05.2016.
 */
public class PointAdapter extends BaseAdapter {
    private final LayoutInflater lInflater;
    private List<String> data = null;
    private List<Integer> pointsIndex = new ArrayList<>();

    public PointAdapter(Context context, List<String> data) {
        this.data = data;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.points_item, parent, false);
        }
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        TextView pointListName = (TextView) view.findViewById(R.id.pointName);
        pointListName.setText(data.get(position));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pointsIndex.add((Integer) position);
                } else {
                    pointsIndex.remove((Integer) position);
                }
            }
        });
        return view;
    }
}
