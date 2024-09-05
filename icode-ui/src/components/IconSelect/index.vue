<template>
  <div class="icon-body">
    <el-input
      v-model="name"
      style="position: relative"
      clearable
      placeholder="请输入图标名称"
      @clear="filterIcons"
      @input="filterIcons">
      <template #suffix><el-icon-search class="el-icon-search el-input__icon" /></template>
    </el-input>
    <div class="icon-list">
      <div v-for="(item, index) in iconList" :key="index" @click="selectedIcon(item)">
        <svg-icon :icon="item" style="height: 30px; width: 16px" />
        <span class="ml10">{{ item }}</span>
      </div>
    </div>
  </div>
</template>

<script>
// import icons from './requireIcons'
let icons = import.meta.glob('./../../assets/icons/svg/**/*.svg')
let iconList = []
for (const key in icons) {
  if (Object.hasOwnProperty.call(icons, key)) {
    let reg = 'svg/'
    iconList.push(key.slice(key.lastIndexOf(reg) + reg.length, -4).replace('/', '-'))
  }
}
export default {
  name: 'IconSelect',
  data() {
    return {
      name: '',
      iconList,
    }
  },
  methods: {
    filterIcons() {
      this.iconList = iconList
      if (this.name) {
        this.iconList = this.iconList.filter((item) => item.includes(this.name))
      }
    },
    selectedIcon(name) {
      this.$emit('selected', name)
      document.body.click()
    },
    reset() {
      this.name = ''
      this.iconList = iconList
    },
  },
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.icon-body {
  width: 100%;
  padding: 10px;
  .icon-list {
    height: 200px;
    overflow-y: scroll;
    div {
      height: 30px;
      line-height: 30px;
      margin-bottom: -5px;
      cursor: pointer;
      width: 33%;
      float: left;
    }
    span {
      display: inline-block;
      vertical-align: -0.15em;
      fill: currentColor;
      overflow: hidden;
    }
  }
}
</style>
