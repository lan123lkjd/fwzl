<template>
  <div>
    <div class="page-header">
      <h2>用户管理</h2>
    </div>
    
    <div class="card" style="margin-bottom: 20px">
      <el-input v-model="keyword" placeholder="搜索用户名/昵称/手机号" style="width: 300px" clearable @keyup.enter="loadData">
        <template #append>
          <el-button @click="loadData"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>
    
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="头像" width="80">
        <template #default="{ row }">
          <el-avatar :size="40" :src="row.avatar || ''">
            <el-icon :size="20"><User /></el-icon>
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 0 ? 'danger' : row.role === 2 ? 'warning' : ''">
            {{ row.role === 0 ? '管理员' : row.role === 2 ? '房东' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
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

    <el-dialog v-model="editDialogVisible" title="编辑用户" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <el-avatar :size="80" :src="editForm.avatar || ''">
              <el-icon :size="32"><Plus /></el-icon>
            </el-avatar>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" style="width: 100%">
            <el-option :value="1" label="普通用户" />
            <el-option :value="2" label="房东" />
            <el-option :value="0" label="管理员" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const keyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)
const editDialogVisible = ref(false)
const editForm = reactive({ id: null, username: '', nickname: '', phone: '', email: '', avatar: '', role: 1 })

const loadData = async () => {
  const res = await adminApi.userList({ page: page.value, size: size.value, keyword: keyword.value })
  if (res.code === 200) {
    list.value = res.data.records || []
    total.value = res.data.total || 0
  }
}

const toggleStatus = async (row) => {
  await adminApi.updateUserStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  loadData()
}

const handleDelete = async (id) => {
  await adminApi.deleteUser(id)
  ElMessage.success('删除成功')
  loadData()
}

const openEditDialog = (row) => {
  Object.assign(editForm, {
    id: row.id,
    username: row.username,
    nickname: row.nickname,
    phone: row.phone,
    email: row.email,
    avatar: row.avatar,
    role: row.role
  })
  editDialogVisible.value = true
}

const handleEdit = async () => {
  await adminApi.updateUser(editForm.id, editForm)
  ElMessage.success('修改成功')
  editDialogVisible.value = false
  loadData()
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    editForm.avatar = response.data
    ElMessage.success('头像上传成功')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) ElMessage.error('只能上传图片文件!')
  if (!isLt2M) ElMessage.error('图片大小不能超过2MB!')
  return isImage && isLt2M
}

onMounted(loadData)
</script>

<style scoped>
.avatar-uploader :deep(.el-upload) {
  border: 2px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}
.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}
</style>
