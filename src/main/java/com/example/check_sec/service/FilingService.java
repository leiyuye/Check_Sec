package com.example.check_sec.service;

import com.example.check_sec.common.enums.*;
import com.example.check_sec.common.exception.BusinessException;
import com.example.check_sec.dto.common.PageResult;
import com.example.check_sec.dto.filing.*;
import com.example.check_sec.entity.FilingFile;
import com.example.check_sec.entity.FilingRecord;
import com.example.check_sec.entity.SysUser;
import com.example.check_sec.repository.FilingFileRepository;
import com.example.check_sec.repository.FilingOperationLogRepository;
import com.example.check_sec.repository.FilingRecordRepository;
import com.example.check_sec.security.LoginUser;
import com.example.check_sec.security.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class FilingService {

    private static final Set<FilingStatus> EDITABLE_FOR_USER = EnumSet.of(
            FilingStatus.DRAFT, FilingStatus.RETURNED, FilingStatus.ANNOTATED_RETURNED);

    private final FilingRecordRepository filingRepository;
    private final FilingFileRepository fileRepository;
    private final FilingOperationLogRepository logRepository;
    private final OperationLogService operationLogService;

    public FilingService(FilingRecordRepository filingRepository,
                         FilingFileRepository fileRepository,
                         FilingOperationLogRepository logRepository,
                         OperationLogService operationLogService) {
        this.filingRepository = filingRepository;
        this.fileRepository = fileRepository;
        this.logRepository = logRepository;
        this.operationLogService = operationLogService;
    }

    public PageResult<FilingResponse> list(String filingUnitName, SubmitterType submitterType,
                                           FilingStatus status, int page, int size) {
        LoginUser user = SecurityUtils.currentUser();
        Specification<FilingRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (user.getRoleType() == RoleType.FILING_UNIT) {
                predicates.add(cb.equal(root.get("filingUnitUserId"), user.getUserId()));
            } else if (user.getRoleType() == RoleType.TESTING_AGENCY) {
                predicates.add(cb.equal(root.get("submitterId"), user.getUserId()));
            }
            if (StringUtils.hasText(filingUnitName)) {
                predicates.add(cb.like(root.get("filingUnitName"), "%" + filingUnitName.trim() + "%"));
            }
            if (submitterType != null) {
                predicates.add(cb.equal(root.get("submitterType"), submitterType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        PageRequest pr = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FilingRecord> result = filingRepository.findAll(spec, pr);
        List<FilingResponse> list = result.getContent().stream().map(this::toResponse).toList();
        return new PageResult<>(list, result.getTotalElements(), page, size);
    }

    public FilingDetailResponse detail(Long id) {
        FilingRecord record = getAccessible(id);
        FilingDetailResponse resp = new FilingDetailResponse();
        resp.setFiling(toResponse(record));
        resp.setLogs(logRepository.findByFilingIdOrderByOperatedAtDesc(id).stream()
                .map(OperationLogResponse::from).toList());
        return resp;
    }

    @Transactional
    public FilingResponse create(FilingRequest request) {
        LoginUser user = SecurityUtils.currentUser();
        FilingRecord record = new FilingRecord();
        record.setFilingNo(generateFilingNo());
        record.setStatus(FilingStatus.DRAFT);
        fillSubmitter(record, user);
        applyRequest(record, request, user, false);
        filingRepository.save(record);
        bindFileToFiling(request.getMaterialFileId(), record.getId());
        operationLogService.log(record.getId(), user, OperationType.CREATE_DRAFT, "创建备案草稿", null);
        return toResponse(record);
    }

    @Transactional
    public FilingResponse update(Long id, FilingRequest request) {
        LoginUser user = SecurityUtils.currentUser();
        FilingRecord record = getAccessible(id);
        assertEditable(record, user);
        applyRequest(record, request, user, false);
        filingRepository.save(record);
        bindFileToFiling(request.getMaterialFileId(), record.getId());
        operationLogService.log(record.getId(), user, OperationType.EDIT_INFO, "编辑备案信息", null);
        return toResponse(record);
    }

    @Transactional
    public FilingResponse submit(Long id) {
        LoginUser user = SecurityUtils.currentUser();
        FilingRecord record = getAccessible(id);
        if (record.getStatus() != FilingStatus.DRAFT
                && record.getStatus() != FilingStatus.RETURNED
                && record.getStatus() != FilingStatus.ANNOTATED_RETURNED) {
            throw new BusinessException("当前状态不允许提交");
        }
        if (record.getMaterialFileId() == null) {
            throw new BusinessException("请先上传备案材料");
        }
        boolean resubmit = record.getStatus() == FilingStatus.RETURNED
                || record.getStatus() == FilingStatus.ANNOTATED_RETURNED;
        record.setStatus(resubmit ? FilingStatus.RESUBMITTED : FilingStatus.PENDING_REVIEW);
        record.setSubmittedAt(LocalDateTime.now());
        record.setLastProcessedAt(LocalDateTime.now());
        filingRepository.save(record);
        bindFileToFiling(record.getMaterialFileId(), record.getId());
        operationLogService.log(record.getId(), user,
                resubmit ? OperationType.RESUBMIT : OperationType.SUBMIT,
                resubmit ? "重新提交备案" : "提交备案申请", null);
        return toResponse(record);
    }

    @Transactional
    public FilingResponse resubmit(Long id, FilingRequest request) {
        update(id, request);
        return submit(id);
    }

    @Transactional
    public FilingResponse approve(Long id, ReviewRequest request) {
        LoginUser user = SecurityUtils.currentUser();
        if (user.getRoleType() != RoleType.ADMIN) {
            throw new BusinessException(403, "仅管理员可审核通过");
        }
        FilingRecord record = getAccessible(id);
        record.setStatus(FilingStatus.APPROVED);
        record.setReviewComment(request.getReviewComment());
        record.setLastProcessedAt(LocalDateTime.now());
        filingRepository.save(record);
        operationLogService.log(record.getId(), user, OperationType.APPROVE, "审核通过", request.getReviewComment());
        return toResponse(record);
    }

    @Transactional
    public FilingResponse reject(Long id, ReviewRequest request) {
        LoginUser user = SecurityUtils.currentUser();
        if (user.getRoleType() != RoleType.ADMIN) {
            throw new BusinessException(403, "仅管理员可退回修改");
        }
        FilingRecord record = getAccessible(id);
        if (record.getStatus() == FilingStatus.APPROVED) {
            throw new BusinessException("已审核通过的备案不能退回");
        }
        record.setStatus(FilingStatus.RETURNED);
        record.setReviewComment(request.getReviewComment());
        record.setLastProcessedAt(LocalDateTime.now());
        filingRepository.save(record);
        operationLogService.log(record.getId(), user, OperationType.REJECT, "退回修改", request.getReviewComment());
        return toResponse(record);
    }

    @Transactional
    public FilingResponse uploadAdminComment(Long id, ReviewRequest request) {
        LoginUser user = SecurityUtils.currentUser();
        if (user.getRoleType() != RoleType.ADMIN) {
            throw new BusinessException(403, "仅管理员可上传批注文件");
        }
        FilingRecord record = getAccessible(id);
        if (request.getAdminCommentFileId() == null) {
            throw new BusinessException("请上传批注文件");
        }
        record.setAdminCommentFileId(request.getAdminCommentFileId());
        record.setReviewComment(request.getReviewComment());
        record.setStatus(request.isReturnForModify() ? FilingStatus.RETURNED : FilingStatus.ANNOTATED_RETURNED);
        record.setLastProcessedAt(LocalDateTime.now());
        filingRepository.save(record);
        bindFileToFiling(request.getAdminCommentFileId(), record.getId());
        operationLogService.log(record.getId(), user, OperationType.UPLOAD_COMMENT_FILE,
                "上传管理员批注文件", request.getReviewComment());
        if (request.isReturnForModify()) {
            operationLogService.log(record.getId(), user, OperationType.REJECT, "退回修改", request.getReviewComment());
        }
        return toResponse(record);
    }

    private void fillSubmitter(FilingRecord record, LoginUser user) {
        SysUser u = user.getUser();
        record.setSubmitterId(u.getId());
        record.setSubmitterName(u.getOrgName());
        if (u.getRole() == RoleType.FILING_UNIT) {
            record.setSubmitterType(SubmitterType.FILING_UNIT);
            record.setFilingUnitUserId(u.getId());
            record.setFilingUnitName(u.getOrgName());
            record.setFilingUnitCreditCode(u.getCreditCode());
            record.setFilingUnitContact(u.getContactPerson());
            record.setFilingUnitPhone(u.getPhone());
            record.setFilingUnitAddress(u.getAddress());
        } else if (u.getRole() == RoleType.TESTING_AGENCY) {
            record.setSubmitterType(SubmitterType.TESTING_AGENCY);
        } else {
            throw new BusinessException(403, "管理员不能创建备案申请");
        }
    }

    private void applyRequest(FilingRecord record, FilingRequest request, LoginUser user, boolean bindImmediately) {
        if (user.getRoleType() == RoleType.TESTING_AGENCY) {
            if (!StringUtils.hasText(request.getFilingUnitName())) {
                throw new BusinessException("备案单位名称不能为空");
            }
            record.setFilingUnitName(request.getFilingUnitName());
            record.setFilingUnitCreditCode(request.getFilingUnitCreditCode());
            record.setFilingUnitContact(request.getFilingUnitContact());
            record.setFilingUnitPhone(request.getFilingUnitPhone());
            record.setFilingUnitAddress(request.getFilingUnitAddress());
        }
        record.setSystemName(request.getSystemName());
        record.setSystemDescription(request.getSystemDescription());
        record.setFilingLevel(request.getFilingLevel());
        record.setIndustry(request.getIndustry());
        record.setSystemOwner(request.getSystemOwner());
        record.setContactPhone(request.getContactPhone());
        if (request.getMaterialFileId() != null) {
            record.setMaterialFileId(request.getMaterialFileId());
            if (bindImmediately && record.getId() != null) {
                bindFileToFiling(request.getMaterialFileId(), record.getId());
            }
        }
    }

    private void bindFileToFiling(Long fileId, Long filingId) {
        if (fileId == null || filingId == null) return;
        fileRepository.findById(fileId).ifPresent(f -> {
            f.setFilingId(filingId);
            fileRepository.save(f);
        });
    }

    private void assertEditable(FilingRecord record, LoginUser user) {
        if (user.getRoleType() == RoleType.ADMIN) {
            return;
        }
        if (record.getStatus() == FilingStatus.APPROVED) {
            throw new BusinessException("审核通过后不允许修改");
        }
        if (!EDITABLE_FOR_USER.contains(record.getStatus())) {
            throw new BusinessException("当前状态不允许编辑，请等待管理员退回后再修改");
        }
    }

    private FilingRecord getAccessible(Long id) {
        FilingRecord record = filingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("备案不存在"));
        LoginUser user = SecurityUtils.currentUser();
        if (user.getRoleType() == RoleType.ADMIN) {
            return record;
        }
        if (user.getRoleType() == RoleType.FILING_UNIT
                && record.getFilingUnitUserId() != null
                && record.getFilingUnitUserId().equals(user.getUserId())) {
            return record;
        }
        if (user.getRoleType() == RoleType.TESTING_AGENCY
                && record.getSubmitterId().equals(user.getUserId())) {
            return record;
        }
        throw new BusinessException(403, "无权限访问该备案");
    }

    private FilingResponse toResponse(FilingRecord record) {
        FilingResponse resp = FilingResponse.from(record);
        if (record.getMaterialFileId() != null) {
            fileRepository.findById(record.getMaterialFileId())
                    .ifPresent(f -> resp.setMaterialFileName(f.getOriginalName()));
        }
        if (record.getAdminCommentFileId() != null) {
            fileRepository.findById(record.getAdminCommentFileId())
                    .ifPresent(f -> resp.setAdminCommentFileName(f.getOriginalName()));
        }
        return resp;
    }

    private String generateFilingNo() {
        String prefix = "BA" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        return prefix + String.format("%04d", (int) (Math.random() * 10000));
    }
}
