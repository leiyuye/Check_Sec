<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">等保备案管理平台</div>
      <el-menu :default-active="activeMenu" router background-color="#001529" text-color="#fff" active-text-color="#409eff">
        <el-menu-item index="/dashboard"><el-icon><HomeFilled /></el-icon>首页</el-menu-item>
        <template v-if="userStore.isAdmin">
          <el-menu-item index="/admin/users"><el-icon><User /></el-icon>用户管理</el-menu-item>
          <el-menu-item index="/admin/filings"><el-icon><Document /></el-icon>备案管理</el-menu-item>
        </template>
        <template v-if="userStore.isFilingUnit">
          <el-menu-item index="/filings"><el-icon><Folder /></el-icon>我的备案</el-menu-item>
          <el-menu-item index="/filings/create"><el-icon><Plus /></el-icon>新增备案</el-menu-item>
        </template>
        <template v-if="userStore.isTestingAgency">
          <el-menu-item index="/filings/create"><el-icon><Upload /></el-icon>代提交备案</el-menu-item>
          <el-menu-item index="/filings"><el-icon><List /></el-icon>我的提交</el-menu-item>
        </template>
        <el-menu-item index="/profile"><el-icon><Setting /></el-icon>{{ profileLabel }}</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="title">{{ pageTitle }}</span>
        <div class="user-area">
          <span>{{ userStore.user?.orgName }}（{{ roleLabel }}）</span>
          <el-button type="danger" link @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ROLE_LABEL, ROLES } from '@/utils/constants'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path.startsWith('/filings/') && route.params.id && route.name !== 'FilingCreate'
  ? (userStore.isFilingUnit ? '/filings' : '/filings')
  : route.path)

const roleLabel = computed(() => ROLE_LABEL[userStore.role] || '')
const profileLabel = computed(() => userStore.isAdmin ? '系统信息' : (userStore.isFilingUnit ? '个人信息' : '机构信息'))

const pageTitle = computed(() => {
  const map = {
    Dashboard: '首页',
    AdminUsers: '用户管理',
    AdminFilings: '备案管理',
    MyFilings: userStore.isTestingAgency ? '我的提交' : '我的备案',
    FilingCreate: userStore.isTestingAgency ? '代提交备案' : '新增备案',
    FilingEdit: '编辑备案',
    FilingDetail: '备案详情',
    Profile: profileLabel.value
  }
  return map[route.name] || '等保备案管理平台'
})

function handleLogout() {
  ElMessageBox.confirm('确定退出登录？', '提示').then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout { height: 100vh; }
.aside { background: #001529; }
.logo {
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  padding: 20px 16px;
  border-bottom: 1px solid #ffffff1a;
  line-height: 1.4;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e8e8e8;
  height: 56px;
}
.title { font-size: 16px; font-weight: 600; color: #303133; }
.user-area { display: flex; align-items: center; gap: 12px; color: #606266; font-size: 14px; }
.main { background: #f0f2f5; padding: 16px; }
</style>
