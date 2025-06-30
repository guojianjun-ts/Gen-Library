package com.gjj.genlibrarybackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum UserRoleEnum {

    /**
     * 普通用户
     */
    USER("普通用户" , "user"),

    /**
     * 管理员
     */
    ADMIN("管理员" , "admin");


    private final String text;
    private final String value;

    UserRoleEnum(String text , String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 RoleValue 获取枚举
     *
     * @param value 枚举值的 value
     * @return 枚举值
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
