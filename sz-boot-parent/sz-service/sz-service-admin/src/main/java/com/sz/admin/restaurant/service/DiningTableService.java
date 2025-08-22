package com.sz.admin.restaurant.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.restaurant.pojo.po.DiningTable;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;
import com.sz.admin.restaurant.pojo.dto.DiningTableCreateDTO;
import com.sz.admin.restaurant.pojo.dto.DiningTableUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.DiningTableListDTO;
import com.sz.admin.restaurant.pojo.vo.DiningTableVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 餐桌表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
public interface DiningTableService extends IService<DiningTable> {

    void create(DiningTableCreateDTO dto);

    void update(DiningTableUpdateDTO dto);

    PageResult<DiningTableVO> page(DiningTableListDTO dto);

    List<DiningTableVO> list(DiningTableListDTO dto);

    void remove(SelectIdsDTO dto);

    DiningTableVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(DiningTableListDTO dto, HttpServletResponse response);
}