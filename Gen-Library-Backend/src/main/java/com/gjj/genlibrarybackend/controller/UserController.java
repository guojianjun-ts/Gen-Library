package com.gjj.genlibrarybackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gjj.genlibrarybackend.annotation.AuthCheck;
import com.gjj.genlibrarybackend.common.BaseResponse;
import com.gjj.genlibrarybackend.common.DeleteRequest;
import com.gjj.genlibrarybackend.common.ResultUtils;
import com.gjj.genlibrarybackend.exception.ErrorCode;
import com.gjj.genlibrarybackend.exception.ThrowUtils;
import com.gjj.genlibrarybackend.model.constant.UserConstant;
import com.gjj.genlibrarybackend.model.dto.user.*;
import com.gjj.genlibrarybackend.model.entity.User;
import com.gjj.genlibrarybackend.model.vo.LoginUserVO;
import com.gjj.genlibrarybackend.model.vo.UserVO;
import com.gjj.genlibrarybackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null , ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount , userPassword , checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest , HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null , ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount , userPassword , request);
        return ResultUtils.success(loginUserVO);
    }

    @GetMapping("/get/login")
    public BaseResponse<User> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null , ErrorCode.NOT_LOGIN_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    //开始用户管理接口开发
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null , ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.addUser(userAddRequest));
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0 , ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.getUserById(id));
    }

    @GetMapping("/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserVO> getUserVOById(long id) {
        ThrowUtils.throwIf(id <= 0 , ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.getUserVOById(id));
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null , ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.deleteUser(deleteRequest.getId()));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null , ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.updateUser(userUpdateRequest));
    }

    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null , ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();  //获取当前页号
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current , pageSize) ,
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current , pageSize , userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}
