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
import com.sz.admin.restaurant.service.InventoryService;
import com.sz.admin.restaurant.pojo.dto.InventoryCreateDTO;
import com.sz.admin.restaurant.pojo.dto.InventoryUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.InventoryListDTO;
import com.sz.admin.restaurant.pojo.vo.InventoryVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 库存表 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Tag(name =  "库存表")
@RestController
@RequestMapping("inventory")
@RequiredArgsConstructor
public class InventoryController  {

    private final InventoryService inventoryService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "inventory.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody InventoryCreateDTO dto) {
        inventoryService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "inventory.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody InventoryUpdateDTO dto) {
        inventoryService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "inventory.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        inventoryService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "全部列表")
    @SaCheckPermission(value = "inventory.all_list")
    @GetMapping("/all")
    public ApiResult<List<InventoryVO>> getAllList() {
        InventoryListDTO dto = new InventoryListDTO();
        return ApiResult.success(inventoryService.list(dto));
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "inventory.query_table")
    @GetMapping
    public ApiResult<PageResult<InventoryVO>> list(InventoryListDTO dto) {
        return ApiPageResult.success(inventoryService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "inventory.query_table")
    @GetMapping("/{id}")
    public ApiResult<InventoryVO> detail(@PathVariable Object id) {
        return ApiResult.success(inventoryService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "inventory.import")
    @PostMapping("/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        inventoryService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "inventory.export")
    @PostMapping("/export")
    public void exportExcel(@RequestBody InventoryListDTO dto, HttpServletResponse response) {
        inventoryService.exportExcel(dto, response);
    }
}