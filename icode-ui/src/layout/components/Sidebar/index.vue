<!-- 服务包弹窗 -->
<template>
  <div class="sidebar-container">
    <el-menu
      ref="menu"
      :default-active="activeMenu"
      :collapse="isCollapse"
      :collapse-transition="false"
      mode="vertical">
      <div class="top-title-wrap" v-for="(item, uni) in topMenus" :key="uni" v-show="uni == $store.app.showMenuIndex">
        <div class="top-title ac">
          {{ item.meta.title.split('-')[1] }}
        </div>
        <div class="top-desc ac mt10 toe">
          {{ item.meta.title.split('-')[2] }}
        </div>
      </div>
      <el-scrollbar wrap-class="scrollbar-wrapper">
        <div class="'menuItem" v-for="(item, uni) in topMenus" :key="uni" v-show="uni == $store.app.showMenuIndex">
          <SidebarItem
            v-for="(route, index) in item.children"
            :key="route.path + index"
            :item="route"
            level="1"
            @openeds="open"
            :base-path="resolvePath(route.path, item)" />
        </div>
      </el-scrollbar>
    </el-menu>
  </div>
</template>

<script>
import path from 'path-browserify'
// import { mapState } from 'pinia'
// import stores from '@/stores'
import { navRoutes } from '@/router/routes'

import SidebarItem from './SidebarItem'

export default {
  components: { SidebarItem },
  computed: {
    // ...mapState(stores, ['topbarRouters', 'sidebarRouters', 'sidebar']),
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      setTimeout(() => {
        this.$store.app.showMenuIndex = this.$store.app.currentMenuIndex = this.topMenus.findIndex((e) =>
          path.startsWith(e.path),
        )
      }, 100)
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      } else if (route.meta.hidden) {
        return route.matched.slice(-2, -1)[0].path
      }
      return path
    },
    isCollapse() {
      // return !this.sidebar.opened
      return false
    },
    topMenus() {
      return navRoutes.filter((e) => !e.hidden)
    },
  },
  data() {
    return { openeds: [], current: 0 }
  },
  mounted() {
    // this.$nextTick(() => {
    //   console.log(this.topbarRouters)
    // })
  },
  methods: {
    open(v) {
      setTimeout(() => {
        this.$refs.menu.open(v)
      }, 0)
    },
    resolvePath(pathStr, parentRoute) {
      return path.resolve(parentRoute.path, pathStr)
    },
  },
}
</script>

<style lang="scss" scoped>
::v-deep.sidebar-container {
  position: absolute;
  display: inline-flex;
  flex-direction: column;
  transition: width 0.28s;
  width: var(--side-bar-width) !important;
  height: 100vh;
  background: var(--bg-white);
  bottom: 0;
  z-index: 1001;
  overflow-x: hidden;
  overflow-y: auto;
  flex: none;

  .top-title-wrap {
    padding: 20px 10px;
    border-bottom: 1px solid var(--border-black-10);
    border-right: 1px solid var(--border-black-10);
    margin-right: -1px;
    z-index: 1;
    .top-title {
      font-size: 16px;
      font-weight: bold;
    }
    .top-desc {
      color: var(--font-black-5);
      font-size: 12px;
    }
  }

  // reset element-ui css
  .horizontal-collapse-transition {
    transition: 0s width ease-in-out, 0s padding-left ease-in-out, 0s padding-right ease-in-out;
  }

  .scrollbar-wrapper {
    overflow-x: hidden !important;
  }

  // .el-scrollbar__bar.is-vertical {
  //   right: 0px;
  // }

  .el-scrollbar {
    flex: auto;
  }

  &.has-logo {
    .el-scrollbar {
      height: calc(100% - 50px);
    }
  }

  .is-horizontal {
    display: none;
  }

  // .svg-icon {
  //   margin-right: 16px;
  // }

  .el-menu {
    border: none;
    height: 100%;
    width: 100% !important;
  }
  .menuItem {
    display: none;
  }

  // 叶子菜单，目录
  .el-menu-item,
  .el-sub-menu > .el-sub-menu__title {
    padding: 0 !important;
    // margin: 4px auto;
    margin: 4px 10px;
    font-weight: 500;
    height: 40px;
    line-height: 40px;
    i {
      color: inherit;
    }
    // background-color: $subMenuBg !important;
    &:hover {
      color: var(--color);
      background-color: transparent !important;
    }
  }
  // 叶子菜单
  .el-menu-item {
    // margin: 4px 16px;
    min-width: auto;
    // a {
    //   &:hover {
    //     color: inherit !important;
    //   }
    // }
  }
  // 当前激活菜单
  .el-menu-item.is-active {
    // background: linear-gradient(to right, var(--color) 0%, var(--color-sub) 100%);
    // box-shadow: 0px 13px 16px 0px hsl(var(--hsl-dark), 0.2);
    border-radius: 8px;
    color: var(--color, #fff) !important;
    z-index: 1;
  }

  // 当前激活菜单所属上级菜单标题目录
  .is-active > .el-sub-menu__title {
    color: var(--color) !important;
  }
}

.hideSidebar {
  .sidebar-container {
    width: 0 !important;
  }

  .submenu-title-noDropdown {
    padding: 0 !important;
    position: relative;

    .el-tooltip {
      padding: 0 !important;

      .svg-icon {
        margin-left: 20px;
      }
    }
  }

  .el-sub-menu {
    overflow: hidden;

    & > .el-sub-menu__title {
      padding: 0 !important;

      .svg-icon {
        margin-left: 20px;
      }

      .el-sub-menu__icon-arrow {
        display: none;
      }
    }
  }

  .el-menu--collapse {
    .el-sub-menu {
      & > .el-sub-menu__title {
        & > span {
          height: 0;
          width: 0;
          overflow: hidden;
          visibility: hidden;
          display: inline-block;
        }
      }
    }
  }
}
.menuHover {
  .sidebar-container {
    width: var(--side-bar-width) !important;
  }
}
</style>
