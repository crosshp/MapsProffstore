package com.proffstore.andrew.mapsproffstore;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 12.05.2016.
 */
public class PointAdapter extends BaseAdapter {
    private final LayoutInflater lInflater;
    private List<String> data = null;
    ArrayList<Boolean> positionArray;

    public List<Integer> getPointsIndex() {
        return pointsIndex;
    }

    private List<Integer> pointsIndex = new ArrayList<>();

    public PointAdapter(Context context, List<String> data) {
        this.data = data;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        positionArray = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            positionArray.add(false);
        }
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
        AppCompatCheckBox checkBox = null;
        if (view == null) {
            view = lInflater.inflate(R.layout.points_item, parent, false);
        }
        checkBox = (AppCompatCheckBox) view.findViewById(R.id.checkBox);
        checkBox.setChecked(positionArray.get(position));
        TextView pointListName = (TextView) view.findViewById(R.id.pointName);
        pointListName.setText(data.get(position));
        final AppCompatCheckBox finalCheckBox = checkBox;
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    positionArray.add(position, true);
                    finalCheckBox.setSelected(true);
                    pointsIndex.add((Integer) position);
                } else {
                    positionArray.add(position, false);
                    finalCheckBox.setSelected(false);
                    pointsIndex.remove((Integer) position);
                }
            }
        });
        return view;
    }
}
