<template>
  <!-- <el-scrollbar class="nav-scrollbar"> -->
  <el-menu :default-active="activeMenu" mode="vertical" @select="handleSelect">
    <el-menu-item
      v-for="(item, index) in topbarRouters"
      :index="item.path"
      :key="index"
      @mouseenter="mouseenter(item, index)">
      <svg-icon v-if="item.meta.icon" :icon="item.meta.icon" />
      <div>
        {{ item.meta.title }}
      </div>
    </el-menu-item>
  </el-menu>
  <!-- </el-scrollbar> -->
</template>

<script>
import { isExternal } from '@/utils/validate'
import { navRoutes } from '@/router/routes'
export default {
  data() {
    return {
      // name: '',
      // 当前激活菜单的
      currentIndex: '',
    }
  },
  computed: {
    // 顶部显示菜单
    topbarRouters() {
      return navRoutes.filter((menu) => {
        if (!menu.hidden) {
          menu.isShowChild = menu.children?.some?.((e) => !e.hidden)
          return true
        }
      })
    },
    // 激活的/高亮的一级菜单路径 eg：/drainageCode
    activeMenu() {
      const path = this.$route.path
      let activePath = path.match(/\/[^\/]+/)[0] // eg: /a
      this.activeRoutes(activePath)
      return activePath
    },
  },
  mounted() {},
  methods: {
    // 菜单选择事件
    handleSelect(path, keyPath) {
      if (isExternal(path)) {
        // http(s):// 路径新窗口打开
        window.open(path, '_blank')
      } else if (path.indexOf('/redirect') !== -1) {
        // /redirect 路径内部打开
        this.$router.push({ path: path.replace('/redirect', '') })
      }
      //  else if (path.includes('/#/')) {
      //   location.href = window.sysConfig.BASE_URL.slice(window.sysConfig.BASE_URL.indexOf('/'), -1) + path
      // }
      else {
        this.activeRoutes(path)

        this.$router.push({ path })
      }
    },
    // 当前激活的侧边栏菜单
    activeRoutes(path) {
      let activeRoute = this.topbarRouters.find((e) => e.path == path)
      let routes = (activeRoute && activeRoute.children) || []
      this.$store.app.sidebarRouters = routes
      this.$store.app.sidebarRouters.parentRoute = activeRoute
      return routes
    },
    mouseenter(item, index) {
      if (item.isShowChild) {
        document.getElementById('app-wrapper').classList.add('menuHover')
        this.$store.app.showMenuIndex = index
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.g-bg-lg {
  // background: linear-gradient(to left, var(--color) 0%, var(--color-sub) 100%);
  // box-shadow: 0px 13px 16px 0px hsl(var(--hsl-dark), 0.2);
}
// .nav-scrollbar {
//   width: calc(100% - 580px);
//   ::v-deep.el-scrollbar__view {
//     white-space: nowrap;
//   }
// }
.el-menu {
  background-color: initial;
  border: none;
  flex: 1 1 1200px;
  z-index: 0;
  height: auto;
  margin: 20px 0;
  overflow: auto;
  & > .el-menu-item {
    display: block;
    height: 65px;
    line-height: 1 !important;
    text-align: center;
    color: var(--font-black-5);
    border-radius: 8px;
    font-weight: bold;
    margin: 0 4px !important;
    border: 0;
    padding: 0px !important;
    float: none;
    .svg-icon {
      font-size: 26px;
      margin-bottom: 5px;
    }
    &::after {
      content: '';
      display: block;
      opacity: 0;
      position: absolute;
      top: 0;
      left: 0;
      z-index: -1;
      width: 100%;
      height: 100%;
      border-radius: 8px;
      transition: opacity 0.3s;
      @extend .g-bg-lg;
    }
    &:not(.is-disabled) {
      &:hover,
      &:focus {
        &::after {
          opacity: 1;
        }
        background-color: initial;
        color: var(--color, #fff);
      }
    }
  }

  & > .el-menu-item.is-active,
  & > .el-submenu.is-active .el-submenu__title {
    @extend .g-bg-lg;
    color: var(--color, #fff) !important;
    border: 0;
  }

  // /* submenu item */
  // & > .el-submenu .el-submenu__title {
  //   float: left;
  //   height: 50px !important;
  //   line-height: 50px !important;
  //   color: #999093 !important;
  //   padding: 0 5px !important;
  //   margin: 0 10px !important;
  // }
}
</style>
