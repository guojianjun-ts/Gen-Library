package com.gjj.genlibrarybackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjj.genlibrarybackend.exception.BusinessException;
import com.gjj.genlibrarybackend.exception.ErrorCode;
import com.gjj.genlibrarybackend.exception.ThrowUtils;
import com.gjj.genlibrarybackend.model.dto.user.UserAddRequest;
import com.gjj.genlibrarybackend.model.dto.user.UserQueryRequest;
import com.gjj.genlibrarybackend.model.dto.user.UserUpdateRequest;
import com.gjj.genlibrarybackend.model.entity.User;
import com.gjj.genlibrarybackend.model.enums.UserRoleEnum;
import com.gjj.genlibrarybackend.model.vo.LoginUserVO;
import com.gjj.genlibrarybackend.model.vo.UserVO;
import com.gjj.genlibrarybackend.service.UserService;
import com.gjj.genlibrarybackend.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.gjj.genlibrarybackend.model.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 78568
 * @description 针对表【user(用户信息表)】的数据库操作Service实现
 * @createDate 2025-06-24 15:56:37
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public long userRegister(String userAccount , String userPassword , String checkPassword) {
        //1.参数检验
        if (StrUtil.hasBlank(userAccount , userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "用户账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "两次密码输入不一致");
        }
        //2.检验账号是否已经存在
        long count = lambdaQuery().eq(User::getUserAccount , userAccount).count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR , "用户已存在");
        }
        //3.对密码进行加密处理
        String encryptPassword = getEncryptPassword(userPassword);
        //4.插入新用户的数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("xxx");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);

        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR , "注册失败，系统异常");
        }
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount , String userPassword , HttpServletRequest request) {
        //1.参数校验
        if (StrUtil.hasBlank(userAccount , userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "账号或密码为空");
        }
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "账号或密码错误");
        }
        //2.查询用户是否存在
        User user = lambdaQuery().eq(User::getUserAccount , userAccount).oneOpt().orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR , "账号不存在或密码错误"));
        //3.查询对应密码是否正确
        String encryptPassword = getEncryptPassword(userPassword);
        if (!user.getUserPassword().equals(encryptPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "账号不存在或密码错误");
        }
        //4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE , user);
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        //1. 从Session中获取用户ID
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        //2.校验会话中的用户信息
        User currentUser = (User) userObj;
        if (currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        //3.从数据库中查询最新用户信息
        currentUser = this.getById(currentUser.getId());
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR , "用户不存在或被删除");
        }
        return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object currentUser = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    //开始用户管理开发
    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        User user = new User();
        BeanUtil.copyProperties(userAddRequest , user);
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR , "注册失败，系统异常");
        }
        return user.getId();
    }

    @Override
    public User getUserById(long id) {
        return this.getById(id);
    }

    @Override
    public UserVO getUserVOById(long id) {
        User user = this.getUserById(id);
        ThrowUtils.throwIf(user == null , ErrorCode.NOT_FOUND_ERROR);
        return this.getUserVO(user);
    }

    @Override
    public boolean deleteUser(long id) {
        ThrowUtils.throwIf(id <= 0 , new BusinessException(ErrorCode.PARAMS_ERROR));
        return this.removeById(id);
    }

    @Override
    public boolean updateUser(UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest.getId() == null , ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest , user);
        boolean result = this.updateById(user);
        ThrowUtils.throwIf(!result , ErrorCode.OPERATION_ERROR);
        return true;
    }


    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user , userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollectionUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id) , "id" , id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole) , "userRole" , userRole);
        queryWrapper.like(StrUtil.isNotBlank(userName) , "userName" , userName);
        queryWrapper.like(StrUtil.isNotBlank(userAccount) , "userAccount" , userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userProfile) , "userProfile" , userProfile);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField) , sortOrder.equals("ascend") , sortField);

        return queryWrapper;

    }


    @Override
    public LoginUserVO getLoginUserVO(User user) {

        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user , loginUserVO);
        return loginUserVO;
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        if (StrUtil.isBlank(userPassword)) {
            return "";
        }
        final String salt = "gjj_salt";
        return DigestUtil.md5Hex(salt + userPassword);
    }

}


