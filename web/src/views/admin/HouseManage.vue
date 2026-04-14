<template>
  <div>
    <div class="page-header">
      <h2>房源管理</h2>
    </div>
    
    <div class="card" style="margin-bottom: 20px">
      <el-radio-group v-model="status" @change="loadData">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="0">待审核</el-radio-button>
        <el-radio-button :label="1">已上架</el-radio-button>
        <el-radio-button :label="2">已下架</el-radio-button>
      </el-radio-group>
    </div>
    
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="price" label="月租(元)" width="100" />
      <el-table-column prop="area" label="面积(㎡)" width="100" />
      <el-table-column label="户型" width="100">
        <template #default="{ row }">{{ row.rooms }}室{{ row.halls }}厅</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button size="small" type="success" @click="handleAudit(row.id, 1)">通过</el-button>
            <el-button size="small" type="danger" @click="handleAudit(row.id, 2)">拒绝</el-button>
          </template>
          <template v-else>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'primary'" 
              @click="handleAudit(row.id, row.status === 1 ? 2 : 1)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
          </template>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const status = ref(null)

const loadData = async () => {
  const res = await adminApi.houseList(status.value)
  if (res.code === 200) list.value = res.data || []
}

const getStatusText = (s) => ({ 0: '待审核', 1: '已上架', 2: '已下架', 3: '已出租' }[s] || '-')
const getStatusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }[s] || '')

const handleAudit = async (id, newStatus) => {
  await adminApi.auditHouse(id, newStatus)
  ElMessage.success('操作成功')
  loadData()
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该房源吗？删除后无法恢复', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await adminApi.deleteHouse(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

onMounted(loadData)
</script>
