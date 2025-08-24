import type { IPageQuery } from '@/api/types';

// 查询条件
export type DineInOrderQuery = IPageQuery & {
    tableName?: string;
    payStatus?:string;
    status?: string;
};

// 编辑form表单
export type DineInOrderForm = {
    tableName?: string;
    numberOfGuests?: number;
    remark?: string;
    orderNumber?: number;
    orderType?: string;
    totalAmount?: number;
    status?: string;
    createTime?: string;
    payStatus?:string;
    payTime?:string
};

// list或detail返回结构
export type DineInOrderRow = {
   id?:number;
   orderId?: number;
   tableId?:number;
   tableName?: string;
   numberOfGuests?: number;
   remark?: string;
   orderNumber?: number;
   orderType?: string;
   totalAmount?: number;
   status?: string;
   createTime?: string;
   payStatus?:string
   payTime?:string
};


