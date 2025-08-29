package com.sz.admin.restaurant.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.restaurant.pojo.dto.*;
import com.sz.admin.restaurant.pojo.po.DineInOrders;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;

import com.sz.admin.restaurant.pojo.vo.DineInOrdersVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 堂食扩展字段 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
public interface DineInOrdersService extends IService<DineInOrders> {

    void create(DineInOrdersCreateDTO dto);

    void update(DineInOrdersUpdateDTO dto);

    PageResult<DineInOrdersVO> page(DineInOrdersListDTO dto);

    List<DineInOrdersVO> list(DineInOrdersListDTO dto);

    void remove(SelectIdsDTO dto);

    DineInOrdersVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(DineInOrdersListDTO dto, HttpServletResponse response);

    void updateStatus(DineInOrdersUpdateDTO dto);

    void updatePayStatus(DineInOrdersUpdateDTO dto);

    DineInOrdersVO getDineInOrderByTableId(Long tableId);
}