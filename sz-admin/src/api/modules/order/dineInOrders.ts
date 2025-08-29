import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/types';
import type {
    DineInOrderQuery,
    DineInOrderForm,
    DineInOrderRow
} from '@/api/types/order/dineInOrder';
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
 * 查询列表
 * @param params
 * @returns {*}
 */
export const getDineInOrderListApi = (params: DineInOrderQuery) => {
    return http.get<IPage<DineInOrderRow>>(ADMIN_MODULE + `/dine-in-orders`, params);
};

/**
 * 添加
 * @param params
 * @returns {*}
 */
export const createDineInOrderApi = (params: DineInOrderForm) => {
    return http.post(ADMIN_MODULE + `/dine-in-orders`, params);
};

/**
 * 修改
 * @param params
 * @returns {*}
 */
export const updateDineInOrderApi = (params: DineInOrderForm) => {
    return http.put(ADMIN_MODULE + `/dine-in-orders`, params);
};

/**
 * 修改订单状态
 * @param params
 * @returns {*}
 */
export const updateDineInOrderStatusApi = (params: DineInOrderForm) => {
    return http.put(ADMIN_MODULE + `/dine-in-orders/status`, params);
};


/**
 * 修改支付状态
 * @param params
 * @returns {*}
 */
export const updateDineInOrderPayStatusApi = (params: DineInOrderForm) => {
    return http.put(ADMIN_MODULE + `/dine-in-orders/payStatus`, params);
};


/**
 * 删除
 * @param params
 * @returns {*}
 */
export const removeDineInOrderApi = (params: { ids: (string | number)[] }) => {
    return http.delete(ADMIN_MODULE + `/dine-in-orders`, params);
};

/**
 * 获取详情
 * @param params
 * @returns {*}
 */
export const getDineInOrderDetailApi = (params: { id: number }) => {
    const { id } = params;
    return http.get<DineInOrderRow>(ADMIN_MODULE + `/dine-in-orders/${id}`);
};

/**
 * 导入excel
 * @param params
 */
export const importDineInOrderExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<any> | undefined) => {
    return http.upload(ADMIN_MODULE + `/dine-in-orders/import`, params, config);
};

/**
 * 导出excel
 * @param params
 * @returns {*}
 */
export const exportDineInOrderExcelApi  = (params: DineInOrderQuery) => {
    return http.download(ADMIN_MODULE + `/dine-in-orders/export`, params);
};

export const getDineInOrderByTableIdApi =(params: {tableId:number | undefined}) =>{
    return http.get<IPage<DineInOrderRow>>(ADMIN_MODULE + `/dine-in-orders/getDineInOrderByTableId`, params);
}
