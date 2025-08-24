# 餐厅管理模块说明

## 菜品配方管理 (Dish Recipe Management)

### 模块概述
菜品配方管理模块用于维护菜品与其所需原材料之间的关系。一个菜品可以包含多种原材料，一种原材料也可以被多个菜品使用，形成多对多的关系。

### 数据库设计

#### dish_recipe 表结构
| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键ID |
| dish_id | BIGINT | 外键，关联菜品ID |
| material_id | BIGINT | 外键，关联原材料ID |
| material_quantity | DECIMAL(10,2) | 制作一份该菜品所需原材料的数量 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| create_id | BIGINT | 创建人ID |
| update_id | BIGINT | 更新人ID |
| delete_time | DATETIME | 删除时间 |
| delete_id | BIGINT | 删除人ID |

#### 索引设计
1. 主键索引：id
2. 普通索引：dish_id, material_id
3. 唯一索引：dish_id + material_id（确保一个菜品的一种原材料只有一条记录）

### 后端API接口

#### 1. 创建配方
- **URL**: POST /dishRecipe
- **权限**: dishRecipe.create
- **请求参数**: 
  ```json
  {
    "dishId": 1,
    "materialId": 1,
    "materialQuantity": 100.50
  }
  ```

#### 2. 修改配方
- **URL**: PUT /dishRecipe
- **权限**: dishRecipe.update
- **请求参数**: 
  ```json
  {
    "id": 1,
    "dishId": 1,
    "materialId": 1,
    "materialQuantity": 150.00
  }
  ```

#### 3. 删除配方
- **URL**: DELETE /dishRecipe
- **权限**: dishRecipe.remove
- **请求参数**: 
  ```json
  {
    "ids": [1, 2, 3]
  }
  ```

#### 4. 查询配方列表
- **URL**: GET /dishRecipe
- **权限**: dishRecipe.query_table
- **请求参数**: 
  ```
  ?dishId=1&materialId=1&pageNum=1&pageSize=10
  ```

#### 5. 查询配方详情
- **URL**: GET /dishRecipe/{id}
- **权限**: dishRecipe.query_table
- **请求参数**: 
  ```
  id=1
  ```

#### 6. 根据菜品ID查询配方列表
- **URL**: GET /dishRecipe/byDishId/{dishId}
- **权限**: dishRecipe.query_table
- **请求参数**: 
  ```
  dishId=1
  ```

### 前端组件

#### 1. 配方管理组件 (DishRecipeManage.vue)
位于: `sz-admin/src/views/restaurant/dish/components/DishRecipeManage.vue`
功能:
- 显示指定菜品的所有配方
- 添加新的配方
- 编辑现有配方
- 删除配方

#### 2. 菜品管理页面更新
位于: `sz-admin/src/views/restaurant/dish/index.vue`
更新内容:
- 在操作列添加"配方"按钮
- 点击配方按钮打开配方管理对话框

### 使用说明

1. **创建菜品配方**:
   - 在菜品管理页面点击"配方"按钮
   - 在弹出的对话框中选择原材料和数量
   - 点击"添加配方"按钮保存

2. **编辑菜品配方**:
   - 在配方列表中点击要编辑的配方行
   - 修改数量后点击"添加配方"按钮（实际执行更新操作）

3. **删除菜品配方**:
   - 在配方列表中点击要删除配方行的"删除"按钮
   - 确认删除操作

### 注意事项

1. 配方数量使用DECIMAL(10,2)类型，支持两位小数
2. dish_id和material_id组成唯一索引，确保一个菜品的一种原材料只有一条记录
3. 前端在选择原材料时会显示原材料的计量单位
4. 时间戳字段由系统自动维护