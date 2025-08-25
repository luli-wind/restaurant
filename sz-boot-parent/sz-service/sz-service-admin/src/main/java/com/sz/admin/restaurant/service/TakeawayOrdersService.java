package com.sz.admin.restaurant.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.restaurant.pojo.dto.DineInOrdersUpdateDTO;
import com.sz.admin.restaurant.pojo.po.TakeawayOrders;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;
import com.sz.admin.restaurant.pojo.dto.TakeawayOrdersCreateDTO;
import com.sz.admin.restaurant.pojo.dto.TakeawayOrdersUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.TakeawayOrdersListDTO;
import com.sz.admin.restaurant.pojo.vo.TakeawayOrdersVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 外卖扩展字段表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
public interface TakeawayOrdersService extends IService<TakeawayOrders> {

    void create(TakeawayOrdersCreateDTO dto);

    void update(TakeawayOrdersUpdateDTO dto);

    PageResult<TakeawayOrdersVO> page(TakeawayOrdersListDTO dto);

    List<TakeawayOrdersVO> list(TakeawayOrdersListDTO dto);

    void remove(SelectIdsDTO dto);

    TakeawayOrdersVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(TakeawayOrdersListDTO dto, HttpServletResponse response);

    void updateStatus(TakeawayOrdersUpdateDTO dto);

    void updatePayStatus(TakeawayOrdersUpdateDTO dto);
}