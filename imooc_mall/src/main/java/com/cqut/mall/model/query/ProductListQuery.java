package com.cqut.mall.model.query;

import java.util.List;

/**
 * 描述：     查询商品列表的Query
 */
public class ProductListQuery {

    private String keyword;

    /**
     * 所有子集目录的categoryId
     */
    private List<Integer> categoryIds;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
