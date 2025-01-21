package com.ftcs.common.upload;

import com.ftcs.common.exception.AppException;
import io.minio.*;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@Slf4j
@Component
public class FileUploadInternalMinio implements UploadFileHandler {
    @Getter
    private String urlFile = null;

    private void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    @Value("${internal.minio.access}")
    private String access;

    @Value("${internal.minio.secret}")
    private String secret;

    @Value("${internal.minio.api}")
    private String minioApi;

    @Value("${internal.minio.endpoint}")
    private String endPoint;

    @Value("${internal.minio.bucket-prefix}")
    private String minioBucketPrefix;

    private MinioClient client;

    @SneakyThrows
    public void initInternalMinIOConfig() {
        Interceptor assetInterceptor = new AssetInterceptor();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(assetInterceptor)
                .build();

        client = MinioClient.builder()
                .endpoint(endPoint) // Base endpoint
                .credentials(access, secret)
                .httpClient(httpClient)
                .build();

        for (FolderEnum bucket : FolderEnum.values()) {
            log.info("{}", minioBucketPrefix + bucket.getFolderName());
            createBucket(minioBucketPrefix + bucket.getFolderName());
        }
    }

    @Override
//    public void upload(String fileName, BucketEnum bucket, MultipartFile file) {
    public void upload(String fileName, String bucketName, MultipartFile file) {
        try {
            String bucket = minioBucketPrefix + bucketName;
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            client.putObject(putObjectArgs);
            setUrlFile(minioApi + "/" + bucket + "/" + fileName);
        } catch (Exception e) {
            throw new AppException(400, e.getMessage());
//            throw new AppException(400, "upload file minio failure internal");
        }
    }

    @Override
    public void delete(String fileName, String bucketPostFix) {
        try {
            String bucket = minioBucketPrefix + bucketPostFix;
            RemoveObjectArgs fileRemove = RemoveObjectArgs
                    .builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build();
            client.removeObject(fileRemove);
        } catch (Exception e) {
            log.error("Delete file minio failure: {}", e.getMessage());
        }
    }

    private void createBucket(String bucketName) {
        try {
            if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

            }

        } catch (Exception e) {
            log.error("Error when create bucket: {}", e.getMessage());
        }
    }

    public byte[] download(String fileName, String bucketPostFix) {
        try {
            String bucket = minioBucketPrefix + bucketPostFix;
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build();

            GetObjectResponse response = client.getObject(getObjectArgs);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[16384];
            int bytesRead;
            while ((bytesRead = response.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Download file minio failure {}", e.getMessage());
            return null;
        }
    }
}