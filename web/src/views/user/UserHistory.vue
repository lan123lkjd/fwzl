<template>
  <div>
    <div class="page-header">
      <h2>浏览历史</h2>
      <el-button v-if="list.length" type="danger" plain @click="handleClear">清空历史</el-button>
    </div>
    <el-empty v-if="!list.length" description="暂无浏览记录" />
    <div class="house-grid">
      <div v-for="house in list" :key="house.id" class="house-card" @click="$router.push(`/house/${house.id}`)">
        <img :src="house.coverImage || '/default-house.jpg'" class="cover" alt="">
        <div class="info">
          <div class="title">{{ house.title }}</div>
          <div class="meta"><div class="price">¥{{ house.price }}<span>/月</span></div></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { houseApi } from '@/api/house'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])

const loadData = async () => {
  const res = await houseApi.browseHistory({ page: 1, size: 100 })
  if (res.code === 200) list.value = res.data.records || []
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有浏览历史吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await houseApi.clearHistory()
    if (res.code === 200) {
      list.value = []
      ElMessage.success('已清空浏览历史')
    }
  } catch {}
}

onMounted(loadData)
</script>
