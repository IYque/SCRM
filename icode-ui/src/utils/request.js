import axios from 'axios'
import { ElNotification as Notification, ElMessageBox as MessageBox, ElMessage as Message } from 'element-plus'
import stores from '@/stores'
import { getToken, setToken } from '@/utils/auth'

// 创建axios实例
function requestFactory(getway = '') {
  const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: (process.env.NODE_ENV === 'development' ? '/api' : window.sysConfig.BASE_API) + getway,
    // 超时
    timeout: 100000,
  })

  // request拦截器
  service.interceptors.request.use(
    (config) => {
      if (getToken()) {
        Object.assign(config.headers, window.sysConfig.headers) // 让每个请求携带自定义token 请根据实际情况自行修改
      }
      return config
    },
    (error) => {
      console.log(error)
      Promise.reject(error)
    },
  )

  // 响应拦截器
  service.interceptors.response.use(
    (res) => {
      let store = stores()
      // 未设置状态码则默认成功状态
      const code = res.data.code || 200
      if ([200, 301].includes(code)) {
        return res.data
      } else if (code === 401) {
        MessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          store.LogOut()
        })
      } else {
        Notification.error({
          title: res.data.msg,
        })
        return Promise.reject()
      }
    },
    (error) => {
      console.log('err: ' + error)
      let { message: msg, config } = error
      Message({
        message: `${msg}:${config.url}`,
        type: 'error',
        duration: 5 * 1000,
      })
      return Promise.reject()
    },
  )

  service.get = (url, params, config = {}) => {
    return service({ url, params, ...config })
  }

  service.delt = (url, params, config = {}) => {
    return service.delete(url, { params, ...config })
  }

  return service
}

export default requestFactory()
