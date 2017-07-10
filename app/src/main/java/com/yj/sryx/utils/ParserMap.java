package com.yj.sryx.utils;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by yangjian on 2016/8/25.
 */

public class ParserMap {
    private static Pattern intPattern = Pattern.compile("^[-\\+]?[\\d]*\\.0*$");

    public static String getString(Map map, String key, String defaultValue) {
        Object obj = map.get(key);
        return obj == null ?
                defaultValue :
                (obj instanceof Number && intPattern.matcher(obj.toString()).matches() ? String.valueOf(Long.valueOf(((Number) obj).longValue())) : obj.toString());
    }
}
