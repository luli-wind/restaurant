import http from '@/api/guestHttp';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { TakeAwayOrderForm, TakeAwayOrderRow } from '@/api/types/order/takeAwayOrder'

/**
 * 访客创建外卖订单
 * @param params
 * @returns {*}
 */
export const createGuestTakeAwayOrderApi = (params: TakeAwayOrderForm) => {
    return http.post<TakeAwayOrderRow>(ADMIN_MODULE + `/takeaway-orders/guest`, params);
};