package com.gjj.genlibrarybackend.model.dto.user;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -1934373754586858526L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

}
