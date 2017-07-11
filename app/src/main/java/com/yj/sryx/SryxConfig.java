package com.yj.sryx;

import com.yj.sryx.common.Category;
import com.yj.sryx.common.Theme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eason.yang on 2017/7/9.
 */

public class SryxConfig {
    public static final String WX_URL = "https://api.weixin.qq.com/";
    public static final String BASE_URL = "https://www.ywwxmm.cn/";

    public static final String WX_APP_SECRET = "cb42bbe845afb2d304851018dcf0d0fc";
    public static final String WX_APP_ID = "wx76c99251f28c1316";

    public class Key {
        public static final String CATEGORY_ID = "category_id";
        public static final String PAGE_ID = "page_id";
    }

    public static final List<Category> categoryList = new ArrayList<>();
    public static final int ID_0 = 0, ID_1 = 1, ID_2 = 2;
    static {
        categoryList.add(new Category(ID_0, "创建游戏", 0, Theme.green));
        categoryList.add(new Category(ID_1, "加入游戏",0, Theme.blue));
        categoryList.add(new Category(ID_2, "我的记录",0, Theme.spe));
    }
}
