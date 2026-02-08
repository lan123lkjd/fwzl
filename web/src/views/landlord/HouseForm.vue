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
        <el-form-item label="封面图片"><el-input v-model="form.coverImage" placeholder="图片URL" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存修改' : '立即发布' }}</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { houseApi } from '@/api/house'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const form = reactive({ title: '', price: '', area: '', houseType: 1, rooms: 2, halls: 1, bathrooms: 1, floor: '', totalFloor: '', orientation: '', decoration: '', address: '', description: '', coverImage: '' })

onMounted(async () => {
  if (isEdit.value) {
    const res = await houseApi.detail(route.params.id)
    if (res.code === 200) Object.assign(form, res.data)
  }
})

const handleSubmit = async () => {
  if (isEdit.value) {
    await houseApi.update(route.params.id, form)
  } else {
    await houseApi.publish(form)
  }
  ElMessage.success(isEdit.value ? '修改成功' : '发布成功，等待审核')
  router.push('/landlord/house')
}
</script>
