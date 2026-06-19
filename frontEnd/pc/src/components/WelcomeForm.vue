<script>
import { dictMsgType } from '@/utils/index'
export default {
	name: '',
	props: {
		data: { type: Object, default: () => {} },
	},
	data() {
		return {
			dictMsgType,
			max: 9,
			active: 0,
			form: {},
			originForm: {
				beginTime: '',
				endTime: '',
				workCycle: [],
				weclomeMsg: '欢迎使用yjaiscrm scrm👉http://yjaiscrm.cn',
			},
			rules: {
				weclomeMsg: [{ required: true, message: '必填项', trigger: 'blur' }],
			},
		}
	},
	computed: {},
	watch: {
		data: {
			deep: true,
			immediate: true,
			handler(val) {
				let element = JSON.parse(JSON.stringify(val))
				if (!element?.annexLists) {
					element.annexLists = []
				}
				if (!element?.periodAnnexLists) {
					element.periodAnnexLists = [JSON.parse(JSON.stringify(this.originForm))]
				} else {
					element.periodAnnexLists.forEach((unit, index) => {
						unit.workCycle = unit.workCycle?.split(',') || []
					})
				}
				this.form = element
			},
		},
	},
	created() {},
	mounted() {},
	methods: {
		add(msgtype, item, index) {
			if (this.form.startPeriodAnnex) {
				item.periodMsgAnnexList ??= []
				item.active = item.periodMsgAnnexList.push({ msgtype, [msgtype]: {} }) - 1
				setTimeout(() => {
					this.scrollIntoView(this.$refs.bottom[index])
				}, 100)
			} else {
				this.active = this.form.annexLists?.push({ msgtype, [msgtype]: {} }) - 1
				setTimeout(() => {
					this.scrollIntoView(this.$refs.bottom)
				}, 100)
			}
		},
		remove(nameIndex, item, index) {
			this.$confirm().then(() => {
				if (this.form.startPeriodAnnex) {
					item.periodMsgAnnexList.splice(nameIndex, 1)
					if (nameIndex <= item.active) {
						item.active = item.periodMsgAnnexList.length - 1
					}
					if (item.periodMsgAnnexList.length == 0) {
						this.$refs.form.clearValidate('weclomeMsg')
					}
				} else {
					this.form.annexLists?.splice(nameIndex, 1)
					if (nameIndex <= this.active) {
						this.active = this.form.annexLists?.length - 1
					}
					if (this.form.annexLists?.length == 0) {
						this.$refs.form.clearValidate('weclomeMsg')
					}
				}
			})
		},
		async submit() {
			let form = JSON.parse(JSON.stringify(this.form))
			if (form.startPeriodAnnex) {
				form.annexLists = []

				let contentFormNum = 0
				let tasks = form.periodAnnexLists?.map(async (item, index) => {
					item.workCycle += ''
					let tasks1 = item.periodMsgAnnexList?.map(async (e, i) => {
						let contentForm = await this.$refs.contentForm[contentFormNum++].submit()
						if (contentForm) {
							e[e.msgtype] = Object.assign(e[e.msgtype] || {}, contentForm)
							return true
						} else {
							return false
						}
					})

					let validate1 = tasks1 ? await Promise.all(tasks1) : true
					return validate1
				})
				let validate1 = tasks ? await Promise.all(tasks) : true
			} else {
				form.periodAnnexLists = []
				let tasks = form.annexLists.map(async (e, i) => {
					let contentForm = await this.$refs.contentForm[i].submit()
					if (contentForm) {
						e[e.msgtype] = Object.assign(e[e.msgtype] || {}, contentForm)
						return true
					} else {
						return false
					}
				})
				let validate1 = tasks ? await Promise.all(tasks) : true
			}
			return form
		},
		scrollIntoView(el) {
			el.scrollIntoViewIfNeeded
				? el.scrollIntoViewIfNeeded(false)
				: el.scrollIntoView({ behavior: 'smooth', block: 'end' })
		},
	},
}
</script>

