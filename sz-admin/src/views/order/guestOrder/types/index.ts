// 订单表单数据类型
export interface OrderFormData {
  customerName: string
  customerPhone: string
  deliveryAddress: string
  remark: string
}

// 购物车项目类型
export interface CartItem {
  dishId: string
  dishName: string
  price: number
  quantity: number
  imageUrl: string
}

// 订单详情类型
export interface OrderItem {
  dishId: string
  dishName: string
  price: number
  quantity: number
}

// 订单数据类型
export interface OrderData {
  orderNumber: string
  createTime: string
  customerName: string
  customerPhone: string
  deliveryAddress: string
  remark: string
  items: OrderItem[]
  totalAmount: number
}