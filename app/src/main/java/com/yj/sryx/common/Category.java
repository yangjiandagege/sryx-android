package com.yj.sryx.common;

/**
 * Created by yangjian on 2016/10/18.
 */

public class Category {
    private int id;
    private String name;
    private int resId;
    private Theme theme;

    public Category(int id, String name, int resId, Theme theme) {
        this.id = id;
        this.name = name;
        this.resId = resId;
        this.theme = theme;
    }

    public Category(int id, String name, Theme theme) {
        this.id = id;
        this.name = name;
        this.theme = theme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int id) {
        this.resId = id;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
