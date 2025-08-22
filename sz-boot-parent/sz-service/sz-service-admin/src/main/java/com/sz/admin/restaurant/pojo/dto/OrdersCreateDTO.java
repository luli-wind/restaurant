package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * Orders添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Orders添加DTO")
public class OrdersCreateDTO {

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