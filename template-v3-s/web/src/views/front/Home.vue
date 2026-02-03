<template>
  <div class="home-container">
    <!-- 轮播图区域 -->
    <el-carousel height="400px" class="banner-carousel" indicator-position="outside">
      <el-carousel-item v-for="(movie, index) in bannerMovies" :key="index">
        <div class="banner-item" :style="{ backgroundImage: `url(${movie.posterUrl || '/default_avatar.png'})` }">
          <div class="banner-overlay">
            <div class="banner-content">
              <h2>{{ movie.name }}</h2>
              <p class="banner-desc">{{ movie.description || '暂无简介' }}</p>
              <el-button type="primary" size="large" @click="goToMovieDetail(movie.id)">
                立即观看
              </el-button>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 猜你喜欢 -->
    <div class="section" v-if="recommendations.length > 0">
      <h2 class="section-title">
        <el-icon><Star /></el-icon>
        猜你喜欢
      </h2>
      <div class="movie-grid">
        <div
          v-for="movie in recommendations"
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
              <span>{{ movie.director }}</span>
              <span v-if="movie.releaseDate">{{ formatDate(movie.releaseDate) }}</span>
            </p>
            <div class="movie-rating" v-if="movie.rating">
              <el-rate v-model="movie.rating" disabled show-score text-color="#ff9900" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门电影 -->
    <div class="section">
      <h2 class="section-title">
        <el-icon>
          <Orange/>
        </el-icon>
        热门电影
      </h2>
      <div class="movie-grid">
        <div
          v-for="movie in popularMovies"
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
              <span>{{ movie.director }}</span>
              <span v-if="movie.releaseDate">{{ formatDate(movie.releaseDate) }}</span>
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 全部电影 -->
    <div class="section">
      <h2 class="section-title">
        <el-icon><Film /></el-icon>
        全部电影
      </h2>
      <div class="movie-grid">
        <div
          v-for="movie in allMovies"
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
              <span>{{ movie.director }}</span>
              <span v-if="movie.releaseDate">{{ formatDate(movie.releaseDate) }}</span>
            </p>
          </div>
        </div>
      </div>
      <div class="load-more" v-if="hasMore">
        <el-button @click="loadMoreMovies">加载更多</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {Film, Orange, Star, VideoPlay} from '@element-plus/icons-vue'
import http from '@/utils/http.js'
import tools from '@/utils/tools.js'

const router = useRouter()

const recommendations = ref([])
const popularMovies = ref([])
const allMovies = ref([])
const bannerMovies = ref([])
const hasMore = ref(false)
const pageNum = ref(1)
const pageSize = ref(12)

onMounted(() => {
  loadRecommendations()
  loadPopularMovies()
  loadAllMovies()
})

// 加载推荐电影
async function loadRecommendations() {
  try {
    if (tools.isLogin()) {
      const res = await http.get('/recommendation/list', { params: { limit: 8 } })
      if (res && res.data) {
        recommendations.value = res.data
        // 记录浏览行为
        res.data.forEach(movie => {
          recordBehavior('browse', movie.id)
        })
      }
    } else {
      // 未登录用户显示热门推荐
      const res = await http.get('/recommendation/popular', { params: { limit: 8 } })
      if (res && res.data) {
        recommendations.value = res.data
      }
    }
  } catch (error) {
    console.error('加载推荐电影失败:', error)
  }
}

// 加载热门电影
async function loadPopularMovies() {
  try {
    const res = await http.get('/recommendation/popular', { params: { limit: 8 } })
    if (res && res.data) {
      popularMovies.value = res.data
      // 取前3个作为轮播图
      bannerMovies.value = res.data.slice(0, 3)
    }
  } catch (error) {
    console.error('加载热门电影失败:', error)
  }
}

// 加载全部电影
async function loadAllMovies() {
  try {
    const res = await http.get('/admin/movie/page', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        status: 'active'
      }
    })
    if (res && res.data) {
      if (pageNum.value === 1) {
        allMovies.value = res.data.list || []
      } else {
        allMovies.value.push(...(res.data.list || []))
      }
      hasMore.value = res.data.total > allMovies.value.length
    }
  } catch (error) {
    console.error('加载电影列表失败:', error)
  }
}

// 加载更多
function loadMoreMovies() {
  pageNum.value++
  loadAllMovies()
}

// 跳转到电影详情
function goToMovieDetail(movieId) {
  router.push(`/movies/${movieId}`)
}

// 记录用户行为
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

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.getFullYear()
}
</script>

<style scoped>
.home-container {
  width: 100%;
}

.banner-carousel {
  margin-bottom: 40px;
  border-radius: 8px;
  overflow: hidden;
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to right, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.4));
  display: flex;
  align-items: center;
  padding: 0 60px;
}

.banner-content {
  color: white;
  max-width: 600px;
}

.banner-content h2 {
  font-size: 36px;
  margin-bottom: 20px;
}

.banner-desc {
  font-size: 16px;
  margin-bottom: 30px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.section {
  margin-bottom: 50px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  margin-bottom: 20px;
  color: #303133;
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
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
}

.movie-rating {
  display: flex;
  align-items: center;
}

.load-more {
  text-align: center;
  margin-top: 30px;
}
</style>