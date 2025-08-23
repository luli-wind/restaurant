<template>
  <div class="table-box">
        <ProTable 
          ref="proTableRef"
          :columns="columns"
          :indent="20"
          :request-api="getDineInOrderListApi"
          :search-columns="searchColumns"
          :data-callback="dataCallback"
          row-key="id"
        >
          <!-- 表格 header 按钮 -->
          <template #tableHeader="scope">
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
          <!-- 自定义操作列 -->
          <template #operation="{ row }">
            <el-button size="small" @click="viewOrder(row)">查看</el-button>
          </template>
        </ProTable>

    
    <!-- 订单详情对话框 -->
    <el-dialog v-model="dialogVisible" title="订单详情" width="600px">
      <el-form :model="currentOrder" label-width="100px">
        <el-form-item label="订单号:"><span>{{ currentOrder.orderNumber }}</span></el-form-item>
        <el-form-item label="桌号:"><span>{{ currentOrder.tableName }}</span></el-form-item>
        <el-form-item label="人数:"><span>{{ currentOrder.numberOfGuests }}</span></el-form-item>
        <el-form-item label="金额:"><span>¥{{ currentOrder.totalAmount }}</span></el-form-item>
        <el-form-item label="状态:">

        </el-form-item>
        <el-form-item label="下单时间:"><span>{{ currentOrder.createTime }}</span></el-form-item>
        <el-form-item v-if="currentOrder.payTime" label="完成时间:"><span>{{ currentOrder.payTime }}</span></el-form-item>
        <el-form-item v-if="currentOrder.remark" label="备注:"><span>{{ currentOrder.remark }}</span></el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="tsx">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProTable from '@/components/ProTable/index.vue'
import { useDictOptions } from '@/hooks/useDictOptions';
import type {DineInOrderQuery, DineInOrderRow} from '@/api/types/order/dineInOrder'
import type { ColumnProps,ProTableInstance, SearchProps } from '@/components/ProTable/interface'
import { useDict } from '@/hooks/useDict';
import {getDineInOrderListApi,
        createDineInOrderApi,
        updateDineInOrderApi,
        removeDineInOrderApi,
        getDineInOrderDetailApi,
        exportDineInOrderExcelApi,
        importDineInOrderExcelApi
} from "@/api/modules/order/dineInOrders";
import {CirclePlus, Delete, Download, Upload} from "@element-plus/icons-vue";
import {useHandleData} from "@/hooks/useHandleData";
import {importDishExcelApi, removeDishApi} from "@/api/modules/restaurant/dish";
import type ImportExcel from "@/components/ImportExcel/index.vue";
import {downloadTemplate} from "@/api/modules/system/common";
import {useDownload} from "@/hooks/useDownload";
import type {DiningTableQuery} from "@/api/types/restaurant/diningTable";

useDict(['dine_in_order_status']);
// 定义响应式数据
const dialogVisible = ref(false)
const currentOrder = ref<DineInOrderRow>({})
const proTableRef = ref()

// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'tableName', label: '桌子编号', el: 'input' },
  {
    prop: 'status',
    label: '状态',
    el: 'select',
    enum: useDictOptions('dine_in_order_status'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  }
];

// 表格列配置
const columns: ColumnProps[] = [
  { prop: 'orderNumber', label: '订单号' },
  { prop: 'tableName', label: '桌号' },
  { prop: 'numberOfGuests', label: '人数' },
  { 
    prop: 'totalAmount', 
    label: '金额',
    render: ({ row }) => `¥${row.totalAmount}`
  },
  {
    prop: 'status',
    label: '状态',
    enum: useDictOptions('dine_in_order_status'),
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'createTime', label: '下单时间' },
  {prop:'payStatus', label:'支付状态'},
  {prop:'payTime',label:'支付时间'},
  { prop: 'operation', label: '操作' }
]


// 数据回调函数
const dataCallback = (data: any) => {
  return data
}

// 查看订单详情
const viewOrder = (row: DineInOrderRow) => {
  currentOrder.value = { ...row }
  dialogVisible.value = true
}



// 取消订单
const cancelOrder = (row: DineInOrderRow) => {
  ElMessageBox.confirm('确认取消该订单吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用实际的API接口取消订单
    row.status = '已取消'
    ElMessage.success('订单已取消')
    // 刷新表格数据
    if (proTableRef.value) {
      proTableRef.value.getTableList()
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 组件挂载时获取数据
onMounted(() => {
  if (proTableRef.value) {
    proTableRef.value.getTableList()
  }
})

const formatParams = (params: DineInOrderQuery) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  return newParams;
};

// 删除信息
const deleteInfo = async (params:DineInOrderRow ) => {
  await useHandleData(removeDineInOrderApi, { ids: [params.id] }, `删除【${params.id}】堂食订单`);
  proTableRef.value?.getTableList();
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeDineInOrderApi, { ids }, '删除所选堂食订单');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>();
const importData = () => {
  const params = {
    title: '堂食订单',
    templateName: '堂食订单',
    tempApi: downloadTemplate,
    importApi: importDineInOrderExcelApi,
    getTableList: proTableRef.value?.getTableList
  };
  ImportExcelRef.value?.acceptParams(params);
};
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as DineInOrderQuery);
  useDownload(exportDineInOrderExcelApi, "堂食订单", newParams);
};
</script>

<style scoped lang="scss">


</style>