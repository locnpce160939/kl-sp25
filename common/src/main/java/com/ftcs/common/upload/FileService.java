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
    private final FileUploadInternalMinio internalMinio;


    public FileService(
            FileUploadLocal fileUploadLocal, FileUploadInternalMinio internalMinio) {
        this.fileUploadLocal = fileUploadLocal;
        this.internalMinio = internalMinio;
        internalMinio.initInternalMinIOConfig();
    }

    @Async
    public CompletableFuture<Void> processFileAsync(
            MultipartFile file,
            String fileName,
            FolderEnum folderEnum,
            Consumer<String> callback

    ) {
        String finalFileName = generateUniqueFileName(fileName);

        final var internalAsyncTask = CompletableFuture.runAsync(() ->
                internalMinio.upload(finalFileName, folderEnum.getFolderName(), file));

        return CompletableFuture.allOf(internalAsyncTask).thenRun(() -> {
            callback.accept(internalMinio.getUrlFile());
            log.info(internalMinio.getUrlFile());
            System.gc();
        });
    }


    @Async
    public void processDeleteFile(String fileName, FolderEnum bucketEnum) {
        fileUploadLocal.delete(fileName, bucketEnum.getLocalPath());
        internalMinio.delete(fileName, bucketEnum.getFolderName());
    }

    public byte[] downloadFile(String fileName, FolderEnum bucketEnum) {
        return internalMinio.download(fileName, bucketEnum.getFolderName());
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

    public FileUploadInternalMinio getInternalMinio() {
        return internalMinio;
    }
}
