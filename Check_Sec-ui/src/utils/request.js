import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 60000
})

function isLoginOrRegisterRequest(url = '') {
  return url.includes('/auth/login') || url.includes('/auth/register')
}

function resolveErrorMessage(error) {
  const data = error.response?.data
  if (data && typeof data === 'object' && data.message) {
    return data.message
  }
  if (typeof data === 'string' && data) {
    return data
  }
  return error.message || '网络错误'
}

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    if (response.config.responseType === 'blob') {
      return response
    }
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res.data
  },
  (error) => {
    const url = error.config?.url || ''
    const msg = resolveErrorMessage(error)

    if (error.response?.status === 401) {
      if (isLoginOrRegisterRequest(url)) {
        ElMessage.error(msg || '账号或密码错误')
      } else {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login')
        }
        ElMessage.error(msg || '登录已过期，请重新登录')
      }
    } else {
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default request
