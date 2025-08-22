import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/types';
import type {
  DishQuery,
  DishRow,
  DishForm
} from '@/api/types/restaurant/dish';
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getDishListApi = (params: DishQuery) => {
  return http.get<IPage<DishRow>>(ADMIN_MODULE + `/dish`, params);
};

/**
* 添加
* @param params
* @returns {*}
*/
export const createDishApi = (params: DishForm) => {
  return http.post(ADMIN_MODULE + `/dish`, params);
};

/**
* 修改
* @param params
* @returns {*}
*/
export const updateDishApi = (params: DishForm) => {
  return http.put(ADMIN_MODULE + `/dish`, params);
};

/**
* 删除
* @param params
* @returns {*}
*/
export const removeDishApi = (params: { ids: (string | number)[] }) => {
 return http.delete(ADMIN_MODULE + `/dish`, params);
};

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getDishDetailApi = (params: { id: number }) => {
  const { id } = params;
  return http.get<DishRow>(ADMIN_MODULE + `/dish/${id}`);
};

/**
* 导入excel
* @param params
*/
export const importDishExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<any> | undefined) => {
  return http.upload(ADMIN_MODULE + `/dish/import`, params, config);
};

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportDishExcelApi  = (params: DishQuery) => {
  return http.download(ADMIN_MODULE + `/dish/export`, params);
};