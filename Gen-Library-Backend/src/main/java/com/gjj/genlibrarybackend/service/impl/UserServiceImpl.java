package com.gjj.genlibrarybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gjj.genlibrarybackend.model.entity.User;
import com.gjj.genlibrarybackend.service.UserService;
import com.gjj.genlibrarybackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 78568
* @description 针对表【user(用户信息表)】的数据库操作Service实现
* @createDate 2025-06-24 15:56:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




