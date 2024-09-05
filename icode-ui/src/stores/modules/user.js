// import { getInfo } from '@/views/system/login/api'
import { removeToken } from '@/utils/auth'

const user = {
  state: {
    name: '',
    avatar: new URL('@/assets/image/profile.jpg', import.meta.url).href,
  },

  actions: {
    // 退出系统
    LogOut() {
      return new Promise((resolve, reject) => {
        removeToken()
        resolve()
        // logout(state.token)
        //   .then(() => {
        //     removeToken()
        //     resolve()
        //   })
        //   .catch((error) => {
        //     reject(error)
        //   })
        location.href = window.sysConfig.BASE_URL
      })
    },
  },
}

export default user
