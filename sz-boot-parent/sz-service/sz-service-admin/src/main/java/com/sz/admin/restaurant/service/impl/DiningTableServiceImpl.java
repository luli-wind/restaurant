package com.sz.admin.restaurant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.admin.restaurant.service.DiningTableService;
import com.sz.admin.restaurant.pojo.po.DiningTable;
import com.sz.admin.restaurant.mapper.DiningTableMapper;
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
import com.sz.admin.restaurant.pojo.dto.DiningTableCreateDTO;
import com.sz.admin.restaurant.pojo.dto.DiningTableUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.DiningTableListDTO;
import com.sz.admin.restaurant.pojo.dto.DiningTableImportDTO;
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.core.ExcelResult;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.core.util.FileUtils;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
import com.sz.admin.restaurant.pojo.vo.DiningTableVO;

/**
 * <p>
 * 餐桌表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Service
@RequiredArgsConstructor
public class DiningTableServiceImpl extends ServiceImpl<DiningTableMapper, DiningTable> implements DiningTableService {
    @Override
    public void create(DiningTableCreateDTO dto){
        DiningTable diningTable = BeanCopyUtils.copy(dto, DiningTable.class);
        save(diningTable);
    }

    @Override
    public void update(DiningTableUpdateDTO dto){
        DiningTable diningTable = BeanCopyUtils.copy(dto, DiningTable.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(DiningTable::getTableId, dto.getTableId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(diningTable);
    }

    @Override
    public PageResult<DiningTableVO> page(DiningTableListDTO dto){
        Page<DiningTableVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), DiningTableVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<DiningTableVO> list(DiningTableListDTO dto){
        return listAs(buildQueryWrapper(dto), DiningTableVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public DiningTableVO detail(Object id){
        DiningTable diningTable = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(diningTable);
        return BeanCopyUtils.copy(diningTable, DiningTableVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<DiningTableImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), DiningTableImportDTO.class, true);
        List<DiningTableImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(DiningTableListDTO dto, HttpServletResponse response) {
        List<DiningTableVO> list = list(dto);
        String fileName = "餐桌管理模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "餐桌管理", DiningTableVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(DiningTableListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(DiningTable.class);
        if (Utils.isNotNull(dto.getTableName())) {
            wrapper.like(DiningTable::getTableName, dto.getTableName());
        }
        if (Utils.isNotNull(dto.getCapacity())) {
            wrapper.ge(DiningTable::getCapacity, dto.getCapacity());
        }
        if (Utils.isNotNull(dto.getStatus())) {
            wrapper.eq(DiningTable::getStatus, dto.getStatus());
        }
        return wrapper;
    }
}