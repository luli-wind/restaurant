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
import com.sz.admin.restaurant.service.OrderDetailService;
import com.sz.admin.restaurant.pojo.dto.OrderDetailCreateDTO;
import com.sz.admin.restaurant.pojo.dto.OrderDetailUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.OrderDetailListDTO;
import com.sz.admin.restaurant.pojo.vo.OrderDetailVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 订单明细表 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-24
 */
@Tag(name =  "订单明细表")
@RestController
@RequestMapping("order-detail")
@RequiredArgsConstructor
public class OrderDetailController  {

    private final OrderDetailService orderDetailService;

    @Operation(summary = "新增")
    @PostMapping
    public ApiResult<Void> create(@RequestBody OrderDetailCreateDTO dto) {
        orderDetailService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @PutMapping
    public ApiResult<Void> update(@RequestBody OrderDetailUpdateDTO dto) {
        orderDetailService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        orderDetailService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @GetMapping
    public ApiResult<PageResult<OrderDetailVO>> list(OrderDetailListDTO dto) {
        return ApiPageResult.success(orderDetailService.page(dto));
    }

    @Operation(summary = "根据OrderId获取订单明细")
    @GetMapping("/{orderId}")
    public ApiResult<List<OrderDetailVO>> getOrderDetailList(@PathVariable Object orderId) {
        return ApiResult.success(orderDetailService.getListByOrderId(orderId));
    }

//    @Operation(summary = "详情")
//    @GetMapping("/{id}")
//    public ApiResult<OrderDetailVO> detail(@PathVariable Object id) {
//        return ApiResult.success(orderDetailService.detail(id));
//    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @PostMapping("/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        orderDetailService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @PostMapping("/export")
    public void exportExcel(@RequestBody OrderDetailListDTO dto, HttpServletResponse response) {
        orderDetailService.exportExcel(dto, response);
    }
}