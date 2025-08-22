import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/types';
import type {
  DiningTableQuery,
  DiningTableRow,
  DiningTableForm
} from '@/api/types/restaurant/diningTable';
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
* 查询列表
* @param params
* @returns {*}
*/
export const getDiningTableListApi = (params: DiningTableQuery) => {
  return http.get<IPage<DiningTableRow>>(ADMIN_MODULE + `/dining-table`, params);
};

/**
* 添加
* @param params
* @returns {*}
*/
export const createDiningTableApi = (params: DiningTableForm) => {
  return http.post(ADMIN_MODULE + `/dining-table`, params);
};

/**
* 修改
* @param params
* @returns {*}
*/
export const updateDiningTableApi = (params: DiningTableForm) => {
  return http.put(ADMIN_MODULE + `/dining-table`, params);
};

/**
* 删除
* @param params
* @returns {*}
*/
export const removeDiningTableApi = (params: { ids: (string | number)[] }) => {
 return http.delete(ADMIN_MODULE + `/dining-table`, params);
};

/**
* 获取详情
* @param params
* @returns {*}
*/
export const getDiningTableDetailApi = (params: { id: number }) => {
  const { id } = params;
  return http.get<DiningTableRow>(ADMIN_MODULE + `/dining-table/${id}`);
};

/**
* 导入excel
* @param params
*/
export const importDiningTableExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<any> | undefined) => {
  return http.upload(ADMIN_MODULE + `/dining-table/import`, params, config);
};

/**
* 导出excel
* @param params
* @returns {*}
*/
export const exportDiningTableExcelApi  = (params: DiningTableQuery) => {
  return http.download(ADMIN_MODULE + `/dining-table/export`, params);
};