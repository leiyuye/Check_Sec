package com.example.check_sec.entity;

import com.example.check_sec.common.enums.FilingLevel;
import com.example.check_sec.common.enums.FilingStatus;
import com.example.check_sec.common.enums.SubmitterType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "filing_record", indexes = {
        @Index(name = "idx_filing_no", columnList = "filingNo", unique = true),
        @Index(name = "idx_filing_submitter", columnList = "submitterId")
})
public class FilingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32, unique = true)
    private String filingNo;

    private Long filingUnitUserId;

    @Column(nullable = false, length = 200)
    private String filingUnitName;

    @Column(length = 32)
    private String filingUnitCreditCode;

    @Column(length = 50)
    private String filingUnitContact;

    @Column(length = 20)
    private String filingUnitPhone;

    @Column(length = 300)
    private String filingUnitAddress;

    @Column(nullable = false, length = 200)
    private String systemName;

    @Column(columnDefinition = "TEXT")
    private String systemDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FilingLevel filingLevel;

    @Column(length = 100)
    private String industry;

    @Column(length = 50)
    private String systemOwner;

    @Column(length = 20)
    private String contactPhone;

    private Long materialFileId;

    private Long adminCommentFileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FilingStatus status = FilingStatus.DRAFT;

    @Column(columnDefinition = "TEXT")
    private String reviewComment;

    @Column(nullable = false)
    private Long submitterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubmitterType submitterType;

    @Column(nullable = false, length = 200)
    private String submitterName;

    private LocalDateTime submittedAt;
    private LocalDateTime lastProcessedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
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
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getSystemOwner() { return systemOwner; }
    public void setSystemOwner(String systemOwner) { this.systemOwner = systemOwner; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public Long getMaterialFileId() { return materialFileId; }
    public void setMaterialFileId(Long materialFileId) { this.materialFileId = materialFileId; }
    public Long getAdminCommentFileId() { return adminCommentFileId; }
    public void setAdminCommentFileId(Long adminCommentFileId) { this.adminCommentFileId = adminCommentFileId; }
    public FilingStatus getStatus() { return status; }
    public void setStatus(FilingStatus status) { this.status = status; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    public Long getSubmitterId() { return submitterId; }
    public void setSubmitterId(Long submitterId) { this.submitterId = submitterId; }
    public SubmitterType getSubmitterType() { return submitterType; }
    public void setSubmitterType(SubmitterType submitterType) { this.submitterType = submitterType; }
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
