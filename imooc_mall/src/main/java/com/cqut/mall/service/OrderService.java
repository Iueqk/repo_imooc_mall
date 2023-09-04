package com.cqut.mall.service;

import com.cqut.mall.model.request.CreateOrderReq;
import com.cqut.mall.model.vo.OrderVO;
import com.github.pagehelper.PageInfo;

public interface OrderService {
    String createOrder(CreateOrderReq createOrderReq);

    OrderVO detailOfOrder(String orderNo);

    PageInfo listForAllOrder(Integer pageNum, Integer pageSize);

    void canselOrder(String orderNo);

    String qrcode(String orderNo);

    void pay(String orderNo);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    //发货
    void deliver(String orderNo);

    void finish(String orderNo);
}
