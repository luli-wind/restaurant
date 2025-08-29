// 测试数据
export const testDishes = [
  {
    dishId: '1',
    dishName: '宫保鸡丁',
    price: 28.00,
    description: '经典川菜，鸡肉丁与花生米炒制而成',
    imageUrl: 'https://example.com/kung-pao-chicken.jpg'
  },
  {
    dishId: '2',
    dishName: '麻婆豆腐',
    price: 22.00,
    description: '嫩豆腐与牛肉末搭配麻辣调料',
    imageUrl: 'https://example.com/mapo-tofu.jpg'
  },
  {
    dishId: '3',
    dishName: '红烧肉',
    price: 35.00,
    description: '五花肉慢炖至软糯，色泽红亮',
    imageUrl: 'https://example.com/braised-pork.jpg'
  },
  {
    dishId: '4',
    dishName: '鱼香肉丝',
    price: 26.00,
    description: '猪肉丝配木耳胡萝卜，酸甜口味',
    imageUrl: 'https://example.com/fish-flavored-shredded-pork.jpg'
  },
  {
    dishId: '5',
    dishName: '回锅肉',
    price: 30.00,
    description: '五花肉片与青椒豆瓣酱炒制',
    imageUrl: 'https://example.com/double-cooked-pork.jpg'
  }
]

export const testCartItems = [
  {
    dishId: '1',
    dishName: '宫保鸡丁',
    price: 28.00,
    quantity: 2,
    imageUrl: 'https://example.com/kung-pao-chicken.jpg'
  },
  {
    dishId: '3',
    dishName: '红烧肉',
    price: 35.00,
    quantity: 1,
    imageUrl: 'https://example.com/braised-pork.jpg'
  }
]

export const testOrderData = {
  customerName: '张三',
  customerPhone: '13800138000',
  deliveryAddress: '北京市朝阳区某某街道某某号',
  remark: '请尽快送达'
}