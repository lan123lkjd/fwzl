<template>
  <div>
    <div class="page-header">
      <h2>评论管理</h2>
      <el-input 
        v-model="keyword" 
        placeholder="搜索评论内容" 
        style="width: 250px" 
        clearable 
        @clear="loadData"
        @keyup.enter="loadData"
      >
        <template #append>
          <el-button @click="loadData">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
    </div>
    
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="houseId" label="房源ID" width="100" />
      <el-table-column prop="content" label="评论内容" show-overflow-tooltip min-width="300">
        <template #default="{ row }">
          <span>{{ row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="upvoteCount" label="点赞数" width="80">
        <template #default="{ row }">
          <el-tag type="primary" size="small">{{ row.upvoteCount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="parentId" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.parentId === 0 ? 'success' : 'info'" size="small">
            {{ row.parentId === 0 ? '主评论' : '回复' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-popconfirm 
            title="确定删除该评论？删除后不可恢复！" 
            @confirm="handleDelete(row.id)"
            confirm-button-text="删除"
            cancel-button-text="取消"
            confirm-button-type="danger"
          >
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
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
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const keyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await adminApi.evaluationList({ page: page.value, size: size.value, keyword: keyword.value })
    if (res.code === 200) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await adminApi.deleteEvaluation(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

onMounted(loadData)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
