package com.example.check_sec.service;

import com.example.check_sec.common.enums.OperationType;
import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.entity.FilingOperationLog;
import com.example.check_sec.repository.FilingOperationLogRepository;
import com.example.check_sec.security.LoginUser;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService {

    private final FilingOperationLogRepository logRepository;

    public OperationLogService(FilingOperationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void log(Long filingId, LoginUser user, OperationType type, String description, String reviewComment) {
        FilingOperationLog log = new FilingOperationLog();
        log.setFilingId(filingId);
        log.setOperatorId(user.getUserId());
        log.setOperatorName(user.getUser().getOrgName());
        log.setOperatorRole(user.getRoleType());
        log.setOperationType(type);
        log.setDescription(description);
        log.setReviewComment(reviewComment);
        logRepository.save(log);
    }

    public void log(Long filingId, Long operatorId, String operatorName, RoleType role,
                    OperationType type, String description, String reviewComment) {
        FilingOperationLog log = new FilingOperationLog();
        log.setFilingId(filingId);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperatorRole(role);
        log.setOperationType(type);
        log.setDescription(description);
        log.setReviewComment(reviewComment);
        logRepository.save(log);
    }
}
