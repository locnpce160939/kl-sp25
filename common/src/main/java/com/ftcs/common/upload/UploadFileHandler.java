package com.ftcs.common.upload;

import org.springframework.web.multipart.MultipartFile;

public interface UploadFileHandler {
    void upload(String fileName, String filePath, MultipartFile file);

    void delete(String fileName, String filePath);
}
