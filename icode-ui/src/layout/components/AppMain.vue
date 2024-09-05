<template>
	<!-- <el-scrollbar class="page-scrollbar"> -->
	<div class="app-main">
		<div class="page-wrap" v-loading="$store.loading">
			<!-- <RouterView class="page container" id="page-container" /> -->
			<router-view v-slot="{ Component }" class="page container" id="page-container">
				<!-- <transition name="fade-transform" mode="out-in">
          <keep-alive :key="route.path">
            <component :is="Component" />
          </keep-alive>
        </transition> -->
				<component :is="Component" :key="$route.path" />
			</router-view>
			<div class="copyright">
				<span v-html="sysConfig.SYSTEM_SLOGAN"></span>
			</div>
		</div>
	</div>
	<!-- </el-scrollbar> -->
</template>
<script>
// import { registerMicroApps, start } from 'qiankun'
export default {
	name: 'AppMain',
	data() {
		return {
			loading: false,
		}
	},
	computed: {},
	mounted() {},
}
</script>

<style lang="scss" scoped>
// ::v-deep .el-scrollbar__wrap {
//   margin: 20px 0;
//   padding: 0 20px;
// }
.app-main {
	position: relative;
	flex: 0 1 auto;
	// overflow: hidden;
	overflow-y: auto;
	overflow-x: hidden;
	margin-top: 20px;
	height: 100%;
}
.page-wrap {
	position: initial !important; // 用以解决加载遮罩层随内容滚动问题
	height: 100%;
	overflow-y: auto;
	overflow-x: hidden;
	scroll-behavior: smooth;
	--gap: 0 15px 0 20px;
	padding: var(--gap);
	::v-deep > .el-loading-mask {
		margin: var(--gap);
	}
}
.page {
	position: initial !important; // 用以解决加载遮罩层随内容滚动问题
	::v-deep > .el-loading-mask {
		margin: var(--gap);
	}
}
.micro-app-wrap {
	position: relative;
}
.copyright {
	width: 100%;
	position: absolute;
	text-align: center;
	bottom: 10px;
	color: var(--font-black-5);
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
	.fixed-header {
		padding-right: 15px;
	}
}
#micro-app > div {
	height: 100%;
}
</style>
