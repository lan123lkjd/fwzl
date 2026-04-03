<template>
  <div class="container">
    <div class="page-header"><h2>房屋资讯</h2></div>
    <el-row :gutter="20">
      <el-col :span="8" v-for="news in list" :key="news.id">
        <div class="news-card" @click="$router.push(`/news/${news.id}`)">
          <div class="image-container">
            <img 
              v-if="!imageErrors[news.id]"
              :src="getImageUrl(news.coverImage)" 
              @error="() => handleImageError(news.id)" 
              class="cover" 
              alt=""
            >
            <div v-else class="cover placeholder">
              <span>暂无图片</span>
            </div>
          </div>
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

// 直接导入默认图片
import defaultImage from '@/image/default-news.png'

const list = ref([])
const page = ref(1)
const total = ref(0)

// 记录每个图片的错误状态
const imageErrors = ref({})

// 处理图片URL
const getImageUrl = (path) => {
  if (!path) return defaultImage
  if (path.startsWith('http')) return path
  return path
}

// 处理图片加载失败
const handleImageError = (id) => {
  // 标记这个图片加载失败
  imageErrors.value = {
    ...imageErrors.value,
    [id]: true
  }
  console.log(`图片 ${id} 加载失败`)
}

const loadData = async () => {
  try {
    const res = await newsApi.list({ page: page.value, size: 9 })
    if (res.code === 200) { 
      list.value = res.data.records || []
      total.value = res.data.total || 0
      
      // 重置错误状态
      imageErrors.value = {}
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.news-card { 
  background: #fff; 
  border-radius: var(--radius); 
  overflow: hidden; 
  box-shadow: var(--shadow); 
  cursor: pointer; 
  margin-bottom: 20px; 
  transition: transform 0.3s; 
  
  &:hover { 
    transform: translateY(-4px); 
  }
  
  .image-container {
    width: 100%;
    height: 150px;
    background-color: #f5f5f5;
  }
  
  .cover { 
    width: 100%; 
    height: 100%; 
    object-fit: cover;
    
    &.placeholder {
      display: flex;
      align-items: center;
      justify-content: center;
      color: #999;
      font-size: 14px;
      background-color: #f5f5f5;
    }
  }
  
  .info { 
    padding: 16px; 
    
    .title { 
      font-weight: 600; 
      margin-bottom: 8px;
    }
    
    .summary { 
      font-size: 13px; 
      color: var(--text-secondary); 
      height: 40px; 
      overflow: hidden;
    }
    
    .meta { 
      margin-top: 8px; 
      font-size: 12px; 
      color: var(--info-color);
    }
  }
}
</style>