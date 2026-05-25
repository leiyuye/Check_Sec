package com.example.check_sec.entity;

import com.example.check_sec.common.enums.OperationType;
import com.example.check_sec.common.enums.RoleType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "filing_operation_log", indexes = {
        @Index(name = "idx_log_filing", columnList = "filingId")
})
public class FilingOperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long filingId;

    @Column(nullable = false)
    private Long operatorId;

    @Column(nullable = false, length = 200)
    private String operatorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleType operatorRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OperationType operationType;

    @Column(length = 500)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String reviewComment;

    @Column(nullable = false)
    private LocalDateTime operatedAt;

    @PrePersist
    public void prePersist() {
        if (operatedAt == null) {
            operatedAt = LocalDateTime.now();
        }
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
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    public LocalDateTime getOperatedAt() { return operatedAt; }
    public void setOperatedAt(LocalDateTime operatedAt) { this.operatedAt = operatedAt; }
}
