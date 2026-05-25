<template>
  <div v-loading="loading">
    <el-card v-if="filing">
      <template #header>
        <div class="header-row">
          <span>备案详情 - {{ filing.filingNo }}</span>
          <el-tag :type="STATUS_TAG[filing.status]">{{ filing.statusLabel }}</el-tag>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="备案单位">{{ filing.filingUnitName }}</el-descriptions-item>
        <el-descriptions-item label="信用代码">{{ filing.filingUnitCreditCode }}</el-descriptions-item>
        <el-descriptions-item label="系统名称">{{ filing.systemName }}</el-descriptions-item>
        <el-descriptions-item label="备案等级">{{ filing.filingLevelLabel }}</el-descriptions-item>
        <el-descriptions-item label="所属行业">{{ filing.industry }}</el-descriptions-item>
        <el-descriptions-item label="系统负责人">{{ filing.systemOwner }}</el-descriptions-item>
        <el-descriptions-item label="提交方">{{ filing.submitterTypeLabel }} / {{ filing.submitterName }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ filing.submittedAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备案材料" :span="2">
          <el-button v-if="filing.materialFileId" link type="primary" @click="dl(filing.materialFileId, filing.materialFileName)">
            {{ filing.materialFileName || '下载材料' }}
          </el-button>
          <span v-else>未上传</span>
        </el-descriptions-item>
        <el-descriptions-item label="批注文件" :span="2">
          <el-button v-if="filing.adminCommentFileId" link type="primary" @click="dl(filing.adminCommentFileId, filing.adminCommentFileName)">
            {{ filing.adminCommentFileName || '下载批注文件' }}
          </el-button>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2">{{ filing.reviewComment || '-' }}</el-descriptions-item>
        <el-descriptions-item label="系统描述" :span="2">{{ filing.systemDescription || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="actions" v-if="userStore.isAdmin && filing.status !== 'APPROVED' && filing.status !== 'DRAFT'">
        <el-divider>管理员操作</el-divider>
        <el-button v-if="filing.materialFileId" @click="dl(filing.materialFileId, filing.materialFileName)">下载材料</el-button>
        <el-button type="warning" @click="showComment = true">上传批注文件</el-button>
        <el-button type="success" @click="handleApprove">审核通过</el-button>
        <el-button type="danger" @click="handleReject">退回修改</el-button>
      </div>
      <div class="actions" v-if="!userStore.isAdmin && canEdit">
        <el-button type="primary" @click="$router.push(`/filings/${filing.id}/edit`)">修改并重新提交</el-button>
      </div>
      <div class="actions">
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </el-card>

    <el-card style="margin-top:16px" v-if="logs.length">
      <template #header>操作历史</template>
      <el-timeline>
        <el-timeline-item v-for="log in logs" :key="log.id" :timestamp="log.operatedAt">
          <strong>{{ log.operationTypeLabel }}</strong> - {{ log.operatorName }}（{{ log.operatorRole }}）
          <div v-if="log.description">{{ log.description }}</div>
          <div v-if="log.reviewComment" class="comment">意见：{{ log.reviewComment }}</div>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-dialog v-model="showComment" title="上传批注文件" width="500px">
      <el-form label-width="100px">
        <el-form-item label="批注文件">
          <el-upload :limit="1" :http-request="uploadComment">
            <el-button type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="commentForm.reviewComment" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="同时退回">
          <el-switch v-model="commentForm.returnForModify" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showComment = false">取消</el-button>
        <el-button type="primary" @click="submitComment">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFiling, approveFiling, rejectFiling, uploadAdminComment } from '@/api/filing'
import { uploadFile, downloadFile } from '@/api/file'
import { useUserStore } from '@/stores/user'
import { STATUS_TAG, FILING_STATUS, FILE_PURPOSE } from '@/utils/constants'

const route = useRoute()
const userStore = useUserStore()
const loading = ref(true)
const filing = ref(null)
const logs = ref([])
const showComment = ref(false)
const commentForm = reactive({ reviewComment: '', adminCommentFileId: null, returnForModify: true })

const canEdit = computed(() => filing.value && [FILING_STATUS.RETURNED, FILING_STATUS.ANNOTATED_RETURNED, FILING_STATUS.DRAFT].includes(filing.value.status))

async function load() {
  loading.value = true
  try {
    const data = await getFiling(route.params.id)
    filing.value = data.filing
    logs.value = data.logs || []
  } finally { loading.value = false }
}

async function dl(id, name) { await downloadFile(id, name) }

async function uploadComment({ file }) {
  const res = await uploadFile(file, FILE_PURPOSE.ADMIN_COMMENT, filing.value.id)
  commentForm.adminCommentFileId = res.id
}

async function submitComment() {
  if (!commentForm.adminCommentFileId) {
    ElMessage.warning('请先上传批注文件')
    return
  }
  await uploadAdminComment(filing.value.id, commentForm)
  ElMessage.success('批注文件已上传')
  showComment.value = false
  load()
}

async function handleApprove() {
  try {
    const { value } = await ElMessageBox.prompt('请输入审核意见（可选）', '审核通过', { inputType: 'textarea' })
    await approveFiling(filing.value.id, { reviewComment: value || '' })
    ElMessage.success('已通过')
    load()
  } catch { /* 取消 */ }
}

async function handleReject() {
  const { value } = await ElMessageBox.prompt('请输入退回原因', '退回修改', { inputType: 'textarea', inputValidator: v => !!v || '必填' })
  await rejectFiling(filing.value.id, { reviewComment: value })
  ElMessage.success('已退回')
  load()
}

onMounted(load)
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
.actions { margin-top: 16px; display: flex; gap: 8px; flex-wrap: wrap; }
.comment { color: #909399; font-size: 13px; margin-top: 4px; }
</style>
