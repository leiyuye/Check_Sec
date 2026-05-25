package com.example.check_sec.dto.filing;

import com.example.check_sec.common.enums.OperationType;
import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.entity.FilingOperationLog;

import java.time.LocalDateTime;

public class OperationLogResponse {

    private Long id;
    private Long filingId;
    private Long operatorId;
    private String operatorName;
    private RoleType operatorRole;
    private OperationType operationType;
    private String operationTypeLabel;
    private String description;
    private String reviewComment;
    private LocalDateTime operatedAt;

    public static OperationLogResponse from(FilingOperationLog log) {
        OperationLogResponse r = new OperationLogResponse();
        r.id = log.getId();
        r.filingId = log.getFilingId();
        r.operatorId = log.getOperatorId();
        r.operatorName = log.getOperatorName();
        r.operatorRole = log.getOperatorRole();
        r.operationType = log.getOperationType();
        r.operationTypeLabel = operationLabel(log.getOperationType());
        r.description = log.getDescription();
        r.reviewComment = log.getReviewComment();
        r.operatedAt = log.getOperatedAt();
        return r;
    }

    private static String operationLabel(OperationType type) {
        return switch (type) {
            case CREATE_DRAFT -> "创建草稿";
            case SUBMIT -> "提交备案";
            case DOWNLOAD_MATERIAL -> "下载材料";
            case UPLOAD_COMMENT_FILE -> "上传批注文件";
            case REJECT -> "退回修改";
            case RESUBMIT -> "重新提交";
            case APPROVE -> "审核通过";
            case EDIT_INFO -> "编辑备案信息";
        };
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFilingId() { return filingId; }
    public void setFilingId(Long filingId) { this.filingId = filingId; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public RoleType getOperatorRole() { return operatorRole; }
    public void setOperatorRole(RoleType operatorRole) { this.operatorRole = operatorRole; }
    public OperationType getOperationType() { return operationType; }
    public void setOperationType(OperationType operationType) { this.operationType = operationType; }
    public String getOperationTypeLabel() { return operationTypeLabel; }
    public void setOperationTypeLabel(String operationTypeLabel) { this.operationTypeLabel = operationTypeLabel; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    public LocalDateTime getOperatedAt() { return operatedAt; }
    public void setOperatedAt(LocalDateTime operatedAt) { this.operatedAt = operatedAt; }
}
