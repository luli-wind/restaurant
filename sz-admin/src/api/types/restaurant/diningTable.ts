import type { IPageQuery } from '@/api/types';

// 查询条件
export type DiningTableQuery = IPageQuery & {
    tableName?: string;
    capacity?: number;
    status?: string;
};

// 编辑form表单
export type DiningTableForm = {
    tableName?: string;
    capacity?: number;
    status?: string;
};

// list或detail返回结构
export type DiningTableRow = {
    tableId?: number;
    tableName?: string;
    capacity?: number;
    status?: string;
};

