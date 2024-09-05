<template>
  <div class="TopBar">
    <logo />
    <TopMenu />

    <div class="bottom">
      <el-dropdown class="avatar-container right-menu-item" trigger="click">
        <div class="avatar-wrapper">
          <svg-icon icon="user" style="font-size: 18px" />
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="logout">
              <span>退出登录</span>
            </el-dropdown-item>
            <!-- <el-dropdown-item @click="$store.app.isDark = !$store.app.isDark">
              <template v-if="$store.app.isDark">
                <el-icon-Moon class="el-icon-Moon right-icon mr5" />
                深色
              </template>
              <template v-else>
                <el-icon-Sunny class="el-icon-Sunny right-icon mr5" />
                浅色
              </template>
            </el-dropdown-item> -->
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { isExternal } from '@/utils/validate'
import Logo from './Logo'

import TopMenu from './TopMenu'

export default {
  data() {
    return {
      setting: false,
    }
  },
  components: {
    Logo,
    TopMenu,
  },
  computed: {},
  mounted() {},
  methods: {
    async logout() {
      this.$confirm('确定退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        this.$store.LogOut()
      })
    },

    goto(url) {
      window.open(url)
    },
    goLink(path) {
      if (!isExternal(path)) {
        this.$router.push({ path })
      } else {
        window.open(path, '_blank')
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.hover-class {
  display: flex;
  flex-direction: column;
  align-items: center;
  span:nth-child(1) {
    font-size: 18px;
    font-weight: bold;
    line-height: 36px;
    margin-bottom: 10px;
  }
  img {
    width: 160px;
    height: 160px;
  }
  span:nth-child(3) {
    font-size: 14px;
    line-height: 16px;
    margin-top: 20px;
  }
}
.right-icon {
  font-size: 20px;
}
//
.TopBar {
  flex: none;
  display: inline-flex;
  align-items: center;
  flex-direction: column;
  justify-content: space-between;
  width: 80px;
  height: 100vh;
  padding: 20px 0;
  overflow: hidden;
  position: relative;
  color: var(--font-black);
  background: var(--bg-white);
  box-shadow: 0px 0px 5px 0px var(--border-black-9);
  z-index: 1002;
  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .bottom {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: none;
    background: var(--bg-white);
    border-top: 1px solid var(--border-black-10);
    width: 100%;
    text-align: center;

    .right-menu-item {
      // color: var(--font-black-4);
      margin-top: 25px;
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        color: var(--color);
      }
    }

    .avatar-container {
      // margin-right: 30px;

      .avatar-wrapper {
        position: relative;
        .user-avatar {
          ---temp: #fafafa;
          display: inline-block;
          position: relative;
          width: 34px;
          height: 34px;
          border-radius: 8px;
          vertical-align: middle;
          // background: var(---temp);
          border: 1px solid var(---temp);
        }
        .user-name {
          display: inline-block;
          max-width: 100px;
          vertical-align: middle;
          font-weight: 500;
          margin-left: 8px;
        }

        .el-icon-arrow-down {
          margin-left: 3px;
          font-size: 12px;
          color: var(--font-black);
        }
      }
    }
  }
}

// .nav-scrollbar {
//   width: calc(100% - 450px);
//   ::v-deep.el-scrollbar__view {
//     white-space: nowrap;
//     line-height: 58px;
//   }
//   .nav {
//     display: inline-block;
//     margin: 0 20px;
//     flex: none;
//     position: relative;
//     cursor: pointer;
//     &.active::after {
//       content: '';
//       display: inline-block;
//       position: absolute;
//       bottom: 2px;
//       width: 42px;
//       height: 2px;
//       left: 50%;
//       transform: translateX(-50%);
//       border-radius: 6px;
//       background: var(--bg-white);
//     }
//   }
// }

.contact-us {
  position: fixed;
  top: 60%;
  width: 100px;
  height: 100px;
  z-index: 99;
  right: 28px;
  width: 50px;
  height: 50px;
  background: linear-gradient(225deg, var(--color-sub) 0%, var(--color) 100%);
  box-shadow: 0px 4px 20px 0px rgba(28, 49, 111, 0.1);
  border-radius: 50%;
  // background: var(--bg-white);
  // color: var(--color);
  .contact-us-icon {
    color: var(--font-white, #fff);
    // color: inherit;
    font-size: 30px;
  }
  // &:hover {
  //   color: var(--font-white, #fff);
  //   background: linear-gradient(225deg, var(--color-sub) 0%, var(--color) 100%);
  //   transition: all 0.3s;
  // }
}
.contact-code {
  width: 118px;
}
</style>
