package com.sz.admin.restaurant.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import com.sz.admin.restaurant.service.DishRecipeService;
import com.sz.admin.restaurant.pojo.po.DishRecipe;
import com.sz.admin.restaurant.mapper.DishRecipeMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.sz.core.common.enums.CommonResponseEnum;
import com.sz.core.util.PageUtils;
import com.sz.core.util.BeanCopyUtils;
import com.sz.core.util.Utils;
import com.sz.core.common.entity.PageResult;
import com.sz.core.common.entity.SelectIdsDTO;
import java.io.Serializable;
import java.util.List;
import com.sz.admin.restaurant.pojo.dto.DishRecipeCreateDTO;
import com.sz.admin.restaurant.pojo.dto.DishRecipeUpdateDTO;
import com.sz.admin.restaurant.pojo.dto.DishRecipeListDTO;
import com.sz.admin.restaurant.pojo.vo.DishRecipeVO;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import com.sz.core.util.FileUtils;
import com.sz.excel.utils.ExcelUtils;

/**
 * <p>
 * 菜品配方表（Bill of Materials） 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Service
@RequiredArgsConstructor
public class DishRecipeServiceImpl extends ServiceImpl<DishRecipeMapper, DishRecipe> implements DishRecipeService {
    
    private final DishRecipeMapper dishRecipeMapper;
    
    @Override
    public void create(DishRecipeCreateDTO dto){
        DishRecipe dishRecipe = BeanCopyUtils.copy(dto, DishRecipe.class);
        save(dishRecipe);
    }

    @Override
    public void update(DishRecipeUpdateDTO dto){
        DishRecipe dishRecipe = BeanCopyUtils.copy(dto, DishRecipe.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
            .eq(DishRecipe::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(dishRecipe);
    }

    @Override
    public PageResult<DishRecipeVO> page(DishRecipeListDTO dto){
        Page<DishRecipeVO> page = pageAs(PageUtils.getPage(dto), buildQueryWrapper(dto), DishRecipeVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<DishRecipeVO> list(DishRecipeListDTO dto){
        return listAs(buildQueryWrapper(dto), DishRecipeVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto){
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public DishRecipeVO detail(Object id){
        DishRecipe dishRecipe = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(dishRecipe);
        return BeanCopyUtils.copy(dishRecipe, DishRecipeVO.class);
    }

    @Override
    public List<DishRecipeVO> listByDishId(Long dishId) {
        return dishRecipeMapper.selectListByDishId(dishId);
    }

    @SneakyThrows
    @Override
    public void exportExcel(DishRecipeListDTO dto, HttpServletResponse response) {
        List<DishRecipeVO> list = list(dto);
        String fileName = "菜品配方表";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "菜品配方表", DishRecipeVO.class, os);
    }

    private static QueryWrapper buildQueryWrapper(DishRecipeListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(DishRecipe.class);
        if (Utils.isNotNull(dto.getDishId())) {
            wrapper.eq(DishRecipe::getDishId, dto.getDishId());
        }
        if (Utils.isNotNull(dto.getMaterialId())) {
            wrapper.eq(DishRecipe::getMaterialId, dto.getMaterialId());
        }
        return wrapper;
    }
}
