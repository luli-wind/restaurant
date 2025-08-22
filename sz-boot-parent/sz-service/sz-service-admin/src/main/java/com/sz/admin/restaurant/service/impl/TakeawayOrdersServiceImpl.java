package com.sz.admin.restaurant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.admin.restaurant.service.TakeawayOrdersService;
import com.sz.admin.restaurant.pojo.po.TakeawayOrders;
import com.sz.admin.restaurant.mapper.TakeawayOrdersMapper;
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
import com.sz.admin.restaurant.pojo.dto.TakeawayOrdersCreateDTO;
import com.sz.admin.restaurant.pojo.dto.TakeawayOrdersUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.TakeawayOrdersListDTO;
import com.sz.admin.restaurant.pojo.dto.TakeawayOrdersImportDTO;
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.core.ExcelResult;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.core.util.FileUtils;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
import com.sz.admin.restaurant.pojo.vo.TakeawayOrdersVO;

/**
 * <p>
 * 外卖扩展字段表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Service
@RequiredArgsConstructor
public class TakeawayOrdersServiceImpl extends ServiceImpl<TakeawayOrdersMapper, TakeawayOrders> implements TakeawayOrdersService {
    @Override
    public void create(TakeawayOrdersCreateDTO dto){
        TakeawayOrders takeawayOrders = BeanCopyUtils.copy(dto, TakeawayOrders.class);
        save(takeawayOrders);
    }

    @Override
    public void update(TakeawayOrdersUpdateDTO dto){
        TakeawayOrders takeawayOrders = BeanCopyUtils.copy(dto, TakeawayOrders.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(TakeawayOrders::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(takeawayOrders);
    }

    @Override
    public PageResult<TakeawayOrdersVO> page(TakeawayOrdersListDTO dto){
        Page<TakeawayOrdersVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), TakeawayOrdersVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<TakeawayOrdersVO> list(TakeawayOrdersListDTO dto){
        return listAs(buildQueryWrapper(dto), TakeawayOrdersVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public TakeawayOrdersVO detail(Object id){
        TakeawayOrders takeawayOrders = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(takeawayOrders);
        return BeanCopyUtils.copy(takeawayOrders, TakeawayOrdersVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<TakeawayOrdersImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), TakeawayOrdersImportDTO.class, true);
        List<TakeawayOrdersImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(TakeawayOrdersListDTO dto, HttpServletResponse response) {
        List<TakeawayOrdersVO> list = list(dto);
        String fileName = "外卖订单模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "外卖订单", TakeawayOrdersVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(TakeawayOrdersListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(TakeawayOrders.class);
        if (Utils.isNotNull(dto.getOrderId())) {
            wrapper.eq(TakeawayOrders::getOrderId, dto.getOrderId());
        }
        if (Utils.isNotNull(dto.getCustomerPhone())) {
            wrapper.like(TakeawayOrders::getCustomerPhone, dto.getCustomerPhone());
        }
        if (Utils.isNotNull(dto.getDeliveryAddress())) {
            wrapper.like(TakeawayOrders::getDeliveryAddress, dto.getDeliveryAddress());
        }
        return wrapper;
    }
}