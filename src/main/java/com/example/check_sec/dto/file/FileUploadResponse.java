package com.example.check_sec.dto.file;

import com.example.check_sec.common.enums.FilePurpose;
import com.example.check_sec.entity.FilingFile;

import java.time.LocalDateTime;

public class FileUploadResponse {

    private Long id;
    private String originalName;
    private Long fileSize;
    private String fileType;
    private FilePurpose purpose;
    private Long filingId;
    private LocalDateTime uploadedAt;

    public static FileUploadResponse from(FilingFile file) {
        FileUploadResponse r = new FileUploadResponse();
        r.id = file.getId();
        r.originalName = file.getOriginalName();
        r.fileSize = file.getFileSize();
        r.fileType = file.getFileType();
        r.purpose = file.getPurpose();
        r.filingId = file.getFilingId();
        r.uploadedAt = file.getUploadedAt();
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public FilePurpose getPurpose() { return purpose; }
    public void setPurpose(FilePurpose purpose) { this.purpose = purpose; }
    public Long getFilingId() { return filingId; }
    public void setFilingId(Long filingId) { this.filingId = filingId; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
