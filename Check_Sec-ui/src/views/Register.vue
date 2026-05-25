<template>
  <div class="auth-page">
    <el-card class="auth-card wide">
      <h2>用户注册</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="注册类型" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio value="FILING_UNIT">备案单位</el-radio>
            <el-radio value="TESTING_AGENCY">测评机构</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="form.role === 'TESTING_AGENCY' ? '机构名称' : '单位名称'" prop="orgName">
          <el-input v-model="form.orgName" />
        </el-form-item>
        <el-form-item label="统一社会信用代码" prop="creditCode">
          <el-input v-model="form.creditCode" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson" />
        </el-form-item>
        <el-form-item label="联系地址" prop="address">
          <el-input v-model="form.address" type="textarea" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item v-if="form.role === 'TESTING_AGENCY'" label="资质证明">
          <el-upload :auto-upload="true" :limit="1" :http-request="uploadQual">
            <el-button type="primary" link>上传资质文件（可选）</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">注册</el-button>
          <el-button @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'
import { uploadFile } from '@/api/file'
import { FILE_PURPOSE } from '@/utils/constants'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  role: 'FILING_UNIT',
  orgName: '', creditCode: '', phone: '', password: '',
  contactPerson: '', address: '', email: '', qualificationFileId: null
})
const rules = {
  role: [{ required: true }],
  orgName: [{ required: true, message: '名称不能为空' }],
  creditCode: [{ required: true, message: '信用代码不能为空' }],
  phone: [{ required: true, pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }],
  password: [{ required: true, min: 6, message: '密码至少6位' }],
  contactPerson: [{ required: true, message: '联系人不能为空' }],
  address: [{ required: true, message: '地址不能为空' }]
}

async function uploadQual({ file }) {
  const res = await uploadFile(file, FILE_PURPOSE.QUALIFICATION)
  form.qualificationFileId = res.id
}

async function handleRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
  background: linear-gradient(135deg, #1a3a5c 0%, #2d5a87 100%);
  padding: 24px;
}
.auth-card { width: 400px; padding: 12px; }
.auth-card.wide { width: 620px; }
.auth-card h2 { text-align: center; margin: 0 0 20px; }
</style>
