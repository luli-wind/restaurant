import type { IPageQuery } from '@/api/types';

// 查询条件
export type InventoryQuery = IPageQuery & {
    materialName?: string;
    currentStockStart?: number;
    currentStockEnd?: number;
};

// 编辑form表单
export type InventoryForm = {
    materialName?: string;
    currentStock?: number;
    minStock?: number;
    unit?: string;
};

// list或detail返回结构
export type InventoryRow = {
    materialName?: string;
    currentStock?: number;
    minStock?: number;
    unit?: string;
};

