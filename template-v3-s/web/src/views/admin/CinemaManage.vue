<template>
  <div class="cinema-manage-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>影院管理</span>
          <el-button type="primary" @click="handleAdd">新增影院</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="影院名称">
          <el-input v-model="queryForm.name" placeholder="请输入影院名称" clearable style="width: 200px"/>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="queryForm.address" placeholder="请输入地址" clearable style="width: 200px"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="待审核" value="pending"/>
            <el-option label="已通过" value="active"/>
            <el-option label="已拒绝" value="inactive"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCinemas">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="cinemas" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="name" label="影院名称" min-width="150"/>
        <el-table-column prop="tel" label="联系电话" width="130"/>
        <el-table-column prop="email" label="邮箱" width="180"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleApprove(row)" v-if="row.status === 'pending'">审批
            </el-button>
            <el-button type="info" size="small" @click="handleViewRooms(row)">房间管理</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadCinemas"
          @current-change="loadCinemas"
          style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
        @close="resetForm"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="ID">
          <el-input v-model="formData.id" disabled/>
        </el-form-item>
        <el-form-item label="影院名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入影院名称"/>
        </el-form-item>
        <el-form-item label="联系电话" prop="tel">
          <el-input v-model="formData.tel" placeholder="请输入联系电话"/>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱"/>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入地址"/>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="4"
              placeholder="请输入影院描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog
        v-model="approveDialogVisible"
        title="影院审批"
        width="400px"
    >
      <div class="approve-options">
        <el-button type="success" @click="handleApproveAction('active')">通过</el-button>
        <el-button type="danger" @click="handleApproveAction('inactive')">拒绝</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {useRouter} from 'vue-router'
import http from '@/utils/http.js'

const router = useRouter()
const loading = ref(false)
const cinemas = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const queryForm = ref({
  name: '',
  address: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.value.id ? '编辑影院' : '新增影院')
const formRef = ref(null)
const approveDialogVisible = ref(false)
const currentApproveRow = ref(null)

const formData = ref({
  id: null,
  name: '',
  address: '',
  tel: '',
  email: '',
  description: ''
})

const rules = {
  name: [{required: true, message: '请输入影院名称', trigger: 'blur'}],
  tel: [{required: true, message: '请输入联系电话', trigger: 'blur'}],
  email: [{required: true, message: '请输入邮箱', trigger: 'blur'}]
}

onMounted(() => {
  loadCinemas()
})

async function loadCinemas() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (queryForm.value.name) {
      params.name = queryForm.value.name
    }
    if (queryForm.value.address) {
      params.address = queryForm.value.address
    }
    if (queryForm.value.status) {
      params.status = queryForm.value.status
    }
    const res = await http.get('/admin/cinema/page', {params})
    if (res && res.data) {
      cinemas.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载影院列表失败:', error)
    ElMessage.error('加载影院列表失败')
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  queryForm.value = {
    name: '',
    address: '',
    status: ''
  }
  pageNum.value = 1
  loadCinemas()
}

function handleAdd() {
  resetForm()
  dialogVisible.value = true
}

async function handleEdit(row) {
  try {
    const res = await http.get(`/admin/cinema/${row.id}`)
    if (res && res.data) {
      formData.value = {
        ...res.data
      }
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('加载影院详情失败:', error)
    ElMessage.error('加载影院详情失败')
  }
}

async function handleApprove(row) {
  currentApproveRow.value = row
  approveDialogVisible.value = true
}

async function handleApproveAction(status) {
  try {
    const res = await http.put(`/admin/cinema/approve/${currentApproveRow.value.id}`, {status})
    if (res) {
      ElMessage.success(status === 'active' ? '已通过' : '已拒绝')
      approveDialogVisible.value = false
      loadCinemas()
    }
  } catch (error) {
    console.error('审批失败:', error)
    ElMessage.error('审批失败')
  }
}

function handleViewRooms(row) {
  router.push({
    path: '/admin/room',
    query: {cinemaId: row.id, cinemaName: row.name}
  })
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除该影院？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await http.delete(`/admin/cinema/delete/${row.id}`)
    if (res) {
      ElMessage.success('删除成功')
      loadCinemas()
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
    name: '',
    address: '',
    tel: '',
    email: '',
    description: ''
  }
  formRef.value?.resetFields()
}

function submitForm() {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      if (formData.value.id) {
        await http.put('/admin/cinema/update', formData.value)
        ElMessage.success('更新成功')
      } else {
        await http.post('/admin/cinema/add', formData.value)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadCinemas()
    } catch (error) {
      console.error('保存失败:', error)
      ElMessage.error('保存失败')
    }
  })
}

function getStatusType(status) {
  const statusMap = {
    'pending': 'warning',
    'active': 'success',
    'inactive': 'danger'
  }
  return statusMap[status] || 'info'
}

function getStatusText(status) {
  const statusMap = {
    'pending': '待审核',
    'active': '已通过',
    'inactive': '已拒绝'
  }
  return statusMap[status] || '未知'
}
</script>

<style scoped>
.cinema-manage-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 20px;
}

.approve-options {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px 0;
}
</style>
