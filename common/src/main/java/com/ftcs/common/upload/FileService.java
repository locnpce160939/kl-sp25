package com.ftcs.common.upload;


import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Component
@Log4j2
public class FileService {
    private final FileUploadLocal fileUploadLocal;

    public FileService(
            FileUploadLocal fileUploadLocal) {
        this.fileUploadLocal = fileUploadLocal;
    }

    @Async
    public void processFileAsync(
            MultipartFile file,
            String fileName,
            FolderEnum folderEnum,
            Consumer<String> callback

    ) {
        String finalFileName = generateUniqueFileName(fileName);

        final var localAsyncTask = CompletableFuture.runAsync(() ->
                fileUploadLocal.upload(finalFileName, folderEnum.getLocalPath(), file)
        );
        localAsyncTask.thenRun(() -> {
            callback.accept(finalFileName);
            System.gc();
        });
    }


    @Async
    public void processDeleteFile(String fileName, FolderEnum folderEnum) {
        fileUploadLocal.delete(fileName, folderEnum.getLocalPath());
    }

    public String generateUniqueFileName(String originalFileName) {
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return UUID.randomUUID() + "_" + timestamp + extension;
    }
}
