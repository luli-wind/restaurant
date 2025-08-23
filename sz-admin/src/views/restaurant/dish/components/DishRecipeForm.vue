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
      <el-form-item label="菜品ID" prop="dishId">
        <el-input v-model="paramsProps.row.dishId" placeholder="请输入菜品ID" clearable></el-input>
      </el-form-item>
      <el-form-item label="原材料ID" prop="materialId">
        <el-input v-model="paramsProps.row.materialId" placeholder="请输入原材料ID" clearable></el-input>
      </el-form-item>
      <el-form-item label="原材料数量" prop="materialQuantity">
        <el-input-number v-model="paramsProps.row.materialQuantity" :precision="2" :min="0.01" :max="999999" />
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
import type {IUploadResult} from "@/api/types/system/upload";

defineOptions({
    name: 'DishRecipeForm'
});

const rules = reactive({
  dishId: [{ required: true, message: '请输入菜品ID' }],
  materialId: [{ required: true, message: '请输入原材料ID' }],
  materialQuantity: [{ required: true, message: '请输入原材料数量' }],
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
// 获取文件变更事件
const fileChange = (data: IUploadResult) => {
  console.log(data);
};
</script>

<style scoped lang="scss"></style>