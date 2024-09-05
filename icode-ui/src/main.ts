import './config.js'
import 'normalize.css/normalize.css'

import { createApp, h, defineAsyncComponent } from 'vue'
// configureCompat({
//   WATCH_ARRAY: false,
// })
import App from './App.vue'
import router from './router'

const app = createApp(App)

import ElementPlus, { ElNotification, ElMessageBox } from 'element-plus'
import 'element-plus/theme-chalk/dark/css-vars.css'
import 'element-plus/dist/index.css'
app.use(ElementPlus, {})
let ElInput = app.component('ElInput')
ElInput.props.clearable.default = true
let ElSelect = app.component('ElSelect')
ElSelect.props.clearable = { type: Boolean, default: true }

import '@/styles/index.scss'

// svg图标
import 'virtual:svg-icons-register'
import SvgIcon from '@/components/SvgIcon'
app.component('SvgIcon', SvgIcon)

import * as ElementPlusIconsVue from '@element-plus/icons-vue'
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component('ElIcon' + key, component)
}

const components = import.meta.glob('./components/**/*.vue')
console.log(components)

for (const c in components) {
  app.component(c.match(/\/([^\/]+)\.vue$/)[1], defineAsyncComponent(components[c]))
}

// 全局方法挂载
import * as methods from '@/utils/common'
Object.assign(app.config.globalProperties, methods, {
  // 动态配置
  sysConfig: window.sysConfig,
  // 日期时间控件快捷配置
  msgSuccess(msg = '操作成功') {
    this.$message({
      showClose: true,
      message: msg,
      type: 'success',
    })
  },
  msgError(msg = '操作失败') {
    this.$message({ showClose: true, message: msg, type: 'error' })
  },
  msgInfo(msg) {
    this.$message.info(msg)
  },
  $confirm(message = '确认删除吗？', title, options = {}) {
    let defaultConfig = {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      title: '警告',
    }
    if (typeof title === 'string') {
      defaultConfig.title = title
      Object.assign(options, defaultConfig)
    } else {
      Object.assign(options, defaultConfig, title || {})
    }
    return ElMessageBox.confirm(message, title, options)
  },
  // $confirmDel(message = '确认删除吗？', title, options = {}) {
  //   this.$confirm(message, title, options)
  // },
})

import stores, { store } from '@/stores'

app.use(store)
app.use(router)
app.config.globalProperties.$store = stores()

app.mount('#app')
