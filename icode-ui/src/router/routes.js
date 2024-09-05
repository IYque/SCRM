/* Layout */
import { RouterView } from 'vue-router'
import { h } from 'vue'
import Layout from '@/layout/index.vue'
const routerView = {
	render: () => h(RouterView),
}
/**
 * Note: 路由配置项
 *
 * hidden: true                   // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true               // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect           // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'             // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * meta : {
    roles: ['admin','editor']    // 设置该路由进入的权限，支持多个权限叠加
    title: 'title'               // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'             // 设置该路由的图标，对应路径src/icons/svg
    breadcrumb: false            // 如果设置为false，则不会在breadcrumb面包屑中显示
  }
 */

export const navRoutes = [
	{
		path: '/liveCode',
		component: Layout,
		redirect: '/liveCode/index',
		meta: { title: '活码', icon: 'user' },
		children: [
			{
				path: 'index',
				component: () => import('@/views/liveCode/index'),
				hidden: true,
				meta: { title: '活码' },
			},
		],
	},
	{
		path: '/customerLink',
		component: Layout,
		redirect: '/customerLink/index',
		meta: { title: '获客', icon: 'system' },
		children: [
			{
				path: 'index',
				component: () => import('@/views/liveCode/index'),
				hidden: true,
				meta: { title: '获客' },
			},
		],
	},
	{
		path: '/config',
		component: Layout,
		redirect: '/config/index',
		meta: { title: '配置', icon: 'system' },
		children: [
			{
				path: 'index',
				component: () => import('@/views/config/index'),
				hidden: true,
				meta: { title: '配置' },
			},
		],
	},
]

// 公共路由
export const constantRoutes = navRoutes.concat([
	{
		path: '/',
		component: () => import('@/layout/visitor'),
		redirect: navRoutes[0].path,
		hidden: true,
		children: [
			{
				path: '/login',
				component: () => import('@/views/system/login'),
			},
			{
				path: '/register',
				component: () => import('@/views/system/register'),
			},
		],
	},
	{
		path: '/404',
		component: () => import('@/views/system/error/404'),
		hidden: true,
	},
	{
		path: '/401',
		component: () => import('@/views/system/error/401'),
		hidden: true,
	},
])
