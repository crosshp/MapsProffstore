package com.proffstore.andrew.mapsproffstore.Entity;

/**
 * Created by Andrew on 18.05.2016.
 */
public class User {
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
