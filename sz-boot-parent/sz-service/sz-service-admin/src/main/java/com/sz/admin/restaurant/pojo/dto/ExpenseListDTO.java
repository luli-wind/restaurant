package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * <p>
 * Expense查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Expense查询DTO")
public class ExpenseListDTO extends PageQuery {

    @Schema(description =  "费用种类")
    private String expenseCategory;

    @Schema(description =  "费用开始")
    private BigDecimal amountStart;

    @Schema(description =  "费用结束")
    private BigDecimal amountEnd;

    @Schema(description =  "日期开始")
    private LocalDate expenseDateStart;

    @Schema(description =  "日期结束")
    private LocalDate expenseDateEnd;

}