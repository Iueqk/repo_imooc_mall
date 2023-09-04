package com.cqut.mall.model.dao;

import com.cqut.mall.model.pojo.Category;
import com.cqut.mall.model.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    Category selectByCategoryName(String name);

    List<Category> selectAllByPage();

    List<CategoryVO> selectCategoriesAndShowAllChild();

    List<CategoryVO> selectCategoriesByParentId(Integer parentId);
}