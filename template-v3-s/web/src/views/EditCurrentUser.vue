<template>
  <div style="width:600px;margin: 0 auto">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息修改</span>
        </div>
      </template>
      <el-form :model="formData" label-width="100px">
        <el-form-item prop="img" label="头像">
          <MyUpLoad type="imageCard" :limit="1" :files="formData.avatarUrl"
                    @setFiles="formData.avatarUrl =$event"></MyUpLoad>
        </el-form-item>
        <el-form-item prop="username" label="用户名">
          <el-input type="text"
                    v-model="formData.username"
                    auto-complete="off"
                    placeholder="用户名"
                    :disabled="true"
          ></el-input>
        </el-form-item>
        <el-form-item prop="nickname" label="昵称">
          <el-input type="text"
                    v-model="formData.nickname"
                    auto-complete="off"
                    placeholder="昵称"
          ></el-input>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input
              v-model="formData.email"
              auto-complete="off"
              placeholder="邮箱"
          ></el-input>
        </el-form-item>
        <el-form-item prop="tel" label="电话">
          <el-input type="text"
                    v-model="formData.tel"
                    auto-complete="off"
                    placeholder="电话"
          ></el-input>
        </el-form-item>
        <el-form-item prop="gender" label="性别">
          <el-radio-group v-model="formData.gender">
            <el-radio value="male">男</el-radio>
            <el-radio value="female">女</el-radio>
            <el-radio value="unknown">保密</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="birthday" label="生日">
          <el-date-picker
              v-model="formData.birthday"
              type="date"
              placeholder="选择生日"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledDate"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-button type="primary" style="width:100px;" @click="handleSubmit">修改</el-button>
          <el-button style="width:100px; margin-left: 10px;" @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import utils from "@/utils/tools.js";
import MyUpLoad from "@/components/MyUpload.vue";
import {ElMessage} from 'element-plus';
import http from "@/utils/http.js";

const router = useRouter();
const formData = ref({});

load();

function load() {
  const currentUser = utils.getCurrentUser();
  // 确保生日格式正确
  if (currentUser && currentUser.birthday) {
    // 将后端返回的LocalDate格式转换为字符串格式
    currentUser.birthday = formatDate(currentUser.birthday);
  }
  formData.value = currentUser;
}

function formatDate(date) {
  if (!date) return '';
  // 处理后端返回的LocalDate对象
  if (date.year && date.month && date.day) {
    return `${date.year}-${String(date.month).padStart(2, '0')}-${String(date.day).padStart(2, '0')}`;
  }
  // 处理JavaScript Date对象
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function disabledDate(time) {
  // 禁用未来日期
  return time.getTime() > Date.now();
}

function handleCancel() {
  router.push('/home');
}

function handleSubmit() {
  http.post('/common/updateCurrentUser', formData.value).then(res => {
    http.get("/common/currentUser").then(res1 => {
      let currentUser = res1.data;
      // 确保生日格式正确
      if (currentUser && currentUser.birthday) {
        // 将后端返回的LocalDate格式转换为字符串格式
        currentUser.birthday = formatDate(currentUser.birthday);
      }
      localStorage.setItem("currentUser", JSON.stringify(currentUser));
      formData.value = currentUser;
      ElMessage({
        message: '修改成功',
        type: 'success',
        duration: 2000,
        onClose: () => {
          router.push('/home');
        }
      });
    })
  });
};
</script>
