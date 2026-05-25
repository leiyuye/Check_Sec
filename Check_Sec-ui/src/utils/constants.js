export const ROLES = {
  ADMIN: 'ADMIN',
  FILING_UNIT: 'FILING_UNIT',
  TESTING_AGENCY: 'TESTING_AGENCY'
}

export const ROLE_LABEL = {
  ADMIN: '管理员',
  FILING_UNIT: '备案单位',
  TESTING_AGENCY: '测评机构'
}

export const FILING_STATUS = {
  DRAFT: 'DRAFT',
  PENDING_REVIEW: 'PENDING_REVIEW',
  UNDER_REVIEW: 'UNDER_REVIEW',
  RETURNED: 'RETURNED',
  ANNOTATED_RETURNED: 'ANNOTATED_RETURNED',
  RESUBMITTED: 'RESUBMITTED',
  APPROVED: 'APPROVED'
}

export const STATUS_TAG = {
  DRAFT: 'info',
  PENDING_REVIEW: 'warning',
  UNDER_REVIEW: 'primary',
  RETURNED: 'danger',
  ANNOTATED_RETURNED: 'warning',
  RESUBMITTED: 'primary',
  APPROVED: 'success'
}

export const FILING_LEVELS = [
  { value: 'LEVEL_2', label: '第二级' },
  { value: 'LEVEL_3', label: '第三级' },
  { value: 'LEVEL_4', label: '第四级' },
  { value: 'LEVEL_5', label: '第五级' }
]

export const FILE_PURPOSE = {
  USER_MATERIAL: 'USER_MATERIAL',
  ADMIN_COMMENT: 'ADMIN_COMMENT',
  QUALIFICATION: 'QUALIFICATION'
}
