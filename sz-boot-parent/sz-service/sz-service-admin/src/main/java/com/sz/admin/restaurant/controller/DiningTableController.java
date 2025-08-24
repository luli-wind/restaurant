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
import com.sz.admin.restaurant.service.DiningTableService;
import com.sz.admin.restaurant.pojo.dto.DiningTableCreateDTO;
import com.sz.admin.restaurant.pojo.dto.DiningTableUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.DiningTableListDTO;
import com.sz.admin.restaurant.pojo.vo.DiningTableVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 餐桌表 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Tag(name =  "餐桌表")
@RestController
@RequestMapping("dining-table")
@RequiredArgsConstructor
public class DiningTableController  {

    private final DiningTableService diningTableService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "dining.table.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody DiningTableCreateDTO dto) {
        diningTableService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "dining.table.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody DiningTableUpdateDTO dto) {
        diningTableService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "dining.table.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        diningTableService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "获取所有桌子")
    @GetMapping("/all")
    public ApiResult<List<DiningTableVO>> getAllList() {
        DiningTableListDTO dto = new DiningTableListDTO();
        return ApiResult.success(diningTableService.list(dto));
    }


    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "dining.table.query_table")
    @GetMapping
    public ApiResult<PageResult<DiningTableVO>> list(DiningTableListDTO dto) {
        return ApiPageResult.success(diningTableService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "dining.table.query_table")
    @GetMapping("/{id}")
    public ApiResult<DiningTableVO> detail(@PathVariable Object id) {
        return ApiResult.success(diningTableService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "dining.table.import")
    @PostMapping("/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        diningTableService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "dining.table.export")
    @PostMapping("/export")
    public void exportExcel(@RequestBody DiningTableListDTO dto, HttpServletResponse response) {
        diningTableService.exportExcel(dto, response);
    }
}