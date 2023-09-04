package com.cqut.mall.service.impl;

import com.cqut.mall.common.Constant;
import com.cqut.mall.exception.ImoocMallException;
import com.cqut.mall.exception.ImoocMallExceptionEnum;
import com.cqut.mall.model.dao.CartMapper;
import com.cqut.mall.model.dao.ProductMapper;
import com.cqut.mall.model.pojo.Cart;
import com.cqut.mall.model.pojo.Product;
import com.cqut.mall.model.vo.CartVO;
import com.cqut.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
*   userId和productId唯一确定一个商品的购物车信息
*   购物车有几种商品，数据库就有几条信息
*/
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<CartVO> listForCart(Integer userId) {
        List<CartVO> cartVOS = cartMapper.selectCartByUserId(userId);
        for (CartVO cartVO : cartVOS) {
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOS;
    }

    @Override
    public List<CartVO> addCart(Integer userId, Integer productId, Integer count) {
        this.validProduct(productId, count);

        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里，需要新增一个记录
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.SELECTED);
            cartMapper.insertSelective(cart);
        } else {
            //这个商品已经在购物车里了，则数量相加
            count = cart.getQuantity() + count;
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.Cart.SELECTED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.listForCart(userId);
    }

    private void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        //判断商品是否存在，商品是否上架
        if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
        }
        //判断商品库存
        if (count > product.getStock()) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_ENOUGH);
        }
    }

    @Override
    public List<CartVO> updateCart(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);

        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里，无法更新
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        } else {
            //这个商品已经在购物车里了，则更新数量
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.Cart.SELECTED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.listForCart(userId);
    }

    @Override
    public List<CartVO> deleteCart(Integer userId, Integer productId) {

        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里，无法删除
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        } else {
            //这个商品已经在购物车里了，则删除该商品
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.listForCart(userId);
    }

    @Override
    public List<CartVO> updateCartSelected(Integer userId, Integer productId, Integer selected) {

        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里，无法选择
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        } else {
            //这个商品已经在购物车里了，则选择商品
            cartMapper.updateSelectedByCondition(userId, productId, selected);
        }
        return this.listForCart(userId);
    }

    @Override
    public List<CartVO> updateAllCartSelected(Integer userId, Integer selected) {
        cartMapper.updateSelectedByCondition(userId, null, selected);
        return this.listForCart(userId);
    }
}
