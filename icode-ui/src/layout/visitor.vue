<script setup>
import { RouterView } from 'vue-router'
</script>
<script>
export default {
  name: '',
  components: {},
  data() {
    return {}
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {},
}
</script>

<template>
  <div class="visitor">
    <router-view></router-view>

    <!--  底部  -->
    <!-- <div class="login-footer">
      <span v-html="sysConfig.COPYRIGHT"></span>
    </div> -->
  </div>
</template>

<style lang="scss" scoped>
.contact-code {
  width: 80px;
}
.visitor {
  height: 100vh;
  background: var(--bg-black-10);
  .login-top--wrap {
    background: var(--bg-white);
    padding: 5px 20px;
    box-shadow: 0px 5px 5px rgb(215 215 215 / 35%);
  }

  .logo-top--wrap {
    display: flex;
    align-items: center;
    .logo-top {
      max-height: 44px;
      max-width: 170px;
    }
  }

  .login-nav--wrap {
    .nav-li {
      color: var(--font-black-7);
      margin-left: 20px;
    }
  }
}

.login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  // color: var(--font-white, #fff);
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
  z-index: 90;
}
</style>
