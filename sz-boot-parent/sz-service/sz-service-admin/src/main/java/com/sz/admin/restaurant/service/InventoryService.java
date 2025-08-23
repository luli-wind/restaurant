package com.sz.admin.restaurant.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.restaurant.pojo.po.Inventory;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;
import com.sz.admin.restaurant.pojo.dto.InventoryCreateDTO;
import com.sz.admin.restaurant.pojo.dto.InventoryUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.InventoryListDTO;
import com.sz.admin.restaurant.pojo.vo.InventoryVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 库存表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
public interface InventoryService extends IService<Inventory> {

    void create(InventoryCreateDTO dto);

    void update(InventoryUpdateDTO dto);

    PageResult<InventoryVO> page(InventoryListDTO dto);

    List<InventoryVO> list(InventoryListDTO dto);

    void remove(SelectIdsDTO dto);

    InventoryVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(InventoryListDTO dto, HttpServletResponse response);

    List<InventoryVO> getAllList();
}