package com.cqut.mall.service;

import com.cqut.mall.model.request.AddCategoryReq;
import com.cqut.mall.model.request.UpdateCategoryReq;
import com.cqut.mall.model.vo.CategoryVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CategoryService {

    void addCategory(AddCategoryReq addCategoryReq);

    void updateCategory(UpdateCategoryReq updateCategoryReq);

    void deleteCategory(Integer id);

    PageInfo listCategoryForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> listCategoryForCustomer();
}
