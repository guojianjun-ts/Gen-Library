package com.gjj.genlibrarybackend.model.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {

    /**
     * 普通用户
     */
    USER("user" , "普通用户"),

    /**
     * 管理员
     */
    ADMIN("ADMIN" , "管理员");


    private final String code;
    private final String description;

    UserRoleEnum(String code,String description) {
        this.code = code;
        this.description = description;
    }


}
