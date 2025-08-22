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
import com.sz.admin.restaurant.service.ExpenseService;
import com.sz.admin.restaurant.pojo.dto.ExpenseCreateDTO;
import com.sz.admin.restaurant.pojo.dto.ExpenseUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.ExpenseListDTO;
import com.sz.admin.restaurant.pojo.vo.ExpenseVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 费用表 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Tag(name =  "费用表")
@RestController
@RequestMapping("expense")
@RequiredArgsConstructor
public class ExpenseController  {

    private final ExpenseService expenseService;

    @Operation(summary = "新增")
    @SaCheckPermission(value = "expense.create")
    @PostMapping
    public ApiResult<Void> create(@RequestBody ExpenseCreateDTO dto) {
        expenseService.create(dto);
        return ApiResult.success();
    }

    @Operation(summary = "修改")
    @SaCheckPermission(value = "expense.update")
    @PutMapping
    public ApiResult<Void> update(@RequestBody ExpenseUpdateDTO dto) {
        expenseService.update(dto);
        return ApiResult.success();
    }

    @Operation(summary = "删除")
    @SaCheckPermission(value = "expense.remove")
    @DeleteMapping
    public ApiResult<Void> remove(@RequestBody SelectIdsDTO dto) {
        expenseService.remove(dto);
        return ApiResult.success();
    }

    @Operation(summary = "列表查询")
    @SaCheckPermission(value = "expense.query_table")
    @GetMapping
    public ApiResult<PageResult<ExpenseVO>> list(ExpenseListDTO dto) {
        return ApiPageResult.success(expenseService.page(dto));
    }

    @Operation(summary = "详情")
    @SaCheckPermission(value = "expense.query_table")
    @GetMapping("/{id}")
    public ApiResult<ExpenseVO> detail(@PathVariable Object id) {
        return ApiResult.success(expenseService.detail(id));
    }

    @Operation(summary = "导入")
    @Parameters({
      @Parameter(name = "file", description = "上传文件", schema = @Schema(type = "string", format = "binary"), required = true),
    })
    @SaCheckPermission(value = "expense.import")
    @PostMapping("/import")
    public void importExcel(@ModelAttribute ImportExcelDTO dto) {
        expenseService.importExcel(dto);
    }

    @Operation(summary = "导出")
    @SaCheckPermission(value = "expense.export")
    @PostMapping("/export")
    public void exportExcel(@RequestBody ExpenseListDTO dto, HttpServletResponse response) {
        expenseService.exportExcel(dto, response);
    }
}