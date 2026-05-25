<template>
  <el-card>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="140px" style="max-width:800px">
      <template v-if="userStore.isTestingAgency">
        <el-divider content-position="left">备案单位信息</el-divider>
        <el-form-item label="单位名称" prop="filingUnitName"><el-input v-model="form.filingUnitName" /></el-form-item>
        <el-form-item label="信用代码" prop="filingUnitCreditCode"><el-input v-model="form.filingUnitCreditCode" /></el-form-item>
        <el-form-item label="联系人" prop="filingUnitContact"><el-input v-model="form.filingUnitContact" /></el-form-item>
        <el-form-item label="联系电话" prop="filingUnitPhone"><el-input v-model="form.filingUnitPhone" /></el-form-item>
        <el-form-item label="联系地址" prop="filingUnitAddress"><el-input v-model="form.filingUnitAddress" /></el-form-item>
      </template>
      <el-divider content-position="left">系统备案信息</el-divider>
      <el-form-item label="系统名称" prop="systemName"><el-input v-model="form.systemName" /></el-form-item>
      <el-form-item label="系统描述"><el-input v-model="form.systemDescription" type="textarea" :rows="3" /></el-form-item>
      <el-form-item label="备案等级" prop="filingLevel">
        <el-select v-model="form.filingLevel" style="width:100%">
          <el-option v-for="l in FILING_LEVELS" :key="l.value" :label="l.label" :value="l.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="所属行业"><el-input v-model="form.industry" /></el-form-item>
      <el-form-item label="系统负责人"><el-input v-model="form.systemOwner" /></el-form-item>
      <el-form-item label="联系电话"><el-input v-model="form.contactPhone" /></el-form-item>
      <el-form-item label="备案材料" prop="materialFileId">
        <el-upload :limit="1" :file-list="fileList" :http-request="uploadMaterial">
          <el-button type="primary">上传材料 (doc/pdf/zip等, ≤50MB)</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="saveDraft">保存草稿</el-button>
        <el-button type="success" :loading="saving" @click="saveAndSubmit">保存并提交</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createFiling, updateFiling, getFiling, submitFiling } from '@/api/filing'
import { uploadFile } from '@/api/file'
import { useUserStore } from '@/stores/user'
import { FILING_LEVELS, FILE_PURPOSE } from '@/utils/constants'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const saving = ref(false)
const fileList = ref([])
const editId = computed(() => route.params.id)

const form = reactive({
  filingUnitName: '', filingUnitCreditCode: '', filingUnitContact: '', filingUnitPhone: '', filingUnitAddress: '',
  systemName: '', systemDescription: '', filingLevel: 'LEVEL_3', industry: '', systemOwner: '', contactPhone: '',
  materialFileId: null
})

const rules = computed(() => {
  const base = {
    systemName: [{ required: true, message: '系统名称必填' }],
    filingLevel: [{ required: true }],
    materialFileId: [{ required: true, message: '请上传备案材料' }]
  }
  if (userStore.isTestingAgency) {
    base.filingUnitName = [{ required: true, message: '备案单位名称必填' }]
    base.filingUnitCreditCode = [{ required: true }]
    base.filingUnitContact = [{ required: true }]
    base.filingUnitPhone = [{ required: true }]
    base.filingUnitAddress = [{ required: true }]
  }
  return base
})

async function uploadMaterial({ file }) {
  const res = await uploadFile(file, FILE_PURPOSE.USER_MATERIAL, editId.value ? Number(editId.value) : undefined)
  form.materialFileId = res.id
  fileList.value = [{ name: res.originalName }]
}

async function saveDraft() {
  await formRef.value.validateField(['systemName', 'filingLevel'].concat(
    userStore.isTestingAgency ? ['filingUnitName', 'filingUnitCreditCode', 'filingUnitContact', 'filingUnitPhone', 'filingUnitAddress'] : []
  ))
  saving.value = true
  try {
    if (editId.value) await updateFiling(editId.value, form)
    else await createFiling(form)
    ElMessage.success('草稿已保存')
    router.push('/filings')
  } finally { saving.value = false }
}

async function saveAndSubmit() {
  await formRef.value.validate()
  saving.value = true
  try {
    let id = editId.value
    if (id) {
      await updateFiling(id, form)
    } else {
      const created = await createFiling(form)
      id = created.id
    }
    await submitFiling(id)
    ElMessage.success('提交成功')
    router.push('/filings')
  } finally { saving.value = false }
}

onMounted(async () => {
  if (editId.value) {
    const detail = await getFiling(editId.value)
    const f = detail.filing
    Object.assign(form, f)
    if (f.materialFileName) fileList.value = [{ name: f.materialFileName }]
  }
})
</script>
