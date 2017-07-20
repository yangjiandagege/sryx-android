package com.yj.sryx.model.beans;

import android.graphics.drawable.Drawable;

import com.yj.sryx.widget.indexlib.IndexBar.bean.BaseIndexPinyinBean;

/**
 * Created by eason.yang on 2017/7/20.
 */

public class Contact extends BaseIndexPinyinBean {
    private String user;
    private String name;
    private Drawable headerPic;
    private boolean isTop;//是否是最上面的 不需要被转化成拼音的

    public Contact(String user, String name, boolean isTop) {
        this.user = user;
        this.name = name;
        this.isTop = isTop;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getHeaderPic() {
        return headerPic;
    }

    public void setHeaderPic(Drawable headerPic) {
        this.headerPic = headerPic;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    @Override
    public String getTarget() {
        return name;
    }

    @Override
    public boolean isNeedToPinyin() {
        return !isTop;
    }

    @Override
    public boolean isShowSuspension() {
        return !isTop;
    }
}
