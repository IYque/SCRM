import { createPinia, defineStore } from 'pinia'
import app from './modules/app'
import user from './modules/user'
import getters from './getters'

export const store = createPinia()

export default defineStore('app', {
  state: () => ({
    loading: false, // 页面loading
    app: { ...app.state },
    user: { ...user.state },
  }),
  actions: {
    ...app.actions,
    ...user.actions,
  },
  getters,
})
