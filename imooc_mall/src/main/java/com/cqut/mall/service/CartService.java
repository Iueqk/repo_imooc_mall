package com.cqut.mall.service;

import com.cqut.mall.model.vo.CartVO;

import java.util.List;

public interface CartService {

    List<CartVO> listForCart(Integer userId);

    /*包含商品信息、用户信息*/
    List<CartVO> addCart(Integer userId, Integer productId, Integer count);

    List<CartVO> updateCart(Integer userId, Integer productId, Integer count);

    List<CartVO> deleteCart(Integer userId, Integer productId);

    List<CartVO> updateCartSelected(Integer userId, Integer productId, Integer selected);

    List<CartVO> updateAllCartSelected(Integer userId, Integer selected);
}
