package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
/**
 * <p>
 * Orders查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Orders查询DTO")
public class OrdersListDTO extends PageQuery {

    @Schema(description =  "")
    private String orderNumber;

    @Schema(description =  "")
    private String orderType;

    @Schema(description =  "")
    private Double totalAmount;

    @Schema(description =  "")
    private String status;

    @Schema(description =  "开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTimeStart;

    @Schema(description =  "结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTimeEnd;

}