<template>
  <div class="profile-page">
    <el-card style="max-width:720px">
      <template #header>{{ userStore.isAdmin ? '系统信息' : (userStore.isFilingUnit ? '个人信息' : '机构信息') }}</template>
      <el-descriptions :column="1" border v-if="info">
        <el-descriptions-item label="名称">{{ info.orgName }}</el-descriptions-item>
        <el-descriptions-item label="统一社会信用代码">{{ info.creditCode }}</el-descriptions-item>
        <el-descriptions-item label="登录账号">{{ info.phone }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ info.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系地址">{{ info.address }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ info.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ roleLabel }}</el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <el-tag :type="info.status === 'ENABLED' ? 'success' : 'danger'">{{ info.status === 'ENABLED' ? '启用' : '禁用' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ info.createdAt }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card id="change-password" style="max-width:720px; margin-top: 16px">
      <template #header>
        <span>修改密码</span>
        <span class="header-tip">（所有角色均可在此修改自己的登录密码）</span>
      </template>
      <el-alert
        v-if="needChangePasswordHint"
        title="您正在使用默认密码，请尽快修改以确保账号安全"
        type="warning"
        show-icon
        :closable="false"
        style="margin-bottom: 16px"
      />
      <el-form ref="pwdRef" :model="pwdForm" :rules="pwdRules" label-width="110px" style="max-width: 440px" @submit.prevent>
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password clearable placeholder="请输入当前登录密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password clearable placeholder="6-32位" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password clearable placeholder="再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="pwdLoading" @click="handleChangePassword">保存新密码</el-button>
          <el-button @click="resetPwdForm">清空</el-button>
        </el-form-item>
      </el-form>
      <p v-if="userStore.isAdmin" class="admin-tip">管理员默认密码为 admin123456，修改后请使用新密码登录。</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { ROLE_LABEL } from '@/utils/constants'
import { getMe, changePassword } from '@/api/auth'

const route = useRoute()
const userStore = useUserStore()
const info = ref(null)
const roleLabel = computed(() => ROLE_LABEL[info.value?.role] || '')
const needChangePasswordHint = computed(() =>
  userStore.suggestChangePassword || info.value?.mustChangePassword === true
)

const pwdRef = ref()
const pwdLoading = ref(false)
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (_rule, value) => {
  if (!value) {
    return Promise.reject(new Error('请再次输入新密码'))
  }
  if (value !== pwdForm.newPassword) {
    return Promise.reject(new Error('两次输入的新密码不一致'))
  }
  return Promise.resolve()
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度6-32位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: ['blur', 'change'] }
  ]
}

function resetPwdForm() {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdRef.value?.clearValidate()
}

async function handleChangePassword() {
  if (!pwdRef.value) {
    ElMessage.error('表单未就绪，请刷新页面重试')
    return
  }
  try {
    await pwdRef.value.validate()
  } catch {
    ElMessage.warning('请正确填写密码信息')
    return
  }
  pwdLoading.value = true
  try {
    await changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功，下次登录请使用新密码')
    userStore.suggestChangePassword = false
    if (info.value) info.value.mustChangePassword = false
    resetPwdForm()
  } catch (e) {
    console.error('change password failed', e)
  } finally {
    pwdLoading.value = false
  }
}

onMounted(async () => {
  info.value = await getMe()
  userStore.setUser(info.value)
  userStore.suggestChangePassword = info.value?.mustChangePassword === true
  if (route.hash === '#change-password') {
    document.getElementById('change-password')?.scrollIntoView({ behavior: 'smooth' })
  }
})
</script>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
}
.header-tip {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
  margin-left: 8px;
}
.admin-tip {
  font-size: 13px;
  color: #909399;
  margin: 0 0 8px 0;
}
</style>
