package com.cqut.mall.model.dao;

import com.cqut.mall.model.pojo.Order;
import com.cqut.mall.model.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    OrderVO selectByOrderNo(String orderNo);

    Order selectOrderByOrderNo(String orderNo);

    List<OrderVO> selectAllOrder(Integer userId);

    List<OrderVO> selectAllOrderForAdmin();
}