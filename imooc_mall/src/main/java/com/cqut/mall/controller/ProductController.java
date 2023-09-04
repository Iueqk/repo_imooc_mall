package com.cqut.mall.controller;

import com.cqut.mall.common.ApiRestResponse;
import com.cqut.mall.model.pojo.Product;
import com.cqut.mall.model.request.ProductListReq;
import com.cqut.mall.service.ProductService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台商品controller
 */
@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @ApiOperation("前台商品列表")
    @GetMapping("product/list")
    public ApiRestResponse listForCustomer(ProductListReq productListReq) {
        PageInfo list = productService.listForCustomer(productListReq);
        return ApiRestResponse.success(list);
    }

    @ApiOperation("前台商品详情")
    @GetMapping("product/detail")
    public ApiRestResponse detailForProduct(@RequestParam Integer id) {
        Product product = productService.detailForProduct(id);
        return ApiRestResponse.success(product);
    }
}
