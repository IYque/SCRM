<template>
  <div class="PhoneInterface">
    <div class="line"></div>
    <div class="preview-bar" v-if="isTopBar">
      <el-icon-arrow-left class="icon el-icon-arrow-left" />
      {{ title }}
      <el-icon-MoreFilled class="icon el-icon-MoreFilled"></el-icon-MoreFilled>
    </div>
    <el-scrollbar class="preview-content" wrap-class="scrollbar-wrapper">
      <slot />
    </el-scrollbar>
    <div class="line2"></div>
  </div>
</template>
<script>
export default {
  components: {},
  props: {
    isTopBar: {
      type: Boolean,
      default: true,
    },
    title: {
      type: String,
      default: window.sysConfig.SYSTEM_NAME,
    },
  },
  data() {
    return {}
  },
  computed: {},
  created() {},
  mounted() {},
  methods: {},
}
</script>
<style lang="scss" scoped>
.PhoneInterface {
  display: flex;
  flex-direction: column;
  margin: 0 auto;
  position: relative;
  width: var(--phone-width, 350px);
  max-width: 375px;
  min-width: 320px;
  height: 720px;
  border: 12px solid var(--border-black-10);
  border-radius: 40px;
  box-sizing: content-box;
  overflow: hidden;
  background: var(--bg-white);
  // .preview-status {
  //   display: flex;
  //   justify-content: space-between;
  //   align-items: center;
  //   padding: 0 10px;
  // }
  .icon {
    font-size: 16px;
  }
  .line {
    flex: none;
    width: 100px;
    height: 25px;
    margin: 6px auto;
    border-radius: 50px;
    background: var(--bg-black-7);
    // border: 10px solid var(--border-black-7);
    // box-sizing: content-box;
    // border-width: 13px 62px;
  }
  .line2 {
    position: absolute;
    width: 120px;
    height: 5px;
    bottom: 0;
    left: 50%;
    margin: 15px auto;
    border-radius: 10px;
    background: var(--bg-black-7);
    transform: translateX(-50%);
    z-index: 10;
  }

  .preview-bar {
    // position: absolute;
    // top: 40px;
    // left: 50%;
    // transform: translateX(-50%);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 10px;
    height: 40px;
    width: 100%;
    flex: none;
    font-size: 14px;
    font-weight: 500;
    color: var(--font-black);
    border-bottom: 1px solid var(--border-black-11);
  }

  .preview-content {
    position: relative;
    flex: auto;
    overflow: hidden;
    top: 0;
    // left: 50%;
    // transform: translateX(-50%);
    border-radius: 0 0 20px 20px;
    background: var(--bg-black-11);
    ::v-deep img {
      max-width: 100%;
    }
  }

  ::v-deep {
    .el-scrollbar__wrap {
      overflow-x: hidden;
    }
    .el-scrollbar__view {
      height: 100%;
      overflow-x: hidden;
      overflow-y: auto;
    }
    .el-scrollbar__bar {
      transform: scale(1);
    }
  }
}
</style>
