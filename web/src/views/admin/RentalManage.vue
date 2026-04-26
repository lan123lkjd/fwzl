<template>
  <div>
    <div class="page-header"><h2>租赁管理</h2></div>

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
      <el-table-column prop="deposit" label="押金" width="100">
        <template #default="{ row }">
          ¥{{ row.deposit || 0 }}
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
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="page" :total="total" layout="prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api/admin'

const list = ref([])
const page = ref(1)
const total = ref(0)
const activeTab = ref('all')

const loadData = async () => {
  const params = { page: page.value, size: 10 }
  if (activeTab.value !== 'all') {
    if (activeTab.value === 'cancelled') {
      params.statusList = '4,5'
    } else {
      params.status = parseInt(activeTab.value)
    }
  }
  const res = await adminApi.rentalList(params)
  if (res.code === 200) {
    list.value = res.data.records || []
    total.value = res.data.total || 0
  }
}

const getStatusText = (s) => ({ 0: '待确认', 1: '待支付', 2: '租赁中', 3: '已完成', 4: '已取消', 5: '已拒绝' }[s] || '-')
const getStatusType = (s) => ({ 0: 'warning', 1: 'danger', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }[s] || '')

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
