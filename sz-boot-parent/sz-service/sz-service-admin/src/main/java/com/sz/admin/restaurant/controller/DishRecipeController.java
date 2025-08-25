package com.sz.admin.restaurant.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sz.admin.restaurant.pojo.dto.DishRecipeCreateDTO;
import com.sz.admin.restaurant.pojo.dto.DishRecipeUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.DishRecipeListDTO;
import com.sz.admin.restaurant.pojo.vo.DishRecipeVO;
import com.sz.admin.restaurant.service.DishRecipeService;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 菜品配方表（Bill of Materials） Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Tag(name =  "菜品配料表")
@RestController
@RequestMapping("dishRecipe")
@RequiredArgsConstructor
public class DishRecipeController {

    private final DishRecipeService dishRecipeService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "dish.query_table")
    @PostMapping
    public ApiResult<Void> create(@RequestBody DishRecipeCreateDTO dto) {
        dishRecipeService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "dish.query_table")
    @PutMapping
    public ApiResult<Void> update(@RequestBody DishRecipeUpdateDTO dto) {
        dishRecipeService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "dish.query_table")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        dishRecipeService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "dish.query_table")
    @GetMapping
    public ApiResult<PageResult<DishRecipeVO>> list(DishRecipeListDTO dto) {
        return ApiPageResult.success(dishRecipeService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "dish.query_table")
    @GetMapping("/{id}")
    public ApiResult<DishRecipeVO> detail(@PathVariable Object id) {
        return ApiResult.success(dishRecipeService.detail(id));
    }

    @Operation(summary = "根据菜品ID查询配方列表")
    @SaCheckPermission(value = "dish.query_table")
    @GetMapping("/byDishId/{dishId}")
    public ApiResult<List<DishRecipeVO>> listByDishId(@PathVariable Long dishId) {
        return ApiResult.success(dishRecipeService.listByDishId(dishId));
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "dish.query_table")
    @PostMapping("/export")
    public void exportExcel(@RequestBody DishRecipeListDTO dto, HttpServletResponse response) {
        dishRecipeService.exportExcel(dto, response);
    }
}
