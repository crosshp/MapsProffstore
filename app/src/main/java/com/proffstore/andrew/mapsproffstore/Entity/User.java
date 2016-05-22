package com.proffstore.andrew.mapsproffstore.Entity;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Andrew on 18.05.2016.
 */
public class User extends RealmObject{
    private String name;
    private int id;

    public User() {
    }

    public User(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
