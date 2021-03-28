package com.example.demo.util;

import com.alibaba.fastjson.JSONObject;

public class JsonUtil {

    public static String toJsonString(Object o) {
        return JSONObject.toJSONString(o);
    }

    public static String toJsonString(String s) {
        return JSONObject.parseObject(s).toJSONString();
    }

    public static String formatString(Object o) {
        return JSONObject.toJSONString(o, true);
    }

    public static String formatString(String s) {
        return formatString(JSONObject.parseObject(s));
    }
}
