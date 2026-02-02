<template>
  <div class="order-detail-container" v-if="order">
    <el-card>
      <div class="order-header">
        <h2>订单详情</h2>
        <el-tag :type="getStatusType(order.status)" size="large">
          {{ getStatusText(order.status) }}
        </el-tag>
      </div>
    </el-card>

    <el-card class="order-info-card">
      <h3>订单信息</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(order.status)">
            {{ getStatusText(order.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(order.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间" v-if="order.payTime">
          {{ formatDateTime(order.payTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span class="amount">¥{{ order.totalAmount }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="movie-info-card">
      <h3>订单信息</h3>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="场次ID">{{ order.screeningId }}</el-descriptions-item>
        <el-descriptions-item label="座位数">{{ order.seatCount }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="seat-info-card" v-if="orderDetails.length > 0">
      <h3>座位信息</h3>
      <div class="seat-list">
        <div v-for="detail in orderDetails" :key="detail.id" class="seat-item">
          <span>{{ detail.seatRow }}{{ detail.seatColumn }}</span>
          <span class="seat-price">¥{{ detail.price }}</span>
        </div>
      </div>
    </el-card>

    <el-card class="action-card">
      <div class="actions">
        <el-button @click="goBack">返回</el-button>
        <el-button
          v-if="order.status === 'pending'"
          type="primary"
          @click="payOrder"
        >
          立即支付
        </el-button>
        <el-button
          v-if="order.status === 'pending'"
          @click="cancelOrder"
        >
          取消订单
        </el-button>
      </div>
    </el-card>
  </div>
  <div v-else class="loading-container">
    <el-skeleton :rows="10" animated />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/http.js'
import tools from '@/utils/tools.js'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const orderDetails = ref([])

onMounted(() => {
  if (!tools.isLogin()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  const orderNo = route.params.orderNo
  if (orderNo) {
    loadOrderDetail(orderNo)
  }
})

async function loadOrderDetail(orderNo) {
  try {
    const res = await http.get(`/user/order/${orderNo}`)
    if (res && res.data) {
      order.value = res.data.order
      orderDetails.value = res.data.details || []
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  }
}

async function payOrder() {
  try {
    await ElMessageBox.confirm('确认支付该订单？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await http.put(`/user/order/pay/${order.value.orderNo}`)
    if (res) {
      ElMessage.success('支付成功')
      loadOrderDetail(order.value.orderNo)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败')
    }
  }
}

async function cancelOrder() {
  try {
    await ElMessageBox.confirm('确认取消该订单？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await http.put(`/user/order/cancel/${order.value.orderNo}`)
    if (res) {
      ElMessage.success('订单已取消')
      loadOrderDetail(order.value.orderNo)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

function goBack() {
  router.back()
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
.order-detail-container {
  width: 100%;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info-card,
.movie-info-card,
.seat-info-card,
.action-card {
  margin-top: 20px;
}

.order-info-card h3,
.movie-info-card h3,
.seat-info-card h3 {
  margin-bottom: 20px;
}

.amount {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.movie-detail {
  display: flex;
  gap: 20px;
}

.movie-poster {
  width: 150px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

.movie-info h4 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #303133;
}

.movie-info p {
  margin-bottom: 10px;
  color: #606266;
}

.seat-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.seat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background: #f5f7fa;
  border-radius: 4px;
  min-width: 120px;
}

.seat-price {
  color: #f56c6c;
  font-weight: bold;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.loading-container {
  padding: 40px;
}
</style>