package com.example.check_sec.repository;

import com.example.check_sec.common.enums.FilingStatus;
import com.example.check_sec.common.enums.SubmitterType;
import com.example.check_sec.entity.FilingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FilingRecordRepository extends JpaRepository<FilingRecord, Long>, JpaSpecificationExecutor<FilingRecord> {

    Optional<FilingRecord> findFirstByMaterialFileId(Long materialFileId);

    Optional<FilingRecord> findFirstByAdminCommentFileId(Long adminCommentFileId);

    long countByStatus(FilingStatus status);

    long countBySubmitterId(Long submitterId);

    long countBySubmitterIdAndStatus(Long submitterId, FilingStatus status);

    long countByFilingUnitUserId(Long filingUnitUserId);

    long countByFilingUnitUserIdAndStatus(Long filingUnitUserId, FilingStatus status);

    @Query("SELECT COUNT(f) FROM FilingRecord f WHERE f.filingUnitUserId = :userId OR f.submitterId = :userId")
    long countByUserRelated(@Param("userId") Long userId);

    boolean existsByFilingUnitUserId(Long filingUnitUserId);

    boolean existsBySubmitterId(Long submitterId);
}
