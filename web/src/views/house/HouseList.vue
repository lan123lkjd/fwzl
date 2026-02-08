<template>
  <div class="container">
    <div class="search-bar">
      <el-input v-model="filters.keyword" placeholder="搜索关键词" clearable style="width: 200px" />
      <el-select v-model="filters.areaId" placeholder="选择区域" clearable style="width: 150px">
        <el-option v-for="area in areaList" :key="area.id" :label="area.name" :value="area.id" />
      </el-select>
      <el-select v-model="filters.houseType" placeholder="租赁方式" clearable style="width: 120px">
        <el-option label="整租" :value="1" />
        <el-option label="合租" :value="2" />
      </el-select>
      <el-input v-model="filters.minPrice" placeholder="最低价" type="number" style="width: 100px" />
      <span>-</span>
      <el-input v-model="filters.maxPrice" placeholder="最高价" type="number" style="width: 100px" />
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetFilters">重置</el-button>
    </div>
    
    <div class="house-grid">
      <div v-for="house in list" :key="house.id" class="house-card" @click="goDetail(house.id)">
        <img :src="house.coverImage || '/default-house.jpg'" class="cover" alt="">
        <div class="info">
          <div class="title">{{ house.title }}</div>
          <div class="address">{{ house.address }}</div>
          <div class="meta">
            <div class="price">¥{{ house.price }}<span>/月</span></div>
            <div class="tags">
              <el-tag size="small">{{ house.rooms }}室{{ house.halls }}厅</el-tag>
              <el-tag size="small" type="info">{{ house.area }}㎡</el-tag>
              <el-tag size="small" :type="house.houseType === 1 ? 'success' : 'warning'">{{ house.houseType === 1 ? '整租' : '合租' }}</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <el-empty v-if="!list.length" description="暂无房源" />
    
    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" 
        layout="prev, pager, next" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { houseApi } from '@/api/house'
import { commonApi } from '@/api/common'

const router = useRouter()
const route = useRoute()

const list = ref([])
const areaList = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const filters = reactive({ keyword: '', areaId: null, houseType: null, minPrice: '', maxPrice: '' })

const loadData = async () => {
  const res = await houseApi.list({ page: page.value, size: size.value, ...filters })
  if (res.code === 200) {
    list.value = res.data.records || []
    total.value = res.data.total || 0
  }
}

const loadAreas = async () => {
  const res = await commonApi.areaList(0)
  areaList.value = res.data || []
}

const resetFilters = () => {
  Object.assign(filters, { keyword: '', areaId: null, houseType: null, minPrice: '', maxPrice: '' })
  loadData()
}

const goDetail = (id) => router.push(`/house/${id}`)

onMounted(() => {
  if (route.query.keyword) filters.keyword = route.query.keyword
  loadAreas()
  loadData()
})
</script>
