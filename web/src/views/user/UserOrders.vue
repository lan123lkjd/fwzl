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
      <el-table-column label="服务评分" width="120">
        <template #default="{ row }">
          <el-rate v-if="row.rating" :model-value="row.rating" disabled />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="row.status < 2" size="small" type="danger" @click="handleCancel(row.id)">取消</el-button>
          <el-button v-if="row.status === 2 && !row.rating" size="small" type="primary" @click="openEvaluate(row)">评价</el-button>
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

    <el-dialog v-model="evaluateVisible" title="服务评价" width="400px">
      <el-form :model="evaluateForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="evaluateForm.rating" show-text :texts="['很差', '较差', '一般', '较好', '很好']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="evaluateForm.content" type="textarea" :rows="3" placeholder="请输入评价内容（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evaluateVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEvaluate">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api/order'
import { ElMessage } from 'element-plus'

const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const evaluateVisible = ref(false)
const evaluateForm = ref({ id: null, rating: 5, content: '' })

const loadData = async () => {
  const res = await orderApi.userList({ page: page.value, size: size.value })
  if (res.code === 200) { list.value = res.data.records || []; total.value = res.data.total || 0 }
}

const getStatusText = (s) => ({ 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消', 4: '已拒绝' }[s] || '-')
const getStatusType = (s) => ({ 0: 'warning', 1: 'primary', 2: 'success', 3: 'info', 4: 'danger' }[s] || '')

const handleCancel = async (id) => {
  await orderApi.cancel(id)
  ElMessage.success('已取消')
  loadData()
}

const openEvaluate = (row) => {
  evaluateForm.value = { id: row.id, rating: 5, content: '' }
  evaluateVisible.value = true
}

const submitEvaluate = async () => {
  if (!evaluateForm.value.rating) {
    ElMessage.warning('请选择评分')
    return
  }
  await orderApi.evaluate(evaluateForm.value.id, evaluateForm.value.rating, evaluateForm.value.content)
  ElMessage.success('评价成功')
  evaluateVisible.value = false
  loadData()
}

onMounted(loadData)
</script>
