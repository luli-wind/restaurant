<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="库存管理"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="materialId"
      :row-class-name="tableRowClassName"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'inventory.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增库存管理')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'inventory.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'inventory.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'inventory.export'"
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
          v-auth="'inventory.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑库存管理', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'inventory.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <InventoryForm ref="inventoryRef" />
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
  createInventoryApi,
  removeInventoryApi,
  updateInventoryApi,
  getInventoryListApi,
  getInventoryDetailApi,
  importInventoryExcelApi,
  exportInventoryExcelApi,
} from '@/api/modules/restaurant/inventory';
import { useHandleData } from '@/hooks/useHandleData';
import InventoryForm from '@/views/restaurant/inventory/components/InventoryForm.vue';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/ProTable/interface';
import type { InventoryQuery, InventoryRow } from '@/api/types/restaurant/inventory';
import ImportExcel from '@/components/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/common';
import { useDownload } from "@/hooks/useDownload";
import { useDict } from '@/hooks/useDict';
import {useDictOptions} from "@/hooks/useDictOptions";
defineOptions({
  name: 'InventoryView'
});
const proTableRef = ref<ProTableInstance>();
useDict(['inventory_status'])
// 表格配置项
const columns: ColumnProps<InventoryRow>[] = [
  { type: 'selection', width: 80 },
  { prop: 'materialName', label: '材料名' },
  { prop: 'currentStock', label: '当前容量' },
  { prop: 'minStock', label: '最小容量' },
  { prop: 'unit', label: '计量单位' },
  {prop:'status',
    label:'库存状态',
    tag: true,
    enum: useDictOptions('inventory_status'),
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
  { prop: 'materialName', label: '材料名', el: 'input' },
  { prop: 'status',
    label: '库存状态',
    el: 'select',
    enum: useDictOptions('inventory_status'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    },
  },
];
// 获取table列表
const getTableList = (params: InventoryQuery) => {
  let newParams = formatParams(params);
  return getInventoryListApi(newParams);
};
const formatParams = (params: InventoryQuery) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  if(newParams.currentStock) {
    newParams.currentStockStart = newParams.currentStock[0];
    newParams.currentStockEnd = newParams.currentStock[1];
    delete newParams.currentStock;
  }

  return newParams;
};
// 打开 drawer(新增、查看、编辑)
const inventoryRef = ref<InstanceType<typeof InventoryForm>>();
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getInventoryDetailApi({ id: row?.materialId });
    row = record?.data;
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createInventoryApi : updateInventoryApi,
    getTableList: proTableRef.value?.getTableList
  };
  inventoryRef.value?.acceptParams(params);
}
// 删除信息
const deleteInfo = async (params: InventoryRow) => {
  await useHandleData(removeInventoryApi, { ids: [params.materialId] }, `删除【${params.materialId}】库存管理`);
  proTableRef.value?.getTableList();
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeInventoryApi, { ids }, '删除所选库存管理');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>();
const importData = () => {
  const params = {
    title: '库存管理',
    templateName: '库存管理',
    tempApi: downloadTemplate,
    importApi: importInventoryExcelApi,
    getTableList: proTableRef.value?.getTableList
  };
  ImportExcelRef.value?.acceptParams(params);
};
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as InventoryQuery);
  useDownload(exportInventoryExcelApi, "库存管理", newParams);
};

// 表格行样式处理函数
const tableRowClassName = ({ row }: { row: InventoryRow }) => {
  // 判断当前库存是否小于最少库存
  if (row.currentStock !== undefined && row.minStock !== undefined && row.currentStock <= row.minStock) {
    return 'warning-row';
  }
  return '';
};
</script>

<style scoped>
:deep(.warning-row) {
  background-color: #fef0f0 !important;
  color: #f56c6c !important;
}
</style>