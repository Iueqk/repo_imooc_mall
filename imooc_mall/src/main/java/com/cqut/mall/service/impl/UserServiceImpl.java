package com.cqut.mall.service.impl;

import com.cqut.mall.common.Constant;
import com.cqut.mall.exception.ImoocMallException;
import com.cqut.mall.exception.ImoocMallExceptionEnum;
import com.cqut.mall.model.dao.UserMapper;
import com.cqut.mall.model.pojo.User;
import com.cqut.mall.service.UserService;
import com.cqut.mall.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUser() {
        User user = userMapper.selectByPrimaryKey(1);
        return user;
    }

    @Override
    public void registerUser(String username, String password) throws ImoocMallException {
        // 判断是否重名 不允许重名
        User user = userMapper.selectByUserName(username);
        if (user != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }

        User newUser = new User();
        newUser.setUsername(username);
        try {
            newUser.setPassword(MD5Utils.md5(password, Constant.SALT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int count = userMapper.insertSelective(newUser);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String username, String password) throws ImoocMallException {
        User user = null;
        try {
            user = userMapper.selectLogin(username, MD5Utils.md5(password, Constant.SALT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateUserInformation(User currentUser, String signature) throws ImoocMallException {
        User user = new User();
        user.setPersonalizedSignature(signature);
        user.setId(currentUser.getId());
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 1) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user) {
        return user.getRole().equals(2);
    }
}
