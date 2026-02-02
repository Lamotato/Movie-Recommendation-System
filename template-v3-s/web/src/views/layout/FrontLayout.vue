<template>
  <el-container class="front-wrapper" style="min-height: 100vh;">
    <el-header height="80px" class="front-header">
      <div class="header-content">
        <div class="logo-section">
          <img src="@/assets/logo.png" width="50" height="50" style="margin-right: 10px;" />
          <span class="logo-text">电影推荐系统</span>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="nav-menu"
          @select="handleMenuSelect"
          router
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/movies">电影列表</el-menu-item>
          <el-menu-item index="/orders">我的订单</el-menu-item>
        </el-menu>
        <div class="user-section">
          <el-dropdown v-if="isUserLogin" trigger="click">
            <div class="user-info">
              <el-avatar
                :size="35"
                :src="currentUser?.avatarUrl || '/default_avatar.png'"
                style="margin-right: 8px;"
              />
              <span>{{ currentUser?.username }}</span>
              <el-icon style="margin-left: 5px;"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToOrders">我的订单</el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button v-else type="primary" @click="goToLogin">登录</el-button>
        </div>
      </div>
    </el-header>
    <el-main class="front-main">
      <router-view />
    </el-main>
    <el-footer height="60px" class="front-footer">
      <div class="footer-content">
        <p>&copy; 2024 电影推荐系统. All rights reserved.</p>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import tools from '@/utils/tools.js'

const route = useRoute()
const router = useRouter()

const isUserLogin = ref(false)
const currentUser = ref(null)

const activeMenu = computed(() => {
  return route.path
})

onMounted(() => {
  checkLogin()
})

function checkLogin() {
  isUserLogin.value = tools.isLogin()
  if (isUserLogin.value) {
    currentUser.value = tools.getCurrentUser()
  }
}

function handleMenuSelect(key) {
  // 菜单选择处理
}

function goToLogin() {
  router.push('/login')
}

function goToOrders() {
  router.push('/orders')
}

function logout() {
  ElMessage({
    message: '退出登录成功',
    type: 'success'
  })
  localStorage.clear()
  isUserLogin.value = false
  currentUser.value = null
  router.push('/')
}
</script>

<style scoped>
.front-wrapper {
  background-color: #f5f5f5;
}

.front-header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo-section {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.logo-text {
  font-size: 22px;
}

.nav-menu {
  flex: 1;
  justify-content: center;
  border-bottom: none;
}

.user-section {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.front-main {
  min-height: calc(100vh - 140px);
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.front-footer {
  background-color: #303133;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-content {
  text-align: center;
}
</style>