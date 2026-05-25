package com.example.check_sec.dto.filing;

import com.example.check_sec.common.enums.FilingLevel;
import com.example.check_sec.common.enums.FilingStatus;
import com.example.check_sec.common.enums.SubmitterType;
import com.example.check_sec.entity.FilingRecord;

import java.time.LocalDateTime;

public class FilingResponse {

    private Long id;
    private String filingNo;
    private Long filingUnitUserId;
    private String filingUnitName;
    private String filingUnitCreditCode;
    private String filingUnitContact;
    private String filingUnitPhone;
    private String filingUnitAddress;
    private String systemName;
    private String systemDescription;
    private FilingLevel filingLevel;
    private String filingLevelLabel;
    private String industry;
    private String systemOwner;
    private String contactPhone;
    private Long materialFileId;
    private String materialFileName;
    private Long adminCommentFileId;
    private String adminCommentFileName;
    private FilingStatus status;
    private String statusLabel;
    private String reviewComment;
    private Long submitterId;
    private SubmitterType submitterType;
    private String submitterTypeLabel;
    private String submitterName;
    private LocalDateTime submittedAt;
    private LocalDateTime lastProcessedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FilingResponse from(FilingRecord r) {
        FilingResponse resp = new FilingResponse();
        resp.id = r.getId();
        resp.filingNo = r.getFilingNo();
        resp.filingUnitUserId = r.getFilingUnitUserId();
        resp.filingUnitName = r.getFilingUnitName();
        resp.filingUnitCreditCode = r.getFilingUnitCreditCode();
        resp.filingUnitContact = r.getFilingUnitContact();
        resp.filingUnitPhone = r.getFilingUnitPhone();
        resp.filingUnitAddress = r.getFilingUnitAddress();
        resp.systemName = r.getSystemName();
        resp.systemDescription = r.getSystemDescription();
        resp.filingLevel = r.getFilingLevel();
        resp.filingLevelLabel = r.getFilingLevel() != null ? r.getFilingLevel().getLabel() : null;
        resp.industry = r.getIndustry();
        resp.systemOwner = r.getSystemOwner();
        resp.contactPhone = r.getContactPhone();
        resp.materialFileId = r.getMaterialFileId();
        resp.adminCommentFileId = r.getAdminCommentFileId();
        resp.status = r.getStatus();
        resp.statusLabel = statusLabelOf(r.getStatus());
        resp.reviewComment = r.getReviewComment();
        resp.submitterId = r.getSubmitterId();
        resp.submitterType = r.getSubmitterType();
        resp.submitterTypeLabel = r.getSubmitterType() == SubmitterType.FILING_UNIT ? "备案单位" : "测评机构";
        resp.submitterName = r.getSubmitterName();
        resp.submittedAt = r.getSubmittedAt();
        resp.lastProcessedAt = r.getLastProcessedAt();
        resp.createdAt = r.getCreatedAt();
        resp.updatedAt = r.getUpdatedAt();
        return resp;
    }

    public static String statusLabelOf(FilingStatus status) {
        if (status == null) return "";
        return switch (status) {
            case DRAFT -> "草稿";
            case PENDING_REVIEW -> "待审核";
            case UNDER_REVIEW -> "审核中";
            case RETURNED -> "退回修改";
            case ANNOTATED_RETURNED -> "已回传批注文件";
            case RESUBMITTED -> "已重新提交";
            case APPROVED -> "审核通过";
        };
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilingNo() { return filingNo; }
    public void setFilingNo(String filingNo) { this.filingNo = filingNo; }
    public Long getFilingUnitUserId() { return filingUnitUserId; }
    public void setFilingUnitUserId(Long filingUnitUserId) { this.filingUnitUserId = filingUnitUserId; }
    public String getFilingUnitName() { return filingUnitName; }
    public void setFilingUnitName(String filingUnitName) { this.filingUnitName = filingUnitName; }
    public String getFilingUnitCreditCode() { return filingUnitCreditCode; }
    public void setFilingUnitCreditCode(String filingUnitCreditCode) { this.filingUnitCreditCode = filingUnitCreditCode; }
    public String getFilingUnitContact() { return filingUnitContact; }
    public void setFilingUnitContact(String filingUnitContact) { this.filingUnitContact = filingUnitContact; }
    public String getFilingUnitPhone() { return filingUnitPhone; }
    public void setFilingUnitPhone(String filingUnitPhone) { this.filingUnitPhone = filingUnitPhone; }
    public String getFilingUnitAddress() { return filingUnitAddress; }
    public void setFilingUnitAddress(String filingUnitAddress) { this.filingUnitAddress = filingUnitAddress; }
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }
    public String getSystemDescription() { return systemDescription; }
    public void setSystemDescription(String systemDescription) { this.systemDescription = systemDescription; }
    public FilingLevel getFilingLevel() { return filingLevel; }
    public void setFilingLevel(FilingLevel filingLevel) { this.filingLevel = filingLevel; }
    public String getFilingLevelLabel() { return filingLevelLabel; }
    public void setFilingLevelLabel(String filingLevelLabel) { this.filingLevelLabel = filingLevelLabel; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getSystemOwner() { return systemOwner; }
    public void setSystemOwner(String systemOwner) { this.systemOwner = systemOwner; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public Long getMaterialFileId() { return materialFileId; }
    public void setMaterialFileId(Long materialFileId) { this.materialFileId = materialFileId; }
    public String getMaterialFileName() { return materialFileName; }
    public void setMaterialFileName(String materialFileName) { this.materialFileName = materialFileName; }
    public Long getAdminCommentFileId() { return adminCommentFileId; }
    public void setAdminCommentFileId(Long adminCommentFileId) { this.adminCommentFileId = adminCommentFileId; }
    public String getAdminCommentFileName() { return adminCommentFileName; }
    public void setAdminCommentFileName(String adminCommentFileName) { this.adminCommentFileName = adminCommentFileName; }
    public FilingStatus getStatus() { return status; }
    public void setStatus(FilingStatus status) { this.status = status; }
    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    public Long getSubmitterId() { return submitterId; }
    public void setSubmitterId(Long submitterId) { this.submitterId = submitterId; }
    public SubmitterType getSubmitterType() { return submitterType; }
    public void setSubmitterType(SubmitterType submitterType) { this.submitterType = submitterType; }
    public String getSubmitterTypeLabel() { return submitterTypeLabel; }
    public void setSubmitterTypeLabel(String submitterTypeLabel) { this.submitterTypeLabel = submitterTypeLabel; }
    public String getSubmitterName() { return submitterName; }
    public void setSubmitterName(String submitterName) { this.submitterName = submitterName; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getLastProcessedAt() { return lastProcessedAt; }
    public void setLastProcessedAt(LocalDateTime lastProcessedAt) { this.lastProcessedAt = lastProcessedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
