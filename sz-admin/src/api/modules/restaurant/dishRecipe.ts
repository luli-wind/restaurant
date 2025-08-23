import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/types';
import type {
  DishRecipeQuery,
  DishRecipeRow,
  DishRecipeForm
} from '@/api/types/restaurant/dishRecipe';
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getDishRecipeListApi = (params: DishRecipeQuery) => {
  return http.get<IPage<DishRecipeRow>>(ADMIN_MODULE + `/dishRecipe`, params);
};

/**
* 添加
* @param params
* @returns {*}
*/
export const createDishRecipeApi = (params: DishRecipeForm) => {
  return http.post(ADMIN_MODULE + `/dishRecipe`, params);
};

/**
* 修改
* @param params
* @returns {*}
*/
export const updateDishRecipeApi = (params: DishRecipeForm) => {
  return http.put(ADMIN_MODULE + `/dishRecipe`, params);
};

/**
* 删除
* @param params
* @returns {*}
*/
export const removeDishRecipeApi = (params: { ids: (string | number)[] }) => {
 return http.delete(ADMIN_MODULE + `/dishRecipe`, params);
};

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getDishRecipeDetailApi = (params: { id: number }) => {
  const { id } = params;
  return http.get<DishRecipeRow>(ADMIN_MODULE + `/dishRecipe/${id}`);
};

/**
* 根据菜品ID查询配方列表
* @param dishId
* @returns {*}
*/
export const getDishRecipeByDishIdApi = (dishId: number) => {
  return http.get<DishRecipeRow[]>(ADMIN_MODULE + `/dishRecipe/byDishId/${dishId}`);
};

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportDishRecipeExcelApi  = (params: DishRecipeQuery) => {
  return http.download(ADMIN_MODULE + `/dishRecipe/export`, params);
};