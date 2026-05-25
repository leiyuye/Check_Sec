import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getMe } from '@/api/auth'
import { ROLES } from '@/utils/constants'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const suggestChangePassword = ref(false)

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => user.value?.role)
  const isAdmin = computed(() => role.value === ROLES.ADMIN)
  const isFilingUnit = computed(() => role.value === ROLES.FILING_UNIT)
  const isTestingAgency = computed(() => role.value === ROLES.TESTING_AGENCY)

  async function login(form) {
    const data = await loginApi(form)
    token.value = data.token
    user.value = data.user
    suggestChangePassword.value = data.suggestChangePassword
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(data.user))
    return data
  }

  async function fetchMe() {
    const data = await getMe()
    user.value = data
    localStorage.setItem('user', JSON.stringify(data))
    return data
  }

  function logout() {
    token.value = ''
    user.value = null
    suggestChangePassword.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  function setUser(data) {
    user.value = data
    localStorage.setItem('user', JSON.stringify(data))
  }

  return {
    token, user, suggestChangePassword, isLoggedIn, role, isAdmin, isFilingUnit, isTestingAgency,
    login, fetchMe, logout, setUser
  }
})
