<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>等保备案管理平台</h2>
      <p class="sub">请登录您的账号</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @submit.prevent>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号码 / 管理员账号 admin" prefix-icon="Iphone" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleLogin">登 录</el-button>
      </el-form>
      <div class="links">
        <router-link to="/register">备案单位 / 测评机构注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ phone: '', password: '' })
const rules = {
  phone: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await userStore.login(form)
    if (data.suggestChangePassword) {
      await ElMessageBox.alert('首次登录建议修改默认密码', '安全提示', { type: 'warning' })
    }
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a3a5c 0%, #2d5a87 50%, #1a3a5c 100%);
}
.auth-card { width: 400px; padding: 12px 8px; }
.auth-card h2 { text-align: center; margin: 0 0 8px; color: #303133; }
.sub { text-align: center; color: #909399; margin: 0 0 24px; font-size: 14px; }
.links { margin-top: 16px; text-align: center; font-size: 14px; }
.links a { color: #409eff; text-decoration: none; }
</style>
