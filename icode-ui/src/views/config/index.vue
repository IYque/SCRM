<template>
  <div>
    <el-tabs v-model="active">
      <el-tab-pane label="应用参数配置" name="agent">
        <agent ref="agent" @submit="submit" :data="form"></agent>
      </el-tab-pane>
      <el-tab-pane label="全局欢迎语配置" name="welcome">
        <welcome ref="welcome" :data="form"></welcome>
      </el-tab-pane>
    </el-tabs>
    <CommonTopRight>
      <el-button type="primary" @click="$refs[active]?.submit()">保存配置</el-button>
    </CommonTopRight>
  </div>
</template>
<script>
import { addOrUpdate, getDetail } from './api'
export default {
  components: {
    agent: defineAsyncComponent(() => import('./agent.vue')),
    welcome: defineAsyncComponent(() => import('./welcome.vue')),
  },
  props: {},
  data() {
    return {
      active: 'agent',
      form: {},
      loading: false,
    }
  },
  watch: {},
  computed: {},
  created() {
    this.getDetail()
  },
  mounted() {},
  methods: {
    submit(data) {
      addOrUpdate(data).then(() => {
        this.msgSuccess('操作成功')
        this.getDetail()
      })
    },
    getDetail() {
      this.loading = true
      getDetail()
        .then(({ data }) => {
          this.form = data
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
  },
}
</script>

<style lang="scss" scoped>
.tips {
  color: var(--font-black-7);
  font-size: 12px;
}
</style>