<template>
	<el-form
		ref="form"
		:model="form"
		label-position="right"
		label-width="100px"
		:rules="rules"
		:scroll-into-view-options="true">
		<el-form-item label="分时段">
			<el-switch v-model="form.startPeriodAnnex"></el-switch>
			<div class="g-tip">开启后，根据添加时间发送当前时段欢迎语</div>
		</el-form-item>

		<template v-if="!form.startPeriodAnnex">
			<el-form-item label="欢迎语" prop="weclomeMsg" :required="form.annexLists?.length > 0">
				<TextareaExtend
					v-model="form.weclomeMsg"
					:toolbar="['emoji', 'insertCustomerNickName']"
					maxlength="200"
					show-word-limit
					placeholder="请输入"
					:autosize="{ minRows: 5, maxRows: 20 }"
					clearable
					:autofocus="false" />
			</el-form-item>
			<el-form-item label="欢迎语附件" prop="">
				<el-popover
					trigger="hover"
					:content="'最多添加' + max + '个'"
					placement="top-start"
					:disabled="form.annexLists?.length < max">
					<template #reference>
						<el-dropdown @command="add" :disabled="form.annexLists?.length >= max">
							<el-button type="primary" class="mb10">添加</el-button>
							<template #dropdown>
								<el-dropdown-menu trigger="click">
									<el-dropdown-item v-for="(item, index) in dictMsgType" :key="index" :command="item.type">
										<el-button text>{{ item.name }}</el-button>
									</el-dropdown-item>
								</el-dropdown-menu>
							</template>
						</el-dropdown>
					</template>
				</el-popover>
				<el-alert
					title="注: 1.图片:10MB,支持JPG,PNG格式; 2.视频:10MB,支持MP4格式; 3.普通文件:20MB"
					type="error"
					:closable="false"></el-alert>
				<br />
				<el-tabs ref="tabs" v-model="active" type="card" class="" closable @tab-remove="remove">
					<el-tab-pane
						v-for="(item, index) in form.annexLists"
						:key="item.msgtype"
						:label="dictMsgType[item.msgtype].name"
						:name="index">
						<MessageContentForm :type="item.msgtype" ref="contentForm" :form="item[item.msgtype]" />
					</el-tab-pane>
				</el-tabs>
				<div ref="bottom"></div>
			</el-form-item>
		</template>

		<!-- 时段欢迎语 -->
		<el-form-item required v-if="form.startPeriodAnnex" label="欢迎语">
			<template
				v-for="(item, index) in form.periodAnnexLists.map((e) => ((e.active ??= 0), e))"
				:key="index + 'welcom'">
				<el-card class="roster-card">
					<el-button
						class="fr"
						v-if="index !== 0"
						text
						icon="el-icon-delete"
						@click="
							$confirm().then(() => {
								form.periodAnnexLists.splice(index, 1)
							})
						">
						删除
					</el-button>
					<el-form-item label="工作周期">
						<el-checkbox-group v-model="item.workCycle">
							<el-checkbox label="1">周一</el-checkbox>
							<el-checkbox label="2">周二</el-checkbox>
							<el-checkbox label="3">周三</el-checkbox>
							<el-checkbox label="4">周四</el-checkbox>
							<el-checkbox label="5">周五</el-checkbox>
							<el-checkbox label="6">周六</el-checkbox>
							<el-checkbox label="7">周日</el-checkbox>
						</el-checkbox-group>
					</el-form-item>
					<el-form-item label="时间段">
						<el-time-select
							v-model="item.beginTime"
							v-bind="{
								start: '00:00',
								end: '23:59',
								step: '00:30',
							}"
							style="width: 160px"
							:max-time="item.endTime"
							placeholder="选择时间"></el-time-select>
						——
						<el-time-select
							v-bind="{
								start: '00:00',
								end: '23:59',
								step: '00:30',
							}"
							style="width: 160px"
							:min-time="item.beginTime || null"
							v-model="item.endTime"
							placeholder="选择时间"></el-time-select>
					</el-form-item>
					<el-form-item label="欢迎语">
						<TextareaExtend
							v-model="item.weclomeMsg"
							:toolbar="['emoji', 'insertCustomerNickName']"
							maxlength="200"
							show-word-limit
							placeholder="请输入欢迎语"
							:autosize="{ minRows: 5, maxRows: 20 }"
							clearable />
					</el-form-item>
					<el-form-item label="欢迎语附件" prop="" style="margin-bottom: 0">
						<el-popover
							trigger="hover"
							:content="'最多添加' + max + '个'"
							placement="top-start"
							:disabled="item.periodMsgAnnexList?.length < max">
							<template #reference>
								<el-dropdown
									@command="(msgtype) => add(msgtype, item, index)"
									:disabled="item.periodMsgAnnexList?.length >= max">
									<el-button type="primary" class="mb10">添加</el-button>
									<template #dropdown>
										<el-dropdown-menu trigger="click">
											<el-dropdown-item v-for="(unit, unique) in dictMsgType" :key="unique" :command="unit.type">
												<el-button text>{{ unit.name }}</el-button>
											</el-dropdown-item>
										</el-dropdown-menu>
									</template>
								</el-dropdown>
							</template>
						</el-popover>
						<el-alert
							title="注: 1.图片:10MB,支持JPG,PNG格式; 2.视频:10MB,支持MP4格式; 3.普通文件:20MB"
							type="error"
							:closable="false"></el-alert>
						<br />
						<el-tabs
							ref="tabs"
							v-model="item.active"
							type="card"
							class=""
							closable
							@tab-remove="(name) => remove(name, item, index)">
							<el-tab-pane
								v-for="(unit, unique) in item.periodMsgAnnexList"
								:key="unit.msgtype"
								:label="dictMsgType[unit.msgtype].name"
								:name="unique">
								<MessageContentForm :type="unit.msgtype" ref="contentForm" :form="unit[unit.msgtype]" />
							</el-tab-pane>
						</el-tabs>
						<div ref="bottom"></div>
					</el-form-item>
				</el-card>
			</template>
			<div class="mt20">
				<el-button type="primary" plain @click="form.periodAnnexLists.push(JSON.parse(JSON.stringify(originForm)))">
					+添加工作周期
				</el-button>
			</div>
		</el-form-item>
	</el-form>
</template>

<style lang="scss" scoped>
.roster-card:not(:first-child) {
	margin-top: 20px;
}
</style>
