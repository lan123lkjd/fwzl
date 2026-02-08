<template>
  <div class="main-layout">
    <header class="header">
      <div class="container header-content">
        <router-link to="/" class="logo">
          <el-icon><House /></el-icon>
          <span>房屋租赁系统</span>
        </router-link>
        
        <nav class="nav-menu">
          <router-link to="/user" class="nav-item">个人中心</router-link>
          <router-link to="/user/orders" class="nav-item">我的预约</router-link>
          <router-link to="/user/rentals" class="nav-item">我的租赁</router-link>
          <router-link to="/user/collect" class="nav-item">我的收藏</router-link>
        </nav>
        
        <div class="user-actions">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.user?.avatar || ''" />
              <span>{{ userStore.user?.nickname || userStore.user?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/')">返回首页</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>
    
    <main class="main-content">
      <div class="container">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style scoped lang="scss">
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #2c3e50;
  color: #fff;
  padding: 0 20px;
  
  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 64px;
  }
  
  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: 700;
    color: #fff;
  }
  
  .nav-menu {
    display: flex;
    gap: 32px;
    
    .nav-item {
      color: rgba(255, 255, 255, 0.85);
      font-size: 15px;
      
      &:hover, &.router-link-active {
        color: #fff;
      }
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #fff;
    cursor: pointer;
  }
}

.main-content {
  flex: 1;
  padding: 24px 0;
  background: var(--bg-color);
}
</style>
