package com.sz.admin.restaurant.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;

/**
* <p>
* 库存表
* </p>
*
* @author sz-admin
* @since 2025-08-22
*/
@Data
@Table(value = "inventory", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "库存表")
public class Inventory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "材料ID")
    private Long materialId;

    @Schema(description = "材料名")
    private String materialName;

    @Schema(description = "当前容量")
    private Double currentStock;

    @Schema(description = "最小容量")
    private Double minStock;

    @Schema(description = "计量单位")
    private String unit;

    @Schema(description = "库存状态")
    private String status;


}