package com.cqut.mall.controller;

import com.cqut.mall.common.ApiRestResponse;
import com.cqut.mall.common.Constant;
import com.cqut.mall.exception.ImoocMallException;
import com.cqut.mall.exception.ImoocMallExceptionEnum;
import com.cqut.mall.model.pojo.User;
import com.cqut.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
//@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/getUser")
    public User getUser() {
        User user = userService.getUser();
        return user;
    }

    @PostMapping("/register")
    public ApiRestResponse register(@RequestParam("username") String username,
                                    @RequestParam("password") String password) throws ImoocMallException {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        //  密码长度不能少于8位
        if (password.length() < 8) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.registerUser(username, password);
        return ApiRestResponse.success();
    }

    @PostMapping("/login")
    public ApiRestResponse login(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 HttpSession session) throws ImoocMallException {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        User loginUser = userService.login(username, password);
        //  返回用户信息时，不返回密码
        loginUser.setPassword(null);
        //  登录成功后，把没有密码的用户信息存入session，即保持登录状态
        session.setAttribute(Constant.IMOOC_MALL_USER, loginUser);
        return ApiRestResponse.success(loginUser);
    }

    @PostMapping("/user/update")
    public ApiRestResponse updateUserInfo(@RequestParam String signature,
                                          HttpSession session) throws ImoocMallException {
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (null == currentUser) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        userService.updateUserInformation(currentUser, signature);
        return ApiRestResponse.success();
    }

    @PostMapping("/user/logout")
    public ApiRestResponse logout(HttpSession session) throws ImoocMallException {
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    @PostMapping("/adminLogin")
    public ApiRestResponse adminLogin(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      HttpSession session) throws ImoocMallException {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        User loginUser = userService.login(username, password);
        if (userService.checkAdminRole(loginUser)) {
            //  返回用户信息时，不返回密码
            loginUser.setPassword(null);
            session.setAttribute(Constant.IMOOC_MALL_USER, loginUser);
            return ApiRestResponse.success(loginUser);
        }
        return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);

    }
}
