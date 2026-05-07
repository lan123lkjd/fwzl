<template>
  <div class="rental-manage">
    <div class="page-header">
      <h2>租赁管理</h2>
    </div>

    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待确认" name="0" />
      <el-tab-pane label="待支付" name="1" />
      <el-tab-pane label="租赁中" name="2" />
      <el-tab-pane label="已完成" name="3" />
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
          <el-button v-if="row.status === 2" size="small" type="primary" @click="handleComplete(row.id)">完成租赁</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
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
const size = ref(10)
const total = ref(0)
const activeTab = ref('all')

const loadData = async () => {
  const params = { page: page.value, size: size.value }
  if (activeTab.value !== 'all') {
    if (activeTab.value === 'cancelled') {
      params.statusList = '4,5'
    } else {
      params.status = parseInt(activeTab.value)
    }
  }
  const res = await rentalApi.landlordList(params)
  if (res.code === 200) {
    list.value = res.data.records || []
    total.value = res.data.total || 0
  }
}

const getStatusText = (s) => ({ 0: '待确认', 1: '待支付', 2: '租赁中', 3: '已完成', 4: '已取消', 5: '已拒绝' }[s] || '-')
const getStatusType = (s) => ({ 0: 'warning', 1: 'danger', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }[s] || '')

const handleConfirm = async (id) => {
  await ElMessageBox.confirm('确定要确认此租赁申请吗？确认后用户将进行支付。', '提示', { type: 'warning' })
  const res = await rentalApi.confirm(id)
  if (res.code === 200) {
    ElMessage.success('已确认，等待用户支付')
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
