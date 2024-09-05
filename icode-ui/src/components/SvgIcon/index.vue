<!-- <svg-icon :icon="icon" style="font-size: 18px" /> -->
<template>
  <svg class="svg-icon" aria-hidden="true">
    <use :xlink:href="iconName" />
  </svg>
</template>

<script>
export default {
  name: 'SvgIcon',
  props: {
    icon: {
      type: String,
    },
    color: {
      type: String,
      default: '',
    },
  },
  // setup(props) {
  //   return {
  //     iconName: computed(() => `#icon-${props.icon }`),
  //   }
  // },
  computed: {
    iconName() {
      return `#icon-${this.icon}`
    },
  },
}
</script>

<style scoped>
.svg-icon {
  width: 1em;
  height: 1em;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}
</style>
