package com.cqut.mall.service;

import com.cqut.mall.model.pojo.Product;
import com.cqut.mall.model.request.AddProductReq;
import com.cqut.mall.model.request.ProductListReq;
import com.cqut.mall.model.request.UpdateProductReq;
import com.github.pagehelper.PageInfo;

public interface ProductService {
    void addProduct(AddProductReq addProductReq);

    void updateProduct(UpdateProductReq updateProductReq);

    void deleteProduct(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    PageInfo listForCustomer(ProductListReq productListReq);

    Product detailForProduct(Integer id);
}
