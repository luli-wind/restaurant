import type { IPageQuery } from '@/api/types';

// 订单项类型
export type OrderItem = {
    dishId: number;
    dishName: string;
    imageUrl: string;
    number: number;
    amount: number;
};

// 查询条件
export type TakeAwayOrderQuery = IPageQuery & {
    customerName?: string;
    payStatus?:string;
    status?: string;
    customerPhone?:string
    deliveryAddress?:string
};

// 编辑form表单
export type TakeAwayOrderForm = {
    id?:number;
    orderId?: number;
    customerName?:string;
    customerPhone?:string;
    deliveryAddress?:string;
    packagingFee?:number;
    deliveryFee?:number;
    remark?: string;
    orderNumber?: number;
    orderType?: string;
    totalAmount?: number;
    status?: string;
    createTime?: string;
    payStatus?:string;
    payTime?:string;
    orderItems?: OrderItem[];
    refundReason?: string;
};

// list或detail返回结构
export type TakeAwayOrderRow = {
    id?:number;
    orderId?: number;
    customerName?:string;
    customerPhone?:string;
    deliveryAddress?:string;
    packagingFee?:number;
    deliveryFee?:number;
    remark?: string;
    orderNumber?: number;
    orderType?: string;
    totalAmount?: number;
    status?: string;
    createTime?: string;
    payStatus?:string;
    payTime?:string;
    orderItems?: OrderItem[];
    refundReason?: string;
};