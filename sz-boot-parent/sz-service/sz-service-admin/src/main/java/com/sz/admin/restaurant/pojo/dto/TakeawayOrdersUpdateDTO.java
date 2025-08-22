package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * <p>
 * TakeawayOrders修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "TakeawayOrders修改DTO")
public class TakeawayOrdersUpdateDTO {

    @Schema(description =  "")
    private Long id;

    @Schema(description =  "")
    private Integer orderId;

    @Schema(description =  "")
    private String customerPhone;

    @Schema(description =  "")
    private String deliveryAddress;

    @Schema(description =  "")
    private BigDecimal packagingFee;

    @Schema(description =  "配送费")
    private BigDecimal deliveryFee;

    @Schema(description =  "")
    private String remark;

}