// 导航面包屑
<template>
  <div class="breadcrumb fxbw aic">
    <div>
      <transition name="breadcrumb">
        <div class="title">
          {{ transformTitle() }}
        </div>
      </transition>

      <el-popover v-if="busininessDesc" placement="bottom-start" :disabled="disabled" trigger="hover">
        <div ref="desc" v-html="busininessDesc"></div>
        <template #reference><div class="desc toe" v-html="transformDesc"></div></template>
      </el-popover>
    </div>
  </div>
</template>

<script>
// import pathToRegexp from 'path-to-regexp'

export default {
  data() {
    return {
      levelList: null,
      disabled: false,
      transformDesc: '',
    }
  },
  watch: {
    $route(route, rw) {
      // 未知的路由自跳转，临时解决方案
      if (route.path == rw.path) {
        return
      }
      // if you go to the redirect page, do not update the breadcrumbs
      if (route.path.startsWith('/redirect/')) {
        return
      }
      this.$store.app.busininessDesc = ''
    },
  },
  computed: {
    busininessDesc() {
      this.$nextTick(() => {
        if (!this.$refs.desc) {
          return ''
        }
        let innerText = this.$refs.desc.innerText
        let index = this.$refs.desc.innerText.indexOf('\n')
        this.disabled = index <= 0
        if (index > 0) {
          this.transformDesc = innerText.substring(0, index) + ' ...'
        } else {
          this.transformDesc = innerText
        }
      })
      return this.$store.app.busininessDesc
    },
  },
  created() {},
  methods: {
    transformTitle() {
      // 对于详情和添加为同一个路由的面包屑处理
      // 匹配菜单标题中带有 {*} 的公用路由
      let reg = /\{(.*)\}/gi
      let title = this.$route.meta.title
      let parentRoute = this.$route.matched.slice(-2, -1)?.[0]
      if (reg.test(title)) {
        let parentRouteTitle = title.length == 4 ? parentRoute?.meta?.title : ''
        let query = this.$route.query
        if (query && query.id) {
          if (query.isDetail) {
            // 带路由查询id和isDetail参数的替换 {*} 为详情 eg:活码{添加} 替换为 活码详情
            title = parentRouteTitle + title.replace(reg, '详情')
          } else {
            // 带路由查询id参数的替换 {*} 为编辑 eg:活码{添加} 替换为 活码编辑
            title = title.replace(reg, '编辑') + parentRouteTitle
          }
        } else {
          // 默认情况下都替换为 新建
          title = title.replace(reg, '新建') + parentRouteTitle
        }
      } else if (title == '列表') {
        title = parentRoute?.meta?.title
      } else if (['详情', '详情统计'].includes(title)) {
        title = parentRoute?.meta?.title + title
      }
      return title
    },
  },
}
</script>

<style lang="scss" scoped>
.breadcrumb {
  padding: 0 20px;
  height: 80px;
  background: var(--bg-white);
  .title {
    font-size: 20px;
    font-weight: bold;
  }
  .desc {
    color: var(--font-black-5);
    margin-top: 9px;
    font-size: 12px;
    height: 1.5em;
  }
}
</style>
