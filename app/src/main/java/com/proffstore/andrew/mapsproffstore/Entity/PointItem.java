package com.proffstore.andrew.mapsproffstore.Entity;

/**
 * Created by Andrew on 18.05.2016.
 */
public class PointItem {
    private String name;
    private boolean isChecked;

    public PointItem(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    public PointItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
