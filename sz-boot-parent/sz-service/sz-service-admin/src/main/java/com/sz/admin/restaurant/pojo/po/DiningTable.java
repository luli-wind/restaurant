package com.sz.admin.restaurant.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;

/**
* <p>
* 餐桌表
* </p>
*
* @author sz-admin
* @since 2025-08-22
*/
@Data
@Table(value = "dining_table", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "餐桌表")
public class DiningTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long tableId;

    @Schema(description ="餐桌编号")
    private String tableName;

    @Schema(description ="容量")
    private Integer capacity;

    @Schema(description ="状态")
    private String status;

}