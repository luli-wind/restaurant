package com.sz.admin.restaurant.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;
import java.math.BigDecimal;

/**
* <p>
* 外卖扩展字段表
* </p>
*
* @author sz-admin
* @since 2025-08-22
*/
@Data
@Table(value = "takeaway_orders")
@Schema(description = "外卖扩展字段表")
public class TakeawayOrders implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long id;

    @Schema(description ="")
    private Integer orderId;

    @Schema(description ="")
    private String customerName;

    @Schema(description ="")
    private String customerPhone;

    @Schema(description ="")
    private String deliveryAddress;

    @Schema(description ="")
    private Double packagingFee;

    @Schema(description ="配送费")
    private Double deliveryFee;

    @Schema(description ="")
    private String remark;

}