import { createRouter, createWebHistory, createWebHashHistory } from 'vue-router'
import { constantRoutes } from '@/router/routes'
import permission from './permission'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes,
})
permission(router)

export default router
