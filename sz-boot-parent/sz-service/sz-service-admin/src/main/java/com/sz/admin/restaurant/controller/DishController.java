package com.sz.admin.restaurant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.admin.restaurant.service.DishService;
import com.sz.admin.restaurant.pojo.dto.DishCreateDTO;
import com.sz.admin.restaurant.pojo.dto.DishUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.DishListDTO;
import com.sz.admin.restaurant.pojo.vo.DishVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 菜品表 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Tag(name =  "菜品表")
@RestController
@RequestMapping("dish")
@RequiredArgsConstructor
public class DishController  {

    private final DishService dishService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "dish.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody DishCreateDTO dto) {
        dishService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "dish.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody DishUpdateDTO dto) {
        dishService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "dish.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        dishService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "全部列表")
    @SaCheckPermission(value = "dish.query_table")
    @GetMapping("/all")
    public ApiResult<List<DishVO>> getAllList() {
        DishListDTO dto = new DishListDTO();
        return ApiResult.success(dishService.list(dto));
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "dish.query_table")
    @GetMapping
    public ApiResult<PageResult<DishVO>> list(DishListDTO dto) {
        return ApiPageResult.success(dishService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "dish.query_table")
    @GetMapping("/{id}")
    public ApiResult<DishVO> detail(@PathVariable Object id) {
        return ApiResult.success(dishService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "dish.import")
    @PostMapping("/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        dishService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "dish.export")
    @PostMapping("/export")
    public void exportExcel(@RequestBody DishListDTO dto, HttpServletResponse response) {
        dishService.exportExcel(dto, response);
    }
}