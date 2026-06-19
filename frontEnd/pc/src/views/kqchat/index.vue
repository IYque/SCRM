<script>
import * as api from './api'
import * as apiLink from './apiLink'
import { env } from '../../../sys.config'
import aev from './aev.vue'
import synch from './synch.vue'

let { getList,findAiAnalysisMsgAudits,synchMsg,buildAISessionWarning,findYjaiscrmMsgRules,del,saveOrUpdateMsgRule,batchStartOrStop} = {}

export default {
	data() {
		// let isLink = location.href.includes('customerLink')
		let _ = ({ getList,findAiAnalysisMsgAudits,synchMsg,buildAISessionWarning,findYjaiscrmMsgRules,del,saveOrUpdateMsgRule,batchStartOrStop} = api)

		return {
			activeName: 'first',
			list: '',
			aiList:'',
			aiRuleList:'',
			total: 0,
			aiTotal: 0,
			aiRuleTotal: 0,
			multipleSelection: [], // 多选数据
			loading: false,
			dialogVisible: false, // 弹窗显示控制
			dialogAiVisible: false,
			form: {},
			queryParm: {
				fromName: null,
				acceptName: null,
				time: null,
				acceptType: 2,
				page: 1,
				size: 10
			},
			aiQueryParm: {
				warning: null,
				employeeName: null,
				customerName: null,
				msgAuditType: 2,
				time: null,
				page: 1,
				size: 10
			},
			aiRuleParm: {
				ruleStatus: null,
				ruleType: 2,
				page: 1,
				size: 10
			},
			options: [],
			xData: [],
			series: [],
			tabCount: {},
			// 下拉选项
            options: [
                { key: '全部', val: null },
                { key: '违规', val: true },
                { key: '不违规', val: false },
            ],
			aiRuleOptions: [
                { key: '全部', val: null },
                { key: '启用', val: true },
                { key: '停用', val: false },
            ]

			
		}
	},
	components: { aev,synch },
	watch: {},
	created() {
		this.getList()
		this.getData()
		this.findAiAnalysisMsgAudits()
		this.findYjaiscrmMsgRules()
	},
	mounted() {},
	methods: {
		openDialog() {
			this.dialogAiVisible = true;
         },
		getList(page) {
			if(page == null){
				page=this.queryParm;
			}
			// page && (this.query.page = page)
			this.$store.loading = true
		    console.log(page);
			
			getList(page)
				.then(response => {
					this.list = response.data
					this.total = response.count
					this.multipleSelection = []
				})
				.catch((e) => console.error(e))
				.finally(() => (this.$store.loading = false))
		},

		del(id) {
			let ids = id
			
			this.$confirm()
				.then(() => {
					this.$store.loading = true
					return del(ids).then((res) => {
						this.msgSuccess('删除成功')
						this.findYjaiscrmMsgRules()
					})
				})
				.catch((e) => {
					console.error(e)
				})
		},



		findAiAnalysisMsgAudits(page) {
			if(page == null){
				page=this.aiQueryParm;
			}

			this.$store.loading = true
		    console.log(page);
			
			findAiAnalysisMsgAudits(page)
				.then(({ data, count }) => {
					this.aiList = data
					this.aiTotal = +count
					this.multipleSelection = []
				})
				.catch((e) => console.error(e))
				.finally(() => (this.$store.loading = false))
		},

		findYjaiscrmMsgRules(page) {
			if(page == null){
				page=this.aiRuleParm;
			}

			this.$store.loading = true
		    console.log(page);
			
			findYjaiscrmMsgRules(page)
				.then(({ data, count }) => {
					this.aiRuleList = data
					this.aiRuleTotal = +count
					this.multipleSelection = []
				})
				.catch((e) => console.error(e))
				.finally(() => (this.$store.loading = false))
		},


		submit() {
			this.loading = true
			this.$refs.aev
				.submit()
				.then(() => {
					this.dialogVisible = false
					return this.findYjaiscrmMsgRules()
				})
				.catch((e) => console.error(e))
				.finally(() => (this.loading = false))
		},

		submitAiVisible(){

			this.loading = true
			this.$refs.synch
				.submit()
				.then(() => {
					this.dialogAiVisible = false
				})
				.catch((e) => console.error(e))
				.finally(() => (this.loading = false))
		},
		synchChatMsg() {
            synchMsg()
			 .then(response => {
			
				this.msgSuccess(response.msg)
			 })
			 .catch((e) => console.error(e))
			.finally(() => (this.$store.loading = false))
		},

		buildAISessionWarning(){
			buildAISessionWarning()
			.then(response => {
				this.msgSuccess(response.msg)
			})
			.catch((e) => console.error(e))
			.finally(() => (this.$store.loading = false))
		},


		getData() {
			var queryParm = {
				fromName: this.queryParm.fromName,
				acceptName: this.queryParm.acceptName,
				startTime: this.queryParm.time === null ? null : this.formatDate(this.queryParm.time?.[0]),
				endTime: this.queryParm.time === null ? null : this.formatDate(this.queryParm.time?.[1]),
				acceptType: 2,
				page: this.queryParm.page,
				size: this.queryParm.size
			}

	
			 this.getList(queryParm)
		},

		getAIData() {
			var queryParm = {
				warning: this.aiQueryParm.warning,
				employeeName: this.aiQueryParm.employeeName,
				customerName: this.aiQueryParm.customerName,
				startTime: this.aiQueryParm.time === null ? null : this.formatDate(this.aiQueryParm.time?.[0]),
				endTime: this.aiQueryParm.time === null ? null : this.formatDate(this.aiQueryParm.time?.[1]),
				msgAuditType: 2,
				page: this.aiQueryParm.page,
				size: this.aiQueryParm.size
			}

	
			 this.findAiAnalysisMsgAudits(queryParm)
		},

		getAIRuleData() {
			var queryParm = {
				ruleStatus: this.aiRuleParm.ruleStatus,
				ruleType: 2,
				page: this.aiRuleParm.page,
				size: this.aiRuleParm.size
			}

	
			 this.findYjaiscrmMsgRules(queryParm)
		},

		restting() {
			this.queryParm.fromName = null
			this.queryParm.acceptName = null
			this.queryParm.time = null
			
			this.getData()
		},

		resttingAi(){
			this.aiQueryParm.warning = null
			this.aiQueryParm.employeeName = null
			this.aiQueryParm.customerName = null
			this.aiQueryParm.time=null

            this.getAIData()
		},


		resttingRuleAi(){
			this.aiRuleParm.ruleStatus = null
	

            this.getAIRuleData()
		},



		formatDate(date) {
			const year = date.getFullYear()
			const month = (date.getMonth() + 1).toString().padStart(2, '0')
			const day = date.getDate().toString().padStart(2, '0')
			return `${year}-${month}-${day}`
		},

		formatWarning(row, column, cellValue) {
            return cellValue ? '违规' : '不违规';
        },
		formatStartOrStop(row, column, cellValue) {
            return cellValue ? '启用' : '停用';
        },
		formatDefaultRule(row, column, cellValue) {
            return cellValue ? '默认' : '非默认';
        },
		formatDateTimeRange(startTime, endTime) {
			if (!startTime || !endTime) return '-';
			return `${startTime} 至 ${endTime}`;
        },
		handleStatusChange() {
			console.log(this.multipleSelection?.join?.(','))
			
        // 这里可以添加处理状态改变的逻辑，例如调用后端接口更新状态
		  this.$confirm('是否需要启用或停用该条规则')
				.then(() => {
					return batchStartOrStop(
						this.multipleSelection?.join?.(',')
					).then((res) => {
						this.msgSuccess('操作成功')
						this.findYjaiscrmMsgRules()
					})
				})
				.catch((e) => {
					console.error(e)
				})
        }
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

		<el-tabs v-model="activeName">
			<div class="g-card">
				<div class="warning">
					<strong>
						获取员工与客户在群中的聊天内容(目前:仅支持文字内容)
					</strong>
		        </div>
					<el-form class="searchForm" ref="searchForm" :model="queryParm" label-width="" inline>
					<el-form-item label="发送人:" prop="value3">
						<el-input v-model="queryParm.fromName" placeholder=""></el-input>
					</el-form-item>


					<el-form-item label="所属群:" prop="value3">
						<el-input v-model="queryParm.acceptName" placeholder=""></el-input>
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
					
					<div class="fxbw">
						<el-button type="primary" @click="synchChatMsg()">同步会话</el-button>
					</div>
					<el-table
						:data="list"
						tooltip-effect="dark"
						highlight-current-row
						@selection-change="(selection) => (multipleSelection = selection.map((item) => item.msgId))">
						<el-table-column label="发送人" prop="fromName" show-overflow-tooltip />
						<el-table-column label="所属群" prop="acceptName"/>
						<el-table-column label="发送内容" prop="content" show-overflow-tooltip />
						<el-table-column label="发送时间" prop="msgTime" />


					</el-table>
					<pagination
						v-show="total > 0"
						:total="total"
						v-model:page="queryParm.page"
						v-model:limit="queryParm.size"
						@pagination="getList()" />
				</div>
		</el-tabs>


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
