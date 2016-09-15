package com.joy.http.samples;

/**
 * Created by Daisw on 16/9/13.
 */

public class User {

    long id;
    String name;

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
