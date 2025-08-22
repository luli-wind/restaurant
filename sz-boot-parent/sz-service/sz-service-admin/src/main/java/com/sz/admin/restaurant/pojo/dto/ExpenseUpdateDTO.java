package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * Expense修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Expense修改DTO")
public class ExpenseUpdateDTO {

    @Schema(description =  "")
    private Long expenseId;

    @Schema(description =  "费用种类")
    private Integer expenseCategory;

    @Schema(description =  "费用")
    private BigDecimal amount;

    @Schema(description =  "日期")
    private LocalDate expenseDate;

    @Schema(description =  "描述")
    private String description;

}