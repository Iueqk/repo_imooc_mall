package com.cqut.mall.model.dao;

import com.cqut.mall.model.pojo.Product;
import com.cqut.mall.model.query.ProductListQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product selectByProductName(String productName);

    void batchUpdateSellStatus(@Param("ids") Integer[] ids, @Param("sellStatus") Integer sellStatus);

    List<Product> selectProductByPage();

    /*
    * 包含前台传来的目录id下的所有子目录(2级、3级...)的id
    * */
    List<Product> selectProductForCustomer(@Param("query")ProductListQuery productListQuery);/**/

}