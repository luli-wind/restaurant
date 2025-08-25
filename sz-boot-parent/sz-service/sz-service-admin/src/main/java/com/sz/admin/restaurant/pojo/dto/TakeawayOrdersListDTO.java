package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
import java.math.BigDecimal;
/**
 * <p>
 * TakeawayOrders查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "TakeawayOrders查询DTO")
public class TakeawayOrdersListDTO extends PageQuery {

    @Schema(description =  "")
    private String status;

    @Schema(description = "")
    private String customerName;

    @Schema(description = "")
    private String payStatus;

    @Schema(description =  "")
    private String customerPhone;

    @Schema(description =  "")
    private String deliveryAddress;

}