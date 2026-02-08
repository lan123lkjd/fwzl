<template>
  <div>
    <h2>数据概览</h2>
    
    <div class="stat-cards">
      <div class="stat-card">
        <div class="label">今日PV</div>
        <div class="value">{{ todayStats.pv || 0 }}</div>
      </div>
      <div class="stat-card success">
        <div class="label">今日UV</div>
        <div class="value">{{ todayStats.uv || 0 }}</div>
      </div>
      <div class="stat-card warning">
        <div class="label">今日预约</div>
        <div class="value">{{ todayStats.orderCount || 0 }}</div>
      </div>
      <div class="stat-card danger">
        <div class="label">新增用户</div>
        <div class="value">{{ todayStats.newUsers || 0 }}</div>
      </div>
    </div>
    
    <div class="stat-cards">
      <div class="stat-card">
        <div class="label">总用户数</div>
        <div class="value">{{ totalStats.totalUsers || 0 }}</div>
      </div>
      <div class="stat-card success">
        <div class="label">上架房源</div>
        <div class="value">{{ totalStats.totalHouses || 0 }}</div>
      </div>
      <div class="stat-card warning">
        <div class="label">总预约数</div>
        <div class="value">{{ totalStats.totalOrders || 0 }}</div>
      </div>
    </div>
    
    <el-row :gutter="24">
      <el-col :span="12">
        <div class="chart-container">
          <div class="chart-title">近7日访问趋势</div>
          <div ref="pvChartRef" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-container">
          <div class="chart-title">近7日预约趋势</div>
          <div ref="orderChartRef" class="chart"></div>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="24">
      <el-col :span="12">
        <div class="chart-container">
          <div class="chart-title">房源浏览排行</div>
          <div ref="houseChartRef" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-container">
          <div class="chart-title">用户增长趋势</div>
          <div ref="userChartRef" class="chart"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { adminApi } from '@/api/admin'

const todayStats = ref({})
const totalStats = ref({})
const trendData = ref([])

const pvChartRef = ref()
const orderChartRef = ref()
const houseChartRef = ref()
const userChartRef = ref()

let pvChart, orderChart, houseChart, userChart

const loadData = async () => {
  const [today, total, trend] = await Promise.all([
    adminApi.todayStats(),
    adminApi.totalStats(),
    adminApi.trendStats(7)
  ])
  todayStats.value = today.data || {}
  totalStats.value = total.data || {}
  trendData.value = trend.data || []
  
  initCharts()
}

const initCharts = () => {
  const dates = trendData.value.map(item => item.date)
  const pvData = trendData.value.map(item => item.pv)
  const uvData = trendData.value.map(item => item.uv)
  const orderData = trendData.value.map(item => item.orderCount)
  const userGrowth = trendData.value.map(item => item.newUsers)
  const houseViews = trendData.value.map(item => item.houseViews)
  
  // PV/UV趋势图
  pvChart = echarts.init(pvChartRef.value)
  pvChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['PV', 'UV'] },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value' },
    series: [
      { name: 'PV', type: 'line', data: pvData, smooth: true, itemStyle: { color: '#409eff' } },
      { name: 'UV', type: 'line', data: uvData, smooth: true, itemStyle: { color: '#67c23a' } }
    ]
  })
  
  // 预约趋势图
  orderChart = echarts.init(orderChartRef.value)
  orderChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value' },
    series: [
      { name: '预约数', type: 'bar', data: orderData, itemStyle: { color: '#e6a23c' } }
    ]
  })
  
  // 房源浏览饼图
  houseChart = echarts.init(houseChartRef.value)
  houseChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: dates.map((d, i) => ({ name: d, value: houseViews[i] })),
      emphasis: { itemStyle: { shadowBlur: 10 } }
    }]
  })
  
  // 用户增长图
  userChart = echarts.init(userChartRef.value)
  userChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value' },
    series: [
      { name: '新增用户', type: 'line', data: userGrowth, smooth: true, areaStyle: {}, itemStyle: { color: '#f56c6c' } }
    ]
  })
}

const handleResize = () => {
  pvChart?.resize()
  orderChart?.resize()
  houseChart?.resize()
  userChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pvChart?.dispose()
  orderChart?.dispose()
  houseChart?.dispose()
  userChart?.dispose()
})
</script>
