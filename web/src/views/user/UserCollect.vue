<template>
  <div>
    <div class="page-header"><h2>我的收藏</h2></div>
    <el-empty v-if="!list.length" description="暂无收藏" />
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

const list = ref([])

onMounted(async () => {
  // 这里简化处理，实际应通过专门的收藏列表接口获取
  const res = await houseApi.recommend(10)
  list.value = res.data || []
})
</script>
