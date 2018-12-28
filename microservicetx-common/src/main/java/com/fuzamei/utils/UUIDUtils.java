package com.fuzamei.utils;

import java.util.UUID;

/**
 * Created by ylx on 2018/12/27.
 */
public class UUIDUtils {

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
