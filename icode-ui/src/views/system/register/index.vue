<template>
  <div class="register cc">
    <el-card>
      <template #header>
        <h1>用户注册</h1>
      </template>
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="用户名" prop="username" required>
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" required>
          <el-input type="password" v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" required>
          <el-input type="password" v-model="form.confirmPassword"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email" required>
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script>
export default {
  data() {
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        email: '',
      },
    }
  },
  methods: {
    handleSubmit() {
      if (this.form.password !== this.form.confirmPassword) {
        this.msgError('密码和确认密码不一致，请重新输入！')
        return
      }
      // 在这里添加注册逻辑，例如调用 API 或者使用 Vuex 提交数据
      this.msgSuccess('注册成功！')
    },
  },
}
</script>
<style scoped>
.register {
  width: 400px;
  margin: 0 auto;
  padding: 20px;
}
</style>
