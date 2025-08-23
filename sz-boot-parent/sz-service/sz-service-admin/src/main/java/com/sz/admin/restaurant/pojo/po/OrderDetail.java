package com.sz.admin.restaurant.pojo.po;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sz.mysql.EntityChangeListener;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;

@Data
@Table(value = "order_detail")
@Schema(description = "订单明细表")
public class OrderDetail {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long id;
    @Schema(description ="")
    private Long orderId;
    @Schema(description ="")
    private Long  dishId;
    @Schema(description ="")
    private String dishName;
    @Schema(description ="")
    private String imageUrl;
    @Schema(description ="")
    private Integer number;
    @Schema(description ="")
    private Double amount;

}
