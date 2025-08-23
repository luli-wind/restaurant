import type { IPageQuery } from '@/api/types';

// 查询条件
export type DishQuery = IPageQuery & {
    dishName?: string;
    category?: string;
    priceStart?: number;
    priceEnd?: number;
};

// 编辑form表单
export type DishForm = {
    dishId?: number;
    imageUrl?: string;
    dishName?: string;
    category?: string;
    price?: number;
    description?: string;
};

// list或detail返回结构
export type DishRow = {
    dishId:number;
    imageUrl?: string;
    dishName?: string;
    category?: string;
    price?: number;
    description?: string;
};

