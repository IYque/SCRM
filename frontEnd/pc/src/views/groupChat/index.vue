<script>
import * as api from './api'
import { env } from '../../../sys.config'
import TagEllipsis from '@/components/TagEllipsis'
let { getList,synchYjaiscrmChat, getGroupTags, tagGroups} = {}

export default {
	data() {
		// let isLink = location.href.includes('customerLink')
		let _ = ({ getList,synchYjaiscrmChat, getGroupTags, tagGroups} = api)

		return {
			activeName: 'first',
			list: '',
			total: 0,
			multipleSelection: [], // 多选数据
			loading: false,
			dialogVisible: false, // 弹窗显示控制
			tagDialogVisible: false, // 标签弹框显示控制
			groupTags: [], // 标签列表
			selectedTags: [], // 选中的标签ID
			selectedGroupIds: [], // 选中的客群ID
			selectedChatId: '', // 选中的客群chatId
			form: {},
			queryParm: {
				chatName: null,
				page: 1,
				size: 10
			},
			options: [],
			xData: [],
			series: [],
			tabCount: {}

			
		}
	},
	watch: {},
	created() {
		this.getList()
		this.loadGroupTags() // 加载标签列表
	},
	mounted() {},
	methods: {
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

		getData() {
			var queryParm = {
				chatName: this.queryParm.chatName,
				page: this.queryParm.page,
				size: this.queryParm.size
			}

	
			 this.getList(queryParm)
		},



		synchYjaiscrmChat() {
            synchYjaiscrmChat()
			 .then(response => {
			
				this.msgSuccess(response.msg)
			 })
			 .catch((e) => console.error(e))
			.finally(() => (this.$store.loading = false))
		},



		restting() {
			this.queryParm.chatName = null
			this.getData()
		},

		 // 格式化状态值
		 formatStatus(row) {
			const statusMap = {
				1: '已激活',
				2: '已禁用',
				4: '未激活',
				5: '退出企业',
			};
			return statusMap[row.status] || '未知状态';
			},



		formatDate(date) {
			const year = date.getFullYear()
			const month = (date.getMonth() + 1).toString().padStart(2, '0')
			const day = date.getDate().toString().padStart(2, '0')
			return `${year}-${month}-${day}`
		},

		// 获取客群标签列表
		loadGroupTags() {
			getGroupTags({}) 
				.then(res => {
					if (res.code === 200) {
						this.groupTags = res.data || []
					}
				})
				.catch(err => console.error('获取标签列表失败:', err))
		},

		// 单个客群打标签按钮点击事件
		handleTagGroup(row) {
			// 设置当前要打标签的客群msgId和chatId
			this.selectedGroupIds = [row.msgId]
			this.selectedChatId = row.chatId // 保存chatId
			
			// 加载客群当前已有的标签，将逗号分隔的字符串转换为数组
			if (row.tagIds) {
				const tagIdArray = row.tagIds.split(',').map(id => id.trim())
				// 过滤出存在于标签列表中的标签ID，确保能正确显示标签名
				this.selectedTags = tagIdArray.filter(tagId => 
					this.groupTags.some(tag => tag.id === tagId)
				)
			} else {
				this.selectedTags = []
			}
			
			// 打开标签选择对话框
			this.tagDialogVisible = true
		},

		// 提交标签
		submitTags() {
			if (this.selectedTags.length === 0) {
				this.msgWarning('请选择标签')
				return
			}
			
			tagGroups({
				externalUserid: this.selectedGroupIds[0], // 使用数组第一个元素作为msgId
				chatId: this.selectedChatId, // 带上群的chatId
				tagIds: this.selectedTags
			})
				.then(res => {
					if (res.code === 200) {
						this.msgSuccess('打标签成功')
						this.tagDialogVisible = false
						this.getList() // 刷新客群列表
					} else {
						this.msgError(res.msg || '打标签失败')
					}
				})
				.catch(err => {
					console.error('打标签失败:', err)
					this.msgError('打标签失败')
				})
		},

		// 消息提示方法
		msgSuccess(msg) {
			this.$message.success(msg)
		},
		
		msgWarning(msg) {
			this.$message.warning(msg)
		},
		
		msgError(msg) {
			this.$message.error(msg)
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
					<el-form class="searchForm" ref="searchForm" :model="queryParm" label-width="" inline>
					<el-form-item label="群名称:" prop="value3">
						<el-input v-model="queryParm.chatName" placeholder=""></el-input>
					</el-form-item>


					<el-form-item>
						<el-button type="primary" @click="getData">查询</el-button>
						<el-button @click="restting">重置</el-button>
					</el-form-item>
				</el-form>
					
					<div class="fxbw">
						<el-button type="primary" @click="synchYjaiscrmChat()">同步客群</el-button>
					</div>
					<el-table
					:data="list"
					tooltip-effect="dark"
					highlight-current-row
					@selection-change="(selection) => (multipleSelection = selection.map((item) => item.msgId))">
					<el-table-column label="群名称" prop="chatName" />
					<el-table-column label="群主" prop="ownerName" ></el-table-column>
					<el-table-column prop="tagNames" label="客群标签" align="center" width="220">
						<template #default="{ row }">
							<TagEllipsis :list="row.tagNames || []" emptyText="无标签"></TagEllipsis>
						</template>
					</el-table-column>
					<el-table-column label="创建时间" prop="createTime" />
					<el-table-column label="操作" fixed="right" width="80">
						<template #default="{ row }">
							<el-button text @click="handleTagGroup(row)">打标签</el-button>
						</template>
					</el-table-column>
				</el-table>
					<pagination
						v-show="total > 0"
						:total="total"
						v-model:page="queryParm.page"
						v-model:limit="queryParm.size"
						@pagination="getList()" />
				</div>


		<!-- 标签选择对话框 -->
		<el-dialog
			v-model="tagDialogVisible"
			title="选择标签"
			width="500px"
			@close="() => this.selectedTags = []"
		>
			<div class="tag-selector">
				<el-select
					v-model="selectedTags"
					multiple
					placeholder="请选择标签"
					style="width: 100%"
					:popper-append-to-body="false"
					:fit-input-width="false"
				>
					<el-option
						v-for="tag in groupTags"
						:key="tag.id"
						:label="tag.name"
						:value="tag.id"
					></el-option>
				</el-select>
			</div>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="tagDialogVisible = false">取消</el-button>
					<el-button type="primary" @click="submitTags">确定</el-button>
				</span>
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
