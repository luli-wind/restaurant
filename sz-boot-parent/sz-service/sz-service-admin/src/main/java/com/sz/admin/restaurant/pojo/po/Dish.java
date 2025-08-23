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
    @Schema(description = "菜品ID")
    private Long dishId;

    @Schema(description = "图片URL")
    private String imageUrl;

    @Schema(description = "菜品名称")
    private String dishName;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "描述")
    private String description;



}