package com.example.check_sec.service;

import com.example.check_sec.common.enums.FilePurpose;
import com.example.check_sec.common.enums.RoleType;
import com.example.check_sec.common.exception.BusinessException;
import com.example.check_sec.config.FileStorageProperties;
import com.example.check_sec.dto.file.FileUploadResponse;
import com.example.check_sec.entity.FilingFile;
import com.example.check_sec.entity.FilingRecord;
import com.example.check_sec.repository.FilingFileRepository;
import com.example.check_sec.repository.FilingRecordRepository;
import com.example.check_sec.security.LoginUser;
import com.example.check_sec.security.SecurityUtils;
import com.example.check_sec.storage.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private final FilingFileRepository fileRepository;
    private final FilingRecordRepository filingRepository;
    private final FileStorageService fileStorageService;
    private final FileStorageProperties properties;
    private final OperationLogService operationLogService;

    public FileService(FilingFileRepository fileRepository,
                       FilingRecordRepository filingRepository,
                       FileStorageService fileStorageService,
                       FileStorageProperties properties,
                       OperationLogService operationLogService) {
        this.fileRepository = fileRepository;
        this.filingRepository = filingRepository;
        this.fileStorageService = fileStorageService;
        this.properties = properties;
        this.operationLogService = operationLogService;
    }

    @Transactional
    public FileUploadResponse upload(MultipartFile file, FilePurpose purpose, Long filingId) {
        validateFile(file);
        LoginUser user = SecurityUtils.currentUser();
        if (filingId != null) {
            FilingRecord filing = filingRepository.findById(filingId)
                    .orElseThrow(() -> new BusinessException("备案不存在"));
            assertFilingFileAccess(user, filing);
        }
        String subDir = purpose.name().toLowerCase();
        String path = fileStorageService.store(file, subDir);
        FilingFile record = new FilingFile();
        record.setOriginalName(file.getOriginalFilename());
        record.setStoredName(path.substring(path.lastIndexOf('/') + 1));
        record.setFilePath(path);
        record.setFileSize(file.getSize());
        record.setFileType(file.getContentType());
        record.setUploaderId(user.getUserId());
        record.setFilingId(filingId);
        record.setPurpose(purpose);
        fileRepository.save(record);
        if (filingId != null && purpose == FilePurpose.USER_MATERIAL) {
            operationLogService.log(filingId, user, com.example.check_sec.common.enums.OperationType.EDIT_INFO,
                    "上传备案材料", null);
        }
        return FileUploadResponse.from(record);
    }

    @Transactional
    public Resource download(Long fileId) {
        FilingFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException("文件不存在"));
        LoginUser user = SecurityUtils.currentUser();
        FilingRecord filing = resolveFilingForFile(file);
        if (filing != null) {
            assertFilingFileAccess(user, filing);
            if (user.getRoleType() == RoleType.ADMIN) {
                operationLogService.log(filing.getId(), user,
                        com.example.check_sec.common.enums.OperationType.DOWNLOAD_MATERIAL,
                        "下载文件: " + file.getOriginalName(), null);
            }
        } else if (file.getPurpose() == FilePurpose.QUALIFICATION) {
            if (!file.getUploaderId().equals(user.getUserId()) && user.getRoleType() != RoleType.ADMIN) {
                throw new BusinessException(403, "无权限下载该文件");
            }
        } else if (!file.getUploaderId().equals(user.getUserId()) && user.getRoleType() != RoleType.ADMIN) {
            throw new BusinessException(403, "无权限下载该文件");
        }
        return fileStorageService.loadAsResource(file.getFilePath());
    }

    private FilingRecord resolveFilingForFile(FilingFile file) {
        if (file.getFilingId() != null) {
            return filingRepository.findById(file.getFilingId()).orElse(null);
        }
        return filingRepository.findFirstByMaterialFileId(file.getId())
                .or(() -> filingRepository.findFirstByAdminCommentFileId(file.getId()))
                .orElse(null);
    }

    public FilingFile getFile(Long fileId) {
        return fileRepository.findById(fileId).orElseThrow(() -> new BusinessException("文件不存在"));
    }

    private void assertFilingFileAccess(LoginUser user, FilingRecord filing) {
        if (user.getRoleType() == RoleType.ADMIN) {
            return;
        }
        if (user.getRoleType() == RoleType.FILING_UNIT
                && filing.getFilingUnitUserId() != null
                && filing.getFilingUnitUserId().equals(user.getUserId())) {
            return;
        }
        if (user.getRoleType() == RoleType.TESTING_AGENCY
                && filing.getSubmitterId().equals(user.getUserId())) {
            return;
        }
        throw new BusinessException(403, "无权限访问该备案文件");
    }

    private void validateFile(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("文件名无效");
        }
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1).toLowerCase() : "";
        if (!properties.getAllowedExtensions().contains(ext)) {
            throw new BusinessException("不支持的文件类型: " + ext);
        }
        long maxBytes = (long) properties.getMaxSizeMb() * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BusinessException("文件大小超过限制 " + properties.getMaxSizeMb() + "MB");
        }
    }
}
