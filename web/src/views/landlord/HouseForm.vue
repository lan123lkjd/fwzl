<template>
  <div>
    <div class="page-header"><h2>{{ isEdit ? '编辑房源' : '发布房源' }}</h2></div>
    <el-card>
      <el-form :model="form" label-width="100px">
        <el-form-item label="房源标题"><el-input v-model="form.title" placeholder="请输入标题" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="月租金"><el-input v-model="form.price" type="number" placeholder="元/月" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="面积"><el-input v-model="form.area" type="number" placeholder="平方米" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="租赁类型">
            <el-radio-group v-model="form.houseType"><el-radio :label="1">整租</el-radio><el-radio :label="2">合租</el-radio></el-radio-group>
          </el-form-item></el-col>
        </el-row>
        <el-form-item label="所在地区">
          <el-select v-model="form.areaId" placeholder="请选择地区" clearable style="width: 200px">
            <el-option v-for="area in areaList" :key="area.id" :label="area.name" :value="area.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="6"><el-form-item label="室"><el-input-number v-model="form.rooms" :min="0" :max="10" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="厅"><el-input-number v-model="form.halls" :min="0" :max="5" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="卫"><el-input-number v-model="form.bathrooms" :min="0" :max="5" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="楼层">
            <el-input v-model="form.floor" placeholder="当前楼层" style="width:80px" /> / 
            <el-input v-model="form.totalFloor" placeholder="总楼层" style="width:80px" />
          </el-form-item></el-col>
        </el-row>
        <el-form-item label="朝向"><el-input v-model="form.orientation" placeholder="如：朝南" /></el-form-item>
        <el-form-item label="装修"><el-input v-model="form.decoration" placeholder="如：精装修" /></el-form-item>
        <el-form-item label="详细地址"><el-input v-model="form.address" placeholder="请输入详细地址" /></el-form-item>
        <el-form-item label="房源描述"><el-input v-model="form.description" type="textarea" :rows="4" placeholder="请描述房源特点" /></el-form-item>
        
        <el-form-item label="封面图片">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
            accept="image/*"
          >
            <img v-if="form.coverImage" :src="getImageUrl(form.coverImage)" class="cover-image" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸：800x600，支持 jpg、png 格式，大小不超过 5MB</div>
        </el-form-item>
        
        <el-form-item><el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存修改' : '立即发布' }}</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { houseApi } from '@/api/house'
import { commonApi } from '@/api/common'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isEdit = computed(() => !!route.params.id)

const baseUrl = 'http://localhost:8080/api'
const uploadUrl = `${baseUrl}/upload/cover`
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${userStore.token}` }))

const areaList = ref([])

const form = reactive({ 
  title: '', price: '', area: '', houseType: 1, rooms: 2, halls: 1, bathrooms: 1, 
  floor: '', totalFloor: '', orientation: '', decoration: '', address: '', 
  description: '', coverImage: '', areaId: null
})

const loadAreas = async () => {
  const res = await commonApi.areaList()
  areaList.value = res.data || []
}

const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `http://localhost:8080${path}`
}

const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const handleCoverSuccess = (response) => {
  if (response.code === 200) {
    form.coverImage = response.data
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

onMounted(async () => {
  await loadAreas()
  if (isEdit.value) {
    const res = await houseApi.detail(route.params.id)
    if (res.code === 200) {
      Object.assign(form, res.data)
    }
  }
})

const handleSubmit = async () => {
  if (!form.title) {
    ElMessage.warning('请输入房源标题')
    return
  }
  if (!form.price) {
    ElMessage.warning('请输入月租金')
    return
  }
  if (!form.coverImage) {
    ElMessage.warning('请上传封面图片')
    return
  }
  
  if (isEdit.value) {
    await houseApi.update(route.params.id, form)
  } else {
    await houseApi.publish(form)
  }
  ElMessage.success(isEdit.value ? '修改成功' : '发布成功，等待审核')
  router.push('/landlord/house')
}
</script>

<style scoped>
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
  height: 150px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fafafa;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.cover-image {
  width: 200px;
  height: 150px;
  object-fit: cover;
  display: block;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>
