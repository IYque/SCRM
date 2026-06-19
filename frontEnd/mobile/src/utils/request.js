import axios from 'axios'
import qs from 'qs'
import { showConfirmDialog, showDialog, closeToast } from 'vant'
// sessionStorage.setItem(
//   'token',
//   'eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX3R5cGUiOiIwMiIsInVzZXJfaWQiOjM0LCJsb2dpbl90eXBlIjoiTGlua1dlQ2hhdEFQSSIsInVzZXJfbmFtZSI6IueOi-eOieabvCIsInVzZXJfa2V5IjoiY2VjYmFmYzItY2VkOC00ZjI5LThiNmQtODMxMDU4MWM2ZTBkIiwiY29ycF9uYW1lIjoi5Luf5b6u56eR5oqAIiwiY29ycF9pZCI6Ind3NjIyZmM4NTJmNzljM2YxMyJ9.M7OykNvW0edCG4lLwJb31acdgrtXtlcbgt8DAQxcCgPukOXGsf03R8YsgwxjX65IdDL8GxHiotu-AxAv1gqydw'
// )
function requestFactory(getway = '') {
  const service = axios.create({
    baseURL: (process.env.NODE_ENV === 'development' ? '/api' : window.sysConfig.BASE_API) + getway,
    timeout: 50000,
    paramsSerializer: (params) => qs.stringify(params, { arrayFormat: 'brackets' }),
  })

  // 请求拦截
  service.interceptors.request.use(
    (config) => {
      config.headers = {
        Authorization: 'Bearer ' + sessionStorage.token,
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    },
  )
  // 响应拦截
  service.interceptors.response.use(
    (res) => {
      // console.log('res: ' + res)
      const { data, status } = res
      // code 200:成功，-1/其它:错误
      if (status === 200 && data.code === 200) {
        return data
      } else if (data.code === 401) {
        closeToast()
        showConfirmDialog({
          title: '提示',
          message: '登录状态已过期，您可以继续留在该页面，或者重新登录',
          theme: 'round-button',
        }).then(() => {
          sessionStorage.clear()
          location.reload()
        })
      } else if (data.code == 501) {
        // 红包领取相关错误
        return data
      }
      // else if ([-10002, -10003].includes(data.code)) {
      //   // 用户信息不存在
      //   location.replace('/#/task/state')
      //   location.reload()
      //   return new Promise(() => {})
      // }
      else {
        closeToast()
        if (process.env.NODE_ENV === 'development' || location.href.includes('show.yjaiscrm.cn')) {
          showDialog({
            title: '接口提示',
            message: `${res.config.url}, ${data.code} @_@: ${data.msg}`,
            theme: 'round-button',
          })
        } else {
          showDialog({
            title: '提示',
            message: `${data.msg}`,
            theme: 'round-button',
          })
        }
        // addErrorLog(res)
      }
      return Promise.reject(data)

      // if (process.env.NODE_ENV === 'development') {
      //   return Promise.reject(res) // 这样控制台会显示报错日志
      // } else {
      //   return new Promise(() => {}) // 中断promise
      // }
    },
    (error) => {
      closeToast()
      if (error.response) {
        addErrorLog(error.response)
      } else {
        showDialog({ message: '服务器未启动或连接超时' })
      }
      return Promise.reject(error)
    },
  )
  // 错误日志
  function addErrorLog(errorInfo) {
    const {
      data,
      statusText,
      status,
      request: { responseText, responseURL },
    } = errorInfo
    // let info = {
    //   type: 'ajax',
    //   code: status,
    //   mes: statusText,
    //   url: responseURL
    // }
    // if (!responseURL.includes('save_error_logger')) store.dispatch('addErrorLog', info)
    try {
      // console.error(`错误: 路径: ${responseURL}, 返回值 : ${responseText}`)
      // let mes =
      //   process.env.ENV === 'production'
      //     ? `错误: 路径: ${responseURL}, 返回值 : ${responseText}`
      //     : `系统繁忙，请稍后再试`
      showDialog({
        title: '接口提示',
        message: `${responseURL}, @_@: ${responseText}`,
        theme: 'round-button',
      })
    } catch (error) {}
  }

  // 重写方法，统一传参格式
  service.get = (url, params, config = {}) => {
    return service({ url, params, ...config })
  }

  let post = service.post
  service.post = (url, data, config = {}) => {
    return post(url, data, config)
  }

  let put = service.put
  service.put = (url, data, config = {}) => {
    return put(url, data, config)
  }

  let del = service.delete
  service.del = (url, params, config = {}) => {
    return del(url, { params, ...config })
  }

  return service
}

// const httpRequest = requestFactory()

// 创建常用 open 网关接口请求
export const requestOpen = requestFactory(window.sysConfig.services.wecom)
export const requestWeChat = requestFactory(window.sysConfig.services.weChat)
export const requestAi = requestFactory(window.sysConfig.services.ai)

// 老的方式，接口路径需要手动加网关
export default requestFactory()
