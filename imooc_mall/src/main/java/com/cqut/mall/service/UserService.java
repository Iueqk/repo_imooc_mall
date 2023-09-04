package com.cqut.mall.service;

import com.cqut.mall.exception.ImoocMallException;
import com.cqut.mall.model.pojo.User;

public interface UserService {
    User getUser();
    void registerUser(String username,String password) throws ImoocMallException;

    User login(String username, String password) throws ImoocMallException;

    void updateUserInformation(User currentUser, String signature) throws ImoocMallException;

    boolean checkAdminRole(User user);
}
