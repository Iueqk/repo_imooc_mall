package com.cqut.mall.service.impl;

import com.cqut.mall.common.Constant;
import com.cqut.mall.exception.ImoocMallException;
import com.cqut.mall.exception.ImoocMallExceptionEnum;
import com.cqut.mall.model.dao.CategoryMapper;
import com.cqut.mall.model.dao.ProductMapper;
import com.cqut.mall.model.pojo.Product;
import com.cqut.mall.model.query.ProductListQuery;
import com.cqut.mall.model.request.AddProductReq;
import com.cqut.mall.model.request.ProductListReq;
import com.cqut.mall.model.request.UpdateProductReq;
import com.cqut.mall.model.vo.CategoryVO;
import com.cqut.mall.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void addProduct(AddProductReq addProductReq) {
        //  防止重名
        Product oldProduct = productMapper.selectByProductName(addProductReq.getName());
        if (oldProduct != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);

        int addCount = productMapper.insertSelective(product);
        if (addCount == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public void updateProduct(UpdateProductReq updateProductReq) {
        //  防止重名
        Product oldProduct = productMapper.selectByProductName(updateProductReq.getName());
        if (oldProduct != null && !oldProduct.getId().equals(updateProductReq.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);

        int updateCount = productMapper.updateByPrimaryKeySelective(product);
        if (updateCount == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void deleteProduct(Integer id) {
        //  查验用户是否存在
        Product product = productMapper.selectByPrimaryKey(id);
        if (product == null)
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);

        int deleteCount = productMapper.deleteByPrimaryKey(id);
        if (deleteCount == 0)
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectProductByPage();
        PageInfo<Product> productPageInfo = new PageInfo<>(products);
        return productPageInfo;
    }

    @Override
    public PageInfo listForCustomer(ProductListReq productListReq) {

        ProductListQuery productListQuery = new ProductListQuery();

        // 1.模糊查询
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {

           /* 1.  业务层进行拼接
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword())
                    .append("%").toString();
            productListQuery.setKeyword(keyword);
            */

            /*
             2. 利用sql函数concat进行拼接
            */
            productListQuery.setKeyword(productListReq.getKeyword());
        }

        // 3.目录查询(展示该目录下的所有子目录)
        List<Integer> arrayList = null;
        if (productListReq.getCategoryId() != null) {
            List<CategoryVO> categoryVOS = categoryMapper.selectCategoriesByParentId(productListReq.getCategoryId());
            if (!categoryVOS.isEmpty()) {
                arrayList = new ArrayList<>();
                getAllSubCategoriesId(categoryVOS, arrayList);
            }
        }

        // 2.排序
        /*前端传来的排序字段包含在枚举的排序情况中*/
        if (Constant.ProductListOrderBy.PRICE_ORDER_ENUM.contains(productListReq.getOrderBy())) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(),
                    productListReq.getOrderBy());
        }   /*不排序*/ else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }

        productListQuery.setCategoryIds(arrayList);
        List<Product> products = productMapper.selectProductForCustomer(productListQuery);

        PageInfo<Product> productPageInfo = new PageInfo<>(products);
        return productPageInfo;
    }

    private void getAllSubCategoriesId(List<CategoryVO> currentVo, List<Integer> allIds) {
        for (CategoryVO categoryVO : currentVo) {
            allIds.add(categoryVO.getId());
            if (categoryVO.getChildCategory() != null) {
                getAllSubCategoriesId(categoryVO.getChildCategory(), allIds);
            }
        }
    }

    @Override
    public Product detailForProduct(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }
}
