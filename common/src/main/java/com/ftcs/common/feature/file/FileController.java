package com.ftcs.common.feature.file;

import com.ftcs.common.upload.FileService;
import com.ftcs.common.upload.FolderEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@Log4j2
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Upload a file asynchronously.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") FolderEnum folderEnum
    ) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            return ResponseEntity.badRequest().body("Invalid file name");
        }

        fileService.processFileAsync(
                file,
                originalFileName,
                folderEnum,
                uploadedFileName -> log.info("File uploaded successfully: {}", uploadedFileName)
        );

        return ResponseEntity.ok("Upload success");
    }

    /**
     * Delete a file.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(
            @RequestParam("fileName") String fileName,
            @RequestParam("folder") FolderEnum folderEnum
    ) {
        fileService.processDeleteFile(fileName, folderEnum);
        log.info("File delete request processed for: {}", fileName);
        return ResponseEntity.ok("File delete started for: " + fileName);
    }
}
