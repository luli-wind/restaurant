import type { IPageQuery } from '@/api/types';

// 查询条件
export type InventoryQuery = IPageQuery & {
    materialName?: string;
    unit?: string;
    status?:string;
};

// 编辑form表单
export type InventoryForm = {
    materialId?: number;
    materialName?: string;
    currentStock?: number;
    minStock?: number;
    unit?: string;
    status?:string
};

// list或detail返回结构
export type InventoryRow = {
    materialId: number;
    materialName?: string;
    currentStock?: number;
    minStock?: number;
    unit?: string;
    status?:string
};
