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
      <el-form-item label="" prop="imageUrl">
        <el-input v-model="paramsProps.row.imageUrl" placeholder="请填写" clearable></el-input>
      </el-form-item>
      <el-form-item label="" prop="dishName">
        <el-input v-model="paramsProps.row.dishName" placeholder="请填写" clearable></el-input>
      </el-form-item>
      <el-form-item label="" prop="category">
        <el-select v-model="paramsProps.row.category" clearable placeholder="请选择">
          <el-option
            v-for="item in optionsStore.getDictOptions('dish_category')"
            :key="item.id"
            :label="item.codeName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="" prop="price">
        <el-input-number v-model="paramsProps.row.price" :precision="0" :min="1" :max="999999" />
      </el-form-item>
      <el-form-item label="" prop="description">
        <el-input v-model="paramsProps.row.description" placeholder="请填写" clearable></el-input>
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
    name: 'DishForm'
});

const optionsStore = useOptionsStore();
const rules = reactive({
  dishName: [{ required: true, message: '请填写' }],
  category: [{ required: true, message: '请填写' }],
  price: [{ required: true, message: '请填写' }],
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