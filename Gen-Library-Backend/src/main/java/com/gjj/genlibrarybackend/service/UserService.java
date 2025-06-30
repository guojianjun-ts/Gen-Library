package com.gjj.genlibrarybackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gjj.genlibrarybackend.model.dto.user.UserAddRequest;
import com.gjj.genlibrarybackend.model.dto.user.UserQueryRequest;
import com.gjj.genlibrarybackend.model.dto.user.UserUpdateRequest;
import com.gjj.genlibrarybackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gjj.genlibrarybackend.model.vo.LoginUserVO;
import com.gjj.genlibrarybackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 78568
 * @description 针对表【user(用户信息表)】的数据库操作Service
 * @createDate 2025-06-24 15:56:37
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 检验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount , String userPassword , String checkPassword);


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      http 请求
     * @return 脱敏后的用户登录信息
     */
    LoginUserVO userLogin(String userAccount , String userPassword , HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request http 请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 管理员添加新用户
     *
     * @param userAddRequest 增加用户
     * @return 新用户 ID
     */
    Long addUser(UserAddRequest userAddRequest);

    /**
     * 根据 ID 获取用户信息（未脱敏）
     * @param id 用户 ID
     * @return 用户信息（未脱敏）
     */
    User getUserById(long id);

    /**
     * 根据 ID 获取用户信息（脱敏）
     * @param id 用户 ID
     * @return 用户信息（脱敏）
     */
    UserVO getUserVOById(long id);

    /**
     * 删除用户
     * @param id 用户 ID
     * @return 是否成功
     */
    boolean deleteUser(long id);

    /**
     * 修改用户信息
     * @param userUpdateRequest 修改用户信息请求
     * @return 是否成功
     */
    boolean updateUser(UserUpdateRequest userUpdateRequest);


    /**
     * 获取用户的脱敏信息
     * @param user 脱敏前的信息
     * @return 脱敏后的信息
     */
    UserVO getUserVO(User user);

    /**
     * 批量获取用户脱敏后的信息
     * @param userList 脱敏前的信息
     * @return 脱敏后的 List 列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取查询条件
     * @param userQueryRequest 查询条件
     * @return 查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取登录用户信息
     *
     * @param user 用户账户
     * @return 返回脱敏的用户登录信息
     */
    LoginUserVO getLoginUserVO(User user);



    /**
     * 对密码进行 md5hex 加密
     *
     * @param userPassword 用户密码
     * @return encryptPassword 加密后密码
     */
    String getEncryptPassword(String userPassword);
}
