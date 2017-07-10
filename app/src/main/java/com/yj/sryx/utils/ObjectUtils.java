package com.yj.sryx.utils;

/**
 * Created by huxley on 17/1/8.
 */

public class ObjectUtils {

    public static boolean equals(Object o1, Object o2) {
        return o1 == null && o2 == null || o1 != null && o1.equals(o2);
    }



    public static boolean equalsString(String s1, String s2) {
        if (StringUtils.isSpace(s1)) {
            s1 = "";
        }
        if (StringUtils.isSpace(s2)) {
            s2 = "";
        }
        return s1.equals(s2);
    }
}
