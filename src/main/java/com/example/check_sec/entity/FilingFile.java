package com.example.check_sec.entity;

import com.example.check_sec.common.enums.FilePurpose;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "filing_file")
public class FilingFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String originalName;

    @Column(nullable = false, length = 255)
    private String storedName;

    @Column(nullable = false, length = 500)
    private String filePath;

    private Long fileSize;

    @Column(length = 100)
    private String fileType;

    @Column(nullable = false)
    private Long uploaderId;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    private Long filingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FilePurpose purpose;

    @PrePersist
    public void prePersist() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getUploaderId() { return uploaderId; }
    public void setUploaderId(Long uploaderId) { this.uploaderId = uploaderId; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public Long getFilingId() { return filingId; }
    public void setFilingId(Long filingId) { this.filingId = filingId; }
    public FilePurpose getPurpose() { return purpose; }
    public void setPurpose(FilePurpose purpose) { this.purpose = purpose; }
}
