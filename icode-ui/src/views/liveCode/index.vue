<script>
import * as api from './api'
import * as apiLink from './apiLink'
import { env } from '../../../sys.config'
import aev from './aev.vue'

let { getList, del, distributeUserCode, findIYqueUserCodeKvs, countTotalTab, countTrend } = {}

export default {
	data() {
		let isLink = location.href.includes('customerLink')
		let _ = ({ getList, del, distributeUserCode, findIYqueUserCodeKvs, countTotalTab, countTrend } = isLink
			? apiLink
			: api)

		return {
			activeName: 'first',
			query: { page: 0, size: 10 },
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
		this.getData()
		this.initSelect()
		this.selectCount()
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
			findIYqueUserCodeKvs()
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
					this.initSelect()
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
			<a href="https://www.iyque.cn?utm_source=iyquecode" target="_blank">
				<strong>
					源雀Scrm-是基于Java源码交付的企微SCRM,帮助企业构建高度自由安全的私域平台。:https://www.iyque.cn/
				</strong>
			</a>
		</div>

		<el-tabs v-model="activeName">
			<el-tab-pane label="渠道码配置" name="first">
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
						<el-table-column label="渠道名称" prop="codeName" show-overflow-tooltip />
						<el-table-column label="活码地址" prop="codeUrl">
							<!-- <template #default="{ row }">
								<el-image :src="row.codeUrl" style="width: 100px"></el-image>
							</template> -->
						</el-table-column>
						<el-table-column label="使用员工">
							<template #default="{ row }">
								<TagEllipsis :list="row.userName"></TagEllipsis>
							</template>
						</el-table-column>
						<el-table-column label="标签">
							<template #default="{ row }">
								<TagEllipsis :list="row.tagName"></TagEllipsis>
							</template>
						</el-table-column>

						<el-table-column label="更新时间" prop="updateTime" />

						<el-table-column label="操作" fixed="right">
							<template #default="{ row }">
								<el-button text @click=";(form = JSON.parse(JSON.stringify(row))), (dialogVisible = true)">
									编辑
								</el-button>
								<el-button text @click="del(row.id)">删除</el-button>
								<template v-if="!isLink">
									<el-button text @click="distributeUserCode(row.id)">通知</el-button>
									<el-button text @click="downloadBlob(row.codeUrl, row.codeName)">活码下载</el-button>
								</template>
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
			</el-tab-pane>

			<el-tab-pane label="渠道码统计" name="second">
				<el-form class="searchForm" ref="searchForm" :model="query2" label-width="" inline>
					<el-form-item label="渠道名称:" prop="value3">
						<el-select
							v-model="queryParm.codeId"
							collapse-tags
							collapse-tags-tooltip
							:max-collapse-tags="2"
							placeholder="全部"
							style="width: 260px">
							<el-option v-for="item in options" :key="item.val" :label="item.key" :value="item.val" />
						</el-select>
					</el-form-item>

					<el-form-item label="时间:">
						<el-date-picker
							v-model="queryParm.time"
							type="daterange"
							range-separator="至"
							start-placeholder="开始日期"
							end-placeholder="结束日期"></el-date-picker>
					</el-form-item>

					<el-form-item>
						<el-button type="primary" @click="getData">查询</el-button>
						<el-button @click="restting">重置</el-button>
					</el-form-item>
				</el-form>

				<div class="g-card">
					<el-row :gutter="20">
						<el-col :span="6">
							<div>
								<el-statistic
									value-style="font-size:20px;"
									title="新增客户总数"
									:value="tabCount.addCustomerNumber"></el-statistic>
							</div>
							<br />
							<div>
								<el-statistic title="今日新增客户数" :value="tabCount.tdAddCustomerNumber"></el-statistic>
							</div>
						</el-col>
						<el-col :span="6">
							<div>
								<el-statistic title="流失客户总数" :value="tabCount.lostCustomerNumber"></el-statistic>
							</div>
							<br />
							<div>
								<el-statistic title="今日流失客户数" :value="tabCount.tdLostCustomerNumber"></el-statistic>
							</div>
						</el-col>
						<el-col :span="6">
							<div>
								<el-statistic title="员工删除客户总数" :value="tabCount.delCustomerNumber"></el-statistic>
							</div>
							<br />
							<div>
								<el-statistic title="今日员工删除客户数" :value="tabCount.tddelCustomerNumber"></el-statistic>
							</div>
						</el-col>
						<el-col :span="6">
							<div>
								<el-statistic title="净增客户总数" :value="tabCount.netGrowthCustomerNumber"></el-statistic>
							</div>
							<br />
							<div>
								<el-statistic title="今日净增客户数" :value="tabCount.tdNetGrowthCustomerNumber"></el-statistic>
							</div>
						</el-col>
					</el-row>
				</div>

				<div class="g-card">
					<ChartLine
						:xData="xData"
						:legend="['新增客户数', '流失客户数', '员工删除客户数', '净增客户数']"
						:series="series"></ChartLine>
				</div>
			</el-tab-pane>
		</el-tabs>

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
