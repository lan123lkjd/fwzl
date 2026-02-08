<template>
  <div class="container" v-if="news">
    <el-card>
      <h1 style="font-size: 24px; margin-bottom: 16px">{{ news.title }}</h1>
      <div style="color: var(--text-secondary); margin-bottom: 24px">
        <span>{{ news.category }}</span> | <span>{{ news.viewCount }} 阅读</span>
      </div>
      <img v-if="news.coverImage" :src="news.coverImage" style="max-width: 100%; margin-bottom: 24px" />
      <div style="line-height: 1.8; font-size: 15px" v-html="news.content"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { newsApi } from '@/api/common'

const route = useRoute()
const news = ref(null)

onMounted(async () => {
  const res = await newsApi.detail(route.params.id)
  if (res.code === 200) news.value = res.data
})
</script>
