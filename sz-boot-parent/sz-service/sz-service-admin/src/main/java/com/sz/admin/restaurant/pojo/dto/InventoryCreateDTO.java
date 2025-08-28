package com.sz.admin.restaurant.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * Inventory添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Inventory添加DTO")
public class InventoryCreateDTO {

   @Schema(description =  "材料名")
   private String materialName;

   @Schema(description =  "当前容量")
   private Double currentStock;

   @Schema(description =  "最小容量")
   private Double minStock;

   @Schema(description =  "计量单位")
   private String unit;

   @Schema(description = "库存状态")
   private String status;


}