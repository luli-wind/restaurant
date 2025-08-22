package com.sz.admin.restaurant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.admin.restaurant.service.OrdersService;
import com.sz.admin.restaurant.pojo.po.Orders;
import com.sz.admin.restaurant.mapper.OrdersMapper;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Service
@RequiredArgsConstructor
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}