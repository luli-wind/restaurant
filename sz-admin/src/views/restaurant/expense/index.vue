<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="费用管理"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="expenseId"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'expense.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增费用管理')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'expense.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'expense.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'expense.export'"
          type="primary"
          :icon="Download"
          plain
          @click="downloadFile"
        >
          导出
        </el-button>
      </template>
      <template #operation="{ row }">
        <el-button
          v-auth="'expense.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑费用管理', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'expense.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <ExpenseForm ref="expenseRef" />
    <ImportExcel ref="ImportExcelRef" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
} from '@element-plus/icons-vue';
import ProTable from '@/components/ProTable/index.vue';
import {
  createExpenseApi,
  removeExpenseApi,
  updateExpenseApi,
  getExpenseListApi,
  getExpenseDetailApi,
  importExpenseExcelApi,
  exportExpenseExcelApi,
} from '@/api/modules/restaurant/expense';
import { useHandleData } from '@/hooks/useHandleData';
import ExpenseForm from '@/views/restaurant/expense/components/ExpenseForm.vue';
import { useDictOptions } from '@/hooks/useDictOptions';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/ProTable/interface';
import type { ExpenseQuery, ExpenseRow } from '@/api/types/restaurant/expense';
import ImportExcel from '@/components/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/common';
import { useDownload } from "@/hooks/useDownload";
import { useDict } from '@/hooks/useDict';
defineOptions({
  name: 'ExpenseView'
});
// 使用useDict Hook 主动加载字典
useDict(['expense_category']);
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<ExpenseRow>[] = [
  { type: 'selection', width: 80 },
  {
    prop: 'expenseCategory',
    label: '费用种类',
    tag: true,
    enum: useDictOptions('expense_category'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'amount', label: '费用' },
  { prop: 'expenseDate', label: '日期' },
  { prop: 'description', label: '描述' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
];
// 搜索条件项
const searchColumns: SearchProps[] = [
  {
    prop: 'expenseCategory',
    label: '费用种类',
    el: 'select',
    enum: useDictOptions('expense_category'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    },
  },
  { prop: 'amount', label: '费用', el: 'input' },
  { prop: 'expenseDate', label: '日期', el: 'input' },
];
// 获取table列表
const getTableList = (params: ExpenseQuery) => {
  let newParams = formatParams(params);
  return getExpenseListApi(newParams);
};
const formatParams = (params: ExpenseQuery) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  if(newParams.amount) {
    newParams.amountStart = newParams.amount[0];
    newParams.amountEnd = newParams.amount[1];
    delete newParams.amount;
  }

  if(newParams.expenseDate) {
    newParams.expenseDateStart = newParams.expenseDate[0];
    newParams.expenseDateEnd = newParams.expenseDate[1];
    delete newParams.expenseDate;
  }

  return newParams;
};
// 打开 drawer(新增、查看、编辑)
const expenseRef = ref<InstanceType<typeof ExpenseForm>>();
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getExpenseDetailApi({ id: row?.expenseId });
    row = record?.data;
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createExpenseApi : updateExpenseApi,
    getTableList: proTableRef.value?.getTableList
  };
  expenseRef.value?.acceptParams(params);
}
// 删除信息
const deleteInfo = async (params: ExpenseRow) => {
  await useHandleData(removeExpenseApi, { ids: [params.expenseId] }, `删除【${params.expenseId}】费用管理`);
  proTableRef.value?.getTableList();
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeExpenseApi, { ids }, '删除所选费用管理');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>();
const importData = () => {
  const params = {
    title: '费用管理',
    templateName: '费用管理',
    tempApi: downloadTemplate,
    importApi: importExpenseExcelApi,
    getTableList: proTableRef.value?.getTableList
  };
  ImportExcelRef.value?.acceptParams(params);
};
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as ExpenseQuery);
  useDownload(exportExpenseExcelApi, "费用管理", newParams);
};
</script>