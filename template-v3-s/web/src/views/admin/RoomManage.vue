<template>
  <div class="room-manage-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ cinemaName ? `${cinemaName} - 房间管理` : '房间管理' }}</span>
          <el-button type="primary" @click="handleAdd" :disabled="!cinemaId">新增房间</el-button>
        </div>
      </template>

      <el-alert
          v-if="!cinemaId"
          title="请先选择影院"
          type="warning"
          :closable="false"
          style="margin-bottom: 20px;"
      />

      <el-table :data="rooms" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="name" label="房间名称" min-width="150"/>
        <el-table-column prop="rowCount" label="行数" width="80"/>
        <el-table-column prop="colCount" label="列数" width="80"/>
        <el-table-column prop="seatCount" label="座位数" width="100"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleSeatManage(row)">座位管理</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
        @close="resetForm"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="房间名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入房间名称"/>
        </el-form-item>
        <el-form-item label="行数" prop="rowCount">
          <el-input-number v-model="formData.rowCount" :min="1" :max="20" style="width: 100%"
                           @change="calculateSeatCount"/>
        </el-form-item>
        <el-form-item label="列数" prop="colCount">
          <el-input-number v-model="formData.colCount" :min="1" :max="20" style="width: 100%"
                           @change="calculateSeatCount"/>
        </el-form-item>
        <el-form-item label="座位数" prop="seatCount">
          <el-input-number v-model="formData.seatCount" disabled style="width: 100%"/>
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
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {useRoute, useRouter} from 'vue-router'
import http from '@/utils/http.js'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const rooms = ref([])
const cinemaId = ref(null)
const cinemaName = ref('')

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.value.id ? '编辑房间' : '新增房间')
const formRef = ref(null)

const formData = ref({
  id: null,
  cinemaId: null,
  name: '',
  rowCount: 8,
  colCount: 10,
  seatCount: 80,
  status: 'active'
})

const rules = {
  name: [{required: true, message: '请输入房间名称', trigger: 'blur'}],
  rowCount: [{required: true, message: '请输入行数', trigger: 'blur'}],
  colCount: [{required: true, message: '请输入列数', trigger: 'blur'}]
}

onMounted(() => {
  // 从路由参数获取影院ID和名称
  if (route.query.cinemaId) {
    cinemaId.value = parseInt(route.query.cinemaId)
    cinemaName.value = route.query.cinemaName || ''
    loadRooms()
  }
})

async function loadRooms() {
  if (!cinemaId.value) return

  loading.value = true
  try {
    const res = await http.get(`/admin/cinema/room/list/${cinemaId.value}`)
    if (res && res.data) {
      rooms.value = res.data.map(room => ({
        ...room,
        seatCount: room.rowCount * room.colCount
      }))
    }
  } catch (error) {
    console.error('加载房间列表失败:', error)
    ElMessage.error('加载房间列表失败')
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  resetForm()
  formData.value.cinemaId = cinemaId.value
  dialogVisible.value = true
}

async function handleEdit(row) {
  formData.value = {
    ...row,
    cinemaId: cinemaId.value
  }
  dialogVisible.value = true
}

function handleSeatManage(row) {
  router.push({
    path: '/admin/seat/manage',
    query: {
      roomId: row.id,
      roomName: row.name
    }
  })
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除该房间？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await http.delete(`/admin/cinema/room/delete/${row.id}`)
    if (res) {
      ElMessage.success('删除成功')
      loadRooms()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

function resetForm() {
  formData.value = {
    id: null,
    cinemaId: cinemaId.value,
    name: '',
    rowCount: 8,
    colCount: 10,
    seatCount: 80,
    status: 'active'
  }
  formRef.value?.resetFields()
}

// 计算座位数
function calculateSeatCount() {
  if (formData.value.rowCount && formData.value.colCount) {
    formData.value.seatCount = formData.value.rowCount * formData.value.colCount
  }
}

function submitForm() {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      if (formData.value.id) {
        await http.put('/admin/cinema/room/update', formData.value)
        ElMessage.success('更新成功')
      } else {
        await http.post('/admin/cinema/room/add', formData.value)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadRooms()
    } catch (error) {
      console.error('保存失败:', error)
      ElMessage.error('保存失败')
    }
  })
}
</script>

<style scoped>
.room-manage-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
