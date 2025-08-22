<template>
  <div class="table-box">
    <ProTable
      ref="proTableRef"
      title="菜品管理"
      :indent="20"
      :columns="columns"
      :search-columns="searchColumns"
      :request-api="getTableList"
      row-key="dishId"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader="scope">
        <el-button type="primary"
          v-auth="'dish.create'"
          :icon="CirclePlus"
          @click="openAddEdit('新增菜品管理')"
        >
          新增
        </el-button>
        <el-button
          v-auth="'dish.remove'"
          type="danger"
          :icon="Delete"
          plain
          :disabled="!scope.isSelected"
          @click="batchDelete(scope.selectedListIds)"
        >
          批量删除
        </el-button>
        <el-button
          v-auth="'dish.import'"
          type="primary"
          :icon="Upload"
          plain
          @click="importData"
        >
          导入
        </el-button>
        <el-button
          v-auth="'dish.export'"
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
          v-auth="'dish.update'"
          type="primary"
          link
          :icon="EditPen"
          @click="openAddEdit('编辑菜品管理', row, false)"
        >
          编辑
        </el-button>
        <el-button
            v-auth="'dish.remove'"
          type="primary"
          link
          :icon="Delete"
          @click="deleteInfo(row)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <DishForm ref="dishRef" />
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
  createDishApi,
  removeDishApi,
  updateDishApi,
  getDishListApi,
  getDishDetailApi,
  importDishExcelApi,
  exportDishExcelApi,
} from '@/api/modules/restaurant/dish';
import { useHandleData } from '@/hooks/useHandleData';
import DishForm from '@/views/restaurant/dish/components/DishForm.vue';
import { useDictOptions } from '@/hooks/useDictOptions';
import type { ColumnProps, ProTableInstance, SearchProps } from '@/components/ProTable/interface';
import type { DishQuery, DishRow } from '@/api/types/restaurant/dish';
import ImportExcel from '@/components/ImportExcel/index.vue';
import { downloadTemplate } from '@/api/modules/system/common';
import { useDownload } from "@/hooks/useDownload";
import { useDict } from '@/hooks/useDict';
defineOptions({
  name: 'DishView'
});
// 使用useDict Hook 主动加载字典
useDict(['dish_category']);
const proTableRef = ref<ProTableInstance>();
// 表格配置项
const columns: ColumnProps<DishRow>[] = [
  { type: 'selection', width: 80 },
  { prop: 'imageUrl', label: '' },
  { prop: 'dishName', label: '' },
  {
    prop: 'category',
    label: '',
    tag: true,
    enum: useDictOptions('dish_category'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'price', label: '' },
  { prop: 'description', label: '' },
  { prop: 'operation', label: '操作', width: 250, fixed: 'right' }
];
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'dishName', label: '', el: 'input' },
  {
    prop: 'category',
    label: '',
    el: 'select',
    enum: useDictOptions('dish_category'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    },
  },
  { prop: 'price', label: '', el: 'input' },
];
// 获取table列表
const getTableList = (params: DishQuery) => {
  let newParams = formatParams(params);
  return getDishListApi(newParams);
};
const formatParams = (params: DishQuery) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  if(newParams.price) {
    newParams.priceStart = newParams.price[0];
    newParams.priceEnd = newParams.price[1];
    delete newParams.price;
  }

  return newParams;
};
// 打开 drawer(新增、查看、编辑)
const dishRef = ref<InstanceType<typeof DishForm>>();
const openAddEdit = async(title: string, row: any = {}, isAdd = true) => {
  if (!isAdd) {
    const record = await getDishDetailApi({ id: row?.dishId });
    row = record?.data;
  }
  const params = {
    title,
    row: { ...row },
    api: isAdd ? createDishApi : updateDishApi,
    getTableList: proTableRef.value?.getTableList
  };
  dishRef.value?.acceptParams(params);
}
// 删除信息
const deleteInfo = async (params: DishRow) => {
  await useHandleData(removeDishApi, { ids: [params.dishId] }, `删除【${params.dishId}】菜品管理`);
  proTableRef.value?.getTableList();
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeDishApi, { ids }, '删除所选菜品管理');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>();
const importData = () => {
  const params = {
    title: '菜品管理',
    templateName: '菜品管理',
    tempApi: downloadTemplate,
    importApi: importDishExcelApi,
    getTableList: proTableRef.value?.getTableList
  };
  ImportExcelRef.value?.acceptParams(params);
};
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as DishQuery);
  useDownload(exportDishExcelApi, "菜品管理", newParams);
};
</script>