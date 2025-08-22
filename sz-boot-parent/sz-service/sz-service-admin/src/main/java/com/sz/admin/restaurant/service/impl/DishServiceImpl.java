package com.sz.admin.restaurant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sz.admin.restaurant.service.DishService;
import com.sz.admin.restaurant.pojo.po.Dish;
import com.sz.admin.restaurant.mapper.DishMapper;
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
import com.sz.admin.restaurant.pojo.dto.DishCreateDTO;
import com.sz.admin.restaurant.pojo.dto.DishUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.DishListDTO;
import com.sz.admin.restaurant.pojo.dto.DishImportDTO;
import com.sz.core.common.entity.ImportExcelDTO;
import com.sz.excel.core.ExcelResult;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.core.util.FileUtils;
import com.sz.excel.utils.ExcelUtils;
import lombok.SneakyThrows;
import com.sz.admin.restaurant.pojo.vo.DishVO;

/**
 * <p>
 * 菜品表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Service
@RequiredArgsConstructor
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Override
    public void create(DishCreateDTO dto){
        Dish dish = BeanCopyUtils.copy(dto, Dish.class);
        save(dish);
    }

    @Override
    public void update(DishUpdateDTO dto){
        Dish dish = BeanCopyUtils.copy(dto, Dish.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(Dish::getDishId, dto.getDishId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(dish);
    }

    @Override
    public PageResult<DishVO> page(DishListDTO dto){
        Page<DishVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), DishVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<DishVO> list(DishListDTO dto){
        return listAs(buildQueryWrapper(dto), DishVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public DishVO detail(Object id){
        Dish dish = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(dish);
        return BeanCopyUtils.copy(dish, DishVO.class);
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<DishImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), DishImportDTO.class, true);
        List<DishImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(DishListDTO dto, HttpServletResponse response) {
        List<DishVO> list = list(dto);
        String fileName = "菜品管理模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "菜品管理", DishVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(DishListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(Dish.class);
        if (Utils.isNotNull(dto.getDishName())) {
            wrapper.like(Dish::getDishName, dto.getDishName());
        }
        if (Utils.isNotNull(dto.getCategory())) {
            wrapper.eq(Dish::getCategory, dto.getCategory());
        }
        if (Utils.isNotNull(dto.getPriceStart()) && Utils.isNotNull(dto.getPriceEnd())) {
            wrapper.between(Dish::getPrice, dto.getPriceStart(), dto.getPriceEnd());
        }
        return wrapper;
    }
}