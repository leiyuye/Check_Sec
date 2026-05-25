package com.example.check_sec.storage;

import com.example.check_sec.common.exception.BusinessException;
import com.example.check_sec.config.FileStorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path rootLocation;

    public LocalFileStorageService(FileStorageProperties properties) {
        this.rootLocation = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new BusinessException("无法创建上传目录");
        }
    }

    @Override
    public String store(MultipartFile file, String subDir) {
        try {
            if (file.isEmpty()) {
                throw new BusinessException("上传文件不能为空");
            }
            String original = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                ext = original.substring(dot);
            }
            String storedName = UUID.randomUUID() + ext;
            Path targetDir = rootLocation.resolve(subDir != null ? subDir : "");
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return rootLocation.relativize(target).toString().replace('\\', '/');
        } catch (IOException e) {
            throw new BusinessException("文件存储失败: " + e.getMessage());
        }
    }

    @Override
    public Resource loadAsResource(String relativePath) {
        try {
            Path file = rootLocation.resolve(relativePath).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new BusinessException("文件不存在");
        } catch (MalformedURLException e) {
            throw new BusinessException("文件路径无效");
        }
    }

    @Override
    public void delete(String relativePath) {
        try {
            Path file = rootLocation.resolve(relativePath).normalize();
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
        }
    }
}
