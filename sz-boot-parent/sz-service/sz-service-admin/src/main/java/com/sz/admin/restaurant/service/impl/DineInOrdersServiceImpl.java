package com.sz.admin.restaurant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.restaurant.mapper.DiningTableMapper;
import com.sz.admin.restaurant.mapper.OrdersMapper;
import com.sz.admin.restaurant.pojo.dto.*;
import com.sz.admin.restaurant.pojo.po.DiningTable;
import com.sz.admin.restaurant.pojo.po.OrderDetail;
import com.sz.admin.restaurant.service.DiningTableService;
import com.sz.admin.restaurant.service.OrderDetailService;
import com.sz.utils.RestaurantOrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.sz.admin.restaurant.service.DineInOrdersService;
import com.sz.admin.restaurant.pojo.po.DineInOrders;
import com.sz.admin.restaurant.mapper.DineInOrdersMapper;
import com.sz.admin.restaurant.service.OrdersService;
import com.sz.admin.restaurant.pojo.po.Orders;
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
import java.time.LocalDateTime;
import java.util.List;

import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.core.ExcelResult;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.core.util.FileUtils;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
import com.sz.admin.restaurant.pojo.vo.DineInOrdersVO;

/**
 * <p>
 * 堂食扩展字段 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Service
@RequiredArgsConstructor
public class DineInOrdersServiceImpl extends ServiceImpl<DineInOrdersMapper, DineInOrders> implements DineInOrdersService {
    private final OrdersService ordersService;
    private final DiningTableService diningTableService;
    private final OrderDetailService orderDetailService;
    @Override
    public void create(DineInOrdersCreateDTO dto){
        // 创建基本订单记录
        Orders orders = new Orders();
        BeanUtils.copyProperties(dto, orders);
        orders.setOrderNumber(RestaurantOrderNumberGenerator.generateOrderNo());
        orders.setOrderType("堂食");
        orders.setStatus("2004001");//已下单
        orders.setPayStatus("2006002");//未支付
        orders.setCreateTime(LocalDateTime.now());
        Double totalAmount = 0.0;
        List<OrderDetail> detailList = dto.getOrderItems();
        for (OrderDetail orderDetail : detailList) {
            orderDetail.setOrderId(orders.getOrderId());
            totalAmount +=orderDetail.getAmount()*orderDetail.getNumber();
        }

        orders.setTotalAmount(totalAmount);
        ordersService.save(orders);
        //将餐桌设为已占用状态
        DiningTable diningTable = diningTableService.getById(dto.getTableId());
        if(diningTable != null){
            diningTable.setStatus("2001002");
            diningTableService.updateById(diningTable);
        }

        // 创建堂食订单扩展记录
        DineInOrders dineInOrders = BeanCopyUtils.copy(dto, DineInOrders.class);
        // 设置订单ID
        dineInOrders.setOrderId(Math.toIntExact(orders.getOrderId()));
        save(dineInOrders);

        for (OrderDetail orderDetail : detailList) {
            orderDetail.setOrderId(orders.getOrderId());
        }


        orderDetailService.saveBatch(detailList);

    }

    @Override
    public void update(DineInOrdersUpdateDTO dto){
        // id有效性校验
        QueryWrapper wrapper = QueryWrapper.create()
            .eq(DineInOrders::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) > 0);

        // 更新堂食订单扩展记录
        DineInOrders dineInOrders = BeanCopyUtils.copy(dto, DineInOrders.class);
        saveOrUpdate(dineInOrders);
        
        // 更新基本订单记录（如果需要）
        // 这里可以根据实际需求更新Orders表中的字段
        // 例如，如果dto中包含了需要更新的订单字段，可以这样处理：
        Orders orders = ordersService.getById(dineInOrders.getOrderId());
        if (orders != null) {
            // 更新订单相关字段
            BeanUtils.copyProperties(dto, orders);
            ordersService.updateById(orders);
        }
    }

    @Override
    public PageResult<DineInOrdersVO> page(DineInOrdersListDTO dto){
        Page<DineInOrders> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), DineInOrders.class);
        Page<DineInOrdersVO> voPage = new Page<>(page.getPageNumber(), page.getPageSize());
        voPage.setTotalRow(page.getTotalRow());
        
        // 转换为VO对象并关联查询Orders表的信息
        List<DineInOrdersVO> voList = page.getRecords().stream().map(dineInOrders -> {
            // 查询对应的基本订单记录
            Orders orders = ordersService.getById(dineInOrders.getOrderId());
            DiningTable table = diningTableService.getById(dineInOrders.getTableId());
            // 将两个记录的信息合并到VO对象中
            DineInOrdersVO vo = BeanCopyUtils.copy(dineInOrders, DineInOrdersVO.class);
            if (orders != null) {
                vo.setOrderId(orders.getOrderId());
                vo.setOrderNumber(orders.getOrderNumber());
                vo.setOrderType(orders.getOrderType());
                vo.setTotalAmount(orders.getTotalAmount());
                vo.setStatus(orders.getStatus());
                vo.setCreateTime(orders.getCreateTime());
                vo.setPayStatus(orders.getPayStatus());
                vo.setPayTime(orders.getPayTime());
                vo.setRefundReason(orders.getRefundReason());
                vo.setTableName(table.getTableName());
            }
            
            return vo;
        }).toList();
        
        voPage.setRecords(voList);
        return PageUtils.getPageResult(voPage);
    }

    @Override
    public List<DineInOrdersVO> list(DineInOrdersListDTO dto){
        List<DineInOrders> dineInOrdersList = listAs(buildQueryWrapper(dto), DineInOrders.class);
        
        // 转换为VO对象并关联查询Orders表的信息
        return dineInOrdersList.stream().map(dineInOrders -> {
            // 查询对应的基本订单记录
            Orders orders = ordersService.getById(dineInOrders.getOrderId());
            DiningTable table = diningTableService.getById(dineInOrders.getTableId());
            // 将两个记录的信息合并到VO对象中
            DineInOrdersVO vo = BeanCopyUtils.copy(dineInOrders, DineInOrdersVO.class);
            if (orders != null) {
                vo.setOrderId(orders.getOrderId());
                vo.setOrderNumber(orders.getOrderNumber());
                vo.setOrderType(orders.getOrderType());
                vo.setTotalAmount(orders.getTotalAmount());
                vo.setStatus(orders.getStatus());
                vo.setCreateTime(orders.getCreateTime());
                vo.setPayStatus(orders.getPayStatus());
                vo.setPayTime(orders.getPayTime());
                vo.setRefundReason(orders.getRefundReason());
                vo.setTableName(table.getTableName());
            }
            
            return vo;
        }).toList();
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(!dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public DineInOrdersVO detail(Object id){
        DineInOrders dineInOrders = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(dineInOrders);
        
        // 查询对应的基本订单记录
        Orders orders = ordersService.getById(dineInOrders.getOrderId());
        DiningTable table = diningTableService.getById(dineInOrders.getTableId());
        // 将两个记录的信息合并到VO对象中
        DineInOrdersVO vo = BeanCopyUtils.copy(dineInOrders, DineInOrdersVO.class);
        if (orders != null) {
            vo.setOrderId(orders.getOrderId());
            vo.setOrderNumber(orders.getOrderNumber());
            vo.setOrderType(orders.getOrderType());
            vo.setTotalAmount(orders.getTotalAmount());
            vo.setStatus(orders.getStatus());
            vo.setCreateTime(orders.getCreateTime());
            vo.setPayStatus(orders.getPayStatus());
            vo.setPayTime(orders.getPayTime());
            vo.setRefundReason(orders.getRefundReason());
            vo.setTableName(table.getTableName());
        }
        
        return vo;
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<DineInOrdersImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), DineInOrdersImportDTO.class, true);
        List<DineInOrdersImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(DineInOrdersListDTO dto, HttpServletResponse response) {
        List<DineInOrdersVO> list = list(dto);
        String fileName = "堂食订单模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "堂食订单", DineInOrdersVO.class, os);
    }

    @Override
    public void updateStatus(DineInOrdersUpdateDTO dto) {
        Orders orders = ordersService.getById(dto.getOrderId());
        orders.setStatus(dto.getStatus());
        //用户完成用餐，将餐桌设置为空闲状态
        if(dto.getStatus().equals("2004005")){
            DiningTable table = diningTableService.getById(dto.getTableId());
            table.setStatus("2001001");
            diningTableService.updateById(table);
        }
        ordersService.updateById(orders);
    }

    @Override
    public void updatePayStatus(DineInOrdersUpdateDTO dto) {
        Orders orders = ordersService.getById(dto.getOrderId());
        orders.setPayStatus(dto.getPayStatus());
        if(dto.getPayStatus().equals("2006001")){
            orders.setPayTime(LocalDateTime.now());
        }
        if(dto.getPayStatus().equals("2006003")){
            orders.setRefundReason(dto.getRefundReason());
        }
        ordersService.updateById(orders);
    }

    private static QueryWrapper buildQueryWrapper(DineInOrdersListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(DineInOrders.class);
        if (Utils.isNotNull(dto.getOrderId())) {
            wrapper.eq(DineInOrders::getOrderId, dto.getOrderId());
        }
        if (Utils.isNotNull(dto.getTableId())) {
            wrapper.eq(DineInOrders::getTableId, dto.getTableId());
        }
        return wrapper;
    }
}