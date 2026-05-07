<template>
  <div>
    <div class="page-header">
      <h2>公告管理</h2>
      <el-button type="primary" @click="showForm = true; editId = null">发布公告</el-button>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">{{ { 1: '系统公告', 2: '活动公告', 3: '政策公告' }[row.type] || '-' }}</template>
      </el-table-column>
      <el-table-column prop="top" label="置顶" width="80">
        <template #default="{ row }"><el-tag :type="row.top ? 'danger' : 'info'">{{ row.top ? '是' : '否' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
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
    
    <el-dialog v-model="showForm" :title="editId ? '编辑公告' : '发布公告'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="系统公告" :value="1" /><el-option label="活动公告" :value="2" /><el-option label="政策公告" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="置顶"><el-switch v-model="form.top" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showForm = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const showForm = ref(false)
const editId = ref(null)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const form = reactive({ title: '', type: 1, top: 0, content: '', status: 1 })

const loadData = async () => { 
  const res = await adminApi.noticeList({ page: page.value, size: size.value })
  if (res.code === 200) {
    list.value = res.data.records || res.data || []
    total.value = res.data.total || 0
  }
}
const handleEdit = (row) => { editId.value = row.id; Object.assign(form, row); showForm.value = true }
const handleDelete = async (id) => { await adminApi.deleteNotice(id); ElMessage.success('删除成功'); loadData() }
const handleSubmit = async () => {
  if (editId.value) await adminApi.updateNotice(editId.value, form)
  else await adminApi.publishNotice(form)
  ElMessage.success('操作成功'); showForm.value = false; loadData()
  Object.assign(form, { title: '', type: 1, top: 0, content: '', status: 1 })
}

onMounted(loadData)
</script>
