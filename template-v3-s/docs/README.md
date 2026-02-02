# 电影推荐系统设计文档总览

## 📋 文档结构

本目录包含电影推荐系统的完整设计方案，包括：

1. **[系统设计方案.md](./系统设计方案.md)** - 整体架构设计、技术选型、执行计划
2. **[推荐算法设计文档.md](./推荐算法设计文档.md)** - 混合推荐算法详细设计
3. **[模块划分与实现计划.md](./模块划分与实现计划.md)** - 各模块详细设计与开发计划

## 🎯 核心设计要点

### 1. 推荐算法策略

采用**多策略融合的混合推荐算法**，根据用户特征动态选择推荐策略：

```
新用户（行为<5条）
  → 兴趣标签推荐 + 热门推荐（70% + 30%）

轻度用户（行为5-20条）
  → 协同过滤 + 热门推荐（60% + 40%）

重度用户（行为>20条）
  → 协同过滤 + 实时推荐 + 热门推荐（50% + 30% + 20%）
```

### 2. 数据库设计

共21张核心表，涵盖：
- **用户相关**：user、admin、user_interest_tag
- **电影相关**：movie、movie_type、movie_tag、movie_cast、movie_rating、user_favorite
- **影院相关**：cinema、cinema_room、seat、screening、order、order_detail
- **推荐相关**：user_behavior、recommendation_result、recommendation_evaluation
- **其他**：announcement

详细SQL文件：`../sql/movie_recommendation_system.sql`

### 3. 技术架构

**后端**：
- Spring Boot 3 + JDK 21
- MyBatis + MySQL
- Redis（推荐结果缓存）
- JWT（身份认证）

**推荐算法**：
- 协同过滤（基于用户/物品相似度）
- 内容推荐（基于电影特征）
- 热门推荐（基于评分、票房）
- 实时推荐（基于最近行为）

### 4. 开发迭代计划

**7个迭代周期，共8-9周**：
1. 基础框架（1周）
2. 电影管理模块（1周）
3. 影院与订单模块（1周）
4. 用户行为采集（3天）
5. 推荐引擎核心（2周）⭐⭐⭐
6. 推荐效果评估（1周）
7. 前端开发（2周）

## 🚀 快速开始

