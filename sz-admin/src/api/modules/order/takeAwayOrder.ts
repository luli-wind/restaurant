import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/types';
import type {
    TakeAwayOrderForm,
    TakeAwayOrderQuery,
    TakeAwayOrderRow
} from '@/api/types/order/takeAwayOrder'
import type { UploadRawFile } from "element-plus/es/components/upload/src/upload";
import type { AxiosRequestConfig } from 'axios';

/**
 * 查询列表
 * @param params
 * @returns {*}
 */
export const getTakeAwayOrderListApi = (params: TakeAwayOrderQuery) => {
    return http.get<IPage<TakeAwayOrderRow>>(ADMIN_MODULE + `/takeaway-orders`, params);
};

/**
 * 添加
 * @param params
 * @returns {*}
 */
export const createTakeAwayOrderApi = (params: TakeAwayOrderForm) => {
    return http.post(ADMIN_MODULE + `/takeaway-orders`, params);
};

/**
 * 修改
 * @param params
 * @returns {*}
 */
export const updateTakeAwayOrderApi = (params: TakeAwayOrderForm) => {
    return http.put(ADMIN_MODULE + `/takeaway-orders`, params);
};

/**
 * 修改订单状态
 * @param params
 * @returns {*}
 */
export const updateTakeAwayOrderStatusApi = (params: TakeAwayOrderForm) => {
    return http.put(ADMIN_MODULE + `/takeaway-orders/status`, params);
};


/**
 * 修改支付状态
 * @param params
 * @returns {*}
 */
export const updateTakeAwayOrderPayStatusApi = (params: TakeAwayOrderForm) => {
    return http.put(ADMIN_MODULE + `/takeaway-orders/payStatus`, params);
};


/**
 * 删除
 * @param params
 * @returns {*}
 */
export const removeTakeAwayOrderApi = (params: { ids: (string | number)[] }) => {
    return http.delete(ADMIN_MODULE + `/takeaway-orders`, params);
};

/**
 * 获取详情
 * @param params
 * @returns {*}
 */
export const getTakeAwayOrderDetailApi = (params: { id: number }) => {
    const { id } = params;
    return http.get<TakeAwayOrderRow>(ADMIN_MODULE + `/takeaway-orders/${id}`);
};

/**
 * 导入excel
 * @param params
 */
export const importTakeAwayOrderExcelApi = (params : UploadRawFile, config?: AxiosRequestConfig<any> | undefined) => {
    return http.upload(ADMIN_MODULE + `/takeaway-orders/import`, params, config);
};

/**
 * 导出excel
 * @param params
 * @returns {*}
 */
export const exportTakeAwayOrderExcelApi  = (params: TakeAwayOrderQuery) => {
    return http.download(ADMIN_MODULE + `/takeaway-orders/export`, params);
};
