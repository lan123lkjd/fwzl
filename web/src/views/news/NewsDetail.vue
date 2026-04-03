<template>
  <div class="container" v-if="news">
    <el-card>
      <h1 style="font-size: 24px; margin-bottom: 16px">{{ news.title }}</h1>
      <div style="color: var(--text-secondary); margin-bottom: 24px">
        <span>{{ news.category }}</span> | <span>{{ news.viewCount }} 阅读</span>
      </div>
      
      <!-- 使用v-if控制图片显示 -->
      <img 
        v-if="news.coverImage && !coverError" 
        :src="getImageUrl(news.coverImage)" 
        @error="coverError = true"
        style="max-width: 100%; margin-bottom: 24px" 
        alt="封面图"
      >
      <div 
        v-else-if="news.coverImage && coverError" 
        class="image-error"
        style="width: 100%; height: 200px; background: #f5f5f5; display: flex; align-items: center; justify-content: center; margin-bottom: 24px; color: #999;"
      >
        图片加载失败
      </div>
      
      <!-- 内容区域 - 简单处理，只信任内容中的图片地址 -->
      <div style="line-height: 1.8; font-size: 15px" v-html="news.content"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { newsApi } from '@/api/common'

// 导入默认图片
import defaultImage from '@/image/default-news.png'

const route = useRoute()
const news = ref(null)
const coverError = ref(false)

// 处理图片URL
const getImageUrl = (path) => {
  if (!path) return defaultImage
  if (path.startsWith('http')) return path
  return path
}

// 重置错误状态
const resetErrors = () => {
  coverError.value = false
}

onMounted(async () => {
  try {
    const res = await newsApi.detail(route.params.id)
    if (res.code === 200) {
      news.value = res.data
      resetErrors()
      
      // 调试信息
      console.log('详情页数据:', {
        coverImage: res.data.coverImage,
        coverImageUrl: getImageUrl(res.data.coverImage)
      })
    }
  } catch (error) {
    console.error('加载详情失败:', error)
  }
})
</script>

<style scoped>
/* 简单样式 */
.image-error {
  width: 100%;
  height: 200px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  color: #999;
}
</style>