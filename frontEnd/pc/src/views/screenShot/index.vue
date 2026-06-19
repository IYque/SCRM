<script>
import * as api from './api'
import { env } from '../../../sys.config'
import aev from './aev.vue'
import synch from './synch.vue'

let { getList,synchMsg,getScreenShotTypes} = {}

export default {
	data() {
		let _ = ({ getList,synchMsg,getScreenShotTypes} = api)

		return {
			activeName: 'first',
			list: '',
			aiList:'',
			aiRuleList:'',
			total: 0,
			multipleSelection: [], // 多选数据
			loading: false,
			dialogVisible: false, // 弹窗显示控制
			dialogAiVisible: false,
			form: {},
			queryParmTime:null,
			queryParm: {
				userType: null,
				shotType: null,
				time:null,
				page: 1,
				size: 10
			},
			options: [],
			xData: [],
			series: [],
			tabCount: {},
			// 下拉选项
            options: [
            ],
			sourceOptions: [
            ],
			userTypeptions: [
                { key: '微信用户', val: 1 },
                { key: '企微用户', val: 2 },
                { key: '企业员工', val: 3 },
            ]

			
		}
	},
	components: { aev,synch },
	watch: {},
	created() {
		 this.getOperations();
		this.getList()
	},
	mounted() {},
	methods: {
		openDialog() {
			this.dialogAiVisible = true;
         },

		 getOperations(){
              getScreenShotTypes()
			  .then(response=>{
				this.options=response.data;
				console.log(response.data);
			  })


		 },
		getList() {
			// if(page == null){
			// 	page=this.queryParm;
			// }
			this.$store.loading = true
		    console.log(this.queryParmTime?.[0]);
			 console.log(this.queryParmTime?.[1]);
			if (this.queryParmTime?.[0] && this.queryParmTime?.[1]){
			this.queryParm.startTime=this.formatDate(this.queryParmTime?.[0])
			this.queryParm.endTime=this.formatDate(this.queryParmTime?.[1])
			}
			
			
			
			getList(this.queryParm)
				.then(response => {
					this.list = response.data
					this.total = response.count
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

		synchChatMsg() {
            synchMsg()
			 .then(response => {
			
				this.msgSuccess(response.msg)
			 })
			 .catch((e) => console.error(e))
			.finally(() => (this.$store.loading = false))
		},


		restting() {
			this.queryParm.userType = null
			this.queryParm.shotType = null
			this.queryParmTime = null
			
			this.getList()
		},




		formatDate(date) {
			const year = date.getFullYear()
			const month = (date.getMonth() + 1).toString().padStart(2, '0')
			const day = date.getDate().toString().padStart(2, '0')
			return `${year}-${month}-${day}`
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
					<el-form class="searchForm" ref="searchForm" :model="query2" label-width="" inline>

						<el-form-item label="时间:">
						<el-date-picker
							v-model="queryParmTime"
							type="daterange"
							range-separator="至"
							start-placeholder="开始日期"
							end-placeholder="结束日期"></el-date-picker>
					    </el-form-item>


						<el-form-item label="截屏类型:" prop="value1">
							<el-select
								v-model="queryParm.shotType"
								collapse-tags
								collapse-tags-tooltip
								:max-collapse-tags="2"
								placeholder="全部"
								style="width: 260px">
								<el-option
									v-for="item in options"
									:key="item.value"
									:label="item.value"
									:value="item.subCode" />
							</el-select>
						</el-form-item>

					
					<el-form-item>
						<el-button type="primary" @click="getList">查询</el-button>
						<el-button @click="restting">重置</el-button>
					</el-form-item>
				</el-form>
					
					<div class="fxbw">
						<el-button type="primary" @click=";(form = {}), (dialogAiVisible = true)">同步</el-button>
					</div>

			

					<el-table
						:data="list"
						tooltip-effect="dark"
						highlight-current-row
						@selection-change="(selection) => (multipleSelection = selection.map((item) => item.id))">
						 <el-table-column label="截屏操作时间" prop="operateTime" />
						<el-table-column label="操作人" prop="userName"/>
					

						<el-table-column label="截屏类型" prop="operateType">
							<template #default="{ row }">
							{{ options?.find((e) => e.subCode == row.shotType)?.value }}
							</template>
						</el-table-column>

			
						<el-table-column label="截屏内容" prop="shotContent"/>

						<el-table-column label="操作系统" prop="systemOs"/>
						
					</el-table>
					<pagination
						v-show="total > 0"
						:total="total"
						v-model:page="queryParm.page"
						v-model:limit="queryParm.size"
						@pagination="getList()" />

						<el-dialog title="同步" v-model="dialogAiVisible" width="80%" :close-on-click-modal="false">
							<synch v-if="dialogAiVisible" :data="form" ref="synch"></synch>
							<template #footer>
								<el-button @click="dialogAiVisible = false">取消</el-button>
								<el-button type="primary" @click="submitAiVisible" v-loading="loading">确定</el-button>
							</template>
						</el-dialog>
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
