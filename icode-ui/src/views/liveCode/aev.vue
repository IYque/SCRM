<template>
	<div class="">
		<el-form
			ref="form"
			:rules="rules"
			:model="form"
			label-position="right"
			label-width="100px"
			:scroll-into-view-options="true">
			<el-form-item label="渠道名称" prop="codeName">
				<el-input v-model="form.codeName" maxlength="15" show-word-limit clearable placeholder="请输入"></el-input>
				<!-- <div class="g-tip">活码名称创建完成后不可修改</div> -->
			</el-form-item>

			<el-form-item label="活码员工" prop="users" :error="userErrorTip">
				<el-select
					v-model="form.users"
					value-key="id"
					multiple
					collapse-tags
					collapse-tags-tooltip
					:max-collapse-tags="3"
					placeholder="请选择">
					<el-option v-for="item in userList" :key="item.id" :label="item.name" :value="item" />
				</el-select>
			</el-form-item>

			<el-form-item label="免验证">
				<el-switch v-model="form.skipVerify"></el-switch>
				<div class="g-tip">（注:勾选后,客户添加员工好友无需员工确认）</div>
			</el-form-item>

			<template v-if="!isLink">
				<el-form-item label="重复添加">
					<el-switch v-model="form.isExclusive"></el-switch>
					<div class="g-tip">（注:开启后，同一个企业的客户会优先添加到同一个跟进人）</div>
				</el-form-item>

				<el-form-item label="二维码logo" prop="logoUrl">
					<Upload v-model:fileUrl="form.logoUrl" :on-remove="handleRemove">
						<template #tip><div>图片大小不超过2M</div></template>
					</Upload>
				</el-form-item>
			</template>

			<el-form-item label="新客标签" :error="tagErrorTip">
				<el-select
					v-model="form.tags"
					value-key="id"
					multiple
					collapse-tags
					collapse-tags-tooltip
					:max-collapse-tags="3"
					placeholder="请选择">
					<el-option v-for="item in tagList" :key="item.id" :label="item.name" :value="item" />
				</el-select>
			</el-form-item>
			<el-form-item label="自动备注">
				<el-select v-model="form.remarkType" value-key="id" placeholder="请选择">
					<el-option v-for="item in remarkList" :key="item.key" :label="item.val" :value="item.key"></el-option>
				</el-select>
				<div class="g-tip">
					（注:选择后，添加的客户会根据所选自动备注如：【客户名-渠道名】,如果选择为标签类型,则新客标签需要存在）
				</div>
			</el-form-item>

			<WelcomeForm :data="j" ref="WelcomeForm"></WelcomeForm>
		</el-form>
	</div>
	<!-- <CommonTopRight>
      <el-button type="primary" size="default" @click="submit()">确定</el-button>
    </CommonTopRight> -->
</template>

<script>
import * as api from './api'
import * as apiLink from './apiLink'
import { getUserList, getTagList, getRemarkList } from '@/api/common'
import { dictMsgType } from '@/utils/index'

let { findIYqueMsgAnnexByMsgId, findIYqueMsgPeriodAnnexByMsgId, add, update } = {}

export default {
	props: { data: {} },
	data() {
		let isLink = location.href.includes('customerLink')
		let _ = ({ findIYqueMsgAnnexByMsgId, findIYqueMsgPeriodAnnexByMsgId, add, update } = isLink ? apiLink : api)
		return {
			rules: {
				codeName: [{ required: true, message: '请输入活码名称', trigger: 'blur' }],
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
				codeName: '',
				skipVerify: 1, // 自动通过
				tags: [], // 标签
				users: [], // 标签
				remarkType: null, //客户备注
				logoUrl: null, //活码logo
				startPeriodAnnex: true,
			},

			selectedUserList: [],
			selectedTagList: [],

			userList: [],
			userErrorTip: '',
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
		this.getUserList()
		this.getTagList()
		this.getRemarkList()
		// let id = this.$route.query.id
		// if (id) {
		//   this.getDetail(id)
		// }
		let id = this.form.id
		if (id) {
			this.getDetail(id)
		}
	},
	methods: {
		getUserList() {
			getUserList().then((res) => {
				if (res.code == 301) {
					this.userErrorTip = res.msg
					return
				}
				this.userList = res.data || []
			})
		},
		getTagList() {
			getTagList().then((res) => {
				if (res.code == 301) {
					this.tagErrorTip = res.msg
					return
				}
				this.tagList = res.data || []
			})
		},

		getRemarkList() {
			getRemarkList().then((res) => {
				this.remarkList = res.data || []
			})
		},

		/** 获取详情 */
		getDetail(id) {
			findIYqueMsgAnnexByMsgId(id).then((res) => {
				console.log(res.data)
				this.form.annexLists = res.data
			})
			this.form.startPeriodAnnex &&
				findIYqueMsgPeriodAnnexByMsgId(id).then((res) => {
					console.log(res.data)
					this.form.periodAnnexLists = res.data
				})
		},

		async submit() {
			let valid = await this.$refs.form.validate()
			if (!valid) return
			let form = JSON.parse(JSON.stringify(this.form))
			form.tagId = form.tags.map((e) => e.id) + ''
			form.tagName = form.tags.map((e) => e.name) + ''
			form.userId = form.users.map((e) => e.id) + ''
			form.userName = form.users.map((e) => e.name) + ''

			let WelcomeForm = await this.$refs.WelcomeForm.submit()
			Object.assign(form, WelcomeForm)
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
