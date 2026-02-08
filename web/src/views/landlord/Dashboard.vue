<template>
  <div>
    <h2>房东工作台</h2>
    <div class="stat-cards">
      <div class="stat-card"><div class="label">房源总数</div><div class="value">{{ stats.houseCount }}</div></div>
      <div class="stat-card success"><div class="label">待处理预约</div><div class="value">{{ stats.pendingOrders }}</div></div>
      <div class="stat-card warning"><div class="label">本月预约</div><div class="value">{{ stats.monthOrders }}</div></div>
    </div>
    <el-card>
      <h3>快捷操作</h3>
      <el-button type="primary" @click="$router.push('/landlord/house/add')">发布房源</el-button>
      <el-button @click="$router.push('/landlord/orders')">查看预约</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { houseApi } from '@/api/house'
import { orderApi } from '@/api/order'

const stats = reactive({ houseCount: 0, pendingOrders: 0, monthOrders: 0 })

onMounted(async () => {
  const [houseRes, orderRes] = await Promise.all([houseApi.landlordList({ page: 1, size: 1 }), orderApi.landlordList({ page: 1, size: 100 })])
  stats.houseCount = houseRes.data?.total || 0
  const orders = orderRes.data?.records || []
  stats.pendingOrders = orders.filter(o => o.status === 0).length
  stats.monthOrders = orders.length
})
</script>
