package com.fuzamei.enums;

import lombok.Getter;

/**
 * Created by ylx on 2018/12/27.
 */
@Getter
public enum ResponseEnum {

    /**
     * 表示成功响应的返回信息
     */
    SUCCESS(200,"SUCCESS"),

    /**
     * 表示失败的响应返回信息
     */
    FAIL(500,"FAIL");

    private Integer code;
    private String name;
    ResponseEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
