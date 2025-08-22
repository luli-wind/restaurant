import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/types';
import type {
  ExpenseQuery,
  ExpenseRow,
  ExpenseForm
} from '@/api/types/restaurant/expense';
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getExpenseListApi = (params: ExpenseQuery) => {
  return http.get<IPage<ExpenseRow>>(ADMIN_MODULE + `/expense`, params);
};

/**
* 添加
* @param params
* @returns {*}
*/
export const createExpenseApi = (params: ExpenseForm) => {
  return http.post(ADMIN_MODULE + `/expense`, params);
};

/**
* 修改
* @param params
* @returns {*}
*/
export const updateExpenseApi = (params: ExpenseForm) => {
  return http.put(ADMIN_MODULE + `/expense`, params);
};

/**
* 删除
* @param params
* @returns {*}
*/
export const removeExpenseApi = (params: { ids: (string | number)[] }) => {
 return http.delete(ADMIN_MODULE + `/expense`, params);
};

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getExpenseDetailApi = (params: { id: number }) => {
  const { id } = params;
  return http.get<ExpenseRow>(ADMIN_MODULE + `/expense/${id}`);
};

/**
* 导入excel
* @param params
*/
export const importExpenseExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<any> | undefined) => {
  return http.upload(ADMIN_MODULE + `/expense/import`, params, config);
};

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportExpenseExcelApi  = (params: ExpenseQuery) => {
  return http.download(ADMIN_MODULE + `/expense/export`, params);
};