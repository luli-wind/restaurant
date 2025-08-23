package com.sz.admin.restaurant.mapper;

import com.mybatisflex.core.BaseMapper;
import com.sz.admin.restaurant.pojo.po.DishRecipe;
import com.sz.admin.restaurant.pojo.vo.DishRecipeVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
* <p>
* 菜品配方表（Bill of Materials） Mapper 接口
* </p>
*
* @author sz-admin
* @since 2025-08-22
*/
public interface DishRecipeMapper extends BaseMapper<DishRecipe> {

    /**
     * 根据菜品ID查询配方列表
     * @param dishId 菜品ID
     * @return 配方列表
     */
    List<DishRecipeVO> selectListByDishId(@Param("dishId") Long dishId);

}