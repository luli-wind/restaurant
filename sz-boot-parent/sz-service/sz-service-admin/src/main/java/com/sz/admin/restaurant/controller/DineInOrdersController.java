package com.sz.admin.restaurant.controller;

import com.sz.admin.restaurant.pojo.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import com.sz.core.common.entity.ApiPageResult;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.constant.GlobalConstant;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.admin.restaurant.service.DineInOrdersService;
import com.sz.admin.restaurant.pojo.vo.DineInOrdersVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 堂食扩展字段 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Tag(name =  "堂食扩展字段")
@RestController
@RequestMapping("dine-in-orders")
@RequiredArgsConstructor
public class DineInOrdersController  {

    private final DineInOrdersService dineInOrdersService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "dine.in.orders.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody DineInOrdersCreateDTO dto) {
        dineInOrdersService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "dine.in.orders.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody DineInOrdersUpdateDTO dto) {
        dineInOrdersService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改订单状态")
    @PutMapping("/status")
    public ApiResult<Void> updateStatus(@RequestBody DineInOrdersUpdateDTO dto) {
        dineInOrdersService.updateStatus(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改支付状态")
    @PutMapping("/payStatus")
    public ApiResult<Void> updatePayStatus(@RequestBody DineInOrdersUpdateDTO dto) {
        dineInOrdersService.updatePayStatus(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "dine.in.orders.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        dineInOrdersService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @GetMapping
    public ApiResult<PageResult<DineInOrdersVO>> list(DineInOrdersListDTO dto) {
        return ApiPageResult.success(dineInOrdersService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "dine.in.orders.query_table")
    @GetMapping("/{id}")
    public ApiResult<DineInOrdersVO> detail(@PathVariable Object id) {
        return ApiResult.success(dineInOrdersService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "dine.in.orders.import")
    @PostMapping("/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        dineInOrdersService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "dine.in.orders.export")
    @PostMapping("/export")
    public void exportExcel(@RequestBody DineInOrdersListDTO dto, HttpServletResponse response) {
        dineInOrdersService.exportExcel(dto, response);
    }
}