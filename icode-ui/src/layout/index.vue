<template>
  <div :class="classObj" class="app-wrapper" id="app-wrapper">
    <div class="main-wrap">
      <div class="main main-size">
        <div class="side-all" @mouseleave="mouseleave">
          <TopBar />
          <sidebar />
        </div>
        <div class="main-container">
          <breadcrumb />
          <AppMain />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AppMain from './components/AppMain'
import TopBar from './components/TopBar/index'
import Sidebar from './components/Sidebar/index.vue'
import Breadcrumb from './components/Breadcrumb'

import { mapState } from 'pinia'
import stores from '@/stores'

export default {
  name: 'Layout',
  components: {
    Breadcrumb,
    AppMain,
    TopBar,
    Sidebar,
  },
  computed: {
    //  ...mapGetters(['sidebar', 'avatar', 'device']),
    ...mapState(stores, {
      sidebar: (state) => state.app.sidebar,
      // needTagsView: (state) => state.settings.tagsView,
      // fixedHeader: (state) => state.settings.fixedHeader,
    }),
    visiableSidebarRouters() {
      let visibleRoutes = this.$store.sidebarRouters.filter((e) => !e.hidden)
      return visibleRoutes && visibleRoutes.length
    },
    isSidebar() {
      return this.visiableSidebarRouters && this.sidebar.opened
    },
    classObj() {
      return {
        openSidebar: this.isSidebar,
        hideSidebar: !this.isSidebar,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile',
      }
    },
  },
  methods: {
    handleClickOutside() {
      this.$store.closeSideBar({ withoutAnimation: false })
    },
    mouseleave() {
      document.getElementById('app-wrapper').classList.remove('menuHover')
      this.$store.app.showMenuIndex = this.$store.app.currentMenuIndex
    },
  },
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;

  .main-container {
    min-height: 100%;
    transition: all 0.28s;
    position: relative;
    flex: auto;
    overflow: auto;
    display: flex;
    flex-direction: column;
    margin-left: var(--side-bar-width);
  }

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
  .main-wrap {
    position: relative;
    height: 100vh;
    background: var(--color-light-11);
    .main {
      position: relative;
      height: 100%;
      // padding: 10px;
      display: flex;
      align-items: stretch;
      .side-all {
        position: relative;
      }
    }
  }
}

.hideSidebar {
  .main-container {
    margin-left: 0 !important;
  }
}

.dark {
  .main-wrap {
    background: var(--bg-black-11);
  }
}
</style>
