<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="logo">管理后台</div>
      <div class="menu">
        <router-link to="/admin" class="menu-item" :class="{ active: $route.path === '/admin' }">
          <el-icon><DataLine /></el-icon>
          <span>数据概览</span>
        </router-link>
        <router-link to="/admin/user" class="menu-item" :class="{ active: $route.path === '/admin/user' }">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/landlord" class="menu-item" :class="{ active: $route.path === '/admin/landlord' }">
          <el-icon><Avatar /></el-icon>
          <span>房东管理</span>
        </router-link>
        <router-link to="/admin/house" class="menu-item" :class="{ active: $route.path === '/admin/house' }">
          <el-icon><House /></el-icon>
          <span>房源管理</span>
        </router-link>
        <router-link to="/admin/news" class="menu-item" :class="{ active: $route.path === '/admin/news' }">
          <el-icon><Document /></el-icon>
          <span>资讯管理</span>
        </router-link>
        <router-link to="/admin/notice" class="menu-item" :class="{ active: $route.path === '/admin/notice' }">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </router-link>
        <router-link to="/admin/comment" class="menu-item" :class="{ active: $route.path === '/admin/comment' }">
         <el-icon><comment /></el-icon>
          <span>评论管理</span>
        </router-link>
        <div class="menu-item" @click="goHome" >
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
