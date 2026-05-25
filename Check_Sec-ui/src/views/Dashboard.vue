<template>
  <div>
    <el-row :gutter="16" v-if="userStore.isAdmin">
      <el-col :span="4" v-for="item in adminCards" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" v-else>
      <el-col :span="6" v-for="item in userCards" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top:16px">
      <template #header>欢迎使用等保备案管理平台</template>
      <p>本系统用于管理定级备案材料的上传、审核、批注回传与重新提交流程。</p>
      <p v-if="userStore.isAdmin">请通过左侧菜单进入「用户管理」或「备案管理」处理业务。</p>
      <p v-else-if="userStore.isFilingUnit">请通过「我的备案」查看申请状态，或通过「新增备案」提交材料。</p>
      <p v-else>请通过「代提交备案」为备案单位提交材料，在「我的提交」中跟踪进度。</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getStats } from '@/api/dashboard'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const stats = ref({})

const adminCards = computed(() => [
  { label: '注册用户', value: stats.value.totalUsers || 0 },
  { label: '备案单位', value: stats.value.filingUnitCount || 0 },
  { label: '测评机构', value: stats.value.testingAgencyCount || 0 },
  { label: '待审核备案', value: stats.value.pendingReviewCount || 0 },
  { label: '已通过', value: stats.value.approvedCount || 0 },
  { label: '退回修改', value: stats.value.returnedCount || 0 }
])

const userCards = computed(() => [
  { label: userStore.isTestingAgency ? '提交总数' : '备案总数', value: stats.value.myTotalFilings || 0 },
  { label: '待审核', value: stats.value.myPendingCount || 0 },
  { label: '退回修改', value: stats.value.myReturnedCount || 0 },
  { label: '已通过', value: stats.value.myApprovedCount || 0 }
])

onMounted(async () => {
  stats.value = await getStats()
})
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-value { font-size: 28px; font-weight: 700; color: #409eff; }
.stat-label { margin-top: 8px; color: #909399; font-size: 14px; }
</style>
