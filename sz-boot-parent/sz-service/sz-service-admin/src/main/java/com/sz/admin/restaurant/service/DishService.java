package com.sz.admin.restaurant.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.restaurant.pojo.po.Dish;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;
import com.sz.admin.restaurant.pojo.dto.DishCreateDTO;
import com.sz.admin.restaurant.pojo.dto.DishUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.DishListDTO;
import com.sz.admin.restaurant.pojo.vo.DishVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 菜品表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
public interface DishService extends IService<Dish> {

    void create(DishCreateDTO dto);

    void update(DishUpdateDTO dto);

    PageResult<DishVO> page(DishListDTO dto);

    List<DishVO> list(DishListDTO dto);

    void remove(SelectIdsDTO dto);

    DishVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(DishListDTO dto, HttpServletResponse response);
}