<template>
  <div class="movie-detail-container" v-if="movie">
    <el-card>
      <div class="detail-header">
        <div class="poster-section">
          <img :src="movie.posterUrl || '/default_avatar.png'" :alt="movie.name" />
        </div>
        <div class="info-section">
          <h1 class="movie-name">{{ movie.name }}</h1>
          <div class="movie-meta-info">
            <p><span class="label">导演：</span>{{ movie.director || '未知' }}</p>
            <p><span class="label">主演：</span>{{ movie.cast || '未知' }}</p>
            <p v-if="movie.releaseDate">
              <span class="label">上映日期：</span>{{ formatDate(movie.releaseDate) }}
            </p>
            <p v-if="movie.duration">
              <span class="label">时长：</span>{{ movie.duration }}分钟
            </p>
            <p v-if="movie.rating">
              <span class="label">评分：</span>
              <el-rate v-model="movie.rating" disabled show-score text-color="#ff9900" />
            </p>
          </div>
          <div class="action-buttons">
            <el-button type="primary" size="large" @click="goToBooking">
              立即购票
            </el-button>
            <el-button size="large" @click="toggleFavorite">
              <el-icon><Star /></el-icon>
              {{ isFavorite ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="detail-content">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="剧情简介" name="intro">
          <div class="intro-content">
            <p>{{ movie.description || '暂无简介' }}</p>
          </div>
        </el-tab-pane>
        <el-tab-pane label="演职人员" name="cast">
          <div class="cast-list" v-if="castList.length > 0">
            <div v-for="cast in castList" :key="cast.id" class="cast-item">
              <p><strong>{{ cast.name }}</strong> - {{ cast.role }}</p>
            </div>
          </div>
          <el-empty v-else description="暂无演职人员信息" />
        </el-tab-pane>
        <el-tab-pane label="放映场次" name="screening">
          <div class="screening-list" v-if="screenings.length > 0">
            <div v-for="screening in screenings" :key="screening.id" class="screening-item">
              <el-card shadow="hover">
                <div class="screening-info">
                  <div class="screening-time">
                    <p class="time">{{ formatTime(screening.startTime) }}</p>
                    <p class="end-time">{{ formatTime(screening.endTime) }} 散场</p>
                  </div>
                  <div class="screening-details">
                    <p><strong>{{ screening.cinemaName }}</strong></p>
                    <p>{{ screening.roomName }} - ¥{{ screening.price }}</p>
                  </div>
                  <div class="screening-action">
                    <el-button type="primary" @click="goToSeatSelection(screening.id)">
                      选座购票
                    </el-button>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
          <el-empty v-else description="暂无放映场次" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
  <div v-else class="loading-container">
    <el-skeleton :rows="10" animated />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import http from '@/utils/http.js'
import tools from '@/utils/tools.js'

const route = useRoute()
const router = useRouter()

const movie = ref(null)
const castList = ref([])
const screenings = ref([])
const activeTab = ref('intro')
const isFavorite = ref(false)

onMounted(() => {
  const movieId = route.params.id
  if (movieId) {
    loadMovieDetail(movieId)
    loadCastList(movieId)
    loadScreenings(movieId)
    checkFavorite(movieId)
    // 记录浏览行为
    recordBehavior('browse', movieId)
  }
})

async function loadMovieDetail(movieId) {
  try {
    const res = await http.get(`/admin/movie/selectById/${movieId}`)
    if (res && res.data) {
      movie.value = res.data.movie
    }
  } catch (error) {
    console.error('加载电影详情失败:', error)
    ElMessage.error('加载电影详情失败')
  }
}

async function loadCastList(movieId) {
  try {
    const res = await http.get(`/admin/movie-cast/list/${movieId}`)
    if (res && res.data) {
      castList.value = res.data
    }
  } catch (error) {
    console.error('加载演职人员失败:', error)
  }
}

async function loadScreenings(movieId) {
  try {
    const res = await http.get('/admin/screening/page', {
      params: {
        movieId: movieId,
        status: 'approved',
        pageNum: 1,
        pageSize: 100
      }
    })
    if (res && res.data) {
      screenings.value = res.data.list || []
    }
  } catch (error) {
    console.error('加载放映场次失败:', error)
  }
}

async function checkFavorite(movieId) {
  // TODO: 实现收藏检查逻辑
  isFavorite.value = false
}

function toggleFavorite() {
  if (!tools.isLogin()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // TODO: 实现收藏/取消收藏逻辑
  isFavorite.value = !isFavorite.value
  ElMessage.success(isFavorite.value ? '收藏成功' : '取消收藏成功')
}

function goToBooking() {
  if (!tools.isLogin()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  activeTab.value = 'screening'
  if (screenings.value.length === 0) {
    ElMessage.warning('暂无放映场次')
  }
}

function goToSeatSelection(screeningId) {
  if (!tools.isLogin()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(`/booking/${screeningId}`)
}

function recordBehavior(type, movieId) {
  if (!tools.isLogin()) return
  try {
    http.post('/behavior/record', {
      behaviorType: type,
      movieId: movieId
    })
  } catch (error) {
    console.error('记录行为失败:', error)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function formatTime(timeStr) {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
.movie-detail-container {
  width: 100%;
}

.detail-header {
  display: flex;
  gap: 30px;
}

.poster-section {
  flex-shrink: 0;
}

.poster-section img {
  width: 300px;
  height: 400px;
  object-fit: cover;
  border-radius: 8px;
}

.info-section {
  flex: 1;
}

.movie-name {
  font-size: 32px;
  margin-bottom: 20px;
  color: #303133;
}

.movie-meta-info {
  margin-bottom: 30px;
}

.movie-meta-info p {
  margin-bottom: 10px;
  font-size: 16px;
  color: #606266;
}

.movie-meta-info .label {
  font-weight: bold;
  color: #303133;
  margin-right: 10px;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.detail-content {
  margin-top: 20px;
}

.intro-content {
  padding: 20px 0;
  line-height: 1.8;
  font-size: 16px;
  color: #606266;
}

.cast-list {
  padding: 20px 0;
}

.cast-item {
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.cast-item:last-child {
  border-bottom: none;
}

.screening-list {
  padding: 20px 0;
}

.screening-item {
  margin-bottom: 15px;
}

.screening-info {
  display: flex;
  align-items: center;
  gap: 30px;
}

.screening-time {
  flex-shrink: 0;
  text-align: center;
  min-width: 100px;
}

.screening-time .time {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.screening-time .end-time {
  font-size: 12px;
  color: #909399;
}

.screening-details {
  flex: 1;
}

.screening-details p {
  margin-bottom: 5px;
}

.screening-action {
  flex-shrink: 0;
}

.loading-container {
  padding: 40px;
}
</style>