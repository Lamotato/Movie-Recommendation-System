<template>
  <div class="seat-selection-container" v-if="screening">
    <el-card>
      <div class="screening-info-header">
        <h2>{{ screening.movieName }}</h2>
        <p>{{ screening.cinemaName }} - {{ screening.roomName }}</p>
        <p>{{ formatDateTime(screening.startTime) }}</p>
      </div>
    </el-card>

    <el-card class="seat-map-card">
      <div class="screen-indicator">
        <div class="screen">荧幕</div>
      </div>
      
      <div class="seat-map" v-if="seats.length > 0">
        <div
          v-for="seat in seats"
          :key="seat.id"
          class="seat"
          :class="{
            'seat-selected': selectedSeats.includes(seat.id),
            'seat-occupied': occupiedSeatIds.includes(seat.id),
            'seat-available': !occupiedSeatIds.includes(seat.id) && !selectedSeats.includes(seat.id)
          }"
          @click="toggleSeat(seat)"
        >
          <span class="seat-label">{{ seat.rowNumber }}{{ seat.columnNumber }}</span>
        </div>
      </div>
      
      <div class="seat-legend">
        <div class="legend-item">
          <div class="legend-seat seat-available"></div>
          <span>可选</span>
        </div>
        <div class="legend-item">
          <div class="legend-seat seat-selected"></div>
          <span>已选</span>
        </div>
        <div class="legend-item">
          <div class="legend-seat seat-occupied"></div>
          <span>已售</span>
        </div>
      </div>
    </el-card>

    <el-card class="order-summary-card">
      <h3>订单信息</h3>
      <div class="selected-seats-info">
        <p v-if="selectedSeats.length === 0" class="no-seat">请选择座位</p>
        <div v-else>
          <div v-for="seatId in selectedSeats" :key="seatId" class="selected-seat-item">
            <span>{{ getSeatLabel(seatId) }}</span>
            <span class="price">¥{{ screening.price }}</span>
          </div>
          <div class="total-price">
            <strong>总计：¥{{ totalPrice }}</strong>
          </div>
        </div>
      </div>
      <div class="order-actions">
        <el-button @click="goBack">返回</el-button>
        <el-button
          type="primary"
          size="large"
          :disabled="selectedSeats.length === 0"
          @click="createOrder"
          :loading="creating"
        >
          确认购票
        </el-button>
      </div>
    </el-card>
  </div>
  <div v-else class="loading-container">
    <el-skeleton :rows="10" animated />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '@/utils/http.js'
import tools from '@/utils/tools.js'

const route = useRoute()
const router = useRouter()

const screening = ref(null)
const seats = ref([])
const occupiedSeatIds = ref([])
const selectedSeats = ref([])
const creating = ref(false)

const totalPrice = computed(() => {
  if (!screening.value) return 0
  return selectedSeats.value.length * screening.value.price
})

onMounted(() => {
  const screeningId = route.params.id
  if (screeningId) {
    loadSeatInfo(screeningId)
  }
})

async function loadSeatInfo(screeningId) {
  try {
    const res = await http.get(`/user/screening/${screeningId}/seats`)
    if (res && res.data) {
      screening.value = res.data.screening
      seats.value = res.data.seats || []
      occupiedSeatIds.value = res.data.occupiedSeatIds || []
    }
  } catch (error) {
    console.error('加载座位信息失败:', error)
    ElMessage.error('加载座位信息失败')
  }
}

function toggleSeat(seat) {
  if (occupiedSeatIds.value.includes(seat.id)) {
    ElMessage.warning('该座位已被占用')
    return
  }
  
  const index = selectedSeats.value.indexOf(seat.id)
  if (index > -1) {
    selectedSeats.value.splice(index, 1)
  } else {
    selectedSeats.value.push(seat.id)
  }
}

function getSeatLabel(seatId) {
  const seat = seats.value.find(s => s.id === seatId)
  return seat ? `${seat.rowNumber}${seat.columnNumber}` : ''
}

async function createOrder() {
  if (!tools.isLogin()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (selectedSeats.value.length === 0) {
    ElMessage.warning('请选择座位')
    return
  }
  
  creating.value = true
  try {
    const orderData = {
      screeningId: screening.value.id,
      seatIds: selectedSeats.value
    }
    const res = await http.post('/user/order/create', orderData)
    if (res && res.data) {
      ElMessage.success('订单创建成功')
      router.push(`/orders/${res.data.orderNo}`)
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    ElMessage.error('创建订单失败')
  } finally {
    creating.value = false
  }
}

function goBack() {
  router.back()
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
.seat-selection-container {
  width: 100%;
}

.screening-info-header {
  text-align: center;
  padding: 20px 0;
}

.screening-info-header h2 {
  font-size: 24px;
  margin-bottom: 10px;
}

.screening-info-header p {
  color: #606266;
  margin-bottom: 5px;
}

.seat-map-card {
  margin-top: 20px;
}

.screen-indicator {
  text-align: center;
  margin-bottom: 30px;
}

.screen {
  display: inline-block;
  width: 60%;
  height: 30px;
  background: linear-gradient(to bottom, #409eff, #66b1ff);
  border-radius: 4px;
  color: white;
  line-height: 30px;
  font-weight: bold;
}

.seat-map {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(50px, 1fr));
  gap: 10px;
  padding: 20px;
  justify-items: center;
}

.seat {
  width: 45px;
  height: 45px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid #dcdfe6;
}

.seat-available {
  background-color: #f0f9ff;
  border-color: #409eff;
}

.seat-available:hover {
  background-color: #e1f3ff;
  transform: scale(1.1);
}

.seat-selected {
  background-color: #409eff;
  border-color: #409eff;
  color: white;
}

.seat-occupied {
  background-color: #f5f7fa;
  border-color: #dcdfe6;
  cursor: not-allowed;
  opacity: 0.6;
}

.seat-label {
  font-size: 12px;
  font-weight: bold;
}

.seat-legend {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-seat {
  width: 30px;
  height: 30px;
  border-radius: 4px;
  border: 2px solid;
}

.order-summary-card {
  margin-top: 20px;
}

.order-summary-card h3 {
  margin-bottom: 20px;
}

.selected-seats-info {
  min-height: 100px;
  margin-bottom: 20px;
}

.no-seat {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

.selected-seat-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.total-price {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 2px solid #409eff;
  text-align: right;
  font-size: 18px;
  color: #f56c6c;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.loading-container {
  padding: 40px;
}
</style>