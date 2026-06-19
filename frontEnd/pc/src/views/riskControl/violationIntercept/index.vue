<script>
import * as api from './api'

export default {
  props: {},
  data() {
    return {
      api,
      // 遮罩层
      loading: false,
      drawerVisible: false,
      // 表单参数
      form: {
        ruleName: '',
        sensitiveWords: '',
        staffIds: [],
        interceptType: 1, // 1:警告并拦截, 2:仅警告
        semantics: [], // 语义类型：1:手机号、2:邮箱地址、3:红包
      },
      // 表单验证规则
      rules: Object.freeze({
        ruleName: [{ required: true, message: '必填项', trigger: 'blur' }],
        sensitiveWords: [{ required: true, message: '必填项', trigger: 'blur' }],
        staffIds: [{ required: true, message: '必填项', trigger: 'blur' }],
        interceptType: [{ required: true, message: '必填项', trigger: 'change' }],
      }),
      // 员工列表数据
      staffList: [],
    }
  },
  computed: {},
  created() {
    this.getStaffList()
  },
  methods: {
    // 获取员工列表
    getStaffList() {
      // 调用员工列表接口，这里需要根据实际情况替换为正确的API
      api.getStaffList().then(res => {
        this.staffList = res.data || []
      })
    },
    // 根据员工ID获取员工名称
    getStaffName(staffId) {
      if (!staffId) return ''
      const staff = this.staffList.find(item => item.id === staffId || item.userId === staffId)
      return staff ? staff.name : staffId
    },
    // 根据语义类型ID获取语义类型名称
    getSemanticName(semanticId) {
      const semanticMap = {
        1: '手机号',
        2: '邮箱地址',
        3: '红包'
      }
      return semanticMap[semanticId] || semanticId
    },
    // 新增规则
    add() {
      this.form = {
        ruleName: '',
        sensitiveWords: '',
        staffIds: [],
        interceptType: 1,
        staffNames: '',
        semantics: [], // 语义类型：1:手机号、2:邮箱地址、3:红包
      }
      this.drawerVisible = true
    },
    // 编辑规则
    edit(data) {
      this.form = JSON.parse(JSON.stringify(data))
      
      // 处理敏感词：如果是数组，转换为逗号分隔的字符串
      if (Array.isArray(this.form.sensitiveWords)) {
        this.form.sensitiveWords = this.form.sensitiveWords.join(',')
      }
      
      // 处理员工ID：如果是字符串，转换为数组
      if (typeof this.form.staffIds === 'string') {
        this.form.staffIds = this.form.staffIds.split(',').filter(Boolean)
      } else if (!Array.isArray(this.form.staffIds)) {
        this.form.staffIds = []
      }
      
      // 处理拦截方式：确保是字符串类型，与表单label一致
      if (typeof this.form.interceptType === 'number') {
        this.form.interceptType = this.form.interceptType.toString()
      }
      
      // 处理语义类型：确保是数组类型
      if (typeof this.form.semantics === 'string') {
        this.form.semantics = this.form.semantics.split(',').filter(Boolean).map(Number)
      } else if (!Array.isArray(this.form.semantics)) {
        this.form.semantics = []
      }
      
      this.drawerVisible = true
    },
    // 删除规则
    del(data) {
      this.$confirm('确定要删除这条规则吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        api.del(data.id).then(() => {
          this.msgSuccess('删除成功')
          this.$refs.rct.getList()
        })
      }).catch(() => {})
    },

    // 远程搜索员工
    handleRemoteSearch(query) {
      if (query) {
        this.loading = true
        // 简单的本地过滤实现
        setTimeout(() => {
          this.loading = false
        }, 200)
      }
    },
    // 提交表单
    submit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          // 格式化敏感词，保持为逗号分隔的字符串
          const sensitiveWords = this.form.sensitiveWords.trim()
          // 将员工ID数组转换为逗号分隔的字符串
          const staffIds = Array.isArray(this.form.staffIds) ? this.form.staffIds.join(',') : ''
          // 将语义类型数组转换为逗号分隔的字符串
          const semantics = Array.isArray(this.form.semantics) ? this.form.semantics.join(',') : ''
          
          const formData = {
            ...this.form,
            sensitiveWords,
            staffIds,
            semantics,
            interceptType: parseInt(this.form.interceptType), // 确保拦截类型是整数
            delFlag: this.form.delFlag || 0 // 添加删除标记字段
          }
          
          api.saveOrUpdate(formData).then(() => {
            this.msgSuccess('操作成功')
            this.drawerVisible = false
            this.$refs.rct.getList()
          })
        }
      })
    },
  },
}
</script>

