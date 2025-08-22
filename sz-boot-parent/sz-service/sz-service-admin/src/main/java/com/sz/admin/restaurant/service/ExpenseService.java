package com.sz.admin.restaurant.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.restaurant.pojo.po.Expense;
import com.sz.core.common.entity.SelectIdsDTO;
import com.sz.core.common.entity.PageResult;
import java.util.List;
import com.sz.admin.restaurant.pojo.dto.ExpenseCreateDTO;
import com.sz.admin.restaurant.pojo.dto.ExpenseUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.ExpenseListDTO;
import com.sz.admin.restaurant.pojo.vo.ExpenseVO;
import com.sz.core.common.entity.ImportExcelDTO;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 费用表 Service
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
public interface ExpenseService extends IService<Expense> {

    void create(ExpenseCreateDTO dto);

    void update(ExpenseUpdateDTO dto);

    PageResult<ExpenseVO> page(ExpenseListDTO dto);

    List<ExpenseVO> list(ExpenseListDTO dto);

    void remove(SelectIdsDTO dto);

    ExpenseVO detail(Object id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(ExpenseListDTO dto, HttpServletResponse response);
}