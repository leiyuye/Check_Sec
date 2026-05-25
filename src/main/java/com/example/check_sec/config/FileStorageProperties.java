package com.example.check_sec.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.file")
public class FileStorageProperties {

    private String uploadDir;
    private int maxSizeMb;
    private List<String> allowedExtensions;

    public String getUploadDir() { return uploadDir; }
    public void setUploadDir(String uploadDir) { this.uploadDir = uploadDir; }
    public int getMaxSizeMb() { return maxSizeMb; }
    public void setMaxSizeMb(int maxSizeMb) { this.maxSizeMb = maxSizeMb; }
    public List<String> getAllowedExtensions() { return allowedExtensions; }
    public void setAllowedExtensions(List<String> allowedExtensions) { this.allowedExtensions = allowedExtensions; }
}
