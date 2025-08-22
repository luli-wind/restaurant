package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * Orders修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Orders修改DTO")
public class OrdersUpdateDTO {

    @Schema(description =  "")
    private Long orderId;

    @Schema(description =  "")
    private String orderNumber;

    @Schema(description =  "")
    private String orderType;

    @Schema(description =  "")
    private Double totalAmount;

    @Schema(description =  "")
    private String status;

    @Schema(description =  "")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

}