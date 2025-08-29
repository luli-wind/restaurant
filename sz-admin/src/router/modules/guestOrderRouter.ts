import type { RouteRecordRaw } from 'vue-router';

/**
 * 访客下单页面路由
 * 该页面完全公开访问，无需登录权限
 */
export const guestOrderRouter: RouteRecordRaw[] = [
  {
    path: '/guest/order',
    name: 'guestOrder',
    component: () => import('@/views/order/guestOrder/index.vue'),
    meta: {
      title: '访客下单',
      icon: 'ShoppingCart',
      isAffix: 'F',
      isFull: 'F',
      isHidden: 'F',
      isKeepAlive: 'T',
      isLink: '',
      isPublic: true // 标记为公开页面，无需权限验证
    }
  }
];