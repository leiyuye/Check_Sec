import axios from 'axios'
import { ElMessage } from 'element-plus'

const fileAxios = axios.create({
  baseURL: '/api',
  timeout: 120000
})

fileAxios.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export function uploadFile(file, purpose, filingId) {
  const form = new FormData()
  form.append('file', file)
  form.append('purpose', purpose)
  if (filingId) form.append('filingId', filingId)
  return fileAxios.post('/files/upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }).then((res) => {
    if (res.data.code !== 200) {
      ElMessage.error(res.data.message)
      return Promise.reject(new Error(res.data.message))
    }
    return res.data.data
  })
}

async function parseBlobError(blob) {
  try {
    const text = await blob.text()
    const json = JSON.parse(text)
    return json.message || '下载失败'
  } catch {
    return '下载失败'
  }
}

export async function downloadFile(fileId, fileName) {
  if (!fileId) {
    ElMessage.warning('文件不存在')
    return
  }
  try {
    const res = await fileAxios.get(`/files/${fileId}/download`, {
      responseType: 'blob'
    })
    const blob = res.data
    if (blob.type && blob.type.includes('application/json')) {
      const msg = await parseBlobError(blob)
      ElMessage.error(msg)
      return
    }
    if (blob.size === 0) {
      ElMessage.error('文件为空或不存在')
      return
    }
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = fileName || 'download'
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    if (error.response?.data instanceof Blob) {
      const msg = await parseBlobError(error.response.data)
      ElMessage.error(msg)
    } else {
      ElMessage.error(error.response?.data?.message || error.message || '下载失败')
    }
  }
}
