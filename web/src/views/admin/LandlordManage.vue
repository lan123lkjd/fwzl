<template>
  <div>
    <div class="page-header">
      <h2>房东管理</h2>
    </div>
    
    <div class="card" style="margin-bottom: 20px">
      <el-radio-group v-model="status" @change="loadData">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="0">待审核</el-radio-button>
        <el-radio-button :label="1">已认证</el-radio-button>
        <el-radio-button :label="2">已拒绝</el-radio-button>
      </el-radio-group>
    </div>
    
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="realName" label="真实姓名" />
      <el-table-column prop="idCard" label="身份证号" />
      <el-table-column prop="contact" label="联系方式" />
      <el-table-column prop="verifyStatus" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.verifyStatus === 1 ? 'success' : row.verifyStatus === 2 ? 'danger' : 'warning'">
            {{ row.verifyStatus === 1 ? '已认证' : row.verifyStatus === 2 ? '已拒绝' : '待审核' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row)">查看详情</el-button>
          <template v-if="row.verifyStatus === 0">
            <el-button size="small" type="success" @click="handleAudit(row.id, 1)">通过</el-button>
            <el-button size="small" type="danger" @click="handleAudit(row.id, 2, '资料不完整')">拒绝</el-button>
          </template>
          <span v-else-if="row.verifyRemark">{{ row.verifyRemark }}</span>
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

    <el-dialog v-model="dialogVisible" title="房东认证详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="真实姓名">{{ currentRow.realName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentRow.idCard }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentRow.contact }}</el-descriptions-item>
        <el-descriptions-item label="身份证人像面">
          <el-image v-if="currentRow.idCardFront" :src="currentRow.idCardFront" :preview-src-list="[currentRow.idCardFront]" fit="contain" style="width: 280px; height: 175px" />
          <span v-else>未上传</span>
        </el-descriptions-item>
        <el-descriptions-item label="身份证国徽面">
          <el-image v-if="currentRow.idCardBack" :src="currentRow.idCardBack" :preview-src-list="[currentRow.idCardBack]" fit="contain" style="width: 280px; height: 175px" />
          <span v-else>未上传</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const status = ref(null)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const currentRow = ref({})

const loadData = async () => {
  const res = await adminApi.landlordList({ page: page.value, size: size.value, status: status.value })
  if (res.code === 200) {
    list.value = res.data.records || []
    total.value = res.data.total || 0
  }
}

const handleAudit = async (id, newStatus, remark = '') => {
  await adminApi.auditLandlord(id, newStatus, remark)
  ElMessage.success('操作成功')
  loadData()
}

const showDetail = (row) => {
  currentRow.value = row
  dialogVisible.value = true
}

onMounted(loadData)
</script>
