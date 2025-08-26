import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type { IPage } from '@/api/types';
import type {
    DineInOrderQuery,
    DineInOrderRow
} from '@/api/types/order/dineInOrder';
import type {
    TakeAwayOrderQuery,
    TakeAwayOrderRow
} from '@/api/types/order/takeAwayOrder';
import {
    getDineInOrderListApi,
    getDineInOrderDetailApi,
    updateDineInOrderStatusApi
} from './dineInOrders';
import {
    getTakeAwayOrderListApi,
    getTakeAwayOrderDetailApi,
    updateTakeAwayOrderStatusApi
} from './takeAwayOrder';

// 定义联合类型
export type ChefOrderRow = DineInOrderRow | TakeAwayOrderRow;
export type ChefOrderQuery = DineInOrderQuery & TakeAwayOrderQuery;

/**
 * 查询厨师订单列表
 * @param params
 * @returns {*}
 */
export const getChefOrderListApi = async (params: ChefOrderQuery & { orderType: 'dineIn' | 'takeaway' }) => {
    // 根据订单类型调用不同的接口
    if (params.orderType === 'dineIn') {
        // 调用堂食订单接口
        const dineInParams = {
            ...params,
            orderType: undefined // 移除orderType参数，避免传递给后端
        };
        return await getDineInOrderListApi(dineInParams);
    } else if (params.orderType === 'takeaway') {
        // 调用外卖订单接口
        const takeAwayParams = {
            ...params,
            orderType: undefined // 移除orderType参数，避免传递给后端
        };
        return await getTakeAwayOrderListApi(takeAwayParams);
    } else {
        // 如果没有指定订单类型，抛出错误
        throw new Error('请指定订单类型: dineIn 或 takeaway');
    }
};

/**
 * 获取订单详情
 * @param params
 * @returns {*}
 */
export const getChefOrderDetailApi = async (params: { id: number, orderType: 'dineIn' | 'takeaway' }) => {
    const { id, orderType } = params;
    
    // 根据订单类型调用不同的接口
    if (orderType === 'dineIn') {
        // 调用堂食订单详情接口
        return await getDineInOrderDetailApi({ id });
    } else if (orderType === 'takeaway') {
        // 调用外卖订单详情接口
        return await getTakeAwayOrderDetailApi({ id });
    } else {
        throw new Error('无效的订单类型');
    }
};

/**
 * 更新订单状态为制作中
 * @param params
 * @returns {*}
 */
export const startCookingApi = async (params: { id: number, orderType: 'dineIn' | 'takeaway' }) => {
    const { id, orderType } = params;
    
    // 根据订单类型调用不同的接口
    if (orderType === 'dineIn') {
        // 调用堂食订单更新状态接口
        return await updateDineInOrderStatusApi({
            id,
            status: 'processing'
        } as any);
    } else if (orderType === 'takeaway') {
        // 调用外卖订单更新状态接口
        return await updateTakeAwayOrderStatusApi({
            id,
            status: 'processing'
        } as any);
    } else {
        throw new Error('无效的订单类型');
    }
};

/**
 * 更新订单状态为已完成
 * @param params
 * @returns {*}
 */
export const finishCookingApi = async (params: { id: number, orderType: 'dineIn' | 'takeaway' }) => {
    const { id, orderType } = params;
    
    // 根据订单类型调用不同的接口
    if (orderType === 'dineIn') {
        // 调用堂食订单更新状态接口
        return await updateDineInOrderStatusApi({
            id,
            status: 'completed'
        } as any);
    } else if (orderType === 'takeaway') {
        // 调用外卖订单更新状态接口
        return await updateTakeAwayOrderStatusApi({
            id,
            status: 'completed'
        } as any);
    } else {
        throw new Error('无效的订单类型');
    }
};