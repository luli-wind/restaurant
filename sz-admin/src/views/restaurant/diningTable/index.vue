<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="餐桌管理"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="tableId"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'dining.table.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增餐桌管理')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'dining.table.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'dining.table.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'dining.table.export'"
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
          v-auth="'dining.table.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑餐桌管理', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'dining.table.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <DiningTableForm ref="diningTableRef" />
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
  createDiningTableApi,
  removeDiningTableApi,
  updateDiningTableApi,
  getDiningTableListApi,
  getDiningTableDetailApi,
  importDiningTableExcelApi,
  exportDiningTableExcelApi,
} from '@/api/modules/restaurant/diningTable';
import { useHandleData } from '@/hooks/useHandleData';
import DiningTableForm from '@/views/restaurant/diningTable/components/DiningTableForm.vue';
import { useDictOptions } from '@/hooks/useDictOptions';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/ProTable/interface';
import type { DiningTableQuery, DiningTableRow } from '@/api/types/restaurant/diningTable';
import ImportExcel from '@/components/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/common';
import { useDownload } from "@/hooks/useDownload";
import { useDict } from '@/hooks/useDict';
defineOptions({
  name: 'DiningTableView'
});
// 使用useDict Hook 主动加载字典
useDict(['table_status']);
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<DiningTableRow>[] = [
  { type: 'selection', width: 80 },
  { prop: 'tableName', label: '餐桌编号' },
  { prop: 'capacity', label: '容量' },
  {
    prop: 'status',
    label: '状态',
    tag: true,
    enum: useDictOptions('table_status'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
];
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'tableName', label: '餐桌编号', el: 'input' },
  { prop: 'capacity', label: '容量', el: 'input' },
  {
    prop: 'status',
    label: '状态',
    el: 'select',
    enum: useDictOptions('table_status'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    },
  },
];
// 获取table列表
const getTableList = (params: DiningTableQuery) => {
  let newParams = formatParams(params);
  return getDiningTableListApi(newParams);
};
const formatParams = (params: DiningTableQuery) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  return newParams;
};
// 打开 drawer(新增、查看、编辑)
const diningTableRef = ref<InstanceType<typeof DiningTableForm>>();
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    if (!row?.tableId) {
      console.error('Invalid tableId:', row);
      return;
    }
    const record = await getDiningTableDetailApi({ id: row.tableId });
    row = record?.data;
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createDiningTableApi : updateDiningTableApi,
    getTableList: proTableRef.value?.getTableList
  };
  diningTableRef.value?.acceptParams(params);
}
// 删除信息
const deleteInfo = async (params: DiningTableRow) => {
  await useHandleData(removeDiningTableApi, { ids: [params.tableId] }, `删除【${params.tableId}】餐桌管理`);
  proTableRef.value?.getTableList();
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeDiningTableApi, { ids }, '删除所选餐桌管理');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>();
const importData = () => {
  const params = {
    title: '餐桌管理',
    templateName: '餐桌管理',
    tempApi: downloadTemplate,
    importApi: importDiningTableExcelApi,
    getTableList: proTableRef.value?.getTableList
  };
  ImportExcelRef.value?.acceptParams(params);
};
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as DiningTableQuery);
  useDownload(exportDiningTableExcelApi, "餐桌管理", newParams);
};
</script>