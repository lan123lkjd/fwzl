<template>
  <div>
    <div class="page-header"><h2>我的预约</h2></div>
    <el-table :data="list" border stripe>
      <el-table-column prop="orderNo" label="预约单号" width="180" />
      <el-table-column prop="houseId" label="房源ID" width="80" />
      <el-table-column prop="orderTime" label="预约时间" width="160" />
      <el-table-column prop="contactName" label="联系人" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button v-if="row.status < 2" size="small" type="danger" @click="handleCancel(row.id)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="page" :total="total" layout="prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api/order'
import { ElMessage } from 'element-plus'

const list = ref([])
const page = ref(1)
const total = ref(0)

const loadData = async () => {
  const res = await orderApi.userList({ page: page.value, size: 10 })
  if (res.code === 200) { list.value = res.data.records || []; total.value = res.data.total || 0 }
}

const getStatusText = (s) => ({ 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消', 4: '已拒绝' }[s] || '-')
const getStatusType = (s) => ({ 0: 'warning', 1: 'primary', 2: 'success', 3: 'info', 4: 'danger' }[s] || '')

const handleCancel = async (id) => {
  await orderApi.cancel(id)
  ElMessage.success('已取消')
  loadData()
}

onMounted(loadData)
</script>
