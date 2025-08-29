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
export type DineInOrderQuery = IPageQuery & {
    orderId?:number;
    tableId?: number;
    payStatus?:string;
    status?: string;
};

// 编辑form表单
export type DineInOrderForm = {
    id?:number;
    orderId?: number;
    tableId?: number;
    tableName?: string;
    numberOfGuests?: number;
    remark?: string;
    orderNumber?: string;
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
export type DineInOrderRow = {
   id?:number;
   orderId?: number;
   tableId?:number;
   tableName?: string;
   numberOfGuests?: number;
   remark?: string;
   orderNumber?: string;
   orderType?: string;
   totalAmount?: number;
   status?: string;
   createTime?: string;
   payStatus?:string;
   payTime?:string;
   orderItems?: OrderItem[];
   refundReason?: string;
};
