import type { IPageQuery } from '@/api/types';

// 查询条件
export type ExpenseQuery = IPageQuery & {
    expenseCategory?: number;
    amountStart?: number;
    amountEnd?: number;
    expenseDateStart?: string;
    expenseDateEnd?: string;
};

// 编辑form表单
export type ExpenseForm = {
    expenseCategory?: number;
    amount?: number;
    expenseDate?: string;
    description?: string;
};

// list或detail返回结构
export type ExpenseRow = {
    expenseCategory?: number;
    amount?: number;
    expenseDate?: string;
    description?: string;
};

