<template>
  <el-dialog
    v-model="visible"
    :title="`${paramsProps.dishName} - 配方管理`"
    :destroy-on-close="true"
    width="800px"
    draggable
  >
    <div class="recipe-manage">
      <div class="recipe-list">
        <el-table :data="recipeList" border>
          <el-table-column prop="materialName" label="原材料名称" width="150"></el-table-column>
          <el-table-column prop="materialQuantity" label="数量" width="100">
            <template #default="scope">
              {{ scope.row.materialQuantity }}
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" width="80"></el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="primary" link @click="editRecipe(scope.row)">编辑</el-button>
              <el-button type="danger" link @click="removeRecipe(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="recipe-form">
        <el-form
          ref="recipeFormRef"
          label-width="100px"
          label-suffix=" :"
          :rules="recipeRules"
          :model="recipeForm"
        >
          <el-form-item label="原材料" prop="materialId">
            <el-select v-model="recipeForm.materialId" placeholder="请选择原材料" clearable>
              <el-option
                v-for="item in materialOptions"
                :key="item.materialId"
                :label="item.materialName"
                :value="item.materialId"
              >
                {{ item.materialName }} ({{ item.unit }})
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="数量" prop="materialQuantity">
            <el-input-number v-model="recipeForm.materialQuantity" :precision="2" :min="0.01" :max="999999" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addRecipe">添加配方</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { type ElForm, ElMessage, ElMessageBox } from 'element-plus';
import {
  getDishRecipeByDishIdApi,
  createDishRecipeApi,
  updateDishRecipeApi,
  removeDishRecipeApi
} from '@/api/modules/restaurant/dishRecipe';
import { getInventoryListApi } from '@/api/modules/restaurant/inventory';

defineOptions({
  name: 'DishRecipeManage'
});

// 配方表单规则
const recipeRules = reactive({
  materialId: [{ required: true, message: '请选择原材料' }],
  materialQuantity: [{ required: true, message: '请输入数量' }],
});

const visible = ref(false);
const paramsProps = ref({
  dishId: 0,
  dishName: ''
});

// 配方列表
const recipeList = ref<any[]>([]);

// 配方表单
const recipeForm = reactive({
  id: undefined as number | undefined,
  dishId: 0,
  materialId: undefined as number | undefined,
  materialQuantity: undefined as number | undefined
});

// 原材料选项
const materialOptions = ref<any[]>([]);

// 配方表单引用
const recipeFormRef = ref<InstanceType<typeof ElForm>>();

// 接收父组件传过来的参数
const acceptParams = (params: any) => {
  paramsProps.value = params;
  recipeForm.dishId = params.dishId;
  visible.value = true;
  loadRecipeList();
  loadMaterialOptions();
};

// 加载配方列表
const loadRecipeList = async () => {
  try {
    const res = await getDishRecipeByDishIdApi(paramsProps.value.dishId);
    recipeList.value = res.data || [];
  } catch (error) {
    console.error('加载配方列表失败:', error);
    recipeList.value = [];
  }
};

// 加载原材料选项
const loadMaterialOptions = async () => {
  try {
    const res = await getInventoryListApi({});
    materialOptions.value = res.data?.records || [];
  } catch (error) {
    console.error('加载原材料选项失败:', error);
    materialOptions.value = [];
  }
};

// 添加配方
const addRecipe = async () => {
  if (!recipeFormRef.value) return;
  
  await recipeFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    try {
      const params = {
        ...recipeForm
      };
      
      if (recipeForm.id) {
        // 更新配方
        await updateDishRecipeApi(params);
        ElMessage.success('配方更新成功');
      } else {
        // 添加配方
        await createDishRecipeApi(params);
        ElMessage.success('配方添加成功');
      }
      
      // 重置表单
      resetRecipeForm();
      // 重新加载列表
      await loadRecipeList();
    } catch (error) {
      console.error('保存配方失败:', error);
      ElMessage.error('保存配方失败');
    }
  });
};

// 编辑配方
const editRecipe = (row: any) => {
  recipeForm.id = row.id;
  recipeForm.materialId = row.materialId;
  recipeForm.materialQuantity = row.materialQuantity;
};

// 删除配方
const removeRecipe = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这个配方吗？', '提示', {
      type: 'warning'
    });
    
    await removeDishRecipeApi({ ids: [row.id] });
    ElMessage.success('配方删除成功');
    await loadRecipeList();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除配方失败:', error);
      ElMessage.error('删除配方失败');
    }
  }
};

// 重置配方表单
const resetRecipeForm = () => {
  recipeForm.id = undefined;
  recipeForm.materialId = undefined;
  recipeForm.materialQuantity = undefined;
};

defineExpose({
  acceptParams
});
</script>

<style scoped lang="scss">
.recipe-manage {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.recipe-list {
  flex: 1;
}

.recipe-form {
  border-top: 1px solid #eee;
  padding-top: 20px;
}
</style>