package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
/**
 * <p>
 * Inventory查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Inventory查询DTO")
public class InventoryListDTO extends PageQuery {

    @Schema(description =  "材料名")
    private String materialName;

    @Schema(description =  "当前容量开始")
    private Integer currentStockStart;

    @Schema(description =  "当前容量结束")
    private Integer currentStockEnd;

}