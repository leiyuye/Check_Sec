import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ROLES } from '@/utils/constants'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { public: true } },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue'), meta: { public: true } },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
      { path: 'admin/users', name: 'AdminUsers', component: () => import('@/views/admin/UserManage.vue'), meta: { roles: [ROLES.ADMIN] } },
      { path: 'admin/filings', name: 'AdminFilings', component: () => import('@/views/admin/FilingManage.vue'), meta: { roles: [ROLES.ADMIN] } },
      { path: 'filings', name: 'MyFilings', component: () => import('@/views/filing/MyFilings.vue'), meta: { roles: [ROLES.FILING_UNIT, ROLES.TESTING_AGENCY] } },
      { path: 'filings/create', name: 'FilingCreate', component: () => import('@/views/filing/FilingForm.vue'), meta: { roles: [ROLES.FILING_UNIT, ROLES.TESTING_AGENCY] } },
      { path: 'filings/:id/edit', name: 'FilingEdit', component: () => import('@/views/filing/FilingForm.vue'), meta: { roles: [ROLES.FILING_UNIT, ROLES.TESTING_AGENCY] } },
      { path: 'filings/:id', name: 'FilingDetail', component: () => import('@/views/filing/FilingDetail.vue') },
      { path: 'profile', name: 'Profile', component: () => import('@/views/profile/Profile.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const store = useUserStore()
  if (to.meta.public) {
    if (store.isLoggedIn && (to.name === 'Login' || to.name === 'Register')) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }
  if (!store.isLoggedIn) {
    next('/login')
    return
  }
  const roles = to.meta.roles
  if (roles && !roles.includes(store.role)) {
    next('/dashboard')
    return
  }
  next()
})

export default router
