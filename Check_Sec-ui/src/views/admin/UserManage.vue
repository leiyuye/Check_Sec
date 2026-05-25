<template>
  <el-card>
    <el-form :inline="true" :model="query">
      <el-form-item label="角色">
        <el-select v-model="query.role" clearable placeholder="全部" style="width:140px">
          <el-option label="备案单位" value="FILING_UNIT" />
          <el-option label="测评机构" value="TESTING_AGENCY" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="名称/手机/信用代码" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="success" @click="openDialog()">新增用户</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="orgName" label="名称" min-width="160" />
      <el-table-column prop="roleLabel" label="角色" width="100" />
      <el-table-column prop="phone" label="手机" width="120" />
      <el-table-column prop="creditCode" label="信用代码" min-width="180" show-overflow-tooltip />
      <el-table-column prop="contactPerson" label="联系人" width="100" />
      <el-table-column prop="statusLabel" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ENABLED' ? 'success' : 'danger'">{{ row.statusLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="170" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link @click="toggleStatus(row)">{{ row.status === 'ENABLED' ? '禁用' : '启用' }}</el-button>
          <el-button link type="warning" @click="handleResetPwd(row)">重置密码</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" v-model:current-page="query.page" v-model:page-size="query.size"
      :total="total" layout="total, prev, pager, next" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑用户' : '新增用户'" width="560px">
      <el-form ref="dialogRef" :model="form" :rules="dialogRules" label-width="140px">
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" :disabled="!!editId">
            <el-option label="备案单位" value="FILING_UNIT" />
            <el-option label="测评机构" value="TESTING_AGENCY" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="orgName"><el-input v-model="form.orgName" /></el-form-item>
        <el-form-item label="信用代码" prop="creditCode"><el-input v-model="form.creditCode" /></el-form-item>
        <el-form-item label="手机" prop="phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item :label="editId ? '新密码(可选)' : '密码'" :prop="editId ? '' : 'password'">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson"><el-input v-model="form.contactPerson" /></el-form-item>
        <el-form-item label="地址" prop="address"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listUsers, createUser, updateUser, deleteUser, updateUserStatus, resetPassword, getUser } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ role: '', keyword: '', page: 1, size: 10 })
const dialogVisible = ref(false)
const editId = ref(null)
const dialogRef = ref()
const form = reactive({
  role: 'FILING_UNIT', orgName: '', creditCode: '', phone: '', password: '',
  contactPerson: '', address: '', email: '', status: 'ENABLED'
})
const dialogRules = {
  role: [{ required: true }],
  orgName: [{ required: true, message: '名称必填' }],
  creditCode: [{ required: true }],
  phone: [{ required: true }],
  password: [{ required: true, min: 6 }],
  contactPerson: [{ required: true }],
  address: [{ required: true }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await listUsers({ ...query, role: query.role || undefined, keyword: query.keyword || undefined })
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.role = ''
  query.keyword = ''
  query.page = 1
  loadData()
}

async function openDialog(row) {
  editId.value = row?.id || null
  if (row) {
    const detail = await getUser(row.id)
    Object.assign(form, detail, { password: '' })
  } else {
    Object.assign(form, { role: 'FILING_UNIT', orgName: '', creditCode: '', phone: '', password: '', contactPerson: '', address: '', email: '', status: 'ENABLED' })
  }
  dialogVisible.value = true
}

async function saveUser() {
  await dialogRef.value.validate()
  if (editId.value) {
    await updateUser(editId.value, form)
  } else {
    await createUser(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function toggleStatus(row) {
  const status = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
  await updateUserStatus(row.id, status)
  ElMessage.success('操作成功')
  loadData()
}

async function handleResetPwd(row) {
  const { value } = await ElMessageBox.prompt('请输入新密码，留空则默认123456', '重置密码', { inputValue: '123456' })
  await resetPassword(row.id, value)
  ElMessage.success('密码已重置')
}

async function handleDelete(row) {
  await ElMessageBox.confirm('有关联备案时将改为禁用，确定继续？', '提示', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('操作成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
