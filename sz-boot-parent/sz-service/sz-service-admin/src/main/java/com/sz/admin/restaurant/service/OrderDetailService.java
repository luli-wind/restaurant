package com.sz.admin.restaurant.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.restaurant.pojo.po.OrderDetail;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;
import com.sz.admin.restaurant.pojo.dto.OrderDetailCreateDTO;
import com.sz.admin.restaurant.pojo.dto.OrderDetailUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.OrderDetailListDTO;
import com.sz.admin.restaurant.pojo.vo.OrderDetailVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 订单明细表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-24
 */
public interface OrderDetailService extends IService<OrderDetail> {

    void create(OrderDetailCreateDTO dto);

    void update(OrderDetailUpdateDTO dto);

    PageResult<OrderDetailVO> page(OrderDetailListDTO dto);

    List<OrderDetailVO> list(OrderDetailListDTO dto);

    void remove(SelectIdsDTO dto);

    OrderDetailVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(OrderDetailListDTO dto, HttpServletResponse response);

    List<OrderDetailVO> getListByOrderId(Object orderId);
}