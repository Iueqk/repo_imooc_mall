package com.cqut.mall.controller;

import com.cqut.mall.common.ApiRestResponse;
import com.cqut.mall.common.Constant;
import com.cqut.mall.exception.ImoocMallExceptionEnum;
import com.cqut.mall.model.pojo.User;
import com.cqut.mall.model.request.AddCategoryReq;
import com.cqut.mall.model.request.UpdateCategoryReq;
import com.cqut.mall.model.vo.CategoryVO;
import com.cqut.mall.service.CategoryService;
import com.cqut.mall.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 目录controller
 */
@RestController
public class CategoryController {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @ApiOperation("后台新增目录")
    @PostMapping("/admin/category/add")
    public ApiRestResponse addCategory(HttpSession session,
                                       @Valid @RequestBody AddCategoryReq addCategoryReq) {
        /**
         * 用@valid + @notNull注解检验是否为空
         */
        /*if (categoryVo.getName() == null || categoryVo.getType() == null ||
                categoryVo.getParentId() == null || categoryVo.getOrderNum() == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.PARAM_NOT_NULL);
        }*/

        //  在session中寻找用户，即要保证为登录状态。UserController中，用户登录成功后会把用户信息保存到session
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (null == currentUser) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        //  校验是否是管理员
        if (userService.checkAdminRole(currentUser)) {
            //  是管理员，执行操作
            categoryService.addCategory(addCategoryReq);
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
        return ApiRestResponse.success();
    }

    @ApiOperation("后台更新目录")
    @PostMapping("/admin/category/update")
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq,
                                          HttpSession session) {
        //  在session中寻找用户，即要保证为登录状态。UserController中，用户登录成功后会把用户信息保存到session
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (null == currentUser) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        //  校验是否是管理员
        if (userService.checkAdminRole(currentUser)) {
            //  是管理员，执行操作
            categoryService.updateCategory(updateCategoryReq);
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除目录")
    @PostMapping("/admin/category/delete")
    public ApiRestResponse deleteCategory(@RequestParam Integer id,
                                          HttpSession session) {

        /*
         *      检验是否登录状态、是否是管理员，已经交给拦截器同意检验。
         */

        categoryService.deleteCategory(id);

        return ApiRestResponse.success();
    }

    @ApiOperation("后台目录列表")
    @PostMapping("/admin/category/list")
    public ApiRestResponse listCategoryForAdmin(@RequestParam("pageNum") Integer pageNum,
                                                @RequestParam("pageSize") Integer pageSize) {
        PageInfo pageInfo = categoryService.listCategoryForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台目录列表")
    @PostMapping("/category/list")
    /*嵌套查询*/
    public ApiRestResponse listCategoryForCustomer() {
        List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer();
        return ApiRestResponse.success(categoryVOS);
    }
}
