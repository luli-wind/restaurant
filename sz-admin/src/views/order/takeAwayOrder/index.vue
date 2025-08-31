<template>
  <div class="table-box">
        <ProTable 
          ref="proTableRef"
          :columns="columns"
          :indent="20"
          :request-api="getTakeAwayOrderListApi"
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
            <el-button type="primary" :icon="EditPen" link @click="viewOrderDetail(row)">订单详情</el-button>
          </template>
        </ProTable>

    
    <!-- 新的订单详情组件 -->
    <OrderDetail
      v-model="orderDetailVisible"
      :order-data="currentOrder"
      :get-status-name="getStatusName"
      :get-pay-status-name="getPayStatusName"
      @update-status="updateStatus"
      @update-pay-status="updatePayStatus"
    />
  </div>
</template>

<script setup lang="tsx">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProTable from '@/components/ProTable/index.vue'
import OrderDetail from './components/orderDetail.vue'
import { useDictOptions } from '@/hooks/useDictOptions';
import type {TakeAwayOrderQuery, TakeAwayOrderRow} from '@/api/types/order/takeAwayOrder'
import type { ColumnProps,ProTableInstance, SearchProps } from '@/components/ProTable/interface'
import { useDict } from '@/hooks/useDict';
import {getTakeAwayOrderListApi,
        updateTakeAwayOrderApi,
        removeTakeAwayOrderApi,
        getTakeAwayOrderDetailApi,
        exportTakeAwayOrderExcelApi,
        importTakeAwayOrderExcelApi,
        updateTakeAwayOrderPayStatusApi,
        updateTakeAwayOrderStatusApi
} from "@/api/modules/order/takeAwayOrder";
import {Delete, Download, EditPen, Upload} from "@element-plus/icons-vue";
import {useHandleData} from "@/hooks/useHandleData";
import type ImportExcel from "@/components/ImportExcel/index.vue";
import {downloadTemplate} from "@/api/modules/system/common";
import {useDownload} from "@/hooks/useDownload";

useDict(['take_away_order_status']);
useDict(['pay_status'])
// 获取字典选项
const takeAwayOrderStatusOptions = useDictOptions('take_away_order_status');
const payStatusOptions = useDictOptions('pay_status');
// 获取状态名称
const getStatusName = (status: string | undefined) => {
  if (!status) return '';
  const statusItem = takeAwayOrderStatusOptions.value.find(item => item.id === status);
  return statusItem ? statusItem.codeName : '';
};

// 获取支付状态名称
const getPayStatusName = (payStatus: string | undefined) => {
  if (!payStatus) return '';
  const payStatusItem = payStatusOptions.value.find(item => item.id === payStatus);
  return payStatusItem ? payStatusItem.codeName : '';
};

// 定义响应式数据
const dialogVisible = ref(false)
const orderDetailVisible = ref(false)
const currentOrder = ref<TakeAwayOrderRow>({})
const proTableRef = ref()

// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'customerName', label: '客户姓名', el: 'input',},
  { prop: 'customerPhone', label: '客户电话', el: 'input',},
  {
    prop: 'status',
    label: '状态',
    el: 'select',
    enum: takeAwayOrderStatusOptions.value,
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
  { prop: 'customerName', label: '客户姓名' },
  { prop: 'customerPhone', label: '客户电话' },
  { prop: 'deliveryAddress', label: '配送地址' },
  {
    prop: 'totalAmount',
    label: '金额',
    render: ({ row }) => `¥${row.totalAmount}`
  },
  {
    prop: 'status',
    label: '状态',
    tag: true,
    enum: takeAwayOrderStatusOptions.value,
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'createTime', label: '下单时间' },


  { prop: 'operation', label: '操作' }
]


// 数据回调函数
const dataCallback = (data: any) => {
  return data
}


// 查看订单详情（新组件）
const viewOrderDetail = (row: TakeAwayOrderRow) => {
  currentOrder.value = { ...row }
  orderDetailVisible.value = true
}
// 统一更新状态方法
const updateStatus = async (row: TakeAwayOrderRow, statusData: {status?: string, payStatus?: string, refundReason?: string}) => {
  const params = {
    id: row.id,
    orderId: row.orderId,
    ...statusData
  };
  await useHandleData(updateTakeAwayOrderStatusApi, params, `更新订单状态`);
  if (proTableRef.value) {
    proTableRef.value.getTableList();
  }
  dialogVisible.value = false;
}

const updatePayStatus = async (row: TakeAwayOrderRow, statusData: {status?: string, payStatus?: string, refundReason?: string}) => {
  const params = {
    id: row.id,
    orderId: row.orderId,
    ...statusData
  };
  await useHandleData(updateTakeAwayOrderPayStatusApi, params, `更新订单支付状态`);
  if (proTableRef.value) {
    proTableRef.value.getTableList();
  }
  dialogVisible.value = false;
}



// 组件挂载时获取数据
onMounted(() => {
  if (proTableRef.value) {
    proTableRef.value.getTableList()
  }
})

const formatParams = (params: TakeAwayOrderQuery) =>{
  let newParams = JSON.parse(JSON.stringify(params));
  return newParams;
};

// 删除信息
const deleteInfo = async (params:TakeAwayOrderRow ) => {
  await useHandleData(removeTakeAwayOrderApi, { ids: [params.id] }, `删除【${params.id}】外卖订单`);
  proTableRef.value?.getTableList();
}
// 批量删除信息
const batchDelete = async (ids: (string | number)[]) => {
  await useHandleData(removeTakeAwayOrderApi, { ids }, '删除所选外卖订单');
  proTableRef.value?.clearSelection();
  proTableRef.value?.getTableList();
}
// 导入
const ImportExcelRef = ref<InstanceType<typeof ImportExcel>>();
const importData = () => {
  const params = {
    title: '外卖订单',
    templateName: '外卖订单',
    tempApi: downloadTemplate,
    importApi: importTakeAwayOrderExcelApi,
    getTableList: proTableRef.value?.getTableList
  };
  ImportExcelRef.value?.acceptParams(params);
};
// 导出
const downloadFile = async () => {
  let newParams = formatParams(proTableRef.value?.searchParam as TakeAwayOrderQuery);
  useDownload(exportTakeAwayOrderExcelApi, "外卖订单", newParams);
};
</script>

<style scoped lang="scss">


</style>