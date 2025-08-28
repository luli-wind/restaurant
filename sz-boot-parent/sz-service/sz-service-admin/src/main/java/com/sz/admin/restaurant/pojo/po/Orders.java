package com.sz.admin.restaurant.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
* <p>
* 订单表
* </p>
*
* @author sz-admin
* @since 2025-08-22
*/
@Data
@Table(value = "orders", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "订单表")
@SuppressWarnings("all")
public class Orders implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long orderId;

    @Schema(description ="")
    private String orderNumber;

    @Schema(description ="")
    private String orderType;

    @Schema(description ="")
    private Double totalAmount;

    @Schema(description ="")
    private String status;

    @Schema(description ="")
    private LocalDateTime createTime;

    @Schema(description ="")
    private String payStatus;

    @Schema(description ="")
    private LocalDateTime payTime;

    @Schema(description ="退款原因")
    private String refundReason;

}