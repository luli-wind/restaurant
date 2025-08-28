package com.sz.admin.restaurant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sz.admin.restaurant.pojo.po.DishRecipe;
import com.sz.admin.restaurant.pojo.po.OrderDetail;
import com.sz.admin.restaurant.pojo.vo.DishRecipeVO;
import com.sz.admin.restaurant.pojo.vo.OrderDetailVO;
import com.sz.admin.restaurant.service.OrderDetailService;
import com.sz.admin.system.service.SysMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.sz.admin.restaurant.service.InventoryService;
import com.sz.admin.restaurant.pojo.po.Inventory;
import com.sz.admin.restaurant.mapper.InventoryMapper;
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
import java.util.ArrayList;
import java.util.List;
import com.sz.admin.restaurant.pojo.dto.InventoryCreateDTO;
import com.sz.admin.restaurant.pojo.dto.InventoryUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.InventoryListDTO;
import com.sz.admin.restaurant.pojo.dto.InventoryImportDTO;
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.core.ExcelResult;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.core.util.FileUtils;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
import com.sz.admin.restaurant.pojo.vo.InventoryVO;

/**
 * <p>
 * 库存表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {
    private final OrderDetailService orderDetailService;
    private final DishRecipeServiceImpl dishRecipeService;
    private final SysMessageService sysMessageService;
    @Override
    public void create(InventoryCreateDTO dto){
        Inventory inventory = BeanCopyUtils.copy(dto, Inventory.class);
        save(inventory);
    }

    @Override
    public void update(InventoryUpdateDTO dto){
        Inventory inventory = BeanCopyUtils.copy(dto, Inventory.class);
        updateStatus(inventory);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(Inventory::getMaterialId, dto.getMaterialId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(inventory);
    }

    @Override
    public PageResult<InventoryVO> page(InventoryListDTO dto){
        Page<InventoryVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), InventoryVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<InventoryVO> list(InventoryListDTO dto){
        return listAs(buildQueryWrapper(dto), InventoryVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public InventoryVO detail(Object id){
        Inventory inventory = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(inventory);
        return BeanCopyUtils.copy(inventory, InventoryVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<InventoryImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), InventoryImportDTO.class, true);
        List<InventoryImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(InventoryListDTO dto, HttpServletResponse response) {
        List<InventoryVO> list = list(dto);
        String fileName = "库存管理模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "库存管理", InventoryVO.class, os);
    }

    @Override
    public List<InventoryVO> getAllList() {
        List<InventoryVO> list = listAs(buildQueryWrapper(null), InventoryVO.class);
        return list;
    }

    //根据订单明细，看看库存是否有足够的食物
    public boolean isEnough(Long orderId) {
        List<OrderDetailVO> list = orderDetailService.getListByOrderId(orderId);
        List<OrderDetail> orderDetailList =new ArrayList<>();
        for (OrderDetailVO orderDetailVO : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(orderDetailVO, orderDetail);
            orderDetailList.add(orderDetail);
        }
        //遍历每道菜的菜谱
        for (OrderDetail orderDetail : orderDetailList) {
            List<DishRecipeVO> dishRecipeList =  dishRecipeService.listByDishId(orderDetail.getDishId());
            //根据菜谱的配方与库存相比较
            for(DishRecipeVO dishRecipeVO : dishRecipeList){
                Inventory inventory = getById(dishRecipeVO.getMaterialId());
                if(inventory.getCurrentStock()<dishRecipeVO.getMaterialQuantity()*orderDetail.getNumber()){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void subtractMatrials(Long orderId) {
        List<OrderDetailVO> list = orderDetailService.getListByOrderId(orderId);
        List<OrderDetail> orderDetailList =new ArrayList<>();
        for (OrderDetailVO orderDetailVO : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(orderDetailVO, orderDetail);
            orderDetailList.add(orderDetail);
        }
        //是否向管理员发送库存警告
        boolean inventoryAlert = false;
        //遍历每道菜的菜谱
        for (OrderDetail orderDetail : orderDetailList) {
            List<DishRecipeVO> dishRecipeList =  dishRecipeService.listByDishId(orderDetail.getDishId());
            //根据菜谱的配方与库存相比较
            for(DishRecipeVO dishRecipeVO : dishRecipeList){
                Inventory inventory = getById(dishRecipeVO.getMaterialId());
                Double currentStock = inventory.getCurrentStock();
                //扣除材料
                currentStock -=dishRecipeVO.getMaterialQuantity()*orderDetail.getNumber();
                inventory.setCurrentStock(currentStock);
                updateById(inventory);
                //如果材料小于最少预警值，向管理员发送通告
                if(inventory.getCurrentStock()<inventory.getMinStock()){
                    updateStatus(inventory);
                    saveOrUpdate(inventory);
                    inventoryAlert=true;
                }
            }
        }
        if(inventoryAlert){
            sysMessageService.sendInventoryAlert();
        }

    }

    public void updateStatus(Inventory inventory){
        if(inventory.getCurrentStock()>inventory.getMinStock()){
            inventory.setStatus("2007001");
        }else {
            inventory.setStatus("2007002");
        }
    }

    private static QueryWrapper buildQueryWrapper(InventoryListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(Inventory.class);
        if (Utils.isNotNull(dto.getMaterialName())) {
            wrapper.like(Inventory::getMaterialName, dto.getMaterialName());
        }
        if (Utils.isNotNull(dto.getCurrentStockStart()) && Utils.isNotNull(dto.getCurrentStockEnd())) {
            wrapper.between(Inventory::getCurrentStock, dto.getCurrentStockStart(), dto.getCurrentStockEnd());
        }
        if (Utils.isNotNull(dto.getStatus())) {
            wrapper.like(Inventory::getStatus, dto.getStatus());
        }
        return wrapper;
    }
}