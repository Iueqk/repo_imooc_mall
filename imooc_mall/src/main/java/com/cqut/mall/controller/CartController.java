package com.cqut.mall.controller;

import com.cqut.mall.common.ApiRestResponse;
import com.cqut.mall.filter.UserFilter;
import com.cqut.mall.model.vo.CartVO;
import com.cqut.mall.service.CartService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车controller
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/list")
    public ApiRestResponse listOfCart() {
        //内部获取用户ID，防止横向越权(去操纵别人的)
        //                 纵向越权(跳跃管理权限)
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOS = cartService.listForCart(userId);
        return ApiRestResponse.success(cartVOS);
    }

    @PostMapping("/add")
    public ApiRestResponse addCart(@RequestParam("productId") Integer productId,
                                   @RequestParam("count") Integer count) {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> cartVOS = cartService.addCart(userId, productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @PostMapping("/update")
    public ApiRestResponse updateCart(@RequestParam("productId") Integer productId,
                                      @RequestParam("count") Integer count) {
        List<CartVO> cartVOS = cartService.updateCart(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @PostMapping("/delete")
    public ApiRestResponse deleteCart(@RequestParam("productId") Integer productId) {
        List<CartVO> cartVOS = cartService.deleteCart(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(cartVOS);
    }

    @PostMapping("/select")
    public ApiRestResponse selectCart(@RequestParam("productId") Integer productId,
                                      @RequestParam("selected") Integer selected) {
        List<CartVO> cartVOS = cartService.updateCartSelected(UserFilter.currentUser.getId(), productId, selected);
        return ApiRestResponse.success(cartVOS);
    }

    @PostMapping("/selectAll")
    public ApiRestResponse selectAllCart(@RequestParam("selected") Integer selected) {
        List<CartVO> cartVOS = cartService.updateAllCartSelected(UserFilter.currentUser.getId(), selected);
        return ApiRestResponse.success(cartVOS);
    }
}
