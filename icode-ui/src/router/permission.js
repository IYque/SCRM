import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'

NProgress.configure({ showSpinner: true })

const whiteList = ['/bind', '/test', '/404', '/401'] // 不管有没有token都可直接进入的页面路径
const noLoginList = ['/authRedirect', '/login', '/register'] // 没有token才能进入的页面

export default function permission(router) {
  router.beforeEach((to, from, next) => {
    NProgress.start()
    // 无需检测token的， 不管有没有token都可直接进入
    if (whiteList.includes(to.path) || to.meta.isNoLogin) {
      next()
    } else if (getToken()) {
      /* has token*/
      if (noLoginList.includes(to.path)) {
        next({ path: window.sysConfig.BASE_URL })
      } else {
        next()
      }
    } else if (noLoginList.includes(to.path)) {
      // 没有token才能进入的页面
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`) // 否则全部重定向到登录页
      // NProgress.done()
    }
  })

  router.afterEach(() => {
    NProgress.done()
    document.getElementById('loader-wrapper').className = 'loaded'
  })
}
