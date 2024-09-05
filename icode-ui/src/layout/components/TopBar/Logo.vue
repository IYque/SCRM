<template>
  <transition name="sidebarLogoFade">
    <div class="logo-container" :class="{ collapse: collapse }">
      <router-link key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="sysConfig.LOGO" :src="$store.app.isDark ? sysConfig.LOGO : sysConfig.LOGO" class="sidebar-logo" />
        <div class="sidebar-title">{{ sysConfig.SYSTEM_NAME }}</div>
      </router-link>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
    },
  },
  components: {},
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
    }
  },
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.logo-container {
  position: relative;
  flex: 0 1 50px;
  display: flex;
  align-items: center;
  & .sidebar-logo-link {
    & .sidebar-logo {
      max-height: 44px;
      max-width: 30px;
    }

    & .sidebar-title {
      color: var(--font-black);
      font-weight: 600;
      line-height: 20px;
      font-size: 16px;
      vertical-align: middle;
      text-align: center;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }

  .el-icon-s-operation {
    color: var(--font-black);
    font-size: 20px;
  }
}
</style>
