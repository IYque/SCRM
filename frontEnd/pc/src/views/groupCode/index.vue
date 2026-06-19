<script>
import * as api from './api'
import aev from './aev.vue'

let { getList, del, distributeUserCode, findYjaiscrmUserCodeKvs, countTotalTab, countTrend } = api

export default {
	data() {
		let isLink = location.href.includes('customerLink')

		return {
			activeName: 'first',
			query: { page: 1, size: 10 },
			list: '',
			total: 0,
			multipleSelection: [], // 多选数据
			loading: false,
			dialogVisible: false, // 弹窗显示控制
			form: {},
			queryParm: {
				codeId: null,
				time: null,
			},
			options: [],
			xData: [],
			series: [],
			tabCount: {},

			isLink,
		}
	},
	components: { aev },
	watch: {},
	created() {
		this.getList()
		// this.getData()
		// this.initSelect()
		// this.selectCount()
	},
	mounted() {},
	methods: {
		getList(page) {
			page && (this.query.page = page)
			this.$store.loading = true
			getList(this.query)
				.then(({ data, count }) => {
					this.list = data
					this.total = +count
					this.multipleSelection = []
				})
				.catch((e) => console.error(e))
				.finally(() => (this.$store.loading = false))
		},
		initSelect() {
			findYjaiscrmUserCodeKvs()
				.then((data) => {
					this.options = data.data
					console.log(data)
				})
				.catch((e) => console.error(e))
		},
		del(id) {
			let ids = id || this.multipleSelection?.join?.(',')
			if (!ids) {
				return this.msgError('请选择需要删除的数据')
			}
			this.$confirm()
				.then(() => {
					this.$store.loading = true
					return del(ids).then((res) => {
						this.msgSuccess('删除成功')
						this.getList()
					})
				})
				.catch((e) => {
					console.error(e)
				})
		},

		distributeUserCode(id) {
			this.$confirm('是否将当前活码下发给员工, 是否继续?', '提示', {
				confirmButtonText: '是',
				cancelButtonText: '否',
				type: 'warning',
			})
				.then(() => {
					distributeUserCode(id).then((res) => {
						this.msgSuccess('已通知')
					})
				})
				.catch((e) => {
					console.error(e)
				})
		},

		downloadBlob(url, fileName) {
			// 检查url是否以http或https开头
			if (!url.startsWith('http') && !url.startsWith('https')) {
				url = env.BASE_API + '/file/fileView/' + url
			}

			// 接下来是原有的downloadBlob方法逻辑
			const link = document.createElement('a')
			link.href = url
			link.download = fileName
			link.click()
		},

		submit() {
			this.loading = true
			this.$refs.aev
				.submit()
				.then(() => {
					this.dialogVisible = false
					this.getList()
					// this.initSelect()
				})
				.catch((e) => console.error(e))
				.finally(() => (this.loading = false))
		},

		getData() {
			var queryParm = {
				userCodeId: this.queryParm.codeId,
				startTime: this.queryParm.time === null ? null : this.formatDate(this.queryParm.time?.[0]),
				endTime: this.queryParm.time === null ? null : this.formatDate(this.queryParm.time?.[1]),
			}

			this.selectCount(queryParm)
		},

		restting() {
			this.queryParm.codeId = null
			this.queryParm.time = null
			this.getData()
		},

		selectCount(query) {
			countTotalTab(query)
				.then(({ data }) => {
					this.tabCount = data
				})
				.catch((e) => console.error(e))

			countTrend(query)
				.then(({ data }) => {
					this.xData = data.xdata

					this.series = data.series

					console.log(data)
				})
				.catch((e) => console.error(e))
		},

		formatDate(date) {
			const year = date.getFullYear()
			const month = (date.getMonth() + 1).toString().padStart(2, '0')
			const day = date.getDate().toString().padStart(2, '0')
			return `${year}-${month}-${day}`
		},
	},
}
</script>
<template>
	<div>
		<div class="warning">
			<a href="https://www.yjaiscrm.cn?utm_source=yjaiscrmcode" target="_blank">
				<strong>
					yjaiscrm Scrm-是基于Java源码交付的企微SCRM,帮助企业构建高度自由安全的私域平台。:https://www.yjaiscrm.cn/
				</strong>
			</a>
		</div>

		<div class="g-card">
			<div class="fxbw">
				<el-button type="primary" @click=";(form = {}), (dialogVisible = true)">新建</el-button>
				<el-button :disabled="!multipleSelection.length" @click="del()" type="danger">批量删除</el-button>
			</div>
			<el-table
				:data="list"
				tooltip-effect="dark"
				highlight-current-row
				@selection-change="(selection) => (multipleSelection = selection.map((item) => item.id))">
				<el-table-column type="selection" width="50" align="center"></el-table-column>
				<el-table-column label="活码名称" prop="chatCodeName" show-overflow-tooltip />
				<el-table-column label="二维码" prop="chatCodeUrl">
					<template #default="{ row }">
						<el-popover placement="bottom" trigger="hover">
							<template #reference><el-image :src="row.chatCodeUrl" style="width: 50px"></el-image></template>
							<el-image :src="row.chatCodeUrl" style="width: 200px"></el-image>
						</el-popover>
					</template>
				</el-table-column>

				<el-table-column label="更新时间" prop="updateTime" />

				<el-table-column label="操作" fixed="right">
					<template #default="{ row }">
						<el-button text @click=";(form = JSON.parse(JSON.stringify(row))), (dialogVisible = true)">编辑</el-button>
						<el-button text @click="del(row.id)">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<pagination
				v-show="total > 0"
				:total="total"
				v-model:page="query.page"
				v-model:limit="query.size"
				@pagination="getList()" />
		</div>

		<el-dialog :title="form.id ? '编辑' : '新建'" v-model="dialogVisible" width="80%" :close-on-click-modal="false">
			<aev v-if="dialogVisible" :data="form" ref="aev"></aev>
			<template #footer>
				<el-button @click="dialogVisible = false">取消</el-button>
				<el-button type="primary" @click="submit" v-loading="loading">确定</el-button>
			</template>
		</el-dialog>
	</div>
</template>

<style scoped lang="scss">
.warning {
	padding: 8px 16px;
	background-color: #fff6f7;
	border-radius: 4px;
	border-left: 5px solid #fe6c6f;
	margin: 20px 0;
	line-height: 40px;
}

.like {
	cursor: pointer;
	font-size: 25px;
	display: inline-block;
}
</style>
