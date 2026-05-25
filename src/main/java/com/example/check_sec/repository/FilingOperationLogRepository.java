package com.example.check_sec.repository;

import com.example.check_sec.entity.FilingOperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilingOperationLogRepository extends JpaRepository<FilingOperationLog, Long> {

    List<FilingOperationLog> findByFilingIdOrderByOperatedAtDesc(Long filingId);
}
