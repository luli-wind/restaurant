package com.sz.admin.restaurant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.admin.restaurant.service.ExpenseService;
import com.sz.admin.restaurant.pojo.po.Expense;
import com.sz.admin.restaurant.mapper.ExpenseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.QueryChain;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.Utils;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import java.io.Serializable;
import java.util.List;
import com.sz.admin.restaurant.pojo.dto.ExpenseCreateDTO;
import com.sz.admin.restaurant.pojo.dto.ExpenseUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.ExpenseListDTO;
import com.sz.admin.restaurant.pojo.dto.ExpenseImportDTO;
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.core.ExcelResult;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.core.util.FileUtils;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
import com.sz.admin.restaurant.pojo.vo.ExpenseVO;

/**
 * <p>
 * 费用表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl extends ServiceImpl<ExpenseMapper, Expense> implements ExpenseService {
    @Override
    public void create(ExpenseCreateDTO dto){
        Expense expense = BeanCopyUtils.copy(dto, Expense.class);
        save(expense);
    }

    @Override
    public void update(ExpenseUpdateDTO dto){
        Expense expense = BeanCopyUtils.copy(dto, Expense.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(Expense::getExpenseId, dto.getExpenseId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(expense);
    }

    @Override
    public PageResult<ExpenseVO> page(ExpenseListDTO dto){
        Page<ExpenseVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), ExpenseVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<ExpenseVO> list(ExpenseListDTO dto){
        return listAs(buildQueryWrapper(dto), ExpenseVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public ExpenseVO detail(Object id){
        Expense expense = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(expense);
        return BeanCopyUtils.copy(expense, ExpenseVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<ExpenseImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), ExpenseImportDTO.class, true);
        List<ExpenseImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(ExpenseListDTO dto, HttpServletResponse response) {
        List<ExpenseVO> list = list(dto);
        String fileName = "费用管理模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "费用管理", ExpenseVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(ExpenseListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(Expense.class);
        if (Utils.isNotNull(dto.getExpenseCategory())) {
            wrapper.eq(Expense::getExpenseCategory, dto.getExpenseCategory());
        }
        if (Utils.isNotNull(dto.getAmountStart()) && Utils.isNotNull(dto.getAmountEnd())) {
            wrapper.between(Expense::getAmount, dto.getAmountStart(), dto.getAmountEnd());
        }
        if (Utils.isNotNull(dto.getExpenseDateStart()) && Utils.isNotNull(dto.getExpenseDateEnd())) {
            wrapper.between(Expense::getExpenseDate, dto.getExpenseDateStart(), dto.getExpenseDateEnd());
        }
        return wrapper;
    }
}