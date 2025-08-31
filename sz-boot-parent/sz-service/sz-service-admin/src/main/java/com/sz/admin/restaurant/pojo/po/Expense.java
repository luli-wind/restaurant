package com.sz.admin.restaurant.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
* <p>
* 费用表
* </p>
*
* @author sz-admin
* @since 2025-08-22
*/
@Data
@Table(value = "expense", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "费用表")
public class Expense implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long expenseId;

    @Schema(description ="费用种类")
    private String expenseCategory;

    @Schema(description ="费用")
    private BigDecimal amount;

    @Schema(description ="日期")
    private LocalDate expenseDate;

    @Schema(description ="描述")
    private String description;

}