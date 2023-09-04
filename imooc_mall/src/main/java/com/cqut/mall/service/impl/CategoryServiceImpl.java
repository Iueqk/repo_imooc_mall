package com.cqut.mall.service.impl;

import com.cqut.mall.exception.ImoocMallException;
import com.cqut.mall.exception.ImoocMallExceptionEnum;
import com.cqut.mall.model.dao.CategoryMapper;
import com.cqut.mall.model.pojo.Category;
import com.cqut.mall.model.request.AddCategoryReq;
import com.cqut.mall.model.request.UpdateCategoryReq;
import com.cqut.mall.model.vo.CategoryVO;
import com.cqut.mall.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void addCategory(AddCategoryReq addCategoryReq) {
        //  防止重名
        if (categoryMapper.selectByCategoryName(addCategoryReq.getName()) != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        //  将vo转为pojo
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq, category);

        //  数据库原因插入失败
        int insertCount = categoryMapper.insertSelective(category);
        if (insertCount == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void updateCategory(UpdateCategoryReq updateCategoryReq) {
        //  更新名字也不能和别人重名(查出来的对象id与当前id不同，说明名字已经被占用)
        Category oldCategory = categoryMapper.selectByCategoryName(updateCategoryReq.getName());
        if (oldCategory != null && !updateCategoryReq.getId().equals(oldCategory.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        // 将vo转为pojo
        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryReq, category);

        int updateCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (updateCount == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void deleteCategory(Integer id) {
        //  查验用户是否存在
        Category category = categoryMapper.selectByPrimaryKey(id);
        if (category == null)
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);

        int deleteCount = categoryMapper.deleteByPrimaryKey(id);
        if (deleteCount == 0)
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
    }

    @Override
    public PageInfo listCategoryForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type, order_num");
        List<Category> categories = categoryMapper.selectAllByPage();

        PageInfo<Category> pageInfo = new PageInfo<>(categories);
        return pageInfo;
    }

    @Override
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVO> listCategoryForCustomer() {
        List<CategoryVO> categoryVOS = categoryMapper.selectCategoriesAndShowAllChild();
        return categoryVOS;
    }
}
