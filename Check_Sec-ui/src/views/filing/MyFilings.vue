<template>
  <el-card>
    <el-form :inline="true" :model="query">
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable style="width:150px">
          <el-option v-for="(label, key) in statusOptions" :key="key" :label="label" :value="key" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" v-if="userStore.isFilingUnit" @click="$router.push('/filings/create')">新增备案</el-button>
        <el-button type="success" v-else @click="$router.push('/filings/create')">代提交备案</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="filingNo" label="备案编号" width="180" />
      <el-table-column prop="filingUnitName" label="备案单位" min-width="140" />
      <el-table-column prop="systemName" label="系统名称" min-width="120" />
      <el-table-column prop="filingLevelLabel" label="等级" width="80" />
      <el-table-column prop="statusLabel" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="STATUS_TAG[row.status]">{{ row.statusLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submittedAt" label="提交时间" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/filings/${row.id}`)">详情</el-button>
          <el-button link v-if="canEdit(row)" @click="$router.push(`/filings/${row.id}/edit`)">编辑</el-button>
          <el-button link v-if="row.adminCommentFileId" @click="dl(row.adminCommentFileId, row.adminCommentFileName)">下载批注</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination class="pager" v-model:current-page="query.page" :total="total" layout="total, prev, pager, next" @current-change="loadData" />
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listFilings } from '@/api/filing'
import { downloadFile } from '@/api/file'
import { useUserStore } from '@/stores/user'
import { STATUS_TAG, FILING_STATUS } from '@/utils/constants'

const userStore = useUserStore()
const statusOptions = { DRAFT: '草稿', PENDING_REVIEW: '待审核', RETURNED: '退回修改', ANNOTATED_RETURNED: '已回传批注', RESUBMITTED: '已重新提交', APPROVED: '审核通过' }
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ status: '', page: 1, size: 10 })

function canEdit(row) {
  return [FILING_STATUS.DRAFT, FILING_STATUS.RETURNED, FILING_STATUS.ANNOTATED_RETURNED].includes(row.status)
}

async function loadData() {
  loading.value = true
  try {
    const res = await listFilings({ ...query, status: query.status || undefined })
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}
async function dl(id, name) { await downloadFile(id, name) }
onMounted(loadData)
</script>

<style scoped>.pager { margin-top: 16px; justify-content: flex-end; }</style>
