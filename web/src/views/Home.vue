<template>
  <div class="home-page">
    <section class="hero">
      <div class="container">
        <h1>找房租房 一站搞定</h1>
        <p>海量真实房源，智能推荐匹配，省心更省力</p>
        <div class="search-box">
          <el-input v-model="keyword" placeholder="搜索区域、小区或关键词" size="large" clearable>
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" @click="handleSearch">搜索房源</el-button>
        </div>
      </div>
    </section>
    
    <section class="container section">
      <div class="section-header">
        <h2>智能推荐</h2>
        <router-link to="/house">查看更多 →</router-link>
      </div>
      <div class="house-grid">
        <div v-for="house in recommendList" :key="house.id" class="house-card" @click="goDetail(house.id)">
          <img :src="house.coverImage || '/default-house.jpg'" class="cover" alt="">
          <div class="info">
            <div class="title">{{ house.title }}</div>
            <div class="address">{{ house.address }}</div>
            <div class="meta">
              <div class="price">¥{{ house.price }}<span>/月</span></div>
              <div class="tags">
                <el-tag size="small">{{ house.rooms }}室{{ house.halls }}厅</el-tag>
                <el-tag size="small" type="info">{{ house.area }}㎡</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <section class="container section">
      <div class="section-header">
        <h2>热门房源</h2>
        <router-link to="/house">查看更多 →</router-link>
      </div>
      <div class="house-grid">
        <div v-for="house in hotList" :key="house.id" class="house-card" @click="goDetail(house.id)">
          <img :src="house.coverImage || '/default-house.jpg'" class="cover" alt="">
          <div class="info">
            <div class="title">{{ house.title }}</div>
            <div class="address">{{ house.address }}</div>
            <div class="meta">
              <div class="price">¥{{ house.price }}<span>/月</span></div>
              <div class="tags">
                <el-tag size="small">{{ house.rooms }}室{{ house.halls }}厅</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <section class="container section">
      <div class="section-header">
        <h2>房屋资讯</h2>
        <router-link to="/news">查看更多 →</router-link>
      </div>
      <el-row :gutter="20">
        <el-col :span="8" v-for="news in newsList" :key="news.id">
          <div class="news-card" @click="router.push(`/news/${news.id}`)">
            <img :src="news.coverImage || '/default-news.jpg'" class="cover" alt="">
            <div class="info">
              <div class="title">{{ news.title }}</div>
              <div class="summary">{{ news.summary }}</div>
              <div class="meta">
                <span>{{ news.viewCount }} 阅读</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>
    
    <section class="container section" v-if="noticeList.length">
      <div class="section-header">
        <h2>系统公告</h2>
        <router-link to="/notice">查看更多 →</router-link>
      </div>
      <el-carousel height="60px" direction="vertical" :autoplay="true">
        <el-carousel-item v-for="notice in noticeList" :key="notice.id">
          <div class="notice-item" @click="router.push(`/notice?id=${notice.id}`)">
            <el-tag type="danger" size="small">公告</el-tag>
            <span>{{ notice.title }}</span>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { houseApi } from '@/api/house'
import { newsApi, commonApi } from '@/api/common'

const router = useRouter()
const keyword = ref('')
const recommendList = ref([])
const hotList = ref([])
const newsList = ref([])
const noticeList = ref([])

const loadData = async () => {
  const [recommend, hot, news, notice] = await Promise.all([
    houseApi.recommend(6),
    houseApi.hot(6),
    newsApi.hot(3),
    commonApi.topNotice()
  ])
  recommendList.value = recommend.data || []
  hotList.value = hot.data || []
  newsList.value = news.data || []
  noticeList.value = notice.data || []
}

const handleSearch = () => {
  router.push({ path: '/house', query: { keyword: keyword.value } })
}

const goDetail = (id) => {
  router.push(`/house/${id}`)
}

onMounted(loadData)
</script>

<style scoped lang="scss">
.hero {
  background: #2c3e50;
  color: #fff;
  padding: 60px 0 80px;
  text-align: center;
  
  h1 {
    font-size: 42px;
    font-weight: 700;
    margin-bottom: 16px;
  }
  
  p {
    font-size: 18px;
    opacity: 0.9;
    margin-bottom: 32px;
  }
  
  .search-box {
    display: flex;
    justify-content: center;
    gap: 12px;
    max-width: 600px;
    margin: 0 auto;
    
    .el-input {
      flex: 1;
    }
  }
}

.section {
  padding: 48px 0;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    h2 {
      font-size: 22px;
      font-weight: 600;
    }
    
    a {
      color: var(--primary-color);
    }
  }
}

.news-card {
  background: #fff;
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
  cursor: pointer;
  transition: transform 0.3s;
  
  &:hover {
    transform: translateY(-4px);
  }
  
  .cover {
    width: 100%;
    height: 150px;
    object-fit: cover;
  }
  
  .info {
    padding: 16px;
    
    .title {
      font-size: 15px;
      font-weight: 600;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .summary {
      font-size: 13px;
      color: var(--text-secondary);
      height: 38px;
      overflow: hidden;
    }
    
    .meta {
      margin-top: 8px;
      font-size: 12px;
      color: var(--info-color);
    }
  }
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: var(--radius);
  cursor: pointer;
}
</style>
