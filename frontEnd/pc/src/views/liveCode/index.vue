<script>
import * as api from './api'
import * as apiLink from './apiLink'
import { env } from '../../../sys.config'
import aev from './aev.vue'
import { getUserList } from '@/api/common'

let {
  getList,
  del,
  distributeUserCode,
  findYjaiscrmUserCodeKvs,
  countTotalTab,
  countTrend,
  synchShortLink,
  synchUserCode,
  synchUserCodeByConfigIds,
  synchShortLinkByLinkIds,
  getShortLinkConfigIds,
  getUserCodeConfigIds,
  getCustomerList,
} = {}

export default {
  data() {
    let isLink = location.href.includes('customerLink')
    let _ = ({
      getList,
      del,
      distributeUserCode,
      findYjaiscrmUserCodeKvs,
      countTotalTab,
      countTrend,
      synchShortLink,
      synchUserCode,
      synchUserCodeByConfigIds,
      synchShortLinkByLinkIds,
      getShortLinkConfigIds,
      getUserCodeConfigIds,
      getCustomerList,
    } = isLink ? apiLink : api)

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

      // 获客外链搜索条件
      searchQuery: {
        codeName: '',
        userName: '',
        dateRange: null,
      },

      // 员工活码搜索条件
      userCodeSearchQuery: {
        codeName: '',
        userName: '',
        dateRange: null,
      },

      // 员工列表
      userList: [],

      // 客户列表相关
      customerDialogVisible: false,
      customerList: [],
      customerLoading: false,
      currentLinkInfo: null, // 当前查看客户的获客链接信息
      customerSearchQuery: {
        customerName: '',
        followUser: '',
        dateRange: null,
      },
      customerPagination: {
        cursor: '',
        hasNext: false,
        hasPrev: false,
        prevCursors: [], // 存储之前页面的游标，用于实现上一页功能
      },

      // 同步模态框相关
      syncDialogVisible: false,
      syncForm: {
        syncType: 'full', // 'full' 常规同步, 'specific' 指定同步
        specificIds: '', // 指定同步的ID列表
      },
      syncLoading: false,
      configIdOptions: [], // 配置ID选项列表
      manualIds: '', // 手动输入的ID
      isInput: false, // 是否手动输入ID

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
  mounted() {
    this.getUserListData()
  },
  methods: {
    getList(page) {
      page && (this.query.page = page)
      this.$store.loading = true

      // 构建查询参数
      let queryParams = { ...this.query }

      // 根据当前页面类型添加搜索条件
      if (this.isLink) {
        // 获客外链搜索条件
        if (this.searchQuery.codeName) {
          queryParams.codeName = this.searchQuery.codeName
        }
        if (this.searchQuery.userName) {
          queryParams.userName = this.searchQuery.userName
        }
        if (this.searchQuery.dateRange && this.searchQuery.dateRange.length === 2) {
          queryParams.beginTime = this.formatDate(this.searchQuery.dateRange[0])
          queryParams.endTime = this.formatDate(this.searchQuery.dateRange[1])
        }
      } else {
        // 员工活码搜索条件
        if (this.userCodeSearchQuery.codeName) {
          queryParams.codeName = this.userCodeSearchQuery.codeName
        }
        if (this.userCodeSearchQuery.userName) {
          queryParams.userName = this.userCodeSearchQuery.userName
        }
        if (this.userCodeSearchQuery.dateRange && this.userCodeSearchQuery.dateRange.length === 2) {
          queryParams.beginTime = this.formatDate(this.userCodeSearchQuery.dateRange[0])
          queryParams.endTime = this.formatDate(this.userCodeSearchQuery.dateRange[1])
        }
      }

      getList(queryParams)
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
        .finally(() => (this.$store.loading = false))
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

    // 同步获客外链
    synchShortLink() {
      this.openSyncDialog()
    },

    // 同步员工活码（联系我配置）
    synchUserCode() {
      this.openSyncDialog()
    },

    // 打开同步模态框
    openSyncDialog() {
      this.syncForm = {
        syncType: 'full',
        specificIds: '',
      }
      this.configIdOptions = []
      this.manualIds = ''
      this.syncDialogVisible = true

      // 加载配置ID列表
      this.loadConfigIds()
    },

    // 加载配置ID列表
    loadConfigIds() {
      const loadFunction = this.isLink ? getShortLinkConfigIds : getUserCodeConfigIds

      loadFunction()
        .then((res) => {
          if (res.code === 200 && res.data) {
            this.configIdOptions = res.data
          } else {
            this.msgError('获取配置ID列表失败')
          }
        })
        .catch((e) => {
          console.error('获取配置ID列表失败:', e)
          this.msgError('获取配置ID列表失败，请重试')
        })
    },

    // 处理配置ID选择变化
    handleConfigIdChange(selectedIds) {
      // if (Array.isArray(selectedIds)) {
      //   this.syncForm.specificIds = selectedIds.join(',')
      // } else {
      //   this.syncForm.specificIds = selectedIds || ''
      // }
      this.isInput || (this.manualIds = '') // 清空手动输入
    },

    // 处理手动输入
    handleManualInput(value) {
      this.isInput = true
      this.syncForm.specificIds = []
      // 清空下拉选择
      this.$nextTick(() => {
        // 这里不能直接清空，因为会触发循环
      })
    },

    // 执行同步操作
    performSync() {
      if (this.syncForm.syncType === 'specific') {
        const ids = this.syncForm.specificIds + '' || this.manualIds
        if (!ids.trim()) {
          this.msgError('请选择或输入要同步的ID')
          return
        }
      }

      this.syncLoading = true

      let syncPromise
      if (this.syncForm.syncType === 'full') {
        // 常规同步（全量同步）
        if (this.isLink) {
          syncPromise = synchShortLink()
        } else {
          syncPromise = synchUserCode()
        }
      } else {
        // 指定同步
        const ids = (this.syncForm.specificIds + '' || this.manualIds).trim()
        if (this.isLink) {
          syncPromise = synchShortLinkByLinkIds(ids)
        } else {
          syncPromise = synchUserCodeByConfigIds(ids)
        }
      }

      syncPromise
        .then((res) => {
          this.msgSuccess(res.msg || '同步任务已启动，请稍后查看')
          this.syncDialogVisible = false
          // 刷新列表
          this.getList()
        })
        .catch((e) => {
          console.error(e)
          this.msgError('同步失败，请重试')
        })
        .finally(() => {
          this.syncLoading = false
        })
    },

    // 获客外链搜索
    searchList() {
      this.query.page = 1 // 重置到第一页
      this.getList()
    },

    // 重置获客外链搜索
    resetSearch() {
      this.searchQuery = {
        codeName: '',
        userName: '',
        dateRange: null,
      }
      this.query.page = 1
      this.getList()
    },

    // 员工活码搜索
    searchUserCodeList() {
      this.query.page = 1 // 重置到第一页
      this.getList()
    },

    // 重置员工活码搜索
    resetUserCodeSearch() {
      this.userCodeSearchQuery = {
        codeName: '',
        userName: '',
        dateRange: null,
      }
      this.query.page = 1
      this.getList()
    },

    // 获取员工列表
    getUserListData() {
      getUserList()
        .then((res) => {
          if (res.code == 200) {
            this.userList = res.data || []
          }
        })
        .catch((e) => {
          console.error('获取员工列表失败:', e)
        })
    },

    // 查看客户列表
    viewCustomers(row) {
      this.currentLinkInfo = row
      this.customerDialogVisible = true
      this.resetCustomerPagination()
      this.loadCustomers()
    },

    // 加载客户列表
    loadCustomers(cursor = '') {
      if (!this.currentLinkInfo || !this.currentLinkInfo.configId) {
        this.msgError('获客链接配置ID不存在')
        return
      }

      this.customerLoading = true

      // 构建搜索参数
      const searchParams = {}
      if (this.customerSearchQuery.customerName) {
        searchParams.customerName = this.customerSearchQuery.customerName
      }
      if (this.customerSearchQuery.followUser) {
        searchParams.followUser = this.customerSearchQuery.followUser
      }
      if (this.customerSearchQuery.dateRange && this.customerSearchQuery.dateRange.length === 2) {
        searchParams.startDate = this.formatDate(this.customerSearchQuery.dateRange[0])
        searchParams.endDate = this.formatDate(this.customerSearchQuery.dateRange[1])
      }

      // 使用configId作为linkId参数，传递搜索参数
      apiLink
        .getCustomerList(this.currentLinkInfo.configId, 100, cursor, searchParams)
        .then((res) => {
          if (res.code === 200 && res.data) {
            const data = res.data
            this.customerList = data.customerList || []

            // 更新分页信息
            this.customerPagination.cursor = data.nextCursor || ''
            this.customerPagination.hasNext = !!data.nextCursor

            // 如果有游标且不是第一页，说明有上一页
            this.customerPagination.hasPrev = this.customerPagination.prevCursors.length > 0

            console.log('客户列表数据:', data)
            console.log('客户列表详细结构:', JSON.stringify(data, null, 2))
          } else {
            this.msgError(res.msg || '获取客户列表失败')
            this.customerList = []
          }
        })
        .catch((e) => {
          console.error('获取客户列表失败:', e)
          this.msgError('获取客户列表失败，请重试')
          this.customerList = []
        })
        .finally(() => {
          this.customerLoading = false
        })
    },

    // 加载下一页客户
    loadNextCustomers() {
      if (this.customerPagination.hasNext && this.customerPagination.cursor) {
        // 保存当前游标到历史记录
        this.customerPagination.prevCursors.push(this.customerPagination.cursor)
        this.loadCustomers(this.customerPagination.cursor)
      }
    },

    // 加载上一页客户
    loadPrevCustomers() {
      if (this.customerPagination.hasPrev && this.customerPagination.prevCursors.length > 0) {
        // 获取上一页的游标
        this.customerPagination.prevCursors.pop() // 移除当前页游标
        const prevCursor =
          this.customerPagination.prevCursors.length > 0
            ? this.customerPagination.prevCursors[this.customerPagination.prevCursors.length - 1]
            : ''
        this.loadCustomers(prevCursor)
      }
    },

    // 重置客户分页信息
    resetCustomerPagination() {
      this.customerPagination = {
        cursor: '',
        hasNext: false,
        hasPrev: false,
        prevCursors: [],
      }
    },

    // 搜索客户（暂时只是重新加载，实际搜索功能需要后端支持）
    searchCustomers() {
      this.resetCustomerPagination()
      this.loadCustomers()
    },

    // 重置客户搜索
    resetCustomerSearch() {
      this.customerSearchQuery = {
        customerName: '',
        followUser: '',
        dateRange: null,
      }
      this.searchCustomers()
    },

    // 获取会话状态文本
    getChatStatusText(status) {
      const statusMap = {
        0: '未开口',
        1: '已开口',
        2: '已删除',
      }
      return statusMap[status] || '未知'
    },

    // 获取会话状态标签类型
    getChatStatusType(status) {
      const typeMap = {
        0: 'info', // 未开口 - 灰色
        1: 'success', // 已开口 - 绿色
        2: 'danger', // 已删除 - 红色
      }
      return typeMap[status] || 'info'
    },

    // 格式化日期为 yyyy-MM-dd 格式
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
  },
}
</script>

<style scoped>
.searchForm {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.searchForm .el-form-item {
  margin-bottom: 10px;
}

.searchForm .el-form-item__label {
  font-weight: 500;
  color: #606266;
}
</style>
<template>
  <div>
    <div class="warning">
      <a href="https://www.yjaiscrm.cn?utm_source=yjaiscrmcode" target="_blank">
        <strong>
          yjaiscrm Scrm-是基于Java源码交付的企微SCRM,帮助企业构建高度自由安全的私域平台。:https://www.yjaiscrm.cn/
        </strong>
      </a>
    </div>

    <el-tabs v-if="isLink" v-model="activeName">
      <el-tab-pane label="外链配置" name="first">
        <div class="g-card">
          <!-- 搜索筛选栏 -->
          <el-form class="searchForm" ref="searchForm" :model="searchQuery" label-width="" inline>
            <el-form-item label="外链名称:">
              <el-input
                v-model="searchQuery.codeName"
                placeholder="请输入外链名称"
                clearable
                style="width: 200px"
                @keyup.enter="searchList"></el-input>
            </el-form-item>

            <el-form-item label="员工名称:">
              <el-select
                v-model="searchQuery.userName"
                placeholder="请选择员工"
                clearable
                filterable
                style="width: 200px"
                @change="searchList">
                <el-option
                  v-for="item in userList"
                  :key="item.userId"
                  :label="item.name"
                  :value="item.name"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="创建时间:">
              <el-date-picker
                v-model="searchQuery.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 240px"
                @change="searchList"></el-date-picker>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="searchList">查询</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>

          <div class="fxbw">
            <div>
              <el-button type="primary" @click=";(form = {}), (dialogVisible = true)">新建</el-button>
              <el-button type="primary" @click="synchShortLink()">同步外链</el-button>
            </div>
            <el-button :disabled="!multipleSelection.length" @click="del()" type="danger">批量删除</el-button>
          </div>
          <el-table
            :data="list"
            tooltip-effect="dark"
            highlight-current-row
            @selection-change="(selection) => (multipleSelection = selection.map((item) => item.id))">
            <el-table-column type="selection" width="50" align="center"></el-table-column>
            <el-table-column label="名称" prop="codeName" show-overflow-tooltip />
            <el-table-column label="地址" prop="codeUrl">
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

            <el-table-column label="操作" fixed="right" width="280">
              <template #default="{ row }">
                <el-button text @click=";(form = JSON.parse(JSON.stringify(row))), (dialogVisible = true)">
                  编辑
                </el-button>
                <el-button text @click="del(row.id)">删除</el-button>
                <!-- <template v-if="isLink">
                  <el-button text @click="viewCustomers(row)">查看客户</el-button>
                </template> -->
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

      <el-tab-pane label="外链统计" name="second">
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

    <el-tabs v-if="!isLink" v-model="activeName">
      <el-tab-pane label="员工活码配置" name="first">
        <div class="g-card">
          <!-- 搜索筛选栏 -->
          <el-form class="searchForm" ref="userCodeSearchForm" :model="userCodeSearchQuery" label-width="" inline>
            <el-form-item label="活码名称:">
              <el-input
                v-model="userCodeSearchQuery.codeName"
                placeholder="请输入活码名称"
                clearable
                style="width: 200px"
                @keyup.enter="searchUserCodeList"></el-input>
            </el-form-item>

            <el-form-item label="员工名称:">
              <el-select
                v-model="userCodeSearchQuery.userName"
                placeholder="请选择员工"
                clearable
                filterable
                style="width: 200px"
                @change="searchUserCodeList">
                <el-option
                  v-for="item in userList"
                  :key="item.userId"
                  :label="item.name"
                  :value="item.name"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="创建时间:">
              <el-date-picker
                v-model="userCodeSearchQuery.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 240px"
                @change="searchUserCodeList"></el-date-picker>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="searchUserCodeList">查询</el-button>
              <el-button @click="resetUserCodeSearch">重置</el-button>
            </el-form-item>
          </el-form>

          <div class="fxbw">
            <div>
              <el-button type="primary" @click=";(form = {}), (dialogVisible = true)">新建</el-button>
              <el-button type="primary" @click="synchUserCode()">同步活码</el-button>
            </div>
            <el-button :disabled="!multipleSelection.length" @click="del()" type="danger">批量删除</el-button>
          </div>
          <el-table
            :data="list"
            tooltip-effect="dark"
            highlight-current-row
            @selection-change="(selection) => (multipleSelection = selection.map((item) => item.id))">
            <el-table-column type="selection" width="50" align="center"></el-table-column>
            <el-table-column label="活码名称" prop="codeName" show-overflow-tooltip />
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

      <el-tab-pane label="员工活码统计" name="second">
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

    <!-- 同步模态框 -->
    <el-dialog
      :title="isLink ? '同步获客外链' : '同步员工活码'"
      v-model="syncDialogVisible"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close>
      <el-form :model="syncForm" label-width="100px">
        <el-form-item label="同步模式:">
          <el-radio-group v-model="syncForm.syncType">
            <el-radio label="full">常规同步（全量同步）</el-radio>
            <el-radio label="specific">指定同步（单条同步）</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="syncForm.syncType === 'specific'" :label="isLink ? '获客外链ID:' : '配置ID:'" required>
          <el-select
            v-model="syncForm.specificIds"
            :placeholder="isLink ? '请选择要同步的获客外链' : '请选择要同步的员工活码'"
            multiple
            filterable
            clearable
            style="width: 100%"
            @change="handleConfigIdChange">
            <el-option v-for="item in configIdOptions" :key="item.val" :label="item.key" :value="item.val"></el-option>
          </el-select>
          <div class="form-tip">
            {{ isLink ? '选择要同步的获客外链，可多选' : '选择要同步的员工活码配置，可多选' }}
          </div>

          <!-- 手动输入选项 -->
          <el-divider content-position="center">或手动输入</el-divider>
          <el-input
            v-model="manualIds"
            :placeholder="isLink ? '手动输入获客外链ID，多个用逗号分隔' : '手动输入员工活码配置ID，多个用逗号分隔'"
            clearable
            @input="handleManualInput"
            @blur="isInput = false"></el-input>
          <div class="form-tip">
            {{ isLink ? '手动输入获客外链ID，多个ID用英文逗号分隔' : '手动输入员工活码配置ID，多个ID用英文逗号分隔' }}
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="syncDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="performSync" :loading="syncLoading">
          {{ syncForm.syncType === 'full' ? '开始同步' : '指定同步' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 客户列表模态框 -->
    <el-dialog
      title="客户列表"
      v-model="customerDialogVisible"
      width="80%"
      :close-on-click-modal="false"
      destroy-on-close>
      <div class="customer-dialog-content">
        <!-- 搜索筛选栏 -->
        <el-form class="searchForm" :model="customerSearchQuery" label-width="" inline>
          <el-form-item label="客户姓名:">
            <el-input
              v-model="customerSearchQuery.customerName"
              placeholder="请输入客户姓名"
              clearable
              style="width: 200px"
              @keyup.enter="searchCustomers"></el-input>
          </el-form-item>

          <el-form-item label="跟进人:">
            <el-select
              v-model="customerSearchQuery.followUser"
              placeholder="请选择跟进人"
              clearable
              filterable
              style="width: 200px"
              @change="searchCustomers">
              <el-option v-for="item in userList" :key="item.userId" :label="item.name" :value="item.name"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="添加时间:">
            <el-date-picker
              v-model="customerSearchQuery.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 240px"></el-date-picker>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="searchCustomers">查询</el-button>
            <el-button @click="resetCustomerSearch">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 客户列表表格 -->
        <el-table :data="customerList" v-loading="customerLoading" tooltip-effect="dark" style="width: 100%">
          <el-table-column prop="externalName" label="客户姓名" width="150" show-overflow-tooltip />
          <el-table-column prop="followUserName" label="跟进人" width="120" />
          <el-table-column prop="createTime" label="添加时间" width="180" />
          <el-table-column prop="chatStatus" label="会话状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getChatStatusType(row.chatStatus)">
                {{ getChatStatusText(row.chatStatus) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container" style="text-align: center; margin-top: 20px">
          <el-button :disabled="!customerPagination.hasPrev" @click="loadPrevCustomers" v-loading="customerLoading">
            上一页
          </el-button>
          <span style="margin: 0 20px">共 {{ customerList.length }} 条记录</span>
          <el-button :disabled="!customerPagination.hasNext" @click="loadNextCustomers" v-loading="customerLoading">
            下一页
          </el-button>
        </div>
      </div>

      <template #footer>
        <el-button @click="customerDialogVisible = false">关闭</el-button>
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

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  line-height: 1.4;
}
</style>
