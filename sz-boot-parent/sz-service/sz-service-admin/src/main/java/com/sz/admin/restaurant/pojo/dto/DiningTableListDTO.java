package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
/**
 * <p>
 * DiningTable查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "DiningTable查询DTO")
public class DiningTableListDTO extends PageQuery {

    @Schema(description =  "餐桌编号")
    private String tableName;

    @Schema(description =  "支付状态")
    private String payStatus;

    @Schema(description =  "状态")
    private String status;

}