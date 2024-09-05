<script setup lang="ts"></script>

<template>
  <div class="common-top-right">
    <slot></slot>
  </div>
</template>

<style lang="scss" scoped>
.common-top-right {
  position: fixed;
  right: 20px;
  top: 24px;
  z-index: 10;
}
</style>
