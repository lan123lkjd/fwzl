<template>
  <div>
    <div class="page-header">
      <h2>资讯管理</h2>
      <el-button type="primary" @click="showForm = true; editId = null">发布资讯</el-button>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="封面" width="100">
        <template #default="{ row }">
          <el-image v-if="row.coverImage" :src="row.coverImage" style="width: 60px; height: 40px" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="viewCount" label="浏览量" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
        </template>
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
    
    <el-dialog v-model="showForm" :title="editId ? '编辑资讯' : '发布资讯'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="封面">
          <el-upload
            class="cover-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <el-image v-if="form.coverImage" :src="form.coverImage" fit="cover" class="cover-image" />
            <div v-else class="cover-placeholder">
              <el-icon :size="24"><Plus /></el-icon>
              <div>上传封面</div>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" /></el-form-item>
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
const form = reactive({ title: '', category: '', summary: '', content: '', coverImage: '' })

const loadData = async () => { const res = await adminApi.newsList(); if (res.code === 200) list.value = res.data || [] }
const handleEdit = (row) => { editId.value = row.id; Object.assign(form, row); showForm.value = true }
const handleDelete = async (id) => { await adminApi.deleteNews(id); ElMessage.success('删除成功'); loadData() }
const handleSubmit = async () => {
  if (editId.value) await adminApi.updateNews(editId.value, form)
  else await adminApi.publishNews(form)
  ElMessage.success('操作成功'); showForm.value = false; loadData()
  Object.assign(form, { title: '', category: '', summary: '', content: '', coverImage: '' })
}

const handleCoverSuccess = (response) => {
  if (response.code === 200) {
    form.coverImage = response.data
    ElMessage.success('封面上传成功')
  }
}

const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 5
  if (!isImage) ElMessage.error('只能上传图片文件!')
  if (!isLt2M) ElMessage.error('图片大小不能超过5MB!')
  return isImage && isLt2M
}

onMounted(loadData)
</script>

<style scoped>
.cover-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}
.cover-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}
.cover-image {
  width: 200px;
  height: 120px;
}
.cover-placeholder {
  width: 200px;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.cover-placeholder .el-icon {
  margin-bottom: 4px;
}
</style>
