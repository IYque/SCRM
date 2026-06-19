<template>
	<div class="">
		<el-form
			ref="form"
			:rules="rules"
			:model="form"
			label-position="right"
			label-width="100px"
			:scroll-into-view-options="true">
			<el-form-item label="渠道名称" prop="chatCodeName">
				<el-input v-model="form.chatCodeName" maxlength="15" show-word-limit clearable placeholder="请输入"></el-input>
				<!-- <div class="g-tip">活码名称创建完成后不可修改</div> -->
			</el-form-item>
			<el-form-item label="活码客群:" prop="chatIds" :error="groupErrorTip">
				<el-select
					v-model="form.yqueChatList"
					value-key="chatId"
					multiple
					collapse-tags
					collapse-tags-tooltip
					:max-collapse-tags="3"
					placeholder="请选择">
					<el-option v-for="item in userList" :key="item.chatId" :label="item.chatName" :value="item" />
				</el-select>
			</el-form-item>

			<el-form-item label="自动备注">
				<el-input v-model="form.remark" maxlength="30" show-word-limit clearable placeholder="请输入"></el-input>
			</el-form-item>

		
			<el-form-item label="群满是否自动建群:">
				<el-switch v-model="form.autoCreateRoom" :active-value="1" :inactive-value="0"></el-switch>
				<!-- <div class="sub-des">默认以第一个群的群主作为新建群的群主</div> -->
			</el-form-item>
			<template v-if="form.autoCreateRoom" label="">
				<el-form-item label="群名前缀:" prop="roomBaseName">
					<el-input show-word-limit maxlength="20" v-model="form.roomBaseName" placeholder="请输入群名前缀"></el-input>
				</el-form-item>
				<el-form-item label="群起始序号:" prop="roomBaseId">
					<el-input-number v-model="form.roomBaseId" controls-position="right" :min="1"></el-input-number>
				</el-form-item>
			</template>

			

		</el-form>
	</div>
</template>

<script>
import { getGroupList, add, update } from './api'
import { dictMsgType } from '@/utils/index'

export default {
	props: { data: {} },
	data() {
		let isLink = location.href.includes('customerLink')
		return {
			rules: {
				chatCodeName: [{ required: true, message: '请输入活码名称', trigger: 'blur' }],
				users: [
					{
						required: true,
						message: '请选择',
						trigger: 'change',
						validator: (rule, value, callback) => {
							if (value.length == 0) {
								callback(new Error('请选择'))
							} else {
								callback()
							}
						},
					},
				],
				weclomeMsg: [{ required: false, message: '必填项', trigger: 'blur' }],
			},
			originForm: {
				beginTime: '',
				endTime: '',
				workCycle: [],
				weclomeMsg: '您好，很高兴为您服务，请问有什么可以帮您？',
			},
			form: {
				chatCodeName: '',
				chatIds:''
				// skipVerify: 1, // 自动通过
				// tags: [], // 标签
				// users: [], // 标签
				// remarkType: null, //客户备注
				// logoUrl: null, //活码logo
				// startPeriodAnnex: true,
			},

			selectedUserList: [],
			selectedTagList: [],

			userList: [],
			groupErrorTip: '',
			tagList: [],
			tagErrorTip: '',
			remarkList: [],
			annexLists: [],
			max: 9,
			active: 0,
			dictMsgType,

			isLink,
		}
	},
	computed: {
		j() {
			let { startPeriodAnnex, weclomeMsg, annexLists, periodAnnexLists } = this.form
			return { startPeriodAnnex, weclomeMsg, annexLists, periodAnnexLists }
		},
	},
	watch: {
		data: {
			deep: true,
			immediate: true,
			handler(val) {
				let element = JSON.parse(JSON.stringify(val))
				element.tags = []
				element.users = []

				if (element.tagId && element.tagName) {
					element.tagId = element.tagId.split(',')
					element.tagName = element.tagName.split(',')
					element.tagId.forEach((unit, index) => {
						element.tags.push({
							id: unit,
							name: element.tagName[index],
						})
					})
				}

				if (element.userId && element.userName) {
					element.userId = element.userId.split(',')
					element.userName = element.userName.split(',')
					element.userId.forEach((unit, index) => {
						element.users.push({
							id: unit,
							name: element.userName[index],
						})
					})
				}
				this.form = element
				setTimeout(() => {
					this.$refs.form.clearValidate()
				}, 0)
			},
		},
	},
	created() {
		this.getGroupList()
		let id = this.form.id
		if (id) {
			console.log(this.form)
			// this.getDetail(id)
		}
	},
	methods: {
		getGroupList() {
			getGroupList().then((res) => {
				if (res.code == 301) {
					this.groupErrorTip = res.msg
					return
				}
				this.userList = res.data || []
			})
		},

		/** 获取详情 */
		getDetail(id) {
			// findYjaiscrmMsgAnnexByMsgId(id).then((res) => {
			// 	console.log(res.data)
			// 	this.form.annexLists = res.data
			// })
			// this.form.startPeriodAnnex &&
			// 	findYjaiscrmMsgPeriodAnnexByMsgId(id).then((res) => {
			// 		console.log(res.data)
			// 		this.form.periodAnnexLists = res.data
			// 	})
		},

		async submit() {
			let valid = await this.$refs.form.validate()
			if (!valid) return
			let form = JSON.parse(JSON.stringify(this.form))
			console.log(form)
			// form.tagId = form.tags.map((e) => e.id) + ''
			// form.tagName = form.tags.map((e) => e.name) + ''
			// form.userId = form.users.map((e) => e.id) + ''
			// form.userName = form.users.map((e) => e.name) + ''

		
			return (form.id ? update : add)(form)
				.then(({ data }) => {
					this.msgSuccess('操作成功')
					// this.$router.back()
				})
				.finally(() => {
					this.$store.loading = false
				})
		},
	},
}
</script>

<style lang="scss" scoped></style>
