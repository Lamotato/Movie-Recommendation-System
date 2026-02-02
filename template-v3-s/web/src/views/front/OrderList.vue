<template>
  <div class="order-list-container">
    <el-card>
      <h2>我的订单</h2>
      <el-tabs v-model="activeStatus" @tab-change="loadOrders">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="待支付" name="pending"></el-tab-pane>
        <el-tab-pane label="已支付" name="paid"></el-tab-pane>
        <el-tab-pane label="已取消" name="cancelled"></el-tab-pane>
      </el-tabs>
    </el-card>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>
    <div v-else-if="orders.length === 0" class="empty">
      <el-empty description="暂无订单" />
    </div>
    <div v-else class="order-list">
      <el-card
        v-for="order in orders"
        :key="order.id"
        class="order-item"
        shadow="hover"
      >
        <div class="order-header">
          <div class="order-info">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ formatDateTime(order.createTime) }}</span>
          </div>
          <div class="order-status">
            <el-tag :type="getStatusType(order.status)">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
        </div>
        <div class="order-content">
          <div class="movie-info">
            <img
              :src="'/default_avatar.png'"
              alt="电影海报"
              class="movie-poster-small"
            />
            <div class="movie-details">
              <h3>订单号：{{ order.orderNo }}</h3>
              <p>场次ID：{{ order.screeningId }}</p>
              <p>座位数：{{ order.seatCount }}</p>
            </div>
          </div>
          <div class="order-summary">
            <p class="total-amount">总计：<strong>¥{{ order.totalAmount }}</strong></p>
          </div>
        </div>
        <div class="order-actions">
          <el-button @click="viewOrderDetail(order.orderNo)">查看详情</el-button>
          <el-button
            v-if="order.status === 'pending'"
            type="primary"
            @click="payOrder(order.orderNo)"
          >
            立即支付
          </el-button>
          <el-button
            v-if="order.status === 'pending'"
            @click="cancelOrder(order.orderNo)"
          >
            取消订单
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/http.js'
import tools from '@/utils/tools.js'

const router = useRouter()

const loading = ref(false)
const orders = ref([])
const activeStatus = ref('all')

onMounted(() => {
  if (!tools.isLogin()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadOrders()
})

async function loadOrders() {
  loading.value = true
  try {
    const res = await http.get('/user/order/list')
    if (res && res.data) {
      let allOrders = res.data
      if (activeStatus.value !== 'all') {
        allOrders = allOrders.filter(order => order.status === activeStatus.value)
      }
      orders.value = allOrders
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

function viewOrderDetail(orderNo) {
  router.push(`/orders/${orderNo}`)
}

async function payOrder(orderNo) {
  try {
    await ElMessageBox.confirm('确认支付该订单？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await http.put(`/user/order/pay/${orderNo}`)
    if (res) {
      ElMessage.success('支付成功')
      loadOrders()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败')
    }
  }
}

async function cancelOrder(orderNo) {
  try {
    await ElMessageBox.confirm('确认取消该订单？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await http.put(`/user/order/cancel/${orderNo}`)
    if (res) {
      ElMessage.success('订单已取消')
      loadOrders()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

function getStatusType(status) {
  const statusMap = {
    pending: 'warning',
    paid: 'success',
    cancelled: 'info'
  }
  return statusMap[status] || 'info'
}

function getStatusText(status) {
  const statusMap = {
    pending: '待支付',
    paid: '已支付',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

function formatDateTime(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}
</script>

<style scoped>
.order-list-container {
  width: 100%;
}

.loading {
  padding: 20px;
}

.empty {
  padding: 60px 0;
}

.order-list {
  margin-top: 20px;
}

.order-item {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.order-no {
  font-weight: bold;
  color: #303133;
}

.order-time {
  font-size: 12px;
  color: #909399;
}

.order-content {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.movie-info {
  display: flex;
  gap: 15px;
  flex: 1;
}

.movie-poster-small {
  width: 80px;
  height: 110px;
  object-fit: cover;
  border-radius: 4px;
}

.movie-details h3 {
  font-size: 18px;
  margin-bottom: 8px;
  color: #303133;
}

.movie-details p {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.order-summary {
  text-align: right;
  min-width: 150px;
}

.order-summary p {
  margin-bottom: 8px;
  color: #606266;
}

.total-amount {
  font-size: 16px;
  color: #f56c6c;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}
</style>