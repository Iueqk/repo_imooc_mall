package com.cqut.mall.model.dao;

import com.cqut.mall.model.pojo.OrderItem;
import com.cqut.mall.model.vo.OrderItemVO;
import com.cqut.mall.model.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItemVO> selectByOrderNo(String orderNo);

    List<OrderItem> selectOrderItemByOrderNo(String orderNo);
}