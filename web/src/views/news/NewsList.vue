<template>
  <div class="container">
    <div class="page-header"><h2>房屋资讯</h2></div>
    <el-row :gutter="20">
      <el-col :span="8" v-for="news in list" :key="news.id">
        <div class="news-card" @click="$router.push(`/news/${news.id}`)">
          <img :src="news.coverImage || '/default-news.jpg'" class="cover" alt="">
          <div class="info">
            <div class="title">{{ news.title }}</div>
            <div class="summary">{{ news.summary }}</div>
            <div class="meta"><span>{{ news.viewCount }} 阅读</span></div>
          </div>
        </div>
      </el-col>
    </el-row>
    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="page" :total="total" layout="prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { newsApi } from '@/api/common'

const list = ref([])
const page = ref(1)
const total = ref(0)

const loadData = async () => {
  const res = await newsApi.list({ page: page.value, size: 9 })
  if (res.code === 200) { list.value = res.data.records || []; total.value = res.data.total || 0 }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.news-card { background: #fff; border-radius: var(--radius); overflow: hidden; box-shadow: var(--shadow); cursor: pointer; margin-bottom: 20px; transition: transform 0.3s; &:hover { transform: translateY(-4px); }
  .cover { width: 100%; height: 150px; object-fit: cover; }
  .info { padding: 16px; .title { font-weight: 600; margin-bottom: 8px; } .summary { font-size: 13px; color: var(--text-secondary); height: 40px; overflow: hidden; } .meta { margin-top: 8px; font-size: 12px; color: var(--info-color); } }
}
</style>
