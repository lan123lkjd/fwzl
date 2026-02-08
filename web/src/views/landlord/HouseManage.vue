<template>
  <div>
    <div class="page-header">
      <h2>房源管理</h2>
      <el-button type="primary" @click="$router.push('/landlord/house/add')">发布房源</el-button>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="price" label="月租(元)" width="100" />
      <el-table-column label="户型" width="100"><template #default="{ row }">{{ row.rooms }}室{{ row.halls }}厅</template></el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }"><el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/landlord/house/edit/${row.id}`)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
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
import { houseApi } from '@/api/house'
import { ElMessage } from 'element-plus'

const list = ref([])
const page = ref(1)
const total = ref(0)

const loadData = async () => {
  const res = await houseApi.landlordList({ page: page.value, size: 10 })
  if (res.code === 200) { list.value = res.data.records || []; total.value = res.data.total || 0 }
}

const getStatusText = (s) => ({ 0: '待审核', 1: '已上架', 2: '已下架', 3: '已出租' }[s] || '-')
const getStatusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }[s] || '')

const toggleStatus = async (row) => {
  await houseApi.updateStatus(row.id, row.status === 1 ? 2 : 1)
  ElMessage.success('操作成功')
  loadData()
}

onMounted(loadData)
</script>
