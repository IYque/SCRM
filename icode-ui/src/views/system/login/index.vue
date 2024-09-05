<template>
  <div class="loginPage ac">
    <div class="login">
      <div class="loginHead">
        <img class="logo" style="vertical-align: middle" :src="sysConfig.LOGO" alt="" />
        <span class="loginTitle ml5" style="vertical-align: middle">{{ sysConfig.SYSTEM_NAME }}</span>
        <div class="loginSlogan mt20">
          <span v-html="sysConfig.SYSTEM_SLOGAN"></span>
        </div>
      </div>
      <el-form ref="loginForm" size="large" :model="loginForm" :rules="loginRules" class="login-form">
        <template v-if="loginType === 'username'">
          <el-form-item prop="username">
            <el-input v-model.trim="loginForm.username" type="text" auto-complete="off" placeholder="账号">
              <template #prefix>
                <svg-icon icon="user" class="input-icon" />
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model.trim="loginForm.password"
              :type="ispassword ? 'password' : 'text'"
              auto-complete="off"
              placeholder="密码"
              @keyup.enter="login">
              <template #prefix>
                <svg-icon icon="password" class="input-icon" />
              </template>
              <template #suffix>
                <svg-icon
                  :icon="ispassword ? 'eye' : 'eye-open'"
                  class="input-icon cp"
                  @click="ispassword = !ispassword" />
              </template>
            </el-input>
          </el-form-item>

          <div class="fxbw g-margin-b">
            <el-checkbox class="fr" v-model="loginForm.rememberMe">记住我</el-checkbox>
          </div>
          <el-form-item>
            <el-button :loading="loading" type="primary" style="width: 100%" @click.prevent="login">
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
          </el-form-item>
        </template>
      </el-form>
      <div class="copyright">
        <span v-html="sysConfig.COPYRIGHT"></span>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from './api'
import Cookies from 'js-cookie'
import { setToken } from '@/utils/auth'

export default {
  name: 'Login',
  data() {
    return {
      loginType: 'username',
      ispassword: true,
      loginForm: {
        username: 'iyque',
        password: '123456',
        rememberMe: false,
        code: '',
        uuid: '',
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', message: '用户名不能为空' }],
        password: [{ required: true, trigger: 'blur', message: '密码不能为空' }],
        code: [{ required: true, trigger: 'change', message: '验证码不能为空' }],
      },
      authLink: '',
      loading: false,
      redirect: undefined,
      dialogVisible: true,
      isDemonstrationLogin: false,
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true,
    },
  },
  created() {
    this.getCookie()
  },
  methods: {
    getCookie() {
      const username = Cookies.get('username')
      const password = Cookies.get('password')
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : password,
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
      }
    },
    login() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set('username', this.loginForm.username, { expires: 30 })
            Cookies.set('password', this.loginForm.password, {
              expires: 30,
            })
            Cookies.set('rememberMe', this.loginForm.rememberMe, {
              expires: 30,
            })
          } else {
            Cookies.remove('username')
            Cookies.remove('password')
            Cookies.remove('rememberMe')
          }
          let loginForm = JSON.parse(JSON.stringify(this.loginForm))
          loginForm.password = this.loginForm.password
          login(loginForm)
            .then(({ data }) => {
              setToken(data.token)
              this.$router.push(this.redirect || window.sysConfig.BASE_URL)
            })
            .catch(() => {
              this.loading = false
            })
        }
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.loginPage {
  width: 100%;
  min-height: 100%;
  background: #f0f2f5 url(./bg.svg) no-repeat 50%;
  background-size: 100%;
  .login {
    width: 360px;
    display: inline-block;
    margin: 50px auto;
  }
}
.logo {
  height: 45px;
}
.loginHead {
  padding: 30px 20px;
}
.loginTitle {
  font-size: 30px;
}
.loginSlogan {
  font-size: 15px;
  line-height: 1.5;
  color: var(--font-black-5);
}

.login-form {
  // .el-input {
  //   height: 38px;
  //   line-height: 38px;
  //   input {
  //     height: 38px;
  //   }
  // }
  .desc {
    text-align: center;
    color: var(--font-black-7);
    font-size: 12px;
    margin: -22px 0 50px;
  }
  .input-icon {
    font-size: 16px;
  }
}
.copyright {
  font-size: 12px;
  bottom: 10px;
  color: var(--font-black-5);
}
</style>
