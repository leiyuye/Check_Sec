<template>
  <el-card>
    <el-form :inline="true" :model="query">
      <el-form-item label="备案单位"><el-input v-model="query.filingUnitName" clearable /></el-form-item>
      <el-form-item label="提交方">
        <el-select v-model="query.submitterType" clearable style="width:130px">
          <el-option label="备案单位" value="FILING_UNIT" />
          <el-option label="测评机构" value="TESTING_AGENCY" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable style="width:150px">
          <el-option v-for="(label, key) in statusOptions" :key="key" :label="label" :value="key" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="filingNo" label="备案编号" width="180" />
      <el-table-column prop="filingUnitName" label="备案单位" min-width="140" />
      <el-table-column prop="submitterTypeLabel" label="提交方" width="100" />
      <el-table-column prop="submitterName" label="提交人" width="120" />
      <el-table-column prop="systemName" label="系统名称" min-width="120" />
      <el-table-column prop="filingLevelLabel" label="等级" width="80" />
      <el-table-column prop="statusLabel" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="STATUS_TAG[row.status]">{{ row.statusLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submittedAt" label="提交时间" width="170" />
      <el-table-column prop="lastProcessedAt" label="最近处理" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/filings/${row.id}`)">查看</el-button>
          <el-button link v-if="row.materialFileId" @click="dl(row.materialFileId, row.materialFileName)">下载材料</el-button>
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
import { STATUS_TAG } from '@/utils/constants'

const statusOptions = {
  DRAFT: '草稿', PENDING_REVIEW: '待审核', UNDER_REVIEW: '审核中', RETURNED: '退回修改',
  ANNOTATED_RETURNED: '已回传批注', RESUBMITTED: '已重新提交', APPROVED: '审核通过'
}
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ filingUnitName: '', submitterType: '', status: '', page: 1, size: 10 })

async function loadData() {
  loading.value = true
  try {
    const res = await listFilings({ ...query, filingUnitName: query.filingUnitName || undefined, submitterType: query.submitterType || undefined, status: query.status || undefined })
    tableData.value = res.records
    total.value = res.total
  } finally { loading.value = false }
}
function resetQuery() { Object.assign(query, { filingUnitName: '', submitterType: '', status: '', page: 1 }); loadData() }
async function dl(id, name) { await downloadFile(id, name) }
onMounted(loadData)
</script>

<style scoped>.pager { margin-top: 16px; justify-content: flex-end; }</style>
