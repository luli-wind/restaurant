import type { IPageQuery } from '@/api/types';

// 查询条件
export type DishRecipeQuery = IPageQuery & {
    dishId?: number;
    materialId?: number;
};

// 编辑form表单
export type DishRecipeForm = {
    id?: number;
    dishId?: number;
    materialId?: number;
    materialQuantity?: number;
};

// list或detail返回结构
export type DishRecipeRow = {
    id: number;
    dishId?: number;
    materialId?: number;
    materialQuantity?: number;
    materialName?: string;
    unit?: string;
    createTime?: string;
    updateTime?: string;
};