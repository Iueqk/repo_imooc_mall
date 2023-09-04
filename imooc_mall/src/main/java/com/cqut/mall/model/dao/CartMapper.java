package com.cqut.mall.model.dao;

import com.cqut.mall.model.pojo.Cart;
import com.cqut.mall.model.pojo.Product;
import com.cqut.mall.model.vo.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId,
                                        @Param("productId") Integer productId);

    List<CartVO> selectCartByUserId(@Param("userId") Integer userId);

    int deleteByProductId(@Param("productId") Integer productId);

    int updateSelectedByCondition(@Param("userId") Integer id, @Param("productId") Integer productId, @Param("selected") Integer selected);

    List<Product> selectAllHaveSelected(Integer userId);
}