<template>
  <div class="violationIntercept">
    <RequestChartTable ref="rct" :request="api.getList" searchBtnType="icon">
      <template #query="{ query }">
        <BaInput label="规则名称" prop="ruleName" v-model="query.ruleName"></BaInput>
      </template>

      <template #operate="{ goRoute }">
        <el-button type="primary" @click="add">新建规则</el-button>
      </template>

      <template #table="{ apiConfirm, goRoute }">
        <el-table-column label="规则名称" align="center" prop="ruleName" />
        <el-table-column label="敏感词" align="center" prop="sensitiveWords">
          <template #default="{ row }">
            <div>
              <span v-for="(word, index) in (Array.isArray(row.sensitiveWords) ? row.sensitiveWords : row.sensitiveWords?.split(',') || [])" :key="index" class="tag">
                {{ word }}
              </span>
            </div>
          </template>
        </el-table-column>
         <el-table-column label="语义类型" align="center">
          <template #default="{ row }">
            <div>
              <span v-for="(semantic, index) in (row.semantics ? row.semantics.split(',').map(Number) : [])" :key="index" class="tag">
                {{ getSemanticName(semantic) }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="风控范围" align="center">
          <template #default="{ row }">
            <div>
              <span v-for="(staffId, index) in (row.staffIds?.split(',') || [])" :key="index" class="tag">
                {{ getStaffName(staffId) }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="拦截方式" align="center" prop="interceptType">
          <template #default="{ row }">
            <span v-if="row.interceptType === 1">警告并拦截</span>
            <span v-else-if="row.interceptType === 2">仅警告</span>
          </template>
        </el-table-column>
       
        <el-table-column label="最近更新时间" align="center" prop="updateTime" />
        <el-table-column label="操作" align="center">
          <template #default="{ row }">
            <el-button text @click="edit(row)">编辑</el-button>
            <el-button text type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>

    <!-- 规则抽屉 -->
    <el-drawer
      :title="form.id ? '修改规则' : '新增规则'"
      :model-value="drawerVisible"
      @update:model-value="drawerVisible = $event"
      width="500px"
      append-to-body
      :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input
            v-model.trim="form.ruleName"
            maxlength="20"
            show-word-limit
            placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="敏感词" prop="sensitiveWords">
          <el-input
            v-model.trim="form.sensitiveWords"
            type="textarea"
            :rows="4"
            maxlength="200"
            show-word-limit
            placeholder="请输入敏感词，多个敏感词用英文逗号隔开" />
        </el-form-item>
        <el-form-item label="额外规则">
          <el-checkbox-group v-model="form.semantics">
            <div class="checkbox-item">
              <el-checkbox :label="1">
                <span class="checkbox-label">手机号</span>
              </el-checkbox>
              <div class="checkbox-desc">检测和拦截包含手机号码</div>
            </div>
            <div class="checkbox-item">
              <el-checkbox :label="2">
                <span class="checkbox-label">邮箱地址</span>
              </el-checkbox>
              <div class="checkbox-desc">检测和拦截包含邮箱地址</div>
            </div>
            <div class="checkbox-item">
              <el-checkbox :label="3">
                <span class="checkbox-label">红包</span>
              </el-checkbox>
              <div class="checkbox-desc">检测和拦截包含红包</div>
            </div>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="风控范围" prop="staffIds">
          <el-select
            v-model="form.staffIds"
            multiple
            filterable
            reserve-keyword
            placeholder="请选择员工"
            clearable
            style="width: 100%">
            <el-option
              v-for="staff in staffList"
              :key="staff.id"
              :label="staff.name"
              :value="staff.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="拦截方式" prop="interceptType">
          <el-radio-group v-model="form.interceptType" vertical>
            <div class="radio-item">
              <el-radio label="1">警告并拦截</el-radio>
              <div class="radio-desc">警告员工，且不允许员工发送</div>
            </div>
            <div class="radio-item">
              <el-radio label="2">仅警告</el-radio>
              <div class="radio-desc">警告员工，但允许员工继续发送</div>
            </div>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" @click="submit" v-preventReClick="1000">确定</el-button>
        </div>
      </template>
    </el-drawer>


  </div>
</template>

<style lang="scss" scoped>
.violationIntercept {
  .tag {
    display: inline-block;
    padding: 2px 8px;
    background-color: #f0f5ff;
    border-radius: 4px;
    margin-right: 8px;
    margin-bottom: 8px;
    color: #666;
  }
  
  // yjaiscrm SCRM广告横幅样式
  .yjaiscrm-ad-banner {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 12px 0;
    margin-bottom: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    
    .ad-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;
      
      .ad-text {
        font-size: 14px;
        line-height: 1.5;
      }
      
      .ad-link {
        color: white;
        text-decoration: underline;
        font-weight: bold;
        padding: 6px 12px;
        border-radius: 4px;
        transition: background-color 0.3s;
        
        &:hover {
          background-color: rgba(255, 255, 255, 0.2);
        }
      }
    }
  }
}
</style>

/* 全局样式，确保提示文字显示 */
<style>
.radio-desc {
  margin-left: 22px !important;
  margin-top: 6px !important;
  color: #909399 !important;
  font-size: 14px !important;
  line-height: 1.5 !important;
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
}
.radio-item {
  display: block !important;
  width: 100% !important;
  margin-bottom: 20px !important;
  clear: both !important;
  float: none !important;
}

.radio-item .el-radio {
  display: block !important;
  margin-bottom: 6px !important;
}

.radio-item .el-radio__label {
  display: inline !important;
}

/* 额外规则复选框样式 */
.checkbox-item {
  margin-bottom: 16px !important;
  display: block !important;
  width: 100% !important;
}

.checkbox-item .el-checkbox {
  display: block !important;
  margin-bottom: 4px !important;
}

.checkbox-label {
  font-weight: 500 !important;
  color: #303133 !important;
}

.checkbox-desc {
  margin-left: 22px !important;
  margin-top: 4px !important;
  color: #909399 !important;
  font-size: 13px !important;
  line-height: 1.4 !important;
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
}
</style>