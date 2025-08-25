import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type {
    OrderDetailRow
} from '@/api/types/order/orderDetail';

import type { AxiosRequestConfig } from 'axios';


/**
 * 获取订单详情列表
 * @param params
 * @returns {*}
 */
export const getOrderDetailListApi = (params: { orderId: number | string }) => {
    const { orderId } = params;
    return http.get<OrderDetailRow>(ADMIN_MODULE + `/order-detail/${orderId}`);
};