### 1. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE movie_recommendation DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行SQL文件
source sql/movie_recommendation_system.sql;
```

### 2. 项目配置

更新 `application.yaml` 中的数据库连接信息。

### 3. 开发顺序建议

1. **第一阶段**：完成基础CRUD功能（用户、电影、影院、订单）
2. **第二阶段**：实现用户行为采集
3. **第三阶段**：实现推荐算法（先实现热门推荐和内容推荐，再实现协同过滤）
4. **第四阶段**：推荐效果评估和优化

## 📊 推荐算法实现优先级

### MVP阶段（必须实现）
1. ✅ 热门推荐（最简单，可作为兜底）
2. ✅ 内容推荐（基于电影类型、标签）
3. ✅ 实时推荐（基于最近浏览行为）

### 进阶阶段（推荐实现）
4. ✅ 协同过滤（基于用户相似度）
5. ✅ 推荐结果融合
6. ✅ Redis缓存优化

### 高级阶段（可选）
7. ✅ 基于物品的协同过滤
8. ✅ A/B测试
9. ✅ 深度学习模型（未来扩展）

## 🔧 关键技术点

### 1. 推荐结果缓存
- 使用Redis存储推荐结果
- Key格式：`recommendation:user:{userId}:{type}`
- TTL：3600秒（1小时）

### 2. 性能优化
- 预计算电影相似度矩阵
- 限制计算范围（Top-K相似用户/电影）
- 异步更新推荐结果

### 3. 冷启动处理
- 新用户：基于注册兴趣标签推荐
- 新电影：基于类型、演员推荐相似电影

## 📝 注意事项

1. **推荐算法复杂度**：建议先实现简化版，后续再优化
2. **数据量考虑**：当前设计支持10万级用户，如需支持更大规模，需引入Spark等大数据组件
3. **实时性要求**：推荐结果缓存1小时，用户行为后异步更新
4. **多样性保证**：限制同一类型/导演的电影数量，确保推荐多样性

## 🎓 学习资源

- [推荐系统实践](https://book.douban.com/subject/10769749/)
- [协同过滤算法详解](https://zhuanlan.zhihu.com/p/80069337)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)

## 📞 问题反馈

如有问题或建议，请及时反馈，我们将持续优化设计方案。


API 接口
用户端
POST /common/register - 用户注册（type=USER）
POST /common/login - 用户登录（type=USER）
POST /common/updatePassword - 修改密码
GET /common/currentUser - 获取当前用户信息
管理员端
POST /common/login - 管理员登录（type=ADMIN）
GET /admin/user/page - 用户分页查询
GET /admin/user/list - 用户列表
POST /admin/user/add - 新增用户
PUT /admin/user/update - 更新用户
DELETE /admin/user/delete - 批量删除用户
电影类型管理
GET /admin/movie-type/page：分页查询（支持 name、status 筛选）
GET /admin/movie-type/selectById/{id}：详情
GET /admin/movie-type/list：列表
POST /admin/movie-type/add：新增
PUT /admin/movie-type/update：更新
DELETE /admin/movie-type/delete：批量删除（传 id 集合）
电影标签管理
GET /admin/movie-tag/page：分页查询（支持 name、category、status）
GET /admin/movie-tag/selectById/{id}：详情
GET /admin/movie-tag/list：列表
POST /admin/movie-tag/add：新增
PUT /admin/movie-tag/update：更新
DELETE /admin/movie-tag/delete：批量删除
电影信息管理
GET /admin/movie/page：分页查询（支持 name、director、status）
GET /admin/movie/selectById/{id}：
返回结构为 Map：
movie：电影基础信息
typeIds：关联类型 ID 列表
tagIds：关联标签 ID 列表
GET /admin/movie/list：列表
POST /admin/movie/add：新增，入参 MovieDTO
PUT /admin/movie/update：更新，入参 MovieDTO
DELETE /admin/movie/delete：批量删除（会同时删掉类型、标签关联和演职人员）
多类型、多标签逻辑：
新增/更新时，MovieDTO.typeIds、MovieDTO.tagIds 会被写入：
movie_type_relation (movie_id, type_id)
movie_tag_relation (movie_id, tag_id)
演职人员管理
GET /admin/movie-cast/list/{movieId}：查询某电影的演职人员列表
POST /admin/movie-cast/add：新增演职人员
PUT /admin/movie-cast/update：更新演职人员
DELETE /admin/movie-cast/delete/{id}：删除单个演职人员
用户行为数据
POST /behavior/record：记录用户行为
GET /behavior/statistics：获取当前用户行为统计
GET /behavior/statistics/{userId}：获取指定用户行为统计（管理员）
GET /behavior/list：获取当前用户行为列表
GET /behavior/list/{userId}：获取指定用户行为列表（管理员）
推荐
GET /recommendation/list - 获取混合推荐列表
GET /recommendation/profile - 获取用户画像
GET /recommendation/collaborative - 协同过滤推荐
GET /recommendation/content - 内容推荐
GET /recommendation/popular - 热门推荐
GET /recommendation/realtime - 实时推荐
推荐结果评估
POST /recommendation/admin/evaluate - 计算并保存评估结果
GET /recommendation/admin/evaluation/list - 获取评估结果列表
GET /recommendation/admin/evaluation/charts - 获取可视化报表数据
GET /recommendation/admin/evaluation/precision-recall - 计算准确率和召回率
GET /recommendation/admin/evaluation/diversity - 计算多样性
GET /recommendation/admin/evaluation/satisfaction - 计算用户满意度
GET /recommendation/admin/evaluation/ctr - 计算点击率