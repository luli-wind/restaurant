package com.sz.admin.restaurant.pojo.dto;

import com.sz.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 外卖订单查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-30
 */
@Data
@Schema(description = "外卖订单查询DTO")
public class TakeawayOrdersQueryDTO extends PageQuery {

    @Schema(description = "第三方用户ID")
    private Long thirdPartyUserId;

    @Schema(description = "客户姓名")
    private String customerName;

    @Schema(description = "客户电话")
    private String customerPhone;

    @Schema(description = "配送地址")
    private String deliveryAddress;

    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "支付状态")
    private String payStatus;
}