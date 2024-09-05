<script>
import { getDetailWel, addOrUpdateWel } from './api'
export default {
	data() {
		return {
			loading: false,
			form: { weclomeMsg: '' },
		}
	},
	computed: {
		chatList() {
			return [
				{
					text: this.form.weclomeMsg,
				},
				...(this.form.annexLists || []),
			]
		},
	},
	created() {
		this.getDetail()
	},
	methods: {
		getDetail() {
			this.loading = true
			getDetailWel()
				.then(({ data }) => {
					data.weclomeMsg = data.defaultContent
					this.form = data
					this.loading = false
				})
				.catch(() => {
					this.loading = false
				})
		},
		async submit() {
			let form = await this.$refs.WelcomeForm.submit()
			form.defaultContent = form.weclomeMsg
			addOrUpdateWel(form).then(() => {
				this.msgSuccess('操作成功')
				this.getDetail()
			})
		},
	},
}
</script>
<template>
	<div class="fxbw">
		<div class="g-card fxauto">
			<WelcomeForm :data="form" ref="WelcomeForm"></WelcomeForm>
		</div>
		<PhoneChatList class="g-margin-l" :list="chatList"></PhoneChatList>
	</div>
</template>

<style lang="scss" scoped>
:deep(.el-tabs__new-tab) {
	width: auto;
}
</style>
