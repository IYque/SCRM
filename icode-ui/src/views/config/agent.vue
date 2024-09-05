<template>
  <div>
    <el-form ref="form" label-position="right" :model="form" :rules="rules" label-width="130px">
      <div class="g-card">
        <div class="my-title">应用参数配置</div>
        <el-form-item label="企业ID:" prop="corpId">
          <el-input v-model="form.corpId" placeholder=""></el-input>
        </el-form-item>
        <el-form-item label="应用ID:" prop="agentId">
          <el-input v-model="form.agentId" placeholder="请输入消息应用ID"></el-input>
        </el-form-item>

         <el-form-item label="应用Secert:" prop="agentSecert">
          <el-input type='password'  v-model="form.agentSecert"  placeholder="请输入应用Secert"></el-input>
        </el-form-item>

      </div>
      <div class="g-card">
        <div class="my-title">回调配置</div>
        <el-form-item label="回调地址:">
          <el-input
            :value="(form.token1 = sysConfig.BASE_API + '/iycallback/handle?corpId={企业id}')"
            readonly
            placeholder="{系统统一访问前缀}/iycallback/handle?corpId={企业id}"></el-input>
        </el-form-item>
        <el-form-item label="Token:" prop="token">
          <el-input v-model="form.token"  placeholder="请输入Token"></el-input>
          <el-button style="margin-left: 20px" type="primary" plain @click="form.token = uuid(28)">随机获取</el-button>
        </el-form-item>
        <el-form-item label="AESKey:" prop="encodingAESKey">
          <el-input v-model="form.encodingAESKey"  placeholder="请输入AESKey"></el-input>
          <el-button style="margin-left: 20px" type="primary" plain @click="form.encodingAESKey = uuid(43)">
            随机获取
          </el-button>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>
<script>
import { uuid } from '@/utils/common'
export default {
  name: 'enterprise-wechat-part1',
  data() {
    return {
      corpSecretCopy: '',
      agentSecertEditState: false,
      // contactSecretCopey: '',
      // contactSecretEditState: false,
      form: {},
      rules: {
        corpId: [{ required: true, message: '必填项', trigger: 'blur' }],
        agentSecert: [{ required: true, message: '必填项', trigger: 'blur' }],
        companyName: [{ required: true, message: '必填项', trigger: 'blur' }],
        // contactSecret: [{ required: true, message: '必填项', trigger: 'blur' }],
        logoUrl: [{ required: true, message: '必填项', trigger: 'blur' }],
        token: [{ required: true, message: '必填项', trigger: 'blur' }],
        encodingAESKey: [{ required: true, message: '必填项', trigger: 'blur' }],
      },
      uuid,
    }
  },
  watch: {
    data: 'setData',
  },
  props: {
    data: {
      type: Object,
      default: {},
    },
  },
  methods: {
    editagentSecert() {
      this.agentSecertEditState = true
      this.corpSecretCopy = JSON.parse(JSON.stringify(this.form.agentSecert))
      this.form.agentSecert = ''
    },
    cancelEditagentSecert() {
      this.agentSecertEditState = false
      this.form.agentSecert = JSON.parse(JSON.stringify(this.corpSecretCopy))
    },
    submit() {
      this.$refs['form'].validate((validate) => {
        if (validate) {
          this.$emit('submit', this.form)
        }
      })
    },
    setData() {
      if (Object.keys(this.data).length) {
        this.form = this.data
      }
    },
  },
  mounted() {},
  created() {},
}
</script>
<style lang="scss" scoped>
.my-title {
}
.tips {
  color: var(--font-black-7);
  font-size: 12px;
}
</style>
