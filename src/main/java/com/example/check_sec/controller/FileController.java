package com.example.check_sec.controller;

import com.example.check_sec.common.enums.FilePurpose;
import com.example.check_sec.common.response.ApiResponse;
import com.example.check_sec.dto.file.FileUploadResponse;
import com.example.check_sec.entity.FilingFile;
import com.example.check_sec.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ApiResponse<FileUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam FilePurpose purpose,
            @RequestParam(required = false) Long filingId) {
        return ApiResponse.ok(fileService.upload(file, purpose, filingId));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        FilingFile meta = fileService.getFile(id);
        Resource resource = fileService.download(id);
        String encoded = URLEncoder.encode(meta.getOriginalName(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .body(resource);
    }
}
