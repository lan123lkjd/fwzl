<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="logo">房东后台</div>
      <div class="menu">
        <router-link to="/landlord" class="menu-item" :class="{ active: $route.path === '/landlord' }">
          <el-icon><DataLine /></el-icon>
          <span>数据概览</span>
        </router-link>
        <router-link to="/landlord/house" class="menu-item">
          <el-icon><House /></el-icon>
          <span>房源管理</span>
        </router-link>
        <router-link to="/landlord/orders" class="menu-item">
          <el-icon><List /></el-icon>
          <span>预约管理</span>
        </router-link>
        <router-link to="/landlord/rentals" class="menu-item">
          <el-icon><Tickets /></el-icon>
          <span>租赁管理</span>
        </router-link>
        <div class="menu-item" @click="goHome">
          <el-icon><Back /></el-icon>
          <span>返回首页</span>
        </div>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const goHome = () => router.push('/')
</script>
