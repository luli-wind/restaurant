package com.sz.admin.restaurant.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;

/**
* <p>
* 堂食扩展字段
* </p>
*
* @author sz-admin
* @since 2025-08-22
*/
@Data
@Table(value = "dine_in_orders")
@Schema(description = "堂食扩展字段")
public class DineInOrders implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long id;

    @Schema(description ="")
    private Integer orderId;

    @Schema(description ="")
    private Integer tableId;

    @Schema(description ="")
    private Integer numberOfGuests;

    @Schema(description ="")
    private String remark;

    @Schema(description ="退款原因")
    private String refundReason;

}