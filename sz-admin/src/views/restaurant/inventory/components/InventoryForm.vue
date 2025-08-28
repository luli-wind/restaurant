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
      <el-form-item label="材料名" prop="materialName">
        <el-input v-model="paramsProps.row.materialName" placeholder="请填写材料名" clearable></el-input>
      </el-form-item>
      <el-form-item label="当前容量" prop="currentStock">
        <el-input-number v-model="paramsProps.row.currentStock" :precision="2" :min="0" :max="999999" :step="0.1" />
      </el-form-item>
      <el-form-item label="最小容量" prop="minStock">
        <el-input-number v-model="paramsProps.row.minStock" :precision="2" :min="0" :max="999999" :step="0.1" />
      </el-form-item>
      <el-form-item label="计量单位" prop="unit">
        <el-input v-model="paramsProps.row.unit" placeholder="请填写计量单位" clearable></el-input>
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

defineOptions({
    name: 'InventoryForm'
});

const rules = reactive({
  materialName: [{ required: true, message: '请填写材料名' }],
  currentStock: [{ required: true, message: '请填写当前容量' }],
  minStock: [{ required: true, message: '请填写最小容量' }],
  unit: [{ required: true, message: '请填写计量单位' }],
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