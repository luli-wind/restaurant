import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/types';
import type {
  InventoryQuery,
  InventoryRow,
  InventoryForm
} from '@/api/types/restaurant/inventory';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getInventoryListApi = (params: InventoryQuery) => {
  return http.get<IPage<InventoryRow>>(ADMIN_MODULE + `/inventory`, params);
};

/**
* 添加
* @param params
* @returns {*}
*/
export const createInventoryApi = (params: InventoryForm) => {
  return http.post(ADMIN_MODULE + `/inventory`, params);
};

/**
* 修改
* @param params
* @returns {*}
*/
export const updateInventoryApi = (params: InventoryForm) => {
  return http.put(ADMIN_MODULE + `/inventory`, params);
};

/**
* 删除
* @param params
* @returns {*}
*/
export const removeInventoryApi = (params: { ids: (string | number)[] }) => {
 return http.delete(ADMIN_MODULE + `/inventory`, params);
};

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getInventoryDetailApi = (params: { id: number }) => {
  const { id } = params;
  return http.get<InventoryRow>(ADMIN_MODULE + `/inventory/${id}`);
};

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportInventoryExcelApi  = (params: InventoryQuery) => {
  return http.download(ADMIN_MODULE + `/inventory/export`, params);
};