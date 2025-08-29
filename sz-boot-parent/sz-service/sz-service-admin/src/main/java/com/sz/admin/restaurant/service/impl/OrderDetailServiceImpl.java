package com.sz.admin.restaurant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.admin.restaurant.service.OrderDetailService;
import com.sz.admin.restaurant.pojo.po.OrderDetail;
import com.sz.admin.restaurant.mapper.OrderDetailMapper;
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
import com.sz.admin.restaurant.pojo.dto.OrderDetailCreateDTO;
import com.sz.admin.restaurant.pojo.dto.OrderDetailUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.OrderDetailListDTO;
import com.sz.admin.restaurant.pojo.dto.OrderDetailImportDTO;
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.core.ExcelResult;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.core.util.FileUtils;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
import com.sz.admin.restaurant.pojo.vo.OrderDetailVO;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-24
 */
@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    @Override
    public void create(OrderDetailCreateDTO dto){
        OrderDetail orderDetail = BeanCopyUtils.copy(dto, OrderDetail.class);
        save(orderDetail);
    }

    @Override
    public void update(OrderDetailUpdateDTO dto){
        OrderDetail orderDetail = BeanCopyUtils.copy(dto, OrderDetail.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(OrderDetail::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(orderDetail);
    }

    @Override
    public PageResult<OrderDetailVO> page(OrderDetailListDTO dto){
        Page<OrderDetailVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), OrderDetailVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<OrderDetailVO> list(OrderDetailListDTO dto){
        return listAs(buildQueryWrapper(dto), OrderDetailVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }
    
    public void removeByOrderId(Long orderId){
        QueryWrapper wrapper = QueryWrapper.create()
            .eq(OrderDetail::getOrderId, orderId);
        remove(wrapper);
    }

    @Override
    public OrderDetailVO detail(Object id){
        OrderDetail orderDetail = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(orderDetail);
        return BeanCopyUtils.copy(orderDetail, OrderDetailVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<OrderDetailImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), OrderDetailImportDTO.class, true);
        List<OrderDetailImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(OrderDetailListDTO dto, HttpServletResponse response) {
        List<OrderDetailVO> list = list(dto);
        String fileName = "订单明细模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "订单明细", OrderDetailVO.class, os);
    }

    @Override
    public List<OrderDetailVO> getListByOrderId(Object orderId) {
        OrderDetailListDTO dto = new OrderDetailListDTO();
        if(orderId instanceof Long){
            dto.setOrderId((Long) orderId);
        }else if(orderId instanceof String){
            dto.setOrderId(Long.parseLong((String) orderId));
        }
        return listAs(buildQueryWrapper(dto), OrderDetailVO.class);
    }

    private static QueryWrapper buildQueryWrapper(OrderDetailListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(OrderDetail.class);
        if (Utils.isNotNull(dto.getDishName())) {
            wrapper.like(OrderDetail::getDishName, dto.getDishName());
        }
        if (Utils.isNotNull(dto.getOrderId())) {
            wrapper.eq(OrderDetail::getOrderId, dto.getOrderId());
        }
        if (Utils.isNotNull(dto.getDishId())) {
            wrapper.eq(OrderDetail::getDishId, dto.getDishId());
        }
        return wrapper;
    }
}