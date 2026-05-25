<template>
  <el-card style="max-width:720px">
    <template #header>{{ userStore.isAdmin ? '系统信息' : (userStore.isFilingUnit ? '个人信息' : '机构信息') }}</template>
    <el-descriptions :column="1" border v-if="info">
      <el-descriptions-item label="名称">{{ info.orgName }}</el-descriptions-item>
      <el-descriptions-item label="统一社会信用代码">{{ info.creditCode }}</el-descriptions-item>
      <el-descriptions-item label="手机号码">{{ info.phone }}</el-descriptions-item>
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ROLE_LABEL } from '@/utils/constants'
import { getMe } from '@/api/auth'

const userStore = useUserStore()
const info = ref(null)
const roleLabel = computed(() => ROLE_LABEL[info.value?.role] || '')

onMounted(async () => {
  info.value = await getMe()
  userStore.setUser(info.value)
})
</script>
