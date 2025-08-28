package com.sz.admin.restaurant.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.restaurant.pojo.po.DishRecipe;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;
import com.sz.admin.restaurant.pojo.dto.DishRecipeCreateDTO;
import com.sz.admin.restaurant.pojo.dto.DishRecipeUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.DishRecipeListDTO;
import com.sz.admin.restaurant.pojo.vo.DishRecipeVO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 菜品配方表（Bill of Materials） Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
public interface DishRecipeService extends IService<DishRecipe> {

    void create(DishRecipeCreateDTO dto);

    void update(DishRecipeUpdateDTO dto);

    PageResult<DishRecipeVO> page(DishRecipeListDTO dto);

    List<DishRecipeVO> list(DishRecipeListDTO dto);

    void remove(SelectIdsDTO dto);

    DishRecipeVO detail(Object id);

    List<DishRecipeVO> listByDishId(Long dishId);

    void exportExcel(DishRecipeListDTO dto, HttpServletResponse response);

}
