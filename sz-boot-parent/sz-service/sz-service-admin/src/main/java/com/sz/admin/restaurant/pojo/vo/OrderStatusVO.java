package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
@Setter
@Schema(description = "订单状态分布响应")
public class OrderStatusVO {

  private String status;
  private String statusName;
  private Long count;
  private Double percentage;

  public OrderStatusVO() {
    this.count=0L;
    this.percentage=0.0;
  }

  public OrderStatusVO(String status, String statusName, Long count, Double percentage) {
    this.status = status;
    this.statusName = statusName;
    this.count = count;
    this.percentage = percentage;
  }

  public void plusCount(Long count){
    this.count +=count;
  }

  public void plusPercentage(Double percentage){
    this.percentage +=percentage;
  }

}