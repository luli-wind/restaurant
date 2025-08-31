package com.sz.admin.restaurant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.restaurant.pojo.po.OrderDetail;
import com.sz.admin.restaurant.pojo.po.Orders;
import com.sz.admin.restaurant.service.InventoryService;
import com.sz.admin.restaurant.service.OrderDetailService;
import com.sz.admin.restaurant.service.OrdersService;
import com.sz.admin.system.service.SysMessageService;
import com.sz.utils.RestaurantOrderNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.sz.admin.restaurant.service.TakeawayOrdersService;
import com.sz.admin.restaurant.pojo.po.TakeawayOrders;
import com.sz.admin.restaurant.mapper.TakeawayOrdersMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.Utils;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import java.io.Serializable;
import java.time.LocalDateTime;
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

import static com.sz.admin.restaurant.pojo.po.table.OrdersTableDef.ORDERS;
import static com.sz.admin.restaurant.pojo.po.table.TakeawayOrdersTableDef.TAKEAWAY_ORDERS;

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
    private final OrdersService ordersService;
    private final OrderDetailService orderDetailService;
    private final InventoryService inventoryService;
    private final SysMessageService messageService;
    @Override
    public void create(TakeawayOrdersCreateDTO dto){
        // 创建基本订单记录
        Orders orders = new Orders();
        BeanUtils.copyProperties(dto, orders);
        orders.setOrderType("外卖");
        orders.setOrderNumber(RestaurantOrderNumberGenerator.generateOrderNo());
        orders.setCreateTime(LocalDateTime.now());
        orders.setStatus("2005001");
        orders.setPayStatus("2006001");//已支付
        orders.setPayTime(LocalDateTime.now());
        Double totalAmount = 0.0;
        List<OrderDetail> detailList = dto.getOrderItems();
        for (OrderDetail orderDetail : detailList) {
            orderDetail.setOrderId(orders.getOrderId());
            totalAmount +=orderDetail.getAmount()*orderDetail.getNumber();
        }

        totalAmount +=dto.getPackagingFee();
        totalAmount +=dto.getDeliveryFee();
        orders.setTotalAmount(totalAmount);
        ordersService.save(orders);
        //处理外卖订单扩展字段
        TakeawayOrders takeawayOrders = BeanCopyUtils.copy(dto, TakeawayOrders.class);
        takeawayOrders.setOrderId(orders.getOrderId());
        takeawayOrders.setThirdPartyUserId(dto.getThirdPartyUserId());
        save(takeawayOrders);
        //处理订单详情
        for (OrderDetail orderDetail : detailList) {
            orderDetail.setOrderId(orders.getOrderId());
        }
        orderDetailService.saveBatch(detailList);
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

        Orders orders = ordersService.getById(takeawayOrders.getOrderId());
        if (orders != null) {
            // 更新订单相关字段
            BeanUtils.copyProperties(dto, orders);
            ordersService.updateById(orders);
        }
    }

    @Override
    public PageResult<TakeawayOrdersVO> page(TakeawayOrdersListDTO dto){
        Page<TakeawayOrders> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), TakeawayOrders.class);
        Page<TakeawayOrdersVO> voPage = new Page<>(page.getPageNumber(),page.getPageSize());
        voPage.setTotalRow(page.getTotalRow());
        // 转换为VO对象并关联查询Orders表的信息
        List<TakeawayOrdersVO> voList = page.getRecords().stream().map(TakeawayOrders -> {
            // 查询对应的基本订单记录
            Orders orders = ordersService.getById(TakeawayOrders.getOrderId());
            // 将两个记录的信息合并到VO对象中
            TakeawayOrdersVO vo = BeanCopyUtils.copy(TakeawayOrders, TakeawayOrdersVO.class);
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
                vo.setOrderItems(orderDetailService.getListByOrderId(orders.getOrderId()));

            }

            return vo;
        }).toList();

        voPage.setRecords(voList);
        return PageUtils.getPageResult(voPage);
    }

    @Override
    public List<TakeawayOrdersVO> list(TakeawayOrdersListDTO dto){
        List<TakeawayOrders> list = listAs(buildQueryWrapper(dto), TakeawayOrders.class);
        return list.stream().map(TakeawayOrders -> {
            // 查询对应的基本订单记录
            Orders orders = ordersService.getById(TakeawayOrders.getOrderId());
            // 将两个记录的信息合并到VO对象中
            TakeawayOrdersVO vo = BeanCopyUtils.copy(TakeawayOrders, TakeawayOrdersVO.class);
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
            }

            return vo;
        }).toList();
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

        Orders orders = ordersService.getById(takeawayOrders.getOrderId());
        TakeawayOrdersVO vo =BeanCopyUtils.copy(takeawayOrders, TakeawayOrdersVO.class);
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
        }
        return vo;
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

    @Override
    public void updateStatus(TakeawayOrdersUpdateDTO dto) {
        Orders orders = ordersService.getById(dto.getOrderId());
        orders.setStatus(dto.getStatus());
        if (dto.getStatus().equals("2005002")) {
            if(inventoryService.isEnough(dto.getOrderId())){
                //扣除库存中的材料
                inventoryService.subtractMatrials(dto.getOrderId());
            }else {
                //告知管理员或服务员材料不足，让他们取消订单或者更换订单
                List<OrderDetail> orderDetailList = inventoryService.InsufficientInventory(dto.getOrderId());
                messageService.sendInventoryInsufficient(orderDetailList,orders);
                orders.setStatus("2005007");
            }
        }
        ordersService.updateById(orders);
    }

    @Override
    public void updatePayStatus(TakeawayOrdersUpdateDTO dto) {
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

    private static QueryWrapper buildQueryWrapper(TakeawayOrdersListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(TAKEAWAY_ORDERS);
        
        // 关联订单表进行查询
        if (Utils.isNotNull(dto.getStatus()) || Utils.isNotNull(dto.getPayStatus())) {
            wrapper.leftJoin(ORDERS).on(TAKEAWAY_ORDERS.ORDER_ID.eq(ORDERS.ORDER_ID));
        }
        
        if (Utils.isNotNull(dto.getStatus())) {
            wrapper.eq(Orders::getStatus, dto.getStatus());
        }
        if (Utils.isNotNull(dto.getPayStatus())) {
            wrapper.eq(Orders::getPayStatus, dto.getPayStatus());
        }
        if (Utils.isNotNull(dto.getCustomerPhone())) {
            wrapper.like(TakeawayOrders::getCustomerPhone, dto.getCustomerPhone());
        }
        if (Utils.isNotNull(dto.getDeliveryAddress())) {
            wrapper.like(TakeawayOrders::getDeliveryAddress, dto.getDeliveryAddress());
        }

        if (Utils.isNotNull(dto.getCustomerName())) {
            wrapper.like(TakeawayOrders::getCustomerName, dto.getCustomerName());
        }
        return wrapper;
    }

    @Override
    public TakeawayOrdersVO createGuestOrder(TakeawayOrdersCreateDTO dto) {
        // 创建订单
        create(dto);
        
        // 获取刚创建的订单详情
        TakeawayOrdersListDTO queryDto = new TakeawayOrdersListDTO();
        queryDto.setCustomerPhone(dto.getCustomerPhone());
        List<TakeawayOrdersVO> orders = list(queryDto);
        
        // 返回最新创建的订单
        return orders.get(orders.size() - 1);
    }

    @Override
    public PageResult<TakeawayOrdersVO> getOrdersByThirdPartyUserId(com.sz.admin.restaurant.pojo.dto.TakeawayOrdersQueryDTO dto) {
        // 构造查询条件
        QueryWrapper wrapper = QueryWrapper.create().from(TAKEAWAY_ORDERS);
        
        // 添加第三方用户ID条件
        if (dto.getThirdPartyUserId() != null) {
            wrapper.eq(TakeawayOrders::getThirdPartyUserId, dto.getThirdPartyUserId());
        }
        
        // 添加其他查询条件
        if (Utils.isNotNull(dto.getStatus())) {
            wrapper.eq(Orders::getStatus, dto.getStatus());
        }
        if (Utils.isNotNull(dto.getPayStatus())) {
            wrapper.eq(Orders::getPayStatus, dto.getPayStatus());
        }
        if (Utils.isNotNull(dto.getCustomerPhone())) {
            wrapper.like(TakeawayOrders::getCustomerPhone, dto.getCustomerPhone());
        }
        if (Utils.isNotNull(dto.getDeliveryAddress())) {
            wrapper.like(TakeawayOrders::getDeliveryAddress, dto.getDeliveryAddress());
        }
        if (Utils.isNotNull(dto.getCustomerName())) {
            wrapper.like(TakeawayOrders::getCustomerName, dto.getCustomerName());
        }
        
        // 执行分页查询
        Page<TakeawayOrders> page = pageAs(PageUtils.getPage(dto), wrapper, TakeawayOrders.class);
        Page<TakeawayOrdersVO> voPage = new Page<>(page.getPageNumber(), page.getPageSize());
        voPage.setTotalRow(page.getTotalRow());
        
        // 转换为VO对象并关联查询Orders表的信息
        List<TakeawayOrdersVO> voList = page.getRecords().stream().map(takeawayOrders -> {
            // 查询对应的基本订单记录
            Orders orders = ordersService.getById(takeawayOrders.getOrderId());
            // 将两个记录的信息合并到VO对象中
            TakeawayOrdersVO vo = BeanCopyUtils.copy(takeawayOrders, TakeawayOrdersVO.class);
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
                vo.setOrderItems(orderDetailService.getListByOrderId(orders.getOrderId()));
            }
            return vo;
        }).toList();
        
        voPage.setRecords(voList);
        return PageUtils.getPageResult(voPage);
    }
}