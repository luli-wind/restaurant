<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.title}`"
    :destroy-on-close="true"
    width="580px"
    draggable
  >
    <el-form
      ref="ruleFormRef"
      label-width="140px"
      label-suffix=" :"
      :rules="rules"
      :model="paramsProps.row"
      @submit.enter.prevent="handleSubmit"
    >
      <el-form-item label="费用种类" prop="expenseCategory">
        <el-select v-model="paramsProps.row.expenseCategory" clearable placeholder="请选择费用种类">
          <el-option
            v-for="item in optionsStore.getDictOptions('expense_category')"
            :key="item.id"
            :label="item.codeName"
            :value="Number(item.id)"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="费用" prop="amount">
        <el-input-number v-model="paramsProps.row.amount" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="日期" prop="expenseDate">
        <el-date-picker clearable
          v-model="paramsProps.row.expenseDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="paramsProps.row.description" placeholder="请填写描述" :rows="2" type="textarea" clearable></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false"> 取消</el-button>
      <el-button type="primary" @click="handleSubmit"> 确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { type ElForm, ElMessage } from 'element-plus';
import { useOptionsStore } from '@/stores/modules/options';

defineOptions({
    name: 'ExpenseForm'
});

const optionsStore = useOptionsStore();
const rules = reactive({
  expenseCategory: [{ required: true, message: '请填写费用种类' }],
  amount: [{ required: true, message: '请填写费用' }],
  expenseDate: [{ required: true, message: '请填写日期' }],
});

const visible = ref(false);
const paramsProps = ref<View.DefaultParams>({
  title: '',
  row: {},
  api: undefined,
  getTableList: undefined
});

// 接收父组件传过来的参数
const acceptParams = (params: View.DefaultParams) => {
  paramsProps.value = params
  visible.value = true
};

// 提交数据（新增/编辑）
const ruleFormRef = ref<InstanceType<typeof ElForm>>();
const handleSubmit = () => {
  ruleFormRef.value!.validate(async (valid) => {
    if (!valid) return
    try {
      await paramsProps.value.api!(paramsProps.value.row);
      ElMessage.success({ message: `${paramsProps.value.title}成功！` });
      paramsProps.value.getTableList!();
      visible.value = false;
    } catch (error) {
      console.log(error);
    }
  });
}

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss"></style>