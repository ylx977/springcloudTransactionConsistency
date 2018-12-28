package com.fuzamei.enums;

import lombok.Getter;

/**
 * Created by ylx on 2018/12/27.
 */
@Getter
public enum TypeEnum {

    /**
     * 事务成功标记
     */
    OK(1,"OK"),

    /**
     * 事务失败标记
     */
    NO(0,"NO");

    private Integer code;
    private String name;
    TypeEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }
}
