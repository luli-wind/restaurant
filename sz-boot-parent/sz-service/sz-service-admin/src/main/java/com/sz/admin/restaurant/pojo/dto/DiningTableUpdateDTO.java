package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * DiningTable修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "DiningTable修改DTO")
public class DiningTableUpdateDTO {

    @Schema(description =  "")
    private Long tableId;

    @Schema(description =  "餐桌编号")
    private String tableName;

    @Schema(description =  "容量")
    private Integer capacity;

    @Schema(description =  "状态")
    private String status;

}