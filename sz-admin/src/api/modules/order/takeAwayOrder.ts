import http from '@/api';
import guestHttp from '@/api/guestHttp';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type {IPage, IResultData} from '@/api/types';
import type {
    TakeAwayOrderQuery,
    TakeAwayOrderRow,
    TakeAwayOrderForm
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
// export const createTakeAwayOrderApi = (params: TakeAwayOrderForm) => {
//     return http.post(ADMIN_MODULE + `/takeaway-orders`, params);
// };

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

/**
 * 创建访客外卖订单
 * @param params
 * @returns {*}
 */
export const createTakeAwayOrderApi = (params: TakeAwayOrderForm) => {
    return http.post<IResultData<any>>(ADMIN_MODULE + `/takeaway-orders/guest`, params);
};

/**
 * 分页查询外卖订单
 * @param params
 * @returns {*}
 */
export const pageTakeAwayOrderApi = (params: TakeAwayOrderQuery) => {
    return http.get<IResultData<IPage<any>>>(ADMIN_MODULE + `/takeaway-orders/page`, params);
};

/**
 * 根据第三方用户ID查询订单
 * @param params
 * @returns {*}
 */
export const getTakeAwayOrdersByThirdPartyUserApi = (params: { thirdPartyUserId: string }) => {
    return http.get<IResultData<IPage<any>>>(ADMIN_MODULE + `/takeaway-orders/third-party-user`, params);
};
