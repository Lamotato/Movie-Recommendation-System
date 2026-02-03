<template>
  <div class="movie-manage-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>电影管理</span>
          <el-button type="primary" @click="handleAdd">新增电影</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="电影名称">
          <el-input v-model="queryForm.name" placeholder="请输入电影名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="导演">
          <el-input v-model="queryForm.director" placeholder="请输入导演" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadMovies">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="movies" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="电影名称" min-width="150" />
        <el-table-column prop="director" label="导演" width="120" />
        <el-table-column prop="releaseDate" label="上映日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.releaseDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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
        @size-change="loadMovies"
        @current-change="loadMovies"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="resetForm"
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="电影名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入电影名称" />
        </el-form-item>
        <el-form-item label="导演" prop="director">
          <el-input v-model="formData.director" placeholder="请输入导演" />
        </el-form-item>
        <el-form-item label="主演" prop="cast">
          <el-input v-model="formData.cast" placeholder="请输入主演" />
        </el-form-item>
        <el-form-item label="上映日期" prop="releaseDate">
          <el-date-picker
            v-model="formData.releaseDate"
            type="date"
            placeholder="选择上映日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="时长(分钟)" prop="duration">
          <el-input-number v-model="formData.duration" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-input-number v-model="formData.rating" :min="0" :max="10" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="海报URL" prop="posterUrl">
          <el-input v-model="formData.posterUrl" placeholder="请输入海报URL" />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入电影简介"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="active">上架</el-radio>
            <el-radio label="inactive">下架</el-radio>
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
import http from '@/utils/http.js'

const loading = ref(false)
const movies = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const queryForm = ref({
  name: '',
  director: ''
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => formData.value.id ? '编辑电影' : '新增电影')
const formRef = ref(null)

const formData = ref({
  id: null,
  name: '',
  director: '',
  cast: '',
  releaseDate: null,
  duration: null,
  rating: null,
  posterUrl: '',
  description: '',
  status: 'active',
  typeIds: [],
  tagIds: []
})

const rules = {
  name: [{ required: true, message: '请输入电影名称', trigger: 'blur' }],
  director: [{ required: true, message: '请输入导演', trigger: 'blur' }]
}

onMounted(() => {
  loadMovies()
})

async function loadMovies() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (queryForm.value.name) {
      params.name = queryForm.value.name
    }
    if (queryForm.value.director) {
      params.director = queryForm.value.director
    }
    const res = await http.get('/admin/movie/page', { params })
    if (res && res.data) {
      movies.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载电影列表失败:', error)
    ElMessage.error('加载电影列表失败')
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  queryForm.value = {
    name: '',
    director: ''
  }
  pageNum.value = 1
  loadMovies()
}

function handleAdd() {
  resetForm()
  dialogVisible.value = true
}

async function handleEdit(row) {
  try {
    const res = await http.get(`/admin/movie/selectById/${row.id}`)
    if (res && res.data) {
      // 从演职人员列表中提取主演信息
      const castList = res.data.castList || []
      const actors = castList
          .filter(cast => cast.role === 'actor')
          .map(cast => cast.name)
          .join(', ')

      formData.value = {
        ...res.data.movie,
        cast: actors,
        typeIds: res.data.typeIds || [],
        tagIds: res.data.tagIds || []
      }
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('加载电影详情失败:', error)
    ElMessage.error('加载电影详情失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除该电影？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await http.delete('/admin/movie/delete', { data: [row.id] })
    if (res) {
      ElMessage.success('删除成功')
      loadMovies()
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
    director: '',
    cast: '',
    releaseDate: null,
    duration: null,
    rating: null,
    posterUrl: '',
    description: '',
    status: 'active',
    typeIds: [],
    tagIds: []
  }
  formRef.value?.resetFields()
}

function submitForm() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      if (formData.value.id) {
        await http.put('/admin/movie/update', formData.value)
        ElMessage.success('更新成功')
      } else {
        await http.post('/admin/movie/add', formData.value)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadMovies()
    } catch (error) {
      console.error('保存失败:', error)
      ElMessage.error('保存失败')
    }
  })
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.movie-manage-container {
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
</style>