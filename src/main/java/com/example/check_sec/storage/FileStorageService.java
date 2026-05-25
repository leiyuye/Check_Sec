package com.example.check_sec.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String store(MultipartFile file, String subDir);

    Resource loadAsResource(String relativePath);

    void delete(String relativePath);
}
