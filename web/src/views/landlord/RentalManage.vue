<template>
  <div class="rental-manage">
    <div class="page-header">
      <h2>租赁管理</h2>
    </div>

    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待确认" name="0" />
      <el-tab-pane label="租赁中" name="1" />
      <el-tab-pane label="已完成" name="2" />
      <el-tab-pane label="已取消/拒绝" name="cancelled" />
    </el-tabs>

    <el-table :data="list" border stripe style="margin-top: 16px">
      <el-table-column prop="rentalNo" label="租赁单号" width="180" />
      <el-table-column prop="houseTitle" label="房源" min-width="150" show-overflow-tooltip />
      <el-table-column label="租赁期限" width="200">
        <template #default="{ row }">
          {{ row.startDate }} 至 {{ row.endDate }}
        </template>
      </el-table-column>
      <el-table-column prop="monthlyRent" label="月租金" width="100">
        <template #default="{ row }">
          ¥{{ row.monthlyRent }}
        </template>
      </el-table-column>
      <el-table-column prop="contactName" label="联系人" width="100" />
      <el-table-column prop="contactPhone" label="联系电话" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button size="small" type="success" @click="handleConfirm(row.id)">确认</el-button>
            <el-button size="small" type="danger" @click="handleReject(row.id)">拒绝</el-button>
          </template>
          <el-button v-if="row.status === 1" size="small" type="primary" @click="handleComplete(row.id)">完成租赁</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination 
        v-model:current-page="page" 
        :total="total" 
        layout="prev, pager, next" 
        @current-change="loadData" 
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { rentalApi } from '@/api/rental'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const page = ref(1)
const total = ref(0)
const activeTab = ref('all')

const loadData = async () => {
  const params = { page: page.value, size: 10 }
  if (activeTab.value !== 'all') {
    if (activeTab.value === 'cancelled') {
      params.status = 3  // 先查已取消，后端会过滤
    } else {
      params.status = parseInt(activeTab.value)
    }
  }
  const res = await rentalApi.landlordList(params)
  if (res.code === 200) {
    let records = res.data.records || []
    // 如果是已取消/拒绝标签，需要再查询状态4的记录并合并
    if (activeTab.value === 'cancelled') {
      const res2 = await rentalApi.landlordList({ page: page.value, size: 10, status: 4 })
      if (res2.code === 200 && res2.data.records) {
        records = [...records, ...res2.data.records]
      }
    }
    list.value = records
    total.value = res.data.total || 0
  }
}

const getStatusText = (s) => ({ 0: '待确认', 1: '租赁中', 2: '已完成', 3: '已取消', 4: '已拒绝' }[s] || '-')
const getStatusType = (s) => ({ 0: 'warning', 1: 'primary', 2: 'success', 3: 'info', 4: 'danger' }[s] || '')

const handleConfirm = async (id) => {
  await ElMessageBox.confirm('确定要确认此租赁申请吗？确认后房源将标记为已出租。', '提示', { type: 'warning' })
  const res = await rentalApi.confirm(id)
  if (res.code === 200) {
    ElMessage.success('已确认')
    loadData()
  } else {
    ElMessage.error(res.message || '操作失败')
  }
}

const handleReject = async (id) => {
  const { value: remark } = await ElMessageBox.prompt('请输入拒绝原因（可选）', '拒绝租赁', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: '拒绝原因'
  })
  const res = await rentalApi.reject(id, remark || '')
  if (res.code === 200) {
    ElMessage.success('已拒绝')
    loadData()
  } else {
    ElMessage.error(res.message || '操作失败')
  }
}

const handleComplete = async (id) => {
  await ElMessageBox.confirm('确定要结束此租赁吗？房源将重新变为可出租状态。', '提示', { type: 'warning' })
  const res = await rentalApi.complete(id)
  if (res.code === 200) {
    ElMessage.success('租赁已完成')
    loadData()
  } else {
    ElMessage.error(res.message || '操作失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-header {
  margin-bottom: 16px;
}
.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>
