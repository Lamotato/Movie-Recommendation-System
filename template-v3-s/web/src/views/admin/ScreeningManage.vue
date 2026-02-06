<template>
  <div class="screening-manage-container">
    <el-card>
      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="电影名称">
          <el-input
              v-model="queryForm.movieName"
              placeholder="请输入电影名称"
              clearable
              style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="影院名称">
          <el-input
              v-model="queryForm.cinemaName"
              placeholder="请输入影院名称"
              clearable
              style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="待审批" value="pending"/>
            <el-option label="已通过" value="active"/>
            <el-option label="已取消" value="cancelled"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadScreenings">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="handleAdd">新增场次</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="screenings" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="movieName" label="电影名称" min-width="150"/>
        <el-table-column prop="cinemaName" label="影院名称" min-width="120"/>
        <el-table-column prop="roomName" label="放映厅" width="100"/>
        <el-table-column label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="票价" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
                v-if="row.status === 'pending'"
                type="success"
                size="small"
                @click="handleApprove(row)"
            >
              审批
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑场次' : '新增场次'"
        width="600px"
        @close="resetForm"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="电影" prop="movieId">
          <el-select
              v-model="formData.movieId"
              placeholder="请选择电影"
              filterable
              style="width: 100%"
          >
            <el-option
                v-for="movie in movies"
                :key="movie.id"
                :label="movie.name"
                :value="movie.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="影院" prop="cinemaId">
          <el-select
              v-model="formData.cinemaId"
              placeholder="请选择影院"
              filterable
              style="width: 100%"
              @change="loadRooms"
          >
            <el-option
                v-for="cinema in cinemas"
                :key="cinema.id"
                :label="cinema.name"
                :value="cinema.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="放映厅" prop="roomId">
          <el-select
              v-model="formData.roomId"
              placeholder="请选择放映厅"
              filterable
              style="width: 100%"
          >
            <el-option
                v-for="room in rooms"
                :key="room.id"
                :label="room.name"
                :value="room.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
              v-model="formData.startTime"
              type="datetime"
              placeholder="选择开始时间"
              style="width: 100%"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
              v-model="formData.endTime"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="票价" prop="price">
          <el-input-number
              v-model="formData.price"
              :min="0"
              :precision="2"
              :step="1"
              style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog
        v-model="approveDialogVisible"
        title="场次审批"
        width="400px"
    >
      <div class="approve-options">
        <el-button type="success" @click="handleApproveAction('active')">通过</el-button>
        <el-button type="danger" @click="handleApproveAction('cancelled')">拒绝</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import http from '@/utils/http.js'

const queryForm = ref({
  movieName: '',
  cinemaName: '',
  status: ''
})

const screenings = ref([])
const movies = ref([])
const cinemas = ref([])
const rooms = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const approveDialogVisible = ref(false)
const currentApproveRow = ref(null)

const formData = ref({
  id: null,
  movieId: null,
  cinemaId: null,
  roomId: null,
  startTime: '',
  endTime: '',
  price: 0
})

const rules = {
  movieId: [{required: true, message: '请选择电影', trigger: 'change'}],
  cinemaId: [{required: true, message: '请选择影院', trigger: 'change'}],
  roomId: [{required: true, message: '请选择放映厅', trigger: 'change'}],
  startTime: [{required: true, message: '请选择开始时间', trigger: 'change'}],
  endTime: [{required: true, message: '请选择结束时间', trigger: 'change'}],
  price: [{required: true, message: '请输入票价', trigger: 'blur'}]
}

onMounted(() => {
  loadScreenings()
  loadMovies()
  loadCinemas()
})

async function loadScreenings() {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (queryForm.value.movieName) {
      params.movieName = queryForm.value.movieName
    }
    if (queryForm.value.cinemaName) {
      params.cinemaName = queryForm.value.cinemaName
    }
    if (queryForm.value.status) {
      params.status = queryForm.value.status
    }
    const res = await http.get('/admin/screening/page', {params})
    if (res && res.data) {
      screenings.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载场次列表失败:', error)
    ElMessage.error('加载场次列表失败')
  }
}

async function loadMovies() {
  try {
    const res = await http.get('/admin/movie/list')
    if (res && res.data) {
      movies.value = res.data || []
    }
  } catch (error) {
    console.error('加载电影列表失败:', error)
    ElMessage.error('加载电影列表失败')
  }
}

async function loadCinemas() {
  try {
    const res = await http.get('/admin/cinema/page', {
      params: {
        pageNum: 1,
        pageSize: 1000
      }
    })
    if (res && res.data) {
      cinemas.value = res.data.list || []
    }
  } catch (error) {
    console.error('加载影院列表失败:', error)
    ElMessage.error('加载影院列表失败')
  }
}

async function loadRooms() {
  if (!formData.value.cinemaId) {
    rooms.value = []
    return
  }
  try {
    const res = await http.get(`/admin/cinema/room/list/${formData.value.cinemaId}`)
    if (res && res.data) {
      rooms.value = res.data || []
    }
  } catch (error) {
    console.error('加载放映厅列表失败:', error)
    ElMessage.error('加载放映厅列表失败')
  }
}

function handleAdd() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  formData.value = {
    id: row.id,
    movieId: row.movieId,
    cinemaId: row.cinemaId,
    roomId: row.roomId,
    startTime: row.startTime,
    endTime: row.endTime,
    price: row.price
  }
  loadRooms()
  dialogVisible.value = true
}

async function handleApprove(row) {
  currentApproveRow.value = row
  approveDialogVisible.value = true
}

async function handleApproveAction(status) {
  try {
    await http.put(`/admin/screening/approve/${currentApproveRow.value.id}`, {status})
    ElMessage.success(status === 'active' ? '已通过' : '已拒绝')
    approveDialogVisible.value = false
    loadScreenings()
  } catch (error) {
    console.error('审批失败:', error)
    ElMessage.error('审批失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除该场次吗？', '提示', {
      type: 'warning'
    })
    await http.delete(`/admin/screening/delete/${row.id}`)
    ElMessage.success('删除成功')
    loadScreenings()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

async function submitForm() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      await http.put('/admin/screening/update', formData.value)
    } else {
      await http.post('/admin/screening/add', formData.value)
    }

    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false
    loadScreenings()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
      ElMessage.error('提交失败')
    }
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  formData.value = {
    id: null,
    movieId: null,
    cinemaId: null,
    roomId: null,
    startTime: '',
    endTime: '',
    price: 0
  }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

function resetSearch() {
  queryForm.value = {
    movieName: '',
    cinemaName: '',
    status: ''
  }
  pageNum.value = 1
  loadScreenings()
}

function handleSizeChange(val) {
  pageSize.value = val
  pageNum.value = 1
  loadScreenings()
}

function handleCurrentChange(val) {
  pageNum.value = val
  loadScreenings()
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

function getStatusType(status) {
  const statusMap = {
    pending: 'warning',
    active: 'success',
    cancelled: 'danger'
  }
  return statusMap[status] || 'info'
}

function getStatusText(status) {
  const statusMap = {
    pending: '待审批',
    active: '已通过',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}
</script>

<style scoped>
.screening-manage-container {
  width: 100%;
}

.filter-form {
  margin: 0;
}

.table-card {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.approve-options {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px 0;
}
</style>
