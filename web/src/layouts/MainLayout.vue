<template>
  <div class="main-layout">
    <header class="header">
      <div class="container header-content">
        <router-link to="/" class="logo">
          <el-icon><House /></el-icon>
          <span>房屋租赁系统</span>
        </router-link>
        
        <nav class="nav-menu">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/house" class="nav-item">找房源</router-link>
          <router-link to="/news" class="nav-item">房屋资讯</router-link>
          <router-link to="/notice" class="nav-item">公告</router-link>
        </nav>
        
        <div class="user-actions">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown>
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.user?.avatar || ''" />
                <span>{{ userStore.user?.nickname || userStore.user?.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goToCenter">个人中心</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isLandlord" @click="router.push('/landlord')">房东后台</el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" @click="router.push('/admin')">管理后台</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="router.push('/login')">登录</el-button>
            <el-button @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    
    <main class="main-content">
      <router-view />
    </main>
    
    <footer class="footer">
      <div class="container">
        <p>© 2024 房屋租赁系统 - 找房租房一站式服务平台</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const goToCenter = () => {
  router.push('/user')
}

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
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  
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
    
    .el-icon { font-size: 24px; }
  }
  
  .nav-menu {
    display: flex;
    gap: 32px;
    
    .nav-item {
      color: rgba(255, 255, 255, 0.85);
      font-size: 15px;
      padding: 8px 0;
      border-bottom: 2px solid transparent;
      transition: all 0.3s;
      
      &:hover, &.router-link-active {
        color: #fff;
        border-bottom-color: #fff;
      }
    }
  }
  
  .user-actions {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #fff;
      cursor: pointer;
    }
  }
}

.main-content {
  flex: 1;
  padding: 0;
}

.footer {
  background: #1f2937;
  color: rgba(255, 255, 255, 0.7);
  padding: 24px 0;
  text-align: center;
  font-size: 14px;
}
</style>
