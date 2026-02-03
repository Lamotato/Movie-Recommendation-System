<template>
  <div class="movie-list-container">
    <div class="filter-section">
      <el-card>
        <el-form :inline="true" :model="queryForm" class="filter-form">
          <el-form-item label="电影名称">
            <el-input
              v-model="queryForm.name"
              placeholder="请输入电影名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="导演">
            <el-input
              v-model="queryForm.director"
              placeholder="请输入导演"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchMovies">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <div class="movie-list-section">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="5" animated />
      </div>
      <div v-else-if="movies.length === 0" class="empty">
        <el-empty description="暂无电影数据" />
      </div>
      <div v-else class="movie-grid">
        <div
          v-for="movie in movies"
          :key="movie.id"
          class="movie-card"
          @click="goToMovieDetail(movie.id)"
        >
          <div class="movie-poster">
            <img :src="movie.posterUrl || '/default_avatar.png'" :alt="movie.name" />
            <div class="movie-overlay">
              <el-button type="primary" circle>
                <el-icon><VideoPlay /></el-icon>
              </el-button>
            </div>
          </div>
          <div class="movie-info">
            <h3 class="movie-title">{{ movie.name }}</h3>
            <p class="movie-meta">
              <span>导演：{{ movie.director || '未知' }}</span>
            </p>
            <p class="movie-meta" v-if="movie.releaseDate">
              <span>上映：{{ formatDate(movie.releaseDate) }}</span>
            </p>
            <div class="movie-rating" v-if="movie.rating">
              <div class="rating-stars">
                <span v-for="i in 10" :key="i"
                      :class="['star', i <= movie.rating ? 'active' : '']">
                  ★
                </span>
                <span class="rating-score">{{ movie.rating }}分</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination-section" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[12, 24, 36, 48]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {VideoPlay} from '@element-plus/icons-vue'
import http from '@/utils/http.js'

const router = useRouter()

const loading = ref(false)
const movies = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)

const queryForm = ref({
  name: '',
  director: ''
})

onMounted(() => {
  loadMovies()
})

async function loadMovies() {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: 'active'
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
  } finally {
    loading.value = false
  }
}

function searchMovies() {
  pageNum.value = 1
  loadMovies()
}

function resetSearch() {
  queryForm.value = {
    name: '',
    director: ''
  }
  searchMovies()
}

function handleSizeChange(val) {
  pageSize.value = val
  pageNum.value = 1
  loadMovies()
}

function handleCurrentChange(val) {
  pageNum.value = val
  loadMovies()
}

function goToMovieDetail(movieId) {
  router.push(`/movies/${movieId}`)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.movie-list-container {
  width: 100%;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-form {
  margin: 0;
}

.movie-list-section {
  margin-bottom: 30px;
}

.loading {
  padding: 20px;
}

.empty {
  padding: 60px 0;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.movie-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.movie-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.movie-poster {
  position: relative;
  width: 100%;
  padding-top: 140%;
  overflow: hidden;
}

.movie-poster img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.movie-card:hover .movie-overlay {
  opacity: 1;
}

.movie-info {
  padding: 15px;
}

.movie-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.movie-meta {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.movie-rating {
  display: flex;
  align-items: center;
  margin-top: 8px;
}

.rating-stars {
  display: inline-flex;
  align-items: center;
  gap: 1px;
}

.rating-stars .star {
  font-size: 14px;
  color: #dcdfe6;
  cursor: default;
}

.rating-stars .star.active {
  color: #ff9900;
}

.rating-score {
  margin-left: 5px;
  font-size: 12px;
  color: #606266;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>