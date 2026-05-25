import request from '@/utils/request'

export function listFilings(params) {
  return request.get('/filings', { params })
}

export function getFiling(id) {
  return request.get(`/filings/${id}`)
}

export function createFiling(data) {
  return request.post('/filings', data)
}

export function updateFiling(id, data) {
  return request.put(`/filings/${id}`, data)
}

export function submitFiling(id) {
  return request.post(`/filings/${id}/submit`)
}

export function resubmitFiling(id, data) {
  return request.post(`/filings/${id}/resubmit`, data)
}

export function approveFiling(id, data) {
  return request.post(`/filings/${id}/approve`, data)
}

export function rejectFiling(id, data) {
  return request.post(`/filings/${id}/reject`, data)
}

export function uploadAdminComment(id, data) {
  return request.post(`/filings/${id}/admin-comment-file`, data)
}
