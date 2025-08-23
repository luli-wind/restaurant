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
          v-auth="'dish.recipe'"
          type="primary"
          link
          :icon="List"
          @click="openRecipeManage(row)"
        >
          配方
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
    <DishRecipeManage ref="dishRecipeRef" />
    <ImportExcel ref="ImportExcelRef" />
  </div>
</template>

<script setup lang="ts">
import { ref,h } from 'vue';
import {
  CirclePlus,
  Delete,
  EditPen,
  Upload,
  Download,
  List
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
import DishRecipeManage from '@/views/restaurant/dish/components/DishRecipeManage.vue';
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
  { prop: 'imageUrl', label: '图片',
    // 使用默认插槽自定义内容
    render: (scope) => h('div', {
      class: 'image-container' // 添加容器类名
    }, [
      h('img', {
        style: 'width: 50px; height: 50px; object-fit: cover; border-radius: 4px;',
        src: scope.row.imageUrl,
        onError: (e) => {
        }
      }),
      h('span', {
        style: 'display: none; font-size: 12px; color: #999;',
        class: 'image-fallback'
      }, '加载失败')
    ])
  },
  { prop: 'dishName', label: '菜品名称' },
  {
    prop: 'category',
    label: '分类',
    tag: true,
    enum: useDictOptions('dish_category'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'price', label: '价格' },
  { prop: 'description', label: '描述' },
  { prop: 'operation', label: '操作', width: 300, fixed: 'right' }
];
// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'dishName', label: '菜品名称', el: 'input' },
  {
    prop: 'category',
    label: '分类',
    el: 'select',
    enum: useDictOptions('dish_category'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    },
  },
  { prop: 'price', label: '价格', el: 'input' },
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
// 打开配方管理
const dishRecipeRef = ref<InstanceType<typeof DishRecipeManage>>();
const openRecipeManage = async(row: any) => {
  const params = {
    title: '配方管理',
    dishId: row?.dishId,
    dishName: row?.dishName
  };
  dishRecipeRef.value?.acceptParams(params);
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