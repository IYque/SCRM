<template>
  <div class="loginPage">
    <div class="w-[1200px] h-[730px] centerCenter flex --Radius">
      <div class="w-[50%] bg-(--ColorLight8) p-[40px]">
        <img
          class="h-[35px]"
          style="vertical-align: middle"
          v-if="sysConfig.LOGO"
          :src="$store.app.isDark ? sysConfig.LOGO : sysConfig.LOGO"
          alt="" />
        <span class="text-[29px] blod ml5" style="vertical-align: middle">{{ sysConfig.SYSTEM_NAME }}</span>
        <el-tag class="bold" type="primary" size="large" effect="dark">AI开源版</el-tag>

        <div class="bold mt20 font16 mb-[90px]">
          源码交付的私域数智化营销解决方案
          <!-- <span v-html="sysConfig.SYSTEM_SLOGAN"></span> -->
        </div>
        <div class="leading-[40px]">
          yjaiscrm SCRM是基于源码100%开放策略的企业微信SCRM，从源头解决了企业私域建设核心痛点，实现高自由度、高私有化及高安全性。系统具备四大优势：
          <ul>
            <li class="!list-disc !list-inside !pl-[30px]">系统高度可用，部署灵活高效</li>
            <li class="!list-disc !list-inside !pl-[30px]">研发降本增效，满足定制需求</li>
            <li class="!list-disc !list-inside !pl-[30px]">数据安全可控，自主知识产权</li>
            <li class="!list-disc !list-inside !pl-[30px]">降低云服务依赖与供应商绑定</li>
          </ul>
          同时yjaiscrm积极拥抱开源，同步推出完全开源、免费使用AI开源版，结合DeepSeek等AI大模型，让企业快速拥有更强大、更丰富、更智能的企业微信管理能力。
        </div>
        <div class="--Color bold font16 mt20">
          <a href="https://www.yjaiscrm.cn?utm_source=yjaiscrmcode" target="_blank">立即了解yjaiscrm SCRM →</a>
        </div>
      </div>
      <div class="w-[50%] bg-(--BgWhite) relative">
        <div class="centerCenter">
          <div class="text-[29px] blod mb-[30px]">登录</div>
          <el-form
            ref="loginForm"
            size="large"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            style="--formItemWidth: 390px">
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
                <el-checkbox class="fr" v-model="loginForm.rememberMe">记住密码</el-checkbox>
              </div>
              <el-form-item>
                <el-button :loading="loading" type="default" class="w-[100%]" @click.prevent="login">
                  <span v-if="!loading">立即登录</span>
                  <span v-else>登 录 中...</span>
                </el-button>
              </el-form-item>
              <!-- <a :href="threeLoginInfo.threeLoginUrl" v-if="threeLoginInfo.startThreeLogin">
                <el-button class="w-[100%] blod" type="primary" @click="">
                  <svg-icon icon="gitee" class="text-[25px] mr5" />
                  使用 Gitee 账号 Star 一下，直接免密登录
                </el-button>
              </a> -->
            </template>
          </el-form>
        </div>
      </div>
    </div>
    <div class="fixed bottom-[30px] left-[50%] translate-x-[-50%] --FontBlack5 font12">
      <span v-html="sysConfig.COPYRIGHT"></span>
    </div>
  </div>
</template>

<script>
import { login, findThreeLoginInfo, giteeLogin } from './api'
import Cookies from 'js-cookie'
import { setToken } from '@/utils/auth'

export default {
  name: 'Login',
  data() {
    return {
      loginType: 'username',
      ispassword: true,
      loginForm: {
        username: '',
        password: '',
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
      threeLoginInfo: {},
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
    this.findThreeLoginInfo()
  },
  methods: {
    findThreeLoginInfo() {
      findThreeLoginInfo().then(({ data }) => {
        this.threeLoginInfo.startThreeLogin = data.startThreeLogin
        this.threeLoginInfo.threeLoginUrl = data.threeLoginUrl
      })
    },
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
              this.$router.push(this.redirect || '/')
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
</style>
