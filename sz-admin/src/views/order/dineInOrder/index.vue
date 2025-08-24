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
          <span>{{ getStatusName(currentOrder.status) }}</span>
        </el-form-item>
        <el-form-item label="支付状态:">
          <span>{{ getPayStatusName(currentOrder.payStatus) }}</span>
        </el-form-item>
        <el-form-item label="下单时间:"><span>{{ currentOrder.createTime }}</span></el-form-item>
        <el-form-item v-if="currentOrder.payTime" label="完成时间:"><span>{{ currentOrder.payTime }}</span></el-form-item>
        <el-form-item v-if="currentOrder.remark" label="备注:"><span>{{ currentOrder.remark }}</span></el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button v-if="currentOrder.status !== '2004004'" type="warning" @click="updateStatus(currentOrder, {status: '2004004'})">取消订单</el-button>
          <el-button v-if="currentOrder.status === '2004001'" type="primary" @click="updateStatus(currentOrder, {status: '2004002'})">开始制作</el-button>
          <el-button v-if="currentOrder.status === '2004002'" type="success" @click="updateStatus(currentOrder, {status: '2004003'})">完成制作</el-button>
          <el-button v-if="currentOrder.payStatus === '2006001'" type="danger" @click="openRefundDialog(currentOrder)">申请退款</el-button>
          <el-button v-if="currentOrder.payStatus !== '2006001'" type="primary" @click="updateStatus(currentOrder, {payStatus: '2006001'})">标记为已支付</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 退款申请对话框 -->
    <el-dialog v-model="refundDialogVisible" title="申请退款" width="500px">
      <el-form :model="refundForm" label-width="100px">
        <el-form-item label="退款原因:">
          <el-input v-model="refundForm.refundReason" type="textarea" placeholder="请输入退款原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="refundDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRefund">确定</el-button>
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
        importDineInOrderExcelApi,
} from "@/api/modules/order/dineInOrders";
import {Delete, Download, Upload} from "@element-plus/icons-vue";
import {useHandleData} from "@/hooks/useHandleData";
import type ImportExcel from "@/components/ImportExcel/index.vue";
import {downloadTemplate} from "@/api/modules/system/common";
import {useDownload} from "@/hooks/useDownload";

useDict(['dine_in_order_status']);
useDict(['pay_status'])
// 获取字典选项
const dineInOrderStatusOptions = useDictOptions('dine_in_order_status');
const payStatusOptions = useDictOptions('pay_status');
// 获取状态名称
const getStatusName = (status: string | undefined) => {
  if (!status) return '';
  const statusItem = dineInOrderStatusOptions.value.find(item => item.id === status);
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
const currentOrder = ref<DineInOrderRow>({})
const refundDialogVisible = ref(false)
const refundForm = ref({
  id: 0,
  refundReason: ''
})
const proTableRef = ref()

// 搜索条件项
const searchColumns: SearchProps[] = [
  { prop: 'tableName', label: '桌子编号', el: 'input',},
  {
    prop: 'status',
    label: '状态',
    el: 'select',
    enum: dineInOrderStatusOptions.value,
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  {
    prop: 'payStatus',
    label: '支付状态',
    el: 'select',
    enum: payStatusOptions.value,
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
    enum: dineInOrderStatusOptions.value,
    fieldNames: {
      label: 'codeName',
      value: 'id',
      tagType: 'callbackShowStyle'
    }
  },
  { prop: 'createTime', label: '下单时间' },
  { prop: 'payStatus', label: '支付状态' },
  { prop: 'payTime', label: '支付时间' },
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

// 统一更新状态方法
const updateStatus = async (row: DineInOrderRow, statusData: {status?: string, payStatus?: string, refundReason?: string}) => {
  const params = {
    id: row.id,
    ...statusData
  };
  await useHandleData(updateDineInOrderApi, params, `更新订单状态`);
  if (proTableRef.value) {
    proTableRef.value.getTableList();
  }
  dialogVisible.value = false;
}

// 打开退款对话框
const openRefundDialog = (row: DineInOrderRow) => {
  refundForm.value.id = row.id;
  refundForm.value.refundReason = '';
  refundDialogVisible.value = true;
}

// 提交退款申请
const submitRefund = async () => {
  await updateStatus({id: refundForm.value.id} as DineInOrderRow, {refundReason: refundForm.value.refundReason});
  if (proTableRef.value) {
    proTableRef.value.getTableList();
  }
  refundDialogVisible.value = false;
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