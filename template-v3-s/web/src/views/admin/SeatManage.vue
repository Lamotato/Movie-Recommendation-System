<template>
  <div class="seat-manage-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ roomName ? `${roomName} - 座位管理` : '座位管理' }}</span>
          <el-button type="primary" @click="handleGenerateSeats" :disabled="!roomId">生成座位</el-button>
        </div>
      </template>

      <el-alert
          v-if="!roomId"
          title="请先选择房间"
          type="warning"
          :closable="false"
          style="margin-bottom: 20px;"
      />

      <div v-if="roomId" class="seat-container">
        <div class="screen-indicator">银幕</div>
        <div class="seat-grid" :style="{ gridTemplateColumns: `repeat(${colCount}, 40px)` }">
          <div
              v-for="seat in seats"
              :key="seat.id"
              class="seat-item"
              :class="{
                'seat-selected': selectedSeat && selectedSeat.id === seat.id,
                'seat-sold': seat.salesStatus === 'sold',
                'seat-inactive': seat.status === 'inactive',
                'seat-vip': seat.seatType === 'vip',
                'seat-love': seat.seatType === 'love_seat'
              }"
              @click="handleSeatClick(seat)"
          >
            <svg viewBox="0 0 24 24" class="seat-icon">
              <path
                  d="M4 18v3h3v-3h10v3h3v-6H4v3zm15-8h3v3h-3v-3zM2 10h3v3H2v-3zm15 3H7V5c0-1.1.9-2 2-2h6c1.1 0 2 .9 2 2v8z"/>
            </svg>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 座位详情对话框 -->
    <el-dialog
        v-model="dialogVisible"
        title="座位详情"
        width="500px"
        @close="resetForm"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="行号">
          <el-input v-model="formData.rowNum" disabled/>
        </el-form-item>
        <el-form-item label="列号">
          <el-input v-model="formData.colNum" disabled/>
        </el-form-item>
        <el-form-item label="销售状态">
          <el-tag :type="formData.salesStatus === 'sold' ? 'danger' : 'success'">
            {{ formData.salesStatus === 'sold' ? '已售出' : '未售出' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="座位类型" prop="seatType">
          <el-select v-model="formData.seatType" placeholder="请选择座位类型" style="width: 100%">
            <el-option label="普通座" value="normal"/>
            <el-option label="VIP座" value="vip"/>
            <el-option label="情侣座" value="love_seat"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="active">启用</el-radio>
            <el-radio label="inactive">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {useRoute} from 'vue-router'
import http from '@/utils/http.js'

const route = useRoute()
const loading = ref(false)
const seats = ref([])
const roomId = ref(null)
const roomName = ref('')
const rowCount = ref(0)
const colCount = ref(0)

const dialogVisible = ref(false)
const formRef = ref(null)
const selectedSeat = ref(null)

const formData = ref({
  id: null,
  roomId: null,
  rowNum: 0,
  colNum: 0,
  seatType: 'normal',
  status: 'active',
  salesStatus: 'unsold'
})

const rules = {
  seatType: [{required: true, message: '请选择座位类型', trigger: 'change'}],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
}

onMounted(() => {
  // 从路由参数获取房间ID和名称
  if (route.query.roomId) {
    roomId.value = parseInt(route.query.roomId)
    roomName.value = route.query.roomName || ''
    loadSeats()
  }
})

async function loadSeats() {
  if (!roomId.value) return

  loading.value = true
  try {
    const res = await http.get(`/admin/seat/list/${roomId.value}`)
    if (res && res.data) {
      seats.value = res.data
      // 获取房间的行列数
      const roomRes = await http.get(`/admin/cinema/room/${roomId.value}`)
      if (roomRes && roomRes.data) {
        rowCount.value = roomRes.data.rowCount
        colCount.value = roomRes.data.colCount
      }
    }
  } catch (error) {
    console.error('加载座位列表失败:', error)
    ElMessage.error('加载座位列表失败')
  } finally {
    loading.value = false
  }
}

async function handleGenerateSeats() {
  try {
    await ElMessageBox.confirm('确认生成座位？这将覆盖现有座位。', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await http.post('/admin/seat/generate', {
      roomId: roomId.value,
      rowCount: rowCount.value,
      colCount: colCount.value
    })
    if (res) {
      ElMessage.success('生成座位成功')
      loadSeats()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('生成座位失败:', error)
      ElMessage.error('生成座位失败')
    }
  }
}

function handleSeatClick(seat) {
  selectedSeat.value = seat
  formData.value = {
    ...seat,
    roomId: roomId.value
  }
  dialogVisible.value = true
}

function resetForm() {
  selectedSeat.value = null
  formData.value = {
    id: null,
    roomId: roomId.value,
    rowNum: 0,
    colNum: 0,
    seatType: 'normal',
    status: 'active',
    salesStatus: 'unsold'
  }
  formRef.value?.resetFields()
}

function submitForm() {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      await http.put('/admin/seat/update', formData.value)
      ElMessage.success('更新成功')
      dialogVisible.value = false
      loadSeats()
    } catch (error) {
      console.error('保存失败:', error)
      ElMessage.error('保存失败')
    }
  })
}
</script>

<style scoped>
.seat-manage-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.seat-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.screen-indicator {
  width: 80%;
  height: 40px;
  background: linear-gradient(to bottom, #666, #999);
  margin-bottom: 40px;
  border-radius: 10px 10px 0 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.seat-grid {
  display: grid;
  gap: 8px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.seat-item {
  width: 40px;
  height: 40px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.seat-item:hover {
  transform: scale(1.1);
}

.seat-icon {
  width: 32px;
  height: 32px;
  fill: #909399;
}

.seat-item.seat-sold .seat-icon {
  fill: #f56c6c;
}

.seat-item.seat-inactive .seat-icon {
  fill: black;
}

.seat-item.seat-vip .seat-icon {
  fill: #e6a23c;
}

.seat-item.seat-love .seat-icon {
  fill: hotpink;
}

.seat-item.seat-selected {
  transform: scale(1.2);
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.5);
}

.seat-item.seat-selected .seat-icon {
  fill: #409eff;
}
</style>
