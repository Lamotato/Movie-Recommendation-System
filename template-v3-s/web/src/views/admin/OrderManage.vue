<template>
  <div class="order-manage-container">
    <el-card>
      <template #header>
        <span>订单管理</span>
      </template>

      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderNo" placeholder="请输入订单号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="待支付" value="pending" />
            <el-option label="已支付" value="paid" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadOrders">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="orders" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="screeningId" label="场次ID" width="100" />
        <el-table-column prop="seatCount" label="座位数" width="100" />
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetail(row.orderNo)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadOrders"
        @current-change="loadOrders"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="800px">
      <el-descriptions :column="2" border v-if="orderDetail">
        <el-descriptions-item label="订单号">{{ orderDetail.order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(orderDetail.order.status)">
            {{ getStatusText(orderDetail.order.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="场次ID">{{ orderDetail.order.screeningId }}</el-descriptions-item>
        <el-descriptions-item label="座位数">{{ orderDetail.order.seatCount }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ orderDetail.order.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(orderDetail.order.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间" v-if="orderDetail.order.payTime">
          {{ formatDateTime(orderDetail.order.payTime) }}
        </el-descriptions-item>
      </el-descriptions>
      <div v-if="orderDetail && orderDetail.details && orderDetail.details.length > 0" style="margin-top: 20px">
        <h4>座位信息</h4>
        <el-table :data="orderDetail.details" border>
          <el-table-column prop="seatId" label="座位ID" width="100" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import http from '@/utils/http.js'

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const queryForm = ref({
  orderNo: '',
  status: ''
})

const detailVisible = ref(false)
const orderDetail = ref(null)

onMounted(() => {
  loadOrders()
})

async function loadOrders() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (queryForm.value.orderNo) {
      params.orderNo = queryForm.value.orderNo
    }
    if (queryForm.value.status) {
      params.status = queryForm.value.status
    }
    const res = await http.get('/admin/order/page', { params })
    if (res && res.data) {
      orders.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  queryForm.value = {
    orderNo: '',
    status: ''
  }
  pageNum.value = 1
  loadOrders()
}

async function viewDetail(orderNo) {
  try {
    const res = await http.get(`/admin/order/${orderNo}`)
    if (res && res.data) {
      orderDetail.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
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
.order-manage-container {
  width: 100%;
}

.filter-form {
  margin-bottom: 20px;
}
</style>