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
* 菜品表
* </p>
*
* @author sz-admin
* @since 2025-08-22
*/
@Data
@Table(value = "dish", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "菜品表")
public class Dish implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description ="")
    private Long dishId;

    @Schema(description ="")
    private String imageUrl;

    @Schema(description ="")
    private String dishName;

    @Schema(description ="")
    private String category;

    @Schema(description ="")
    private BigDecimal price;

    @Schema(description ="")
    private String description;

}