package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.sz.excel.annotation.DictFormat;

/**
 * <p>
 * Expense返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Expense返回vo")
public class ExpenseVO {
    @ExcelProperty(value = "费用Id")
    @Schema(description =  "费用Id")
    private Long expenseId;

    @ExcelProperty(value = "费用种类")
    @DictFormat(dictType = "expense_category")
    @Schema(description =  "费用种类")
    private Integer expenseCategory;

    @ExcelProperty(value = "费用")
    @Schema(description =  "费用")
    private BigDecimal amount;

    @ExcelProperty(value = "日期")
    @Schema(description =  "日期")
    private LocalDate expenseDate;

    @ExcelProperty(value = "描述")
    @Schema(description =  "描述")
    private String description;

}