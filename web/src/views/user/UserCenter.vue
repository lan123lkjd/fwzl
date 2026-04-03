<template>
  <div>
    <div class="page-header"><h2>个人中心</h2></div>
    
    <!-- 头像上传区域 -->
    <el-card style="margin-bottom: 20px">
      <div style="display: flex; align-items: center; gap: 24px ;margin-left: 50px;margin-bottom: 30px;">
        <el-upload
          class="avatar-uploader"
          action="/api/upload"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
          :headers="{ Authorization: 'Bearer ' + userStore.token }"
        >
          <el-avatar :size="80" :src="form.avatar || ''" style="cursor: pointer">
            <el-icon :size="32"><Plus /></el-icon>
          </el-avatar>
        </el-upload>
        <div>
          <div style="font-size: 18px; font-weight: 600">{{ form.nickname || form.username }}</div>
          <div style="color: var(--text-secondary); margin-top: 8px">点击头像更换</div>
        </div>
      </div>

      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleUpdate">保存修改</el-button></el-form-item>
      </el-form>
    </el-card>
    
    <el-card style="margin-top: 20px">
      <h3 style="margin-bottom: 16px">修改密码</h3>
      <el-form :model="pwdForm" label-width="100px">
        <el-form-item label="原密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item><el-button type="warning" @click="handleChangePwd">修改密码</el-button></el-form-item>
      </el-form>
    </el-card>
    
    <el-card style="margin-top: 20px" v-if="!userStore.isLandlord">
      <h3 style="margin-bottom: 16px">申请成为房东</h3>
      <el-form :model="landlordForm" label-width="120px">
        <el-form-item label="真实姓名"><el-input v-model="landlordForm.realName" /></el-form-item>
        <el-form-item label="身份证号"><el-input v-model="landlordForm.idCard" /></el-form-item>
        <el-form-item label="联系方式"><el-input v-model="landlordForm.contact" /></el-form-item>
        <el-form-item label="身份证人像面" required>
          <el-upload
            class="id-card-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="(res) => handleIdCardSuccess(res, 'front')"
            :before-upload="beforeIdCardUpload"
            :headers="{ Authorization: 'Bearer ' + userStore.token }"
          >
            <el-image v-if="landlordForm.idCardFront" :src="landlordForm.idCardFront" fit="contain" class="id-card-image" />
            <div v-else class="id-card-placeholder">
              <el-icon :size="32"><Plus /></el-icon>
              <div>上传人像面</div>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="身份证国徽面" required>
          <el-upload
            class="id-card-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="(res) => handleIdCardSuccess(res, 'back')"
            :before-upload="beforeIdCardUpload"
            :headers="{ Authorization: 'Bearer ' + userStore.token }"
          >
            <el-image v-if="landlordForm.idCardBack" :src="landlordForm.idCardBack" fit="contain" class="id-card-image" />
            <div v-else class="id-card-placeholder">
              <el-icon :size="32"><Plus /></el-icon>
              <div>上传国徽面</div>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item><el-button type="success" @click="handleApply">提交申请</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { landlordApi } from '@/api/landlord'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const form = reactive({ username: '', nickname: '', phone: '', email: '', avatar: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '' })
const landlordForm = reactive({ realName: '', idCard: '', contact: '', idCardFront: '', idCardBack: '' })

onMounted(() => { if (userStore.user) Object.assign(form, userStore.user) })

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    form.avatar = response.data
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

const handleUpdate = async () => {
  await userStore.updateProfile(form)
  ElMessage.success('修改成功')
  userStore.fetchUserInfo()
}

const handleChangePwd = async () => {
  await userStore.updatePassword(pwdForm.oldPassword, pwdForm.newPassword)
  ElMessage.success('密码修改成功')
  pwdForm.oldPassword = ''; pwdForm.newPassword = ''
}

const handleApply = async () => {
  if (!landlordForm.idCardFront) {
    ElMessage.error('请上传身份证人像面')
    return
  }
  if (!landlordForm.idCardBack) {
    ElMessage.error('请上传身份证国徽面')
    return
  }
  const res = await landlordApi.apply(landlordForm)
  if (res.code === 200) ElMessage.success('申请已提交，等待审核')
  else ElMessage.error(res.message)
}

const handleIdCardSuccess = (response, type) => {
  if (response.code === 200) {
    if (type === 'front') {
      landlordForm.idCardFront = response.data
    } else {
      landlordForm.idCardBack = response.data
    }
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const beforeIdCardUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) ElMessage.error('只能上传图片文件!')
  if (!isLt5M) ElMessage.error('图片大小不能超过5MB!')
  return isImage && isLt5M
}
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

.id-card-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}
.id-card-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.id-card-image {
  width: 280px;
  height: 175px;
}

.id-card-placeholder {
  width: 280px;
  height: 175px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  font-size: 14px;
}
.id-card-placeholder .el-icon {
  margin-bottom: 8px;
}
</style>
