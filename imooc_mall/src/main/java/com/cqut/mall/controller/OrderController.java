package com.cqut.mall.controller;

import com.cqut.mall.common.ApiRestResponse;
import com.cqut.mall.model.request.CreateOrderReq;
import com.cqut.mall.model.vo.OrderVO;
import com.cqut.mall.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class OrderController {


    @Autowired
    private OrderService orderService;

    @PostMapping("/order/create")
    @ApiOperation("创建订单")
    public ApiRestResponse createOrder(@RequestBody @Valid CreateOrderReq createOrderReq) {
        String orderNo = orderService.createOrder(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @GetMapping("/order/detail")
    @ApiOperation("订单详情")
    public ApiRestResponse detailOfOrder(@RequestParam String orderNo) {
        OrderVO orderVO = orderService.detailOfOrder(orderNo);
        return ApiRestResponse.success(orderVO);
    }

    @GetMapping("/order/list")
    @ApiOperation("订单列表")
    public ApiRestResponse listForAllOrder(@RequestParam Integer pageNum,
                                           @RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listForAllOrder(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @PostMapping("/order/cancel")
    @ApiOperation("订单列表")
    public ApiRestResponse canselOrder(@RequestParam String orderNo) {
        orderService.canselOrder(orderNo);
        return ApiRestResponse.success();
    }

    /**
     * 生成支付二维码
     */
    @PostMapping("order/qrcode")
    @ApiOperation("生成支付二维码")
    public ApiRestResponse qrcode(@RequestParam String orderNo) {
        String pngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }

    @GetMapping("pay")
    @ApiOperation("支付接口")
    public ApiRestResponse pay(@RequestParam String orderNo) {
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }
}
