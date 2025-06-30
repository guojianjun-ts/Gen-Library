package com.gjj.genlibrarybackend.model.dto.user;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -2041681969452325977L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
