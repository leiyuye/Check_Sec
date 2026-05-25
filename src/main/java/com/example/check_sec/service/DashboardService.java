package com.example.check_sec.service;

import com.example.check_sec.common.enums.FilingStatus;
import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.dto.dashboard.DashboardStatsResponse;
import com.example.check_sec.repository.FilingRecordRepository;
import com.example.check_sec.repository.SysUserRepository;
import com.example.check_sec.security.LoginUser;
import com.example.check_sec.security.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final SysUserRepository userRepository;
    private final FilingRecordRepository filingRepository;

    public DashboardService(SysUserRepository userRepository, FilingRecordRepository filingRepository) {
        this.userRepository = userRepository;
        this.filingRepository = filingRepository;
    }

    public DashboardStatsResponse stats() {
        LoginUser user = SecurityUtils.currentUser();
        DashboardStatsResponse stats = new DashboardStatsResponse();
        if (user.getRoleType() == RoleType.ADMIN) {
            stats.setTotalUsers(userRepository.count());
            stats.setFilingUnitCount(userRepository.countByRole(RoleType.FILING_UNIT));
            stats.setTestingAgencyCount(userRepository.countByRole(RoleType.TESTING_AGENCY));
            stats.setPendingReviewCount(countFilings(FilingStatus.PENDING_REVIEW, FilingStatus.RESUBMITTED, FilingStatus.UNDER_REVIEW));
            stats.setApprovedCount(filingRepository.countByStatus(FilingStatus.APPROVED));
            stats.setReturnedCount(filingRepository.countByStatus(FilingStatus.RETURNED));
        } else if (user.getRoleType() == RoleType.FILING_UNIT) {
            Long uid = user.getUserId();
            stats.setMyTotalFilings(filingRepository.countByFilingUnitUserId(uid));
            stats.setMyPendingCount(countByUnit(uid, FilingStatus.PENDING_REVIEW, FilingStatus.RESUBMITTED, FilingStatus.UNDER_REVIEW));
            stats.setMyReturnedCount(countByUnit(uid, FilingStatus.RETURNED, FilingStatus.ANNOTATED_RETURNED));
            stats.setMyApprovedCount(filingRepository.countByFilingUnitUserIdAndStatus(uid, FilingStatus.APPROVED));
        } else {
            Long sid = user.getUserId();
            stats.setMyTotalFilings(filingRepository.countBySubmitterId(sid));
            stats.setMyPendingCount(countBySubmitter(sid, FilingStatus.PENDING_REVIEW, FilingStatus.RESUBMITTED, FilingStatus.UNDER_REVIEW));
            stats.setMyReturnedCount(countBySubmitter(sid, FilingStatus.RETURNED, FilingStatus.ANNOTATED_RETURNED));
            stats.setMyApprovedCount(filingRepository.countBySubmitterIdAndStatus(sid, FilingStatus.APPROVED));
        }
        return stats;
    }

    private long countFilings(FilingStatus... statuses) {
        long total = 0;
        for (FilingStatus s : statuses) {
            total += filingRepository.countByStatus(s);
        }
        return total;
    }

    private long countByUnit(Long uid, FilingStatus... statuses) {
        long total = 0;
        for (FilingStatus s : statuses) {
            total += filingRepository.countByFilingUnitUserIdAndStatus(uid, s);
        }
        return total;
    }

    private long countBySubmitter(Long sid, FilingStatus... statuses) {
        long total = 0;
        for (FilingStatus s : statuses) {
            total += filingRepository.countBySubmitterIdAndStatus(sid, s);
        }
        return total;
    }
}
