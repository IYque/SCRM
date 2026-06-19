<script setup lang="ts">
import { getList, setYjaiscrmComplaintTip, distributeHandle, updateStatus, findYjaiscrmComplaintTips } from './api'
const handleState = { '1': '未处理', '2': '已处理' }

const employees = ref([])
const form = ref({})

const selectedEmployeeName = computed(() => {
  console.log(employees.value)
  if (employees.value && employees.value.trim() !== '') {
    return employees.value
  }
  return '未设置'
})
onMounted(async () => {
  try {
    const response = await findYjaiscrmComplaintTips()
    if (response.data && Array.isArray(response.data)) {
      const userNames = response.data
        .filter((item) => item && typeof item.userName === 'string')
        .map((item) => item.userName)
        .join(', ')
      employees.value = userNames
      form.value.users = response.data
    } else {
      // ElMessageBox.warning('获取员工数据格式不正确');
    }
  } catch (error) {
    console.error('获取员工数据失败:', error)
  }
})
</script>

<template>
  <div :_="$store.setBusininessDesc(`<div>查看和管理客户投诉和处理详情</div>`)">
    <RequestChartTable ref="rct" :request="getList" searchBtnType="icon">
      <template #query="{ query }">
        <el-form-item label="处理状态" prop="complainType">
          <el-select v-model="query.handleState" :popper-append-to-body="false">
            <el-option v-for="(value, key) in handleState" :key="key" :label="value" :value="key" />
          </el-select>
        </el-form-item>
      </template>

      <template #operation="{ selectedIds }">
        <el-button type="primary" @click="$refs.dialogRef.action()">通知配置</el-button>

        <BaDialog
          ref="dialogRef"
          title="通知配置"
          width="500"
          :formProps="{ 'label-width': 'auto' }"
          :rules="{ users: { required: true, message: '必选项', trigger: 'change' } }"
          @confirm="
            () => $refs.dialogRef.confirm(() => setYjaiscrmComplaintTip(form.users?.map((e) => ({ userId: e.userId }))))
          ">
          <template #form="{}">
            <el-form-item prop="users" class="w100" label="投诉处理人">
              <SelectStaff v-model="form.users"></SelectStaff>

              <div class="g-tip">
                当前投诉通知员工为:
                <el-button type="success" size="small" plain>
                  {{ selectedEmployeeName }}
                </el-button>
              </div>
            </el-form-item>
          </template>
        </BaDialog>
      </template>

      <template #table="{ data }">
        <el-table-column prop="complainTypeContent" label="投诉类型" align="center"></el-table-column>
        <el-table-column prop="complainUserPhone" label="联系方式" align="center"></el-table-column>
        <el-table-column prop="complainTime" label="投诉时间" align="center"></el-table-column>
        <el-table-column prop="groupNameRule" label="处理状态" align="center">
          <template #default="{ row }">
            {{ handleState[row.handleState] }}
          </template>
        </el-table-column>
        <el-table-column prop="handleUserName" label="处理人" align="center"></el-table-column>
        <el-table-column prop="handleTime" label="处理时间" align="center"></el-table-column>
        <el-table-column label="操作" align="center" width="280" fixed="right">
          <template #default="{ row }">
            <TableOperateBtn type="" @click="$refs.dialogRefDetail.action(row)">详情</TableOperateBtn>
            <TableOperateBtn type="" @click="distributeHandle(row.id).then(() => msgSuccess())">通知</TableOperateBtn>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>

    <BaDialog
      ref="dialogRefDetail"
      title="详情"
      width="500"
      :formProps="{ 'label-position': 'top', style: 'display:grid;grid:auto / 1fr 1fr' }">
      <template #form="{ form }">
        <div>
          <el-form-item>
            <template #label>
              <span style="color: #ff0000; font-size: 16px">投诉类型:</span>
            </template>
            {{ handleState[form.handleState] }}
          </el-form-item>
          <el-form-item>
            <template #label>
              <span style="color: #ff0000; font-size: 16px">投诉时间:</span>
            </template>
            {{ form.complainTime }}
          </el-form-item>
          <el-form-item>
            <template #label>
              <span style="color: #ff0000; font-size: 16px">联系方式:</span>
            </template>
            {{ form.complainUserPhone ? form.complainUserPhone : '-' }}
          </el-form-item>
          <el-form-item>
            <template #label>
              <span style="color: #ff0000; font-size: 16px">投诉内容:</span>
            </template>
            {{ form.complainUserContent ? form.complainUserContent : '-' }}
          </el-form-item>
          <el-form-item>
            <template #label>
              <span style="color: #ff0000; font-size: 16px">投诉举证:</span>
            </template>
            <div class="gap10 gap5" v-if="form.complainAnnexList && form.complainAnnexList.length > 0">
              <el-image
                v-for="(item, index) in form.complainAnnexList?.filter((e) => e.annexType == 1)"
                :key="index"
                :src="item.annexUrl"
                style="width: 80px; height: 80px"
                fit="fill"
                :lazy="true"></el-image>
            </div>
            <template v-else>
              <!-- 当列表为空时显示 '-' -->
              <span>-</span>
            </template>
          </el-form-item>
        </div>
        <div>
          <el-form-item>
            <template #label>
              <span style="color: #ff0000; font-size: 16px">处理人:</span>
            </template>
            {{ form.handleUserName ? form.handleUserName : '-' }}
          </el-form-item>
          <el-form-item>
            <template #label>
              <span style="color: #ff0000; font-size: 16px">处理时间:</span>
            </template>
            {{ form.handleTime ? form.handleTime : '-' }}
          </el-form-item>
          <el-form-item>
            <template #label>
              <span style="color: #ff0000; font-size: 16px">处理内容:</span>
            </template>
            {{ form.handleContent ? form.handleContent : '-' }}
          </el-form-item>
          <el-form-item>
            <template #label>
              <span style="color: #ff0000; font-size: 16px">处理证明:</span>
            </template>
            <div class="gap10 gap5" v-if="form.complainAnnexList && form.complainAnnexList.length > 0">
              <!-- 渲染符合条件的图片 -->
              <el-image
                v-for="(item, index) in form.complainAnnexList.filter((e) => e.annexType === 2)"
                :key="index"
                :src="item.annexUrl"
                fit="fill"
                lazy></el-image>
            </div>
            <template v-else>
              <!-- 当列表为空时显示 '-' -->
              <span>-</span>
            </template>
          </el-form-item>
        </div>
      </template>
    </BaDialog>
  </div>
</template>

<style scoped lang="scss">
.custom-label {
  color: #ff0000; /* 修改为你想要的颜色 */
  font-size: 16px; /* 修改为你想要的字体大小 */
}
.code-image {
  width: 200px;
  height: 200px;
}

.code-image--small {
  width: 50px;
  height: 50px;
}
</style>
