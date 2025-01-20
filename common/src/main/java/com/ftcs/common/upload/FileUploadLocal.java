package com.ftcs.common.upload;

import com.ftcs.common.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
public class FileUploadLocal implements UploadFileHandler {

    @Override
    public void upload(String fileName, String filePath, MultipartFile file) {
        try {
            Path uploadPath = Paths.get("./" + filePath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePathNew = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePathNew, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new AppException(400, "upload file local failure");
        }
    }

    @Override
    public void delete(String fileName, String filePath) {
        try {
            Path pathRemove = Paths.get(filePath);
            Path oldFilePath = pathRemove.resolve(fileName);
            Files.deleteIfExists(oldFilePath);
        } catch (Exception e) {
            log.error("Delete file {} local {}", fileName, filePath);
        }
    }
}

