package com.sz.admin.restaurant.controller;

import com.sz.admin.restaurant.pojo.dto.DineInOrdersUpdateDTO;
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
import com.sz.admin.restaurant.service.TakeawayOrdersService;
import com.sz.admin.restaurant.pojo.dto.TakeawayOrdersCreateDTO;
import com.sz.admin.restaurant.pojo.dto.TakeawayOrdersUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.TakeawayOrdersListDTO;
import com.sz.admin.restaurant.pojo.vo.TakeawayOrdersVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 外卖扩展字段表 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Tag(name =  "外卖扩展字段表")
@RestController
@RequestMapping("takeaway-orders")
@RequiredArgsConstructor
public class TakeawayOrdersController  {

    private final TakeawayOrdersService takeawayOrdersService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "takeaway.orders.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody TakeawayOrdersCreateDTO dto) {
        takeawayOrdersService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改订单状态")
    @PutMapping("/status")
    public ApiResult<Void> updateStatus(@RequestBody TakeawayOrdersUpdateDTO dto) {
        takeawayOrdersService.updateStatus(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改支付状态")
    @PutMapping("/payStatus")
    public ApiResult<Void> updatePayStatus(@RequestBody TakeawayOrdersUpdateDTO dto) {
        takeawayOrdersService.updatePayStatus(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "takeaway.orders.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody TakeawayOrdersUpdateDTO dto) {
        takeawayOrdersService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "takeaway.orders.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        takeawayOrdersService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @GetMapping
    public ApiResult<PageResult<TakeawayOrdersVO>> list(TakeawayOrdersListDTO dto) {
        return ApiPageResult.success(takeawayOrdersService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "takeaway.orders.query_table")
    @GetMapping("/{id}")
    public ApiResult<TakeawayOrdersVO> detail(@PathVariable Object id) {
        return ApiResult.success(takeawayOrdersService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "takeaway.orders.import")
    @PostMapping("/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        takeawayOrdersService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "takeaway.orders.export")
    @PostMapping("/export")
    public void exportExcel(@RequestBody TakeawayOrdersListDTO dto, HttpServletResponse response) {
        takeawayOrdersService.exportExcel(dto, response);
    }

    @Operation(summary = "访客下单")
    @PostMapping("/guest")
    public ApiResult<TakeawayOrdersVO> guestOrder(@RequestBody TakeawayOrdersCreateDTO dto) {
        takeawayOrdersService.create(dto);
        // 获取刚创建的订单详情
        TakeawayOrdersListDTO queryDto = new TakeawayOrdersListDTO();
        queryDto.setCustomerPhone(dto.getCustomerPhone());
        List<TakeawayOrdersVO> orders = takeawayOrdersService.list(queryDto);
        // 返回最新创建的订单
        TakeawayOrdersVO latestOrder = orders.get(orders.size() - 1);
        return ApiResult.success(latestOrder);
    }